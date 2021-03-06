package com.icashflow.to;

import org.springframework.core.io.Resource;

/**
 * @author Rohith Pingili
 *
 */
public class AwardsFileTO {
	private double minDiscount;
	private double maxDiscount;
	private String desiredDateOfPayment;
	private String sellerId;
	private double mroi;
	private double droi;
	private double minReserveAmount;
	private double maxReserveAmount;
	private Resource resource;
	
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
	 * @return the mroi
	 */
	public double getMroi() {
		return mroi;
	}
	/**
	 * @param mroi the mroi to set
	 */
	public void setMroi(double mroi) {
		this.mroi = mroi;
	}
	/**
	 * @return the droi
	 */
	public double getDroi() {
		return droi;
	}
	/**
	 * @param droi the droi to set
	 */
	public void setDroi(double droi) {
		this.droi = droi;
	}
	/**
	 * @return the minReserveAmount
	 */
	public double getMinReserveAmount() {
		return minReserveAmount;
	}
	/**
	 * @param minReserveAmount the minReserveAmount to set
	 */
	public void setMinReserveAmount(double minReserveAmount) {
		this.minReserveAmount = minReserveAmount;
	}
	/**
	 * @return the maxReserveAmount
	 */
	public double getMaxReserveAmount() {
		return maxReserveAmount;
	}
	/**
	 * @param maxReserveAmount the maxReserveAmount to set
	 */
	public void setMaxReserveAmount(double maxReserveAmount) {
		this.maxReserveAmount = maxReserveAmount;
	}
	/**
	 * @return the resource
	 */
	public Resource getResource() {
		return resource;
	}
	/**
	 * @param resource the resource to set
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
	}

}
