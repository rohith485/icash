package com.icashflow.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.icashflow.dao.IcashDao;
import com.icashflow.to.SellerInputDetails;

@Service
public class SellerService {

	private IcashDao iCashDao;
	
	public boolean updateSellerInputs(SellerInputDetails sellerInputDetails) {
		return iCashDao.updateSellerInputs(sellerInputDetails);
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

	public List<SellerInputDetails> getSellerInputDetails() throws ClassNotFoundException, SQLException {
		return this.iCashDao.getSellerTodayInputDetails();
	}

}
