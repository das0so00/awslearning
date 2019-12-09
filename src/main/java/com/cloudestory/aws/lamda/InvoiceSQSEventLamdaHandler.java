package com.cloudestory.aws.lamda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.cloudestory.aws.fin.ProcessInvoice;
import com.cloudestory.aws.model.Record;
import com.cloudestory.aws.model.S3EventMessages;
import com.cloudestory.aws.model.SNSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InvoiceSQSEventLamdaHandler implements RequestHandler<SQSEvent, String> {

	private static LambdaLogger contextLogger = null;

	@Override
	public String handleRequest(SQSEvent sqsEvent, Context context) {
		contextLogger = context.getLogger();

		ProcessInvoice processInvoice = new ProcessInvoice();

		if (sqsEvent != null) {
			contextLogger.log("SQS Event ->  " + sqsEvent);

			ObjectMapper om = new ObjectMapper();
			for (SQSMessage sqsMessage : sqsEvent.getRecords()) {
				try {
					SNSEvent snsEvent = om.readValue(sqsMessage.getBody(), SNSEvent.class);
					S3EventMessages s3EventMessages = om.readValue(snsEvent.getMessage(), S3EventMessages.class);
					for (Record aRecord : s3EventMessages.getRecords()) {
						processInvoice.processInvoiceInformation(aRecord.getS3().getObject().getKey());
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return "";
	}

}
