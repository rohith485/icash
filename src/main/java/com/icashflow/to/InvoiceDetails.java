package com.icashflow.to;

import java.util.Date;

/**
 * @author Rohith Pingili
 *
 */
public class InvoiceDetails {
	
	private int supplierInvoceId;
	private Date originalDueDate;
	private Date invoiceDate;
	private double invoiceAmount;
	private Date entryDate;
	private String buyerKey;
	private String sellerKey;
	private int invoiceDueDays;
	
	/**
	 * @return the supplierInvoceId
	 */
	public int getSupplierInvoceId() {
		return supplierInvoceId;
	}
	/**
	 * @param supplierInvoceId the supplierInvoceId to set
	 */
	public void setSupplierInvoceId(int supplierInvoceId) {
		this.supplierInvoceId = supplierInvoceId;
	}
	/**
	 * @return the originalDueDate
	 */
	public Date getOriginalDueDate() {
		return originalDueDate;
	}
	/**
	 * @param originalDueDate the originalDueDate to set
	 */
	public void setOriginalDueDate(Date originalDueDate) {
		this.originalDueDate = originalDueDate;
	}
	/**
	 * @return the invoiceDate
	 */
	public Date getInvoiceDate() {
		return invoiceDate;
	}
	/**
	 * @param invoiceDate the invoiceDate to set
	 */
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	/**
	 * @return the invoiceAmount
	 */
	public double getInvoiceAmount() {
		return invoiceAmount;
	}
	/**
	 * @param invoiceAmount the invoiceAmount to set
	 */
	public void setInvoiceAmount(double invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}
	/**
	 * @return the entryDate
	 */
	public Date getEntryDate() {
		return entryDate;
	}
	/**
	 * @param entryDate the entryDate to set
	 */
	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
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
	/**
	 * @return the invoiceDueDays
	 */
	public int getInvoiceDueDays() {
		return invoiceDueDays;
	}
	/**
	 * @param invoiceDueDays the invoiceDueDays to set
	 */
	public void setInvoiceDueDays(int invoiceDueDays) {
		this.invoiceDueDays = invoiceDueDays;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InvoiceDetails [supplierInvoceId=" + supplierInvoceId + ", originalDueDate=" + originalDueDate
				+ ", invoiceDate=" + invoiceDate + ", invoiceAmount=" + invoiceAmount + ", entryDate=" + entryDate
				+ ", buyerKey=" + buyerKey + ", sellerKey=" + sellerKey + ", invoiceDueDays=" + invoiceDueDays + "]";
	}

}
