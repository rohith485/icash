package com.icashflow.command;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Rohith Pingili
 *
 */
public class BuyerIcashCommand {
	
	private MultipartFile file;
	
	private double mroi;
	
	private double droi;
	
	private double minReserveAmount;
	
	private double maxReserveAmount;
	

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public double getMroi() {
		return mroi;
	}

	public void setMroi(double mroi) {
		this.mroi = mroi;
	}

	public double getDroi() {
		return droi;
	}

	public void setDroi(double droi) {
		this.droi = droi;
	}

	public double getMinReserveAmount() {
		return minReserveAmount;
	}

	public void setMinReserveAmount(double minReserveAmount) {
		this.minReserveAmount = minReserveAmount;
	}

	public double getMaxReserveAmount() {
		return maxReserveAmount;
	}

	public void setMaxReserveAmount(double maxReserveAmount) {
		this.maxReserveAmount = maxReserveAmount;
	}
	
}
