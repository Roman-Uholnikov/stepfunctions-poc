package com.example.springboot.content.guidance.external.microservices;

import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.SendTaskSuccessRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;

import static com.example.springboot.content.guidance.StateMachineUtils.getToken;

@Log
@Service
public class HtmlCollectorService {

    @Autowired
    private AWSStepFunctions stepFunctions;

    private ObjectMapper objectMapper = new ObjectMapper();

    @SqsListener(value = "${sqs.html.collector.meta.queue.name}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void insightCalculatorListener(String incomingMessage) throws JsonProcessingException, InterruptedException {

        try {

            log.log(Level.INFO, "microservice received request: " + incomingMessage);

            stepFunctions.sendTaskSuccess(new SendTaskSuccessRequest()
                    .withOutput(this.objectMapper.writeValueAsString("responce from " + this.getClass().getSimpleName()))
                    .withTaskToken(getToken(incomingMessage)));
        } catch (Exception e) {
            //I do not care for now
            log.log(Level.WARNING, e.getMessage());
        }


    }


}
