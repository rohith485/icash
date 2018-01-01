package com.icashflow.service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.icashflow.to.BuyerInputDetails;
import com.icashflow.to.InvoiceDetails;
import com.icashflow.to.InvoiceDiscountDetails;
import com.icashflow.to.SellerInputDetails;

@Service
@Qualifier("iCashServiceImpl")
public class IcashServiceImpl implements IcashService{
	private BuyerService buyerService;
	private SellerService sellerService;
	private InvoiceDetailsService invoiceDetailsService; 
	private UserCredentialService userCredentialService;
	private UserProfileService userProfileService;
	private UserRoleService userRoleService;
	
	@Override
	public boolean processBuyerInput(BuyerInputDetails buyerInputDetails) throws Exception {
		boolean insertInvoiceDetailsStatus = this.insertInvoiceDetails(buyerInputDetails);
		boolean insertBuyerInputsStatus = this.insertBuyerInputs(buyerInputDetails);
		return insertInvoiceDetailsStatus && insertBuyerInputsStatus;
	}
	
	@Override
	public boolean insertBuyerInputs(BuyerInputDetails buyerInputDetails) throws ClassNotFoundException, SQLException {
		return buyerService.updateBuyerInputs(buyerInputDetails);
	}

	@Override
	public boolean insertSellerInputs(SellerInputDetails sellerInputDetails) {
		return sellerService.updateSellerInputs(sellerInputDetails);
	}

	@Override
	public boolean insertInvoiceDetails(BuyerInputDetails buyerInputDetails) throws Exception {
		return invoiceDetailsService.updateInvoiceDetails(buyerInputDetails);
	}
	
	@Override
	public void generateAwardsFile() throws ClassNotFoundException, SQLException, IOException {
		List<InvoiceDetails> allInvoicesUploadedToday = getAllInvoicesUploadedToday();
		Map<String, List<InvoiceDetails>> allSuppliersInvoiceMap = allInvoicesUploadedToday.stream()
				.collect(Collectors.groupingBy(InvoiceDetails::getSellerKey));
		BuyerInputDetails buyerTodayInputDetails = getBuyerTodayInputDetails();
		List<SellerInputDetails> sellerTodayInputDetails = getSellerTodayInputDetails();
		List<InvoiceDiscountDetails> eligibleInvoiceList = new ArrayList<>();
		List<InvoiceDiscountDetails> inEligibleInvoiceList = new ArrayList<>();
		
		for (SellerInputDetails sellerInputDetails : sellerTodayInputDetails) {
			InvoiceDiscountDetails invoiceDiscountDetails = new InvoiceDiscountDetails();
			//invoiceDiscountDetails.setBuyerKey(buyerKey);
			float maxDiscount = sellerInputDetails.getMaxDiscount();
			float minDiscount = sellerInputDetails.getMinDiscount();
			invoiceDiscountDetails.setSellerKey(sellerInputDetails.getSellerId());
			invoiceDiscountDetails.setMaximumDiscount(maxDiscount);
			invoiceDiscountDetails.setMinimunDiscount(minDiscount);
			List<InvoiceDetails> invoicesListOfSelectedSeller = allSuppliersInvoiceMap.get(sellerInputDetails.getSellerId());
			invoicesListOfSelectedSeller.sort(new Comparator<InvoiceDetails>() {
				@Override
				public int compare(InvoiceDetails o1, InvoiceDetails o2) {
					return o2.getInvoiceDueDays() - o1.getInvoiceDueDays();
				}
			});
			invoiceDiscountDetails.getFilteredInvoicesList().addAll(invoicesListOfSelectedSeller);

			if(maxDiscount < buyerTodayInputDetails.getMroi()) {
				inEligibleInvoiceList.add(invoiceDiscountDetails);
			}else {
				eligibleInvoiceList.add(invoiceDiscountDetails);
			}
		}
		
		eligibleInvoiceList.sort(new Comparator<InvoiceDiscountDetails>() {
			@Override
			public int compare(InvoiceDiscountDetails o1, InvoiceDiscountDetails o2) {
				return Double.compare(o2.getMinimunDiscount(), o1.getMaximumDiscount());
			}
		});
		
		
		float totalAmountToBePaid = 0.0f;
		boolean terminateLoop = false;
		int invoiceRow = 0;
		//int tempIndex = 0;
		//Loop for each seller
		for(int tempIndex = 0 ; tempIndex < eligibleInvoiceList.size() ;) {
			InvoiceDiscountDetails invoiceDiscDetails = eligibleInvoiceList.get(tempIndex);
			for( int j=0 ; j < invoiceDiscDetails.getFilteredInvoicesList().size() 
					&& invoiceRow < invoiceDiscDetails.getFilteredInvoicesList().size(); j++ ) {
				InvoiceDetails invoiceDetails = invoiceDiscDetails.getFilteredInvoicesList().get(invoiceRow);
				float days = (float)invoiceDetails.getInvoiceDueDays() / (float)365;
				float discountAmount = (float)invoiceDiscDetails.getMinimunDiscount() * 
						days * (float)invoiceDetails.getInvoiceAmount();
				float amoutToBePaidAfterDiscounting = (float)invoiceDetails.getInvoiceAmount() - discountAmount;
				float tempTotalAmount = totalAmountToBePaid + amoutToBePaidAfterDiscounting;
				
				if(tempTotalAmount < buyerTodayInputDetails.getMaxReserveAmount()) {
					totalAmountToBePaid = tempTotalAmount;
					invoiceDetails.setEligibleForFinalDiscounting(true);
					invoiceDetails.setDiscountedAmount(discountAmount);
					invoiceDetails.setAmoutToBePaidAfterDiscounting(amoutToBePaidAfterDiscounting);
					invoiceDetails.setIcashComission(discountAmount * 0.15f);
					invoiceDetails.setApr((discountAmount/(float)invoiceDetails.getInvoiceAmount()) * (365/invoiceDetails.getInvoiceDueDays()) );
				}else {
					terminateLoop = true;
				}
				System.out.println("***********totalAmountToBePaid********* : " +totalAmountToBePaid );
				break;
			}
			System.out.println("value of i :" + tempIndex + " total size " + " invoiceDiscDetails.size() "+ eligibleInvoiceList.size());
			if( tempIndex == eligibleInvoiceList.size() -1 ) {
				invoiceRow = invoiceRow + 1;
				tempIndex=0;
			}else {
				tempIndex = tempIndex + 1;
			}
			if(terminateLoop) {
				break;
			}
		}
		
		prepareAwardsFile(eligibleInvoiceList, inEligibleInvoiceList);
		

	}
	
	 /**
     * @param eligibleInvoiceList
     * @param inEligibleInvoiceList
     */
    private void prepareAwardsFile(List<InvoiceDiscountDetails> eligibleInvoiceList,
			List<InvoiceDiscountDetails> inEligibleInvoiceList)  throws IOException {
    	
    	XSSFWorkbook workbook = new XSSFWorkbook(); 
    	// creating a blank sheet
        XSSFSheet awardssheet = workbook.createSheet("awardssheet");
        int rownum = 0;
        
        for (InvoiceDiscountDetails invoiceDiscountDetails : eligibleInvoiceList)
           {
           int updatedRownum = createList(invoiceDiscountDetails, awardssheet, rownum);
           rownum = updatedRownum;
               
       } 
        
        for (InvoiceDiscountDetails invoiceDiscountDetails : inEligibleInvoiceList)
        {
        	int updatedRownum = createList(invoiceDiscountDetails, awardssheet, rownum);
        
        rownum = updatedRownum;
            
    } 
        for(int i = 0 ; i < 10 ; i ++) {
        	awardssheet.autoSizeColumn(i);
        }
        FileOutputStream fileOut = new FileOutputStream("F:\\Freelancing\\Documents\\awardssheet.xls");
        workbook.write(fileOut);
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        bos.close();
        byte[] awardsFileArray = bos.toByteArray();
        buyerService.updateAwardsFile(awardsFileArray);
        fileOut.close();
    }
    
    
    private static int createList(InvoiceDiscountDetails invoiceDiscountDetails, XSSFSheet awardssheet, int rownum) {
		float totalInvoicesAmtPerSupplier = 0.0f;
		float eligibleInvoicesAmtPerSupplier = 0.0f;
		float combinedDiscount = 0.0f;
		float amountTobePaidForClearedInvoice = 0.0f;
		Row commonRows = awardssheet.createRow(rownum++);
			Cell cell = commonRows.createCell(1);
			Cell cell2 = commonRows.createCell(2);
			Cell cell3 = commonRows.createCell(3);
			Cell cell4 = commonRows.createCell(4);
			Cell cell5 = commonRows.createCell(5);
			Cell cell6 = commonRows.createCell(6);
			Cell cell7 = commonRows.createCell(7);
			Cell cell8 = commonRows.createCell(8);
			Cell cell9 = commonRows.createCell(9);
				cell.setCellValue("INVOICE NUMBER");
				cell2.setCellValue("INVOICE AMOUNT");
				cell3.setCellValue("INVOICE DUE(In days)");
				cell4.setCellValue("DISCOUNT AMOUNT");
				cell5.setCellValue("AMOUNT TO BE PAID");
				cell6.setCellValue("MIN DISCOUNT RATE");
				cell7.setCellValue("MAX DISCOUNT RATE");
				cell8.setCellValue("Icash comission");
				cell9.setCellValue("APR");
			
		for (int i = 0; i < invoiceDiscountDetails.getFilteredInvoicesList().size(); i++) {
			Row row = awardssheet.createRow(rownum++);
			Cell datacell = row.createCell(1);
			Cell dataCell2 = row.createCell(2);
			Cell dataCell3 = row.createCell(3);
			Cell dataCell4 = row.createCell(4);
			Cell dataCell5 = row.createCell(5);
			Cell dataCell6 = row.createCell(6);
			Cell dataCell7 = row.createCell(7);
			Cell dataCell8 = row.createCell(8);
			Cell dataCell9 = row.createCell(9);
			
			InvoiceDetails filteredInvoice = invoiceDiscountDetails.getFilteredInvoicesList().get(i);
			totalInvoicesAmtPerSupplier = totalInvoicesAmtPerSupplier + filteredInvoice.getInvoiceAmount();
			datacell.setCellValue(filteredInvoice.getSupplierInvoceId());
			dataCell2.setCellValue(String.valueOf(filteredInvoice.getInvoiceAmount()));
			dataCell3.setCellValue(filteredInvoice.getInvoiceDueDays());
			cell.setCellValue(filteredInvoice.getSellerKey());
			if (filteredInvoice.isEligibleForFinalDiscounting()) {
				eligibleInvoicesAmtPerSupplier = eligibleInvoicesAmtPerSupplier
						+ filteredInvoice.getInvoiceAmount();
				dataCell4.setCellValue(String.valueOf(filteredInvoice.getDiscountedAmount()));
				dataCell5.setCellValue(String.valueOf(filteredInvoice.getAmoutToBePaidAfterDiscounting()));
				amountTobePaidForClearedInvoice = amountTobePaidForClearedInvoice
						+ filteredInvoice.getAmoutToBePaidAfterDiscounting();
				combinedDiscount = combinedDiscount + filteredInvoice.getDiscountedAmount();
				dataCell6.setCellValue(String.valueOf(invoiceDiscountDetails.getMinimunDiscount()));
				dataCell7.setCellValue(String.valueOf(invoiceDiscountDetails.getMaximumDiscount()));
				dataCell8.setCellValue(String.valueOf(filteredInvoice.getIcashComission()));
				dataCell9.setCellValue(String.valueOf(filteredInvoice.getApr()));
			} else {
				dataCell4.setCellValue("N.A");
				dataCell5.setCellValue("N.A");
			}

		}

		Row row = awardssheet.createRow(rownum++);
		Cell totalLabelCell = row.createCell(1);
		totalLabelCell.setCellValue("TOTAL");
		Cell totalValue = row.createCell(2);
		totalValue.setCellValue(totalInvoicesAmtPerSupplier);
		
		
		Row lastRow = awardssheet.createRow(rownum++);
		Cell eligibleTotalLabelCell = lastRow.createCell(1);
		eligibleTotalLabelCell.setCellValue("Eligible Invoices Total");
		Cell eligibleTotalValue = lastRow.createCell(2);
		eligibleTotalValue.setCellValue(eligibleInvoicesAmtPerSupplier);
		
		Cell eligibleDiscTotalValue = lastRow.createCell(4);
		
		eligibleDiscTotalValue.setCellValue(combinedDiscount);
		Cell amountTotalValueAfterDisc = lastRow.createCell(5);
		amountTotalValueAfterDisc.setCellValue(amountTobePaidForClearedInvoice);
		awardssheet.createRow(rownum++);
		awardssheet.createRow(rownum++);
		return rownum;
	}
    
    

	@Override
	public List<InvoiceDetails> getAllInvoicesUploadedToday() throws ClassNotFoundException, SQLException {
		return invoiceDetailsService.getallInvoicesUploadedToday();
	}

	@Override
	public List<SellerInputDetails> getSellerTodayInputDetails() throws ClassNotFoundException, SQLException {
		return sellerService.getSellerInputDetails();
	}
	
	@Override
	public BuyerInputDetails getBuyerTodayInputDetails() throws ClassNotFoundException, SQLException {
		return buyerService.getBuyerInputDetails();
	}

	/**
	 * @param buyerService the buyerService to set
	 */
	@Autowired
	public void setBuyerService(BuyerService buyerService) {
		this.buyerService = buyerService;
	}

	/**
	 * @param sellerService the sellerService to set
	 */
	@Autowired
	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	/**
	 * @param invoiceDetailsService the invoiceDetailsService to set
	 */
	@Autowired
	public void setInvoiceDetailsService(InvoiceDetailsService invoiceDetailsService) {
		this.invoiceDetailsService = invoiceDetailsService;
	}

	/**
	 * @param userCredentialService the userCredentialService to set
	 */
	@Autowired
	public void setUserCredentialService(UserCredentialService userCredentialService) {
		this.userCredentialService = userCredentialService;
	}

	/**
	 * @param userProfileService the userProfileService to set
	 */
	@Autowired
	public void setUserProfileService(UserProfileService userProfileService) {
		this.userProfileService = userProfileService;
	}

	/**
	 * @param userRoleService the userRoleService to set
	 */
	@Autowired
	public void setUserRoleService(UserRoleService userRoleService) {
		this.userRoleService = userRoleService;
	}

	/**
	 * @return the buyerService
	 */
	public BuyerService getBuyerService() {
		return buyerService;
	}

	/**
	 * @return the sellerService
	 */
	public SellerService getSellerService() {
		return sellerService;
	}

	/**
	 * @return the invoiceDetailsService
	 */
	public InvoiceDetailsService getInvoiceDetailsService() {
		return invoiceDetailsService;
	}

	/**
	 * @return the userCredentialService
	 */
	public UserCredentialService getUserCredentialService() {
		return userCredentialService;
	}

	/**
	 * @return the userProfileService
	 */
	public UserProfileService getUserProfileService() {
		return userProfileService;
	}

	/**
	 * @return the userRoleService
	 */
	public UserRoleService getUserRoleService() {
		return userRoleService;
	}

}
