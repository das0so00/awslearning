package com.cloudestory.aws.fin.model;

import java.io.Serializable;

public class InvoiceItem implements Serializable {

	private static final long serialVersionUID = -3671686717288063899L;

	private String description;
	private String max;
	private String min;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

}
