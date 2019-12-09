package com.cloudestory.aws.standalone;

import java.io.IOException;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.cloudestory.aws.fin.ProcessInvoice;
import com.cloudestory.aws.model.Record;
import com.cloudestory.aws.model.S3EventMessages;
import com.cloudestory.aws.model.SNSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InvoiceSNSEventStandaloneHandler {
	
	private static Logger logger = Logger.getLogger(InvoiceSNSEventStandaloneHandler.class);
	
	//private static final String TOPIC_ARN = "arn:aws:sns:ap-south-1:911640699189:invoice-generation";

	public static void main(String[] args) throws Exception {
		
		//AmazonSNS amazonSNS = AWSUtil.getSNS();

		// Get an HTTP Port
		int port = args.length == 1 ? Integer.parseInt(args[0]) : 8989;

		// Create and start HTTP server
		Server server = new Server(port);
		server.setHandler(new AmazonSNSHandler());
		server.start();
		
        // Subscribe to topic
		/*String serviceURL = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port;
		logger.info("Service URL -> " + serviceURL);
		
        SubscribeRequest subscribeReq = new SubscribeRequest()
            .withTopicArn(TOPIC_ARN)
            .withProtocol("http")
            .withEndpoint("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port);
        amazonSNS.subscribe(subscribeReq);
        
        logger.info("Topic subscribed -> " + TOPIC_ARN);*/
	}

	// HTTP handler
	private static class AmazonSNSHandler extends AbstractHandler {

		// Handle HTTP request
		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
			
			logger.info("Request received");
			
			// Scan request into a string
			Scanner scanner = new Scanner(request.getInputStream());
			StringBuilder sb = new StringBuilder();
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
			}
			scanner.close();

			logger.info("Request payload: \n" + sb);
			
			ProcessInvoice processInvoice = new ProcessInvoice();
			ObjectMapper om = new ObjectMapper();
			try {
				SNSEvent snsEvent = om.readValue(sb.toString(), SNSEvent.class);
				S3EventMessages s3EventMessages = om.readValue(snsEvent.getMessage(), S3EventMessages.class);
				for (Record aRecord : s3EventMessages.getRecords()) {
					processInvoice.processInvoiceInformation(aRecord.getS3().getObject().getKey());
				}
				
				logger.info("Request processed successfully.");
			} catch (Exception e) {
				logger.error("Request failed due to, " + e.getMessage());
				
				throw new RuntimeException(e);
			}

			// Set HTTP response
			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);
			((Request) request).setHandled(true);

		}

	}

}
