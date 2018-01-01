package com.icashflow.to;

/**
 * @author Rohith Pingili
 *
 */
public class SellerInputDetails {
	
	private String sellerId;
	private float minDiscount;
	private float maxDiscount;
	private String desiredDateOfPayment;
	
	/**
	 * @return the sellerId
	 */
	public String getSellerId() {
		return sellerId;
	}
	/**
	 * @param sellerId the sellerId to set
	 */
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SellerInputDetails [sellerId=" + sellerId + ", minDiscount=" + minDiscount + ", maxDiscount="
				+ maxDiscount + ", desiredDateOfPayment=" + desiredDateOfPayment + "]";
	}
	
}
