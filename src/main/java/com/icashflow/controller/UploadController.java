package com.icashflow.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.icashflow.batch.item.excel.AbstractExcelItemReader;
import com.icashflow.batch.item.excel.RowCallbackHandler;
import com.icashflow.batch.item.excel.Sheet;
import com.icashflow.batch.item.excel.mapping.PassThroughRowMapper;
import com.icashflow.batch.item.excel.poi.PoiItemReader;
import com.icashflow.batch.item.excel.support.rowset.RowSet;
import com.icashflow.command.BuyerIcashCommand;
import com.icashflow.command.SellerIcashCommand;
import com.icashflow.to.AwardsFileTO;
import com.icashflow.to.InvoiceDetails;
import com.icashflow.to.InvoiceDiscountDetails;

@Controller
public class UploadController {

    @GetMapping("/")
    public String index(Model model) {
    	BuyerIcashCommand buyerIcashCommand = new BuyerIcashCommand();
    	model.addAttribute("buyerIcashCommand", buyerIcashCommand);
    	
    	SellerIcashCommand sellerIcashCommand = new SellerIcashCommand();
    	model.addAttribute("sellerIcashCommand", sellerIcashCommand);
    	
        return "upload";
    }

    private String dbURL = "jdbc:mysql://127.0.0.1:3306/filedb";
    private String dbUser = "root";
    private String dbPass = "admin";
    
    
    @PostMapping("/updatebuyerdata")
	public String processBuyerInput(@ModelAttribute("buyerIcashCommand") BuyerIcashCommand buyerIcashCommand, RedirectAttributes redirectAttributes) {
		MultipartFile file = buyerIcashCommand.getFile();
		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
			return "redirect:uploadStatus";
		}
		
        try {
        	AbstractExcelItemReader itemReader = new PoiItemReader<>();
            itemReader.setRowMapper(new PassThroughRowMapper());
            Resource resource = new InputStreamResource(file.getInputStream(), "File uploaded by user");
            itemReader.setResource(resource);
            itemReader.afterPropertiesSet();
            ExecutionContext executionContext = new ExecutionContext();
            itemReader.open(executionContext);
            int numberOfSheets = itemReader.getNumberOfSheets();
            Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
            for(int i=0 ; i< numberOfSheets; i++) {
            	Sheet sheet = itemReader.getSheet(i);
            	// skipping Zeroth row as it contains column names
            	for(int j=1; j< sheet.getNumberOfRows() ; j++) {
            		String[] row = sheet.getRow(j);
            		if(row.length < 3) {
            			// TODO throw exception
            		}
            		int sellerInvoiceId =Double.valueOf((row[0].trim())).intValue();
            		Double invoiceAmount = Double.valueOf(row[1]);
            		int invoiceDueDays = Double.valueOf((row[2])).intValue();
            		String supplierName = sheet.getName().trim().replaceAll(".0", "");
            		System.out.println(sheet.getName());
            		
            		String invoiceInsertQuery = " insert into INVOICE_DETAILS (SUPPLIER_INVOICE_ID,INVOICE_AMOUNT, "
            				+ "ORIGINAL_DUE_DATE ,BUYER_KEY, SELLER_KEY, ENTRY_DATE)"
            				+ "values(? ,? ,? ,? ,? ,?)";
            		Date dt = new Date();
        			Calendar c = Calendar.getInstance(); 
        			c.setTime(dt); 
        			c.add(Calendar.DATE, invoiceDueDays);
        			dt = c.getTime();
        			
        			PreparedStatement preparedStmt = conn.prepareStatement(invoiceInsertQuery);
        			preparedStmt.setInt(1, sellerInvoiceId);
        			preparedStmt.setDouble(2, invoiceAmount);
        			preparedStmt.setDate(3, new java.sql.Date(dt.getTime()));
        			preparedStmt.setString(4, "BUYER1");
        			preparedStmt.setString(5, sheet.getName());
        			java.util.Date today = new java.util.Date();
        			preparedStmt.setDate(6, new java.sql.Date(today.getTime()));
        			preparedStmt.execute();
        			System.out.println("Read: " + StringUtils.arrayToCommaDelimitedString(row));
            	}
            }
			String query = " insert into UPLOADED_FILES (FILE_NAME,FILE_TYPE,FILE_DATA,"
					+ "UPLOADED_USER_ID,UPLOADED_DATE,FILE_SIZE, FILE_STATUS,"
					+ "MINIMUM_ROI,DESIRED_ROI, MINIMUM_RESERVE_AMOUNT, MAXIMUM_RESERVE_AMOUNT)"
					+ " values (?, ? ,? ,? ,? ,? ,? ,? ,? ,? ,?)";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, file.getOriginalFilename());
			preparedStmt.setString(2, FilenameUtils.getExtension(file.getOriginalFilename()));
			preparedStmt.setBytes(3, file.getBytes());
			preparedStmt.setString(4, "ROHITH");
			java.util.Date today = new java.util.Date();
			preparedStmt.setDate(5, new java.sql.Date(today.getTime()));
			preparedStmt.setLong(6, file.getSize());
			preparedStmt.setString(7, "ACTIVE");
			preparedStmt.setDouble(8, buyerIcashCommand.getMroi());
			preparedStmt.setDouble(9, buyerIcashCommand.getDroi());
			preparedStmt.setDouble(10, buyerIcashCommand.getMinReserveAmount());
			preparedStmt.setDouble(11, buyerIcashCommand.getMaxReserveAmount());
			preparedStmt.execute();
			conn.close();
			redirectAttributes.addFlashAttribute("message",
					"You successfully uploaded '" + file.getOriginalFilename() + "'");
		} catch (Exception e) {
			System.out.println("Exception occured " + e);
		}

		return "redirect:/uploadStatus";
	}
    
    @PostMapping("/updatesellerdata")
	public String processSellerInput(@ModelAttribute("sellerIcashCommand") SellerIcashCommand sellerIcashCommand, RedirectAttributes redirectAttributes) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
			String query = " insert into SELLER_INPUTS (SELLER_ID,MIN_DISCOUNT,MAX_DISCOUNT,UPLOADED_DATE,DESIRED_PAYMEMT_DATE)"
					+ " values (? ,? ,? ,? ,? )";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, sellerIcashCommand.getSellerId());
			preparedStmt.setDouble(2, sellerIcashCommand.getMinDiscount());
			preparedStmt.setDouble(3, sellerIcashCommand.getMaxDiscount());
			java.util.Date today = new java.util.Date();
			preparedStmt.setDate(4, new java.sql.Date(today.getTime()));
			Date dt = new Date();
			Calendar c = Calendar.getInstance(); 
			c.setTime(dt); 
			c.add(Calendar.DATE, 3);
			dt = c.getTime();
			preparedStmt.setDate(5, new java.sql.Date(dt.getTime()));
			preparedStmt.execute();
			conn.close();
			redirectAttributes.addFlashAttribute("message",
					"You successfully entered Discount details");
		} catch (Exception e) {
			System.out.println("Exception occured " + e);
		}

		return "redirect:/uploadStatus";
	}
    
    
    
    @GetMapping("/generateawardsfile")
	public String generateawardsfile(RedirectAttributes redirectAttributes) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
			AwardsFileTO awardsFileTO = new AwardsFileTO();
			String userNameQuery = "select ICASH_LOGIN_USER_NAME from user_credentials where ICASH_LOGIN_KEY = 'BUYER1'";
			PreparedStatement loginNamepreparedStmt = conn.prepareStatement(userNameQuery);
			ResultSet loginResultSet = loginNamepreparedStmt.executeQuery();
			String userName = null;
			while(loginResultSet.next()) {
				userName = loginResultSet.getString("ICASH_LOGIN_USER_NAME");
			}
			
			
			String invoiceDetailsQuery = "select SUPPLIER_INVOICE_ID,ORIGINAL_DUE_DATE,INVOICE_DATE,INVOICE_AMOUNT,ENTRY_DATE,BUYER_KEY,SELLER_KEY from INVOICE_DETAILS where ENTRY_DATE = '2017-12-26 00:00:00';";
			PreparedStatement invoiceDetailsPrepareStmt = conn.prepareStatement(invoiceDetailsQuery);
			java.util.Date today = new java.util.Date();
			//invoiceDetailsPrepareStmt.setDate(1, new java.sql.Date(today.getTime()));
			System.out.println(invoiceDetailsPrepareStmt.toString());
			ResultSet invoiceResultSet = invoiceDetailsPrepareStmt.executeQuery();
			List<InvoiceDetails> invoiceDetailsList = new ArrayList<>();
			while(invoiceResultSet.next()) {
				InvoiceDetails invoiceDetails = new InvoiceDetails();
				invoiceDetails.setBuyerKey(invoiceResultSet.getString("BUYER_KEY"));
				invoiceDetails.setEntryDate(invoiceResultSet.getDate("ENTRY_DATE"));
				invoiceDetails.setInvoiceAmount(invoiceResultSet.getDouble("INVOICE_AMOUNT"));
				invoiceDetails.setInvoiceDate(invoiceResultSet.getDate("INVOICE_DATE"));
				invoiceDetails.setOriginalDueDate(invoiceResultSet.getDate("ORIGINAL_DUE_DATE"));
				invoiceDetails.setSellerKey(invoiceResultSet.getString("SELLER_KEY"));
				invoiceDetails.setSupplierInvoceId(invoiceResultSet.getInt("SUPPLIER_INVOICE_ID"));
				long difference = invoiceDetails.getOriginalDueDate().getTime() - today.getTime();
				float daysBetween = (difference / (1000 * 60 * 60 * 24));
				invoiceDetails.setInvoiceDueDays(Float.valueOf(daysBetween).intValue());
				invoiceDetailsList.add(invoiceDetails);
			}
			
			Map<String, List<InvoiceDetails>> allSuppliersInvoiceMap = invoiceDetailsList.stream().collect(Collectors.groupingBy(InvoiceDetails::getSellerKey));
			
			
			
			for(Entry<String, List<InvoiceDetails>> supplierInvoice: allSuppliersInvoiceMap.entrySet()) {
				
				supplierInvoice.getKey();
				
				supplierInvoice.getValue();
				
			}
			
			
			
			System.out.println(allSuppliersInvoiceMap);
			
			String buyerInputQuery = "select * from uploaded_files where FILE_STATUS = 'ACTIVE' "
					+ "AND UPLOADED_USER_ID = 'BUYER1' AND UPLOADED_DATE = '2017-12-26 00:00:00'";
			PreparedStatement buyerPreparedStmt = conn.prepareStatement(buyerInputQuery);
			//buyerPreparedStmt.setDate(1, new java.sql.Date(today.getTime()));
			System.out.println(buyerPreparedStmt.toString());
			
			ResultSet buyerResultSet = buyerPreparedStmt.executeQuery();
			double mroi = 0.0d;;
			double droi = 0.0d;
			double miam = 0.0d;
			double maam = 0.0d;
			
			while(buyerResultSet.next()) {
				mroi = buyerResultSet.getDouble("MINIMUM_ROI");
				droi = buyerResultSet.getDouble("DESIRED_ROI");
				miam = buyerResultSet.getDouble("MINIMUM_RESERVE_AMOUNT");
				maam = buyerResultSet.getDouble("MAXIMUM_RESERVE_AMOUNT");
				
				awardsFileTO.setMroi(mroi);
				awardsFileTO.setDroi(droi);
				awardsFileTO.setMinReserveAmount(miam);
				awardsFileTO.setMaxReserveAmount(maam);
			}
			
			String sellerInputQuery = "select * from seller_inputs where UPLOADED_DATE = '2017-12-26 00:00:00'";
			PreparedStatement preparedStmt = conn.prepareStatement(sellerInputQuery);
			//preparedStmt.setDate(1, new java.sql.Date(today.getTime()));
			System.out.println(preparedStmt.toString());
			ResultSet resultSet = preparedStmt.executeQuery();
			
			List<InvoiceDiscountDetails> eligibleInvoiceList = new ArrayList<>();
			List<InvoiceDiscountDetails> inEligibleInvoiceList = new ArrayList<>();
			while(resultSet.next()) {
				double maxSellerDisc = resultSet.getDouble("MAX_DISCOUNT");
				InvoiceDiscountDetails invoiceDiscountDetails = new InvoiceDiscountDetails();
				invoiceDiscountDetails.setMinimunDiscount(resultSet.getDouble("MIN_DISCOUNT"));
				invoiceDiscountDetails.setMaximumDiscount(resultSet.getDouble("MAX_DISCOUNT"));
				String sellerId = resultSet.getString("SELLER_ID");
				
				List<InvoiceDetails> invoicesListOfSelectedSeller = allSuppliersInvoiceMap.get(sellerId);

				invoicesListOfSelectedSeller.sort(new Comparator<InvoiceDetails>() {
					@Override
					public int compare(InvoiceDetails o1, InvoiceDetails o2) {
						return o2.getInvoiceDueDays() - o1.getInvoiceDueDays();
					}
				});
				
				invoiceDiscountDetails.getFilteredInvoicesList().addAll(invoicesListOfSelectedSeller);
				
				if(maxSellerDisc < mroi) {
					inEligibleInvoiceList.add(invoiceDiscountDetails);
				}else {
					eligibleInvoiceList.add(invoiceDiscountDetails);
				}
				
			}
			
			System.out.println(eligibleInvoiceList);
			
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
					float days = (float)(invoiceDetails.getInvoiceDueDays()+5) / (float)365;
					float discountAmount = (float)invoiceDiscDetails.getMinimunDiscount() * 
							days * (float)invoiceDetails.getInvoiceAmount();
					float amoutToBePaidAfterDiscounting = (float)invoiceDetails.getInvoiceAmount() - discountAmount;
					float tempTotalAmount = totalAmountToBePaid + amoutToBePaidAfterDiscounting;
					
					if(tempTotalAmount < (float)awardsFileTO.getMaxReserveAmount()) {
						totalAmountToBePaid = tempTotalAmount;
						invoiceDetails.setEligibleForFinalDiscounting(true);
						invoiceDetails.setDiscountedAmount(discountAmount);
						invoiceDetails.setAmoutToBePaidAfterDiscounting(amoutToBePaidAfterDiscounting);
						invoiceDetails.setIcashComission(discountAmount * 0.15f);
						invoiceDetails.setApr((discountAmount/(float)invoiceDetails.getInvoiceAmount()) * (365/invoiceDetails.getInvoiceDueDays()) );
					}else {
						terminateLoop = true;
					}
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
			System.out.println("---------------------------------------------------------------");
			System.out.println(totalAmountToBePaid);
			System.out.println(BigDecimal.valueOf(totalAmountToBePaid).toPlainString());
			System.out.println(BigDecimal.valueOf(totalAmountToBePaid).toBigInteger());
			System.out.println(BigDecimal.valueOf(totalAmountToBePaid).doubleValue());
			System.out.println(BigDecimal.valueOf(totalAmountToBePaid).longValue());
			System.out.println("---------------------------------------------------------------");
			
			System.out.println(eligibleInvoiceList);
			
			System.out.println("totalAmountToBePaid - " + totalAmountToBePaid);

			System.out.println("awardsFileTO.getMaxReserveAmount() - " + awardsFileTO.getMaxReserveAmount());
			
			
			prepareAwardsFile(eligibleInvoiceList, inEligibleInvoiceList);
			
			
			conn.close();
			redirectAttributes.addFlashAttribute("message",
					"You File is ready @ F:\\Freelancing\\Documents - please verify");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message",
					"Failed to prepare file");
			e.printStackTrace();
			System.out.println("Exception occured " + e);
		}

		return "redirect:/uploadStatus";
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
			totalInvoicesAmtPerSupplier = totalInvoicesAmtPerSupplier + (float) filteredInvoice.getInvoiceAmount();
			datacell.setCellValue(filteredInvoice.getSupplierInvoceId());
			dataCell2.setCellValue((float) filteredInvoice.getInvoiceAmount());
			dataCell3.setCellValue(filteredInvoice.getInvoiceDueDays());
			cell.setCellValue(filteredInvoice.getSellerKey());
			if (filteredInvoice.isEligibleForFinalDiscounting()) {
				eligibleInvoicesAmtPerSupplier = eligibleInvoicesAmtPerSupplier
						+ (float) filteredInvoice.getInvoiceAmount();
				dataCell4.setCellValue((float) filteredInvoice.getDiscountedAmount());
				dataCell5.setCellValue((float) filteredInvoice.getAmoutToBePaidAfterDiscounting());
				amountTobePaidForClearedInvoice = amountTobePaidForClearedInvoice
						+ (float) filteredInvoice.getAmoutToBePaidAfterDiscounting();
				combinedDiscount = combinedDiscount + (float) filteredInvoice.getDiscountedAmount();
				dataCell6.setCellValue((float) invoiceDiscountDetails.getMinimunDiscount());
				dataCell7.setCellValue((float) invoiceDiscountDetails.getMaximumDiscount());
				dataCell8.setCellValue(filteredInvoice.getIcashComission());
				dataCell9.setCellValue(filteredInvoice.getApr());
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

	//@RequestMapping(value = "/upload", method = RequestMethod.POST)
    @PostMapping("/upload") //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }
        
        try {
        	
        	AbstractExcelItemReader itemReader = new PoiItemReader<>();
            itemReader.setLinesToSkip(1); //First line is column names
            itemReader.setRowMapper(new PassThroughRowMapper());
            itemReader.setSkippedRowsCallback(new RowCallbackHandler() {
            	
                public void handleRow(RowSet rs) {
                    System.out.println("Skipping: " + StringUtils.arrayToCommaDelimitedString(rs.getCurrentRow()));
                }
            });
            
            Class.forName("com.mysql.jdbc.Driver");  
            
            Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
            
            Statement stmt=conn.createStatement();  
            //ResultSet rs = stmt.executeQuery("select * from files_upload");  
            
         // create a sql date object so we can use it in our INSERT statement
            Calendar calendar = Calendar.getInstance();
            java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());

            // the mysql insert statement
            String query = " insert into files_upload (file_name, file_data)"
              + " values (?, ?)";
            
            query = " insert into UPLOADED_FILES (FILE_NAME,FILE_TYPE,FILE_DATA,UPLOADED_USER_ID,UPLOADED_DATE,FILE_SIZE, FILE_STATUS)"
            		+ " values (?, ? ,? ,? ,? ,? ,? )";

            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString (1, file.getOriginalFilename());
            preparedStmt.setString(2, FilenameUtils.getExtension(file.getOriginalFilename()));
            preparedStmt.setBytes(3, file.getBytes());
            preparedStmt.setString(4, "ROHITH");
            java.util.Date today = new java.util.Date();
            preparedStmt.setDate(5, new java.sql.Date(today.getTime()));
            preparedStmt.setLong(6, file.getSize());
            preparedStmt.setString (7, "ACTIVE");
            // execute the preparedstatement
            preparedStmt.execute();
            
            conn.close();
            
            Resource resource = new InputStreamResource(file.getInputStream(), "File uploaded by user");
            itemReader.setResource(resource);
            itemReader.afterPropertiesSet();
            ExecutionContext executionContext = new ExecutionContext();
            itemReader.open(executionContext);
            int numberOfSheets = itemReader.getNumberOfSheets();
            
            for(int i=0 ; i< numberOfSheets; i++) {
            	Sheet sheet = itemReader.getSheet(i);
            	System.out.println(sheet.getName());
            	for(int j=0; j< sheet.getNumberOfRows() ; j++) {
            		String[] row = sheet.getRow(j);
            		System.out.println("Read: " + StringUtils.arrayToCommaDelimitedString(row));
            	}
            }
            
            /*
            String[] row;
            do {
                row = (String[]) itemReader.read();
                System.out.println("Read: " + StringUtils.arrayToCommaDelimitedString(row));
                if (row != null) {
                    //assertEquals(6, row.length);
                }
            } while (row != null);*/
            //int readCount = (Integer) ReflectionTestUtils.getField(itemReader, "currentItemCount" );
            //assertEquals(4321, readCount);
        
        
        	// Creates a workbook object from the uploaded excelfile
     		/*HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
     		int numberOfSheets = workbook.getNumberOfSheets();
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);*/

            redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + file.getOriginalFilename() + "'");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/uploadStatus";
    }

    @PostMapping("/uploadMulti")
    public String multiFileUpload(@RequestParam("files") MultipartFile[] files,
                                  RedirectAttributes redirectAttributes) {

        StringJoiner sj = new StringJoiner(" , ");

        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                continue; //next pls
            }

            try {

                byte[] bytes = file.getBytes();
                Path path = null;//Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
                Files.write(path, bytes);

                sj.add(file.getOriginalFilename());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        String uploadedFileName = sj.toString();
        if (StringUtils.isEmpty(uploadedFileName)) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
        } else {
            redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + uploadedFileName + "'");
        }

        return "redirect:/uploadStatus";

    }
    
    public static byte[] toByteArray(Object obj) throws IOException {
        byte[] bytes = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
        return bytes;
    }


    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

    @GetMapping("/uploadMultiPage")
    public String uploadMultiPage() {
        return "uploadMulti";
    }

}