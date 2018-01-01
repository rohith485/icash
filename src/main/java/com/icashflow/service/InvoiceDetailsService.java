package com.icashflow.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.icashflow.dao.IcashDao;
import com.icashflow.to.BuyerInputDetails;
import com.icashflow.to.InvoiceDetails;

@Service
public class InvoiceDetailsService {
	
	private IcashDao iCashDao;

	public boolean updateInvoiceDetails(BuyerInputDetails buyerInputDetails) throws Exception {
		return iCashDao.updateInvoiceDetails(buyerInputDetails);
	}

	/**
	 * @return the iCashDao
	 */
	public IcashDao getiCashDao() {
		return iCashDao;
	}

	/**
	 * @param iCashDao the iCashDao to set
	 */
	@Autowired
	@Qualifier("iCashDaoImpl")
	public void setiCashDao(IcashDao iCashDao) {
		this.iCashDao = iCashDao;
	}

	public List<InvoiceDetails> getallInvoicesUploadedToday() throws ClassNotFoundException, SQLException {
		return iCashDao.getAllInvoicesUploadedToday();
	}
	
	

}
