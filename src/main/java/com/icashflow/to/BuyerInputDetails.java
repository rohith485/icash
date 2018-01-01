package com.icashflow.to;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Rohith Pingili
 *
 */
public class BuyerInputDetails {
	private MultipartFile file;
	private float mroi;
	private float droi;
	private float minReserveAmount;
	private float maxReserveAmount;
	private String buyerKey;
	/**
	 * @return the file
	 */
	public MultipartFile getFile() {
		return file;
	}
	/**
	 * @param file the file to set
	 */
	public void setFile(MultipartFile file) {
		this.file = file;
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
	 * @return the buyerKey
	 */
	public String getBuyerKey() {
		return buyerKey;
	}
	/**
	 * @param buyerKey the buyerKey to set
	 */
	public void setBuyerKey(String buyerKey) {
		this.buyerKey = buyerKey;
	}
	
}
