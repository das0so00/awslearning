package com.cloudestory.aws.fin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.cloudestory.aws.fin.model.Invoice;
import com.cloudestory.aws.fin.model.InvoiceItem;
import com.cloudestory.aws.util.AWSUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProcessInvoice {

	private static final String INVOICE_DATA_FOLDER = "dropbox-athena/data";
	private static final String INVOICE_DATE_FORMAT = "MMM dd yyyy";
	private static final String CSV_DATE_FORMAT = "yyyy-MM-dd";

	public void processInvoiceInformation(String objectKey) throws IOException, ParseException {
		System.out.println("Processing S3 Object ->  " + objectKey);
		String invoiceId = null;
		String invoiceDate = null;
		
		Invoice invoice = new Invoice();
		invoice.setInvoiceItemList(new ArrayList<InvoiceItem>());

		S3Object s3Object = AWSUtil.getS3().getObject(AWSUtil.BUCKET_NAME, objectKey);
		InputStream is = s3Object.getObjectContent();

		StringBuilder originalInvoice = new StringBuilder();
		StringBuilder invoiceHeader = new StringBuilder();
		StringBuilder invoiceItems = new StringBuilder();

		boolean invoiceHeaderFlag = false;
		boolean invoiceItemFlag = false;

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = br.readLine();
		while (line != null) {
			if (line.trim().startsWith("Customer-ID")) {
				invoiceHeaderFlag = true;
			}

			if (invoiceHeaderFlag) {
				String[] tokens = line.replaceAll(":", "~").split("~");
				if (tokens.length == 2) {
					if (tokens[0].trim().startsWith("Inv-ID")) {
						invoiceId = tokens[1].trim();
						invoice.setInvoiceId(invoiceId);
					} else if (tokens[0].trim().startsWith("Customer-ID")) {
						invoice.setCustomerId(tokens[1].trim());
					} else if (tokens[0].trim().startsWith("Dated")) {
						SimpleDateFormat invoiceDtfmt = new SimpleDateFormat(INVOICE_DATE_FORMAT);
						Date invoiceDateAsDate = invoiceDtfmt.parse(tokens[1].trim());

						SimpleDateFormat csvDtfmt = new SimpleDateFormat(CSV_DATE_FORMAT);
						tokens[1] = csvDtfmt.format(invoiceDateAsDate);

						invoiceDate = tokens[1];
						invoice.setInvoiceDate(invoiceDate);
					} else if (tokens[0].trim().startsWith("From")) {
						invoice.setFrom(tokens[1].trim());
					} else if (tokens[0].trim().startsWith("To")) {
						invoice.setTo(tokens[1].trim());
					} else if (tokens[0].trim().startsWith("Amount")) {
						invoice.setAmount(tokens[1].trim());
					} else if (tokens[0].trim().startsWith("SGST")) {
						invoice.setSgst(tokens[1].trim());
					} else if (tokens[0].trim().startsWith("Total")) {
						invoice.setTotal(tokens[1].trim());
					} else if (tokens[0].trim().startsWith("InWords")) {
						invoice.setTotalInWords(tokens[1].trim());
					} 
					invoiceHeader.append(tokens[1].trim()).append(",");
				}
			}

			if (line.trim().startsWith("Items")) {
				invoiceHeader.replace(invoiceHeader.length() - 1, invoiceHeader.length(), "\n");

				invoiceHeaderFlag = false;
				invoiceItemFlag = true;

				line = br.readLine();
				continue;
			}

			if (invoiceItemFlag) {
				String[] tokens = line.trim().split(",");
				if (tokens[0].trim().indexOf(")") != -1) {
					tokens[0] = tokens[0].trim().substring(tokens[0].trim().indexOf(")") + 1,
							tokens[0].trim().length());
				}
				if (tokens.length == 3) {
					invoiceItems.append(invoiceId).append(",").append(tokens[0].trim()).append(",")
							.append(tokens[1].trim()).append(",").append(tokens[2].trim()).append("\n");
					
					InvoiceItem invoiceItem = new InvoiceItem();
					invoiceItem.setDescription(tokens[0].trim());
					invoiceItem.setMax(tokens[1].trim());
					invoiceItem.setMin(tokens[2].trim());
					
					invoice.getInvoiceItemList().add(invoiceItem);
				}
			}
			
			originalInvoice.append(line).append("\n");
			
			line = br.readLine();
		}
		
		//if (getFromDynamoDB(invoiceId, invoiceDate) == null) {
			// Save to S3
		System.out.println("Putting Invoice Header ->  " + invoiceHeader);
		putInvoiceDataFile("invoice/invoice-data_" + System.currentTimeMillis(),
				invoiceHeader.toString().getBytes());

		System.out.println("Putting Invoice Itemes ->  " + invoiceItems);
		putInvoiceDataFile("items/invoice-item-data_" + System.currentTimeMillis(),
				invoiceItems.toString().getBytes());

		// Save to Dynamo DB
		System.out.println("Putting Invoice to Dynamo DB ");
		saveToDynamoDB(invoiceId, invoiceDate, invoice);
		//}
	}

	private void putInvoiceDataFile(String dataFileName, byte[] data) throws IOException {
		String tempDataFileName = "." + dataFileName.substring(dataFileName.lastIndexOf("/") + 1, dataFileName.length())
				+ "_" + System.currentTimeMillis();

		if (data.length == 0) {
			return;
		}

		System.out.println("Creating temporary file ->  " + tempDataFileName);
		File tempDataFile = File.createTempFile(tempDataFileName, null);
		if (tempDataFile.exists()) {
			tempDataFile.deleteOnExit();
		}

		FileOutputStream fos = new FileOutputStream(tempDataFile);
		fos.write(data);
		fos.close();

		AmazonS3 amazonS3 = AWSUtil.getS3();
		System.out.println("Putting the file to S3, " + dataFileName + " to S3");
		amazonS3.putObject(new PutObjectRequest(AWSUtil.BUCKET_NAME, INVOICE_DATA_FOLDER + "/" + dataFileName + ".csv",
				tempDataFile));

	}


	private void saveToDynamoDB(String invoiceId, String invoiceDate, Invoice invoice) throws IOException {
		PutItemOutcome outcome = null;

		AmazonDynamoDB amazonDynamoDB = AWSUtil.getDynamoDB();

		DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
		Table invoiceTable = dynamoDB.getTable("CLIENT_INVOICE");

		System.out.println("Adding a new item...");
        String invoiceJSON = new ObjectMapper().writeValueAsString(invoice); 
        
		outcome = invoiceTable.putItem(new Item().withPrimaryKey("INVOICE_ID", invoiceId, "DATE", invoiceDate).withJSON("INVOICE_INF", invoiceJSON));

		System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
	}

}
