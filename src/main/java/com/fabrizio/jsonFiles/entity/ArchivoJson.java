package com.fabrizio.jsonFiles.entity;

import lombok.Data;

@Data
public class ArchivoJson {
	
	public String number;
	public String status;
	public String pago;
	public String createdDate;
	public String dueDate;
	public String total;
	public String taxes;
	public String discount;
	public String amountPaid;
	public String amountDue;
	public String organizationName;
	public String clientName;
	public String organizationAddress;
	public String clientAddress;

}
