package com.icashflow.to;

/**
 * @author Rohith Pingili
 *
 */
public class SellerInputDetails {
	
	private String sellerId;
	private double minDiscount;
	private double maxDiscount;
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
	public double getMinDiscount() {
		return minDiscount;
	}
	/**
	 * @param minDiscount the minDiscount to set
	 */
	public void setMinDiscount(double minDiscount) {
		this.minDiscount = minDiscount;
	}
	/**
	 * @return the maxDiscount
	 */
	public double getMaxDiscount() {
		return maxDiscount;
	}
	/**
	 * @param maxDiscount the maxDiscount to set
	 */
	public void setMaxDiscount(double maxDiscount) {
		this.maxDiscount = maxDiscount;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SellerInputDetails [sellerId=" + sellerId + ", minDiscount=" + minDiscount + ", maxDiscount="
				+ maxDiscount + "]";
	}
	
}
