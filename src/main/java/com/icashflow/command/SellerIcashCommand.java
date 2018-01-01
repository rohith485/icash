package com.icashflow.command;

public class SellerIcashCommand {
	
	private float minDiscount;
	private float maxDiscount;
	private String desiredDateOfPayment;
	private String sellerId;
	
	public float getMinDiscount() {
		return minDiscount;
	}
	public void setMinDiscount(float minDiscount) {
		this.minDiscount = minDiscount;
	}
	public float getMaxDiscount() {
		return maxDiscount;
	}
	public void setMaxDiscount(float maxDiscount) {
		this.maxDiscount = maxDiscount;
	}
	
	public String getDesiredDateOfPayment() {
		return desiredDateOfPayment;
	}
	public void setDesiredDateOfPayment(String desiredDateOfPayment) {
		this.desiredDateOfPayment = desiredDateOfPayment;
	}
	public String getSellerId() {
		return sellerId;
	}
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}
}
