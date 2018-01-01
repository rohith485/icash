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
	private float invoiceAmount;
	private Date entryDate;
	private String buyerKey;
	private String sellerKey;
	private int invoiceDueDays;
	
	private float discountedAmount;
	private boolean isEligibleForFinalDiscounting;
	private float amoutToBePaidAfterDiscounting;
	
	private float icashComission;
	private float apr;
	
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
	public float getInvoiceAmount() {
		return invoiceAmount;
	}
	/**
	 * @param invoiceAmount the invoiceAmount to set
	 */
	public void setInvoiceAmount(float invoiceAmount) {
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
	/**
	 * @return the discountedAmount
	 */
	public float getDiscountedAmount() {
		return discountedAmount;
	}
	/**
	 * @param discountedAmount the discountedAmount to set
	 */
	public void setDiscountedAmount(float discountedAmount) {
		this.discountedAmount = discountedAmount;
	}
	/**
	 * @return the isEligibleForFinalDiscounting
	 */
	public boolean isEligibleForFinalDiscounting() {
		return isEligibleForFinalDiscounting;
	}
	/**
	 * @param isEligibleForFinalDiscounting the isEligibleForFinalDiscounting to set
	 */
	public void setEligibleForFinalDiscounting(boolean isEligibleForFinalDiscounting) {
		this.isEligibleForFinalDiscounting = isEligibleForFinalDiscounting;
	}
	/**
	 * @return the amoutToBePaidAfterDiscounting
	 */
	public float getAmoutToBePaidAfterDiscounting() {
		return amoutToBePaidAfterDiscounting;
	}
	/**
	 * @param amoutToBePaidAfterDiscounting the amoutToBePaidAfterDiscounting to set
	 */
	public void setAmoutToBePaidAfterDiscounting(float amoutToBePaidAfterDiscounting) {
		this.amoutToBePaidAfterDiscounting = amoutToBePaidAfterDiscounting;
	}
	
	/**
	 * @return the icashComission
	 */
	public float getIcashComission() {
		return icashComission;
	}
	/**
	 * @param icashComission the icashComission to set
	 */
	public void setIcashComission(float icashComission) {
		this.icashComission = icashComission;
	}
	
	/**
	 * @return the apr
	 */
	public float getApr() {
		return apr;
	}
	/**
	 * @param apr the apr to set
	 */
	public void setApr(float apr) {
		this.apr = apr;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InvoiceDetails [supplierInvoceId=" + supplierInvoceId + ", originalDueDate=" + originalDueDate
				+ ", invoiceDate=" + invoiceDate + ", invoiceAmount=" + invoiceAmount + ", entryDate=" + entryDate
				+ ", buyerKey=" + buyerKey + ", sellerKey=" + sellerKey + ", invoiceDueDays=" + invoiceDueDays
				+ ", discountedAmount=" + discountedAmount + ", isEligibleForFinalDiscounting="
				+ isEligibleForFinalDiscounting + ", amoutToBePaidAfterDiscounting=" + amoutToBePaidAfterDiscounting
				+ ", icashComission=" + icashComission + ", apr=" + apr + "]";
	}

}
