package com.cloudestory.aws.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;

public final class AWSUtil {

	public static final String DYNAMODB_ENDPOINT_URL = "http://dynamodb.ap-south-1.amazonaws.com";
	public static final String BUCKET_NAME = "cloudestory-su-shared-bucket";

	public static final String ACCESS_KEY = "AKIA5IQQFEE2QFHLD4PK";
	public static final String SECRET_KEY = "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";

	public static AWSStaticCredentialsProvider getAWSCredentialProvider() {
		return new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
	}

	public static AmazonS3 getS3() {
		AmazonS3ClientBuilder amazonS3Builder = AmazonS3Client.builder()
				.withRegion(Regions.AP_SOUTH_1)
				.withCredentials(getAWSCredentialProvider());

		return amazonS3Builder.build();
	}

	public static AmazonDynamoDB getDynamoDB() {
		AmazonDynamoDBClientBuilder amazonDynamoDBBuilder = AmazonDynamoDBClient.builder()
				.withCredentials(getAWSCredentialProvider())
				.withEndpointConfiguration(new EndpointConfiguration(DYNAMODB_ENDPOINT_URL, Regions.AP_SOUTH_1.getName()));

		return amazonDynamoDBBuilder.build();
	}

	public static AmazonSNS getSNS() {
		AmazonSNSClientBuilder amazonSNSClientBuilder = AmazonSNSClientBuilder.standard()
				.withCredentials(getAWSCredentialProvider())
				.withRegion(Regions.AP_SOUTH_1.getName());
		
		return amazonSNSClientBuilder.build();
	}
}
