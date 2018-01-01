package com.icashflow.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.icashflow.dao.IcashDao;
import com.icashflow.to.BuyerInputDetails;

@Service
public class BuyerService {
	private IcashDao iCashDao;
	public boolean updateBuyerInputs(BuyerInputDetails buyerInputDetails) throws ClassNotFoundException, SQLException {
		return iCashDao.updateBuyerInputs(buyerInputDetails);
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
	
	public BuyerInputDetails getBuyerInputDetails() throws ClassNotFoundException, SQLException {
		return iCashDao.getBuyerInputDetails();
	}
	public void updateAwardsFile(byte[] awardsFileArray) {
		iCashDao.updateAwardsFile(awardsFileArray);
		
	}
}
