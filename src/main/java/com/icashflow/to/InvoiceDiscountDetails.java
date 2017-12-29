package com.icashflow.to;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rohith Pingili
 *
 */
public class InvoiceDiscountDetails {
	
	private double minimunDiscount;
	private double maximumDiscount;
	private List<InvoiceDetails> filteredInvoicesList;
	
	
	private String buyerKey;
	private String sellerKey;
	
	/**
	 * @return the minimunDiscount
	 */
	public double getMinimunDiscount() {
		return minimunDiscount;
	}
	/**
	 * @param minimunDiscount the minimunDiscount to set
	 */
	public void setMinimunDiscount(double minimunDiscount) {
		this.minimunDiscount = minimunDiscount;
	}
	/**
	 * @return the maximumDiscount
	 */
	public double getMaximumDiscount() {
		return maximumDiscount;
	}
	/**
	 * @param maximumDiscount the maximumDiscount to set
	 */
	public void setMaximumDiscount(double maximumDiscount) {
		this.maximumDiscount = maximumDiscount;
	}
	/**
	 * @param filteredInvoicesList the filteredInvoicesList to set
	 * @return 
	 */
	public List<InvoiceDetails> getFilteredInvoicesList() {
		if(this.filteredInvoicesList == null) {
			filteredInvoicesList = new ArrayList<>();
		}
		return this.filteredInvoicesList;
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
	/**
	 * @return the sellerKey
	 */
	public String getSellerKey() {
		return sellerKey;
	}
	/**
	 * @param sellerKey the sellerKey to set
	 */
	public void setSellerKey(String sellerKey) {
		this.sellerKey = sellerKey;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InvoiceDiscountDetails [minimunDiscount=" + minimunDiscount + ", maximumDiscount=" + maximumDiscount
				+ ", filteredInvoicesList=" + filteredInvoicesList + ", buyerKey=" + buyerKey + ", sellerKey="
				+ sellerKey + "]";
	}

}
