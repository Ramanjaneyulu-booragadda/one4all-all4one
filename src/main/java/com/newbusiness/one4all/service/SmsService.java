package com.newbusiness.one4all.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

@Service
@Slf4j
public class SmsService {

    @Value("${aws.ses.region}")
    private String awsRegion;

    @Value("${aws.ses.access-key}")
    private String awsAccessKey;

    @Value("${aws.ses.secret-key}")
    private String awsSecretKey;

    public boolean sendSms(String phoneNumber, String message) {
        try (SnsClient snsClient = SnsClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsAccessKey, awsSecretKey)))
                .build()) {

            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .phoneNumber(phoneNumber)
                    .build();
            PublishResponse result = snsClient.publish(request);
            log.info("SMS sent to {} with messageId {}", phoneNumber, result.messageId());
            return true;
        } catch (SnsException e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.awsErrorDetails().errorMessage());
            return false;
        }
    }
}
