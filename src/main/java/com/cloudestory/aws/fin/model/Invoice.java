package com.cloudestory.aws.fin.model;

import java.io.Serializable;
import java.util.List;

public class Invoice implements Serializable {

	private static final long serialVersionUID = -4755784577602465719L;

	private String customerId;
	private String invoiceId;
	private String invoiceDate;
	private String from;
	private String to;
	private String amount;
	private String sgst;
	private String total;
	private String totalInWords;

	private List<InvoiceItem> invoiceItemList;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getSgst() {
		return sgst;
	}

	public void setSgst(String sgst) {
		this.sgst = sgst;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getTotalInWords() {
		return totalInWords;
	}

	public void setTotalInWords(String totalInWords) {
		this.totalInWords = totalInWords;
	}

	public List<InvoiceItem> getInvoiceItemList() {
		return invoiceItemList;
	}

	public void setInvoiceItemList(List<InvoiceItem> invoiceItemList) {
		this.invoiceItemList = invoiceItemList;
	}

}
