package com.icashflow.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.icashflow.to.BuyerInputDetails;
import com.icashflow.to.InvoiceDetails;
import com.icashflow.to.SellerInputDetails;

@Service
public interface IcashService {
	public boolean insertBuyerInputs(BuyerInputDetails buyerInputDetails) throws ClassNotFoundException, SQLException;
	public boolean insertSellerInputs(SellerInputDetails sellerInputDetails);
	public boolean insertInvoiceDetails(BuyerInputDetails buyerInputDetails) throws Exception;
	public List<InvoiceDetails> getAllInvoicesUploadedToday() throws ClassNotFoundException, SQLException;
	public List<SellerInputDetails> getSellerTodayInputDetails() throws ClassNotFoundException, SQLException;
	public BuyerInputDetails getBuyerTodayInputDetails() throws ClassNotFoundException, SQLException;
	public boolean processBuyerInput(BuyerInputDetails buyerInputDetails) throws Exception;
	public void generateAwardsFile() throws ClassNotFoundException, SQLException, IOException;
}
