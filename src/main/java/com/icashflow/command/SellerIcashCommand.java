package com.icashflow.command;

public class SellerIcashCommand {
	
	private double minDiscount;
	private double maxDiscount;
	private String desiredDateOfPayment;
	private String sellerId;
	
	public double getMinDiscount() {
		return minDiscount;
	}
	public void setMinDiscount(double minDiscount) {
		this.minDiscount = minDiscount;
	}
	public double getMaxDiscount() {
		return maxDiscount;
	}
	public void setMaxDiscount(double maxDiscount) {
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
