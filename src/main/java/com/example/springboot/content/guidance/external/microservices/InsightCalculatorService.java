package com.example.springboot.content.guidance.external.microservices;

import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.SendTaskSuccessRequest;
import com.example.springboot.content.guidance.StateMachineUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;

@Log
@Service
public class InsightCalculatorService {

    @Autowired
    private AWSStepFunctions stepFunctions;

    private ObjectMapper objectMapper = new ObjectMapper();

    @SqsListener(value = "${sqs.insight-calculator.queue.name}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void insightCalculatorListener(String incomingMessage) throws JsonProcessingException, InterruptedException {

        try {

            log.log(Level.INFO, "microservice received request: " + incomingMessage);

            stepFunctions.sendTaskSuccess(new SendTaskSuccessRequest()
                    .withOutput(this.objectMapper.writeValueAsString("responce from " + this.getClass().getSimpleName()))
                    .withTaskToken(StateMachineUtils.getToken(incomingMessage)));
        } catch (Exception e) {
            //I do not care for now
            log.log(Level.WARNING, e.getMessage());
        }
    }

}
