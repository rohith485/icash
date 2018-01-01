package com.icashflow.to;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rohith Pingili
 *
 */
public class AwardsFileTO {
	private float minDiscount;
	private float maxDiscount;
	private String desiredDateOfPayment;
	private float mroi;
	private float droi;
	private float minReserveAmount;
	private float maxReserveAmount;
	
	private List<InvoiceDiscountDetails> invoiceDiscountDetailsList;
	
	/**
	 * @return the minDiscount
	 */
	public float getMinDiscount() {
		return minDiscount;
	}
	/**
	 * @param minDiscount the minDiscount to set
	 */
	public void setMinDiscount(float minDiscount) {
		this.minDiscount = minDiscount;
	}
	/**
	 * @return the maxDiscount
	 */
	public float getMaxDiscount() {
		return maxDiscount;
	}
	/**
	 * @param maxDiscount the maxDiscount to set
	 */
	public void setMaxDiscount(float maxDiscount) {
		this.maxDiscount = maxDiscount;
	}
	/**
	 * @return the desiredDateOfPayment
	 */
	public String getDesiredDateOfPayment() {
		return desiredDateOfPayment;
	}
	/**
	 * @param desiredDateOfPayment the desiredDateOfPayment to set
	 */
	public void setDesiredDateOfPayment(String desiredDateOfPayment) {
		this.desiredDateOfPayment = desiredDateOfPayment;
	}
	/**
	 * @return the mroi
	 */
	public float getMroi() {
		return mroi;
	}
	/**
	 * @param mroi the mroi to set
	 */
	public void setMroi(float mroi) {
		this.mroi = mroi;
	}
	/**
	 * @return the droi
	 */
	public float getDroi() {
		return droi;
	}
	/**
	 * @param droi the droi to set
	 */
	public void setDroi(float droi) {
		this.droi = droi;
	}
	/**
	 * @return the minReserveAmount
	 */
	public float getMinReserveAmount() {
		return minReserveAmount;
	}
	/**
	 * @param minReserveAmount the minReserveAmount to set
	 */
	public void setMinReserveAmount(float minReserveAmount) {
		this.minReserveAmount = minReserveAmount;
	}
	/**
	 * @return the maxReserveAmount
	 */
	public float getMaxReserveAmount() {
		return maxReserveAmount;
	}
	/**
	 * @param maxReserveAmount the maxReserveAmount to set
	 */
	public void setMaxReserveAmount(float maxReserveAmount) {
		this.maxReserveAmount = maxReserveAmount;
	}
	/**
	 * @return the invoiceDiscountDetailsList
	 */
	public List<InvoiceDiscountDetails> getInvoiceDiscountDetailsList() {
		if(null == invoiceDiscountDetailsList) {
			invoiceDiscountDetailsList = new ArrayList<>();
		}
		return invoiceDiscountDetailsList;
	}
	
	
}
