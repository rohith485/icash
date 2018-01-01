package com.icashflow.dao;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.icashflow.to.BuyerInputDetails;
import com.icashflow.to.InvoiceDetails;
import com.icashflow.to.SellerInputDetails;

@Repository
public interface IcashDao {

	public boolean updateBuyerInputs(BuyerInputDetails buyerInputDetails) throws SQLException, ClassNotFoundException;
	public boolean updateSellerInputs(SellerInputDetails sellerInputDetails);
	public boolean updateInvoiceDetails(BuyerInputDetails buyerInputDetails) throws Exception;
	public List<InvoiceDetails> getAllInvoicesUploadedToday() throws ClassNotFoundException, SQLException;
	public List<SellerInputDetails> getSellerTodayInputDetails() throws SQLException, ClassNotFoundException;
	public BuyerInputDetails getBuyerInputDetails() throws SQLException, ClassNotFoundException;
	public void updateAwardsFile(byte[] awardsFileArray);

}
