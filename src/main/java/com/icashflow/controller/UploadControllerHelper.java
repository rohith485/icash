package com.icashflow.controller;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.icashflow.command.BuyerIcashCommand;
import com.icashflow.command.SellerIcashCommand;
import com.icashflow.service.IcashService;
import com.icashflow.to.BuyerInputDetails;
import com.icashflow.to.SellerInputDetails;

@Component
public class UploadControllerHelper {

	private IcashService icashService;
	
	/**
	 * @return the icashService
	 */
	public IcashService getIcashService() {
		return icashService;
	}

	/**
	 * @param icashService the icashService to set
	 */
	@Autowired
	@Qualifier("iCashServiceImpl")
	public void setIcashService(IcashService icashService) {
		this.icashService = icashService;
	}

	public void updateBuyerData(BuyerIcashCommand buyerIcashCommand) throws Exception {
		BuyerInputDetails buyerInputDetails = prepareBuyersDetails(buyerIcashCommand);
		icashService.processBuyerInput(buyerInputDetails);
	}

	private BuyerInputDetails prepareBuyersDetails(BuyerIcashCommand buyerIcashCommand) {
		BuyerInputDetails buyerInputDetails = new BuyerInputDetails();
		buyerInputDetails.setDroi(buyerIcashCommand.getDroi());
		buyerInputDetails.setMroi(buyerIcashCommand.getMroi());
		buyerInputDetails.setFile(buyerIcashCommand.getFile());
		buyerInputDetails.setMaxReserveAmount(buyerIcashCommand.getMaxReserveAmount());
		buyerInputDetails.setMinReserveAmount(buyerIcashCommand.getMinReserveAmount());
		return buyerInputDetails;
	}

	public void updateSellerData(SellerIcashCommand sellerIcashCommand) {
		SellerInputDetails sellerInputDetails= prepareSellerDetails(sellerIcashCommand);
		icashService.insertSellerInputs(sellerInputDetails);
		
	}
	
	public void generateRewardsFile() throws ClassNotFoundException, SQLException, IOException {
		icashService.generateAwardsFile();
	}

	private SellerInputDetails prepareSellerDetails(SellerIcashCommand sellerIcashCommand) {
		SellerInputDetails sellerInputDetails = new SellerInputDetails();
		sellerInputDetails.setDesiredDateOfPayment(sellerIcashCommand.getDesiredDateOfPayment());
		sellerInputDetails.setMaxDiscount(sellerIcashCommand.getMaxDiscount());
		sellerInputDetails.setMinDiscount(sellerIcashCommand.getMinDiscount());
		sellerInputDetails.setSellerId(sellerIcashCommand.getSellerId());
		return sellerInputDetails;
	}

}
