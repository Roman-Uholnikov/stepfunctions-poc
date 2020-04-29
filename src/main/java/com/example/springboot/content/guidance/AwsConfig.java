package com.example.springboot.content.guidance;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableSqs
public class AwsConfig {

    @Value("${sqs.max.num.of.messages}")
    private int maxNumOfMessages;

    @Value("${aws.key.id}")
    private String awsKeyId;

    @Value("${aws.access.key}")
    private String awsAccessKey;

    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSqs) {
        SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
        factory.setAmazonSqs(amazonSqs);
        factory.setWaitTimeOut(20);
        factory.setMaxNumberOfMessages(maxNumOfMessages);
        return factory;
    }

    @Bean
    public AmazonSQSAsync amazonSqs() {
        return AmazonSQSAsyncClientBuilder.standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsKeyId, awsAccessKey)))
                .build();
    }


    @Bean
    QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSqs) {
        return new QueueMessagingTemplate(amazonSqs);
    }

    @Bean
    @Profile("!local")
    AWSStepFunctions stepFunctionsAws() {
        return AWSStepFunctionsClientBuilder.standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsKeyId, awsAccessKey)))
                .build();
    }

    @Bean
    @Profile("local")
    AWSStepFunctions stepFunctionsLocal(@Value("${step.function.endpoint}") String stepFunctionEndpoint) {
        return AWSStepFunctionsClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(stepFunctionEndpoint, Regions.DEFAULT_REGION.getName()))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsKeyId, awsAccessKey)))
                .build();
    }
}
