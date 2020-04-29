package com.example.springboot.content.guidance.service;

import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.CreateStateMachineRequest;
import com.amazonaws.services.stepfunctions.model.CreateStateMachineResult;
import com.amazonaws.services.stepfunctions.model.LogLevel;
import com.amazonaws.services.stepfunctions.model.LoggingConfiguration;
import com.amazonaws.services.stepfunctions.model.StateMachineAlreadyExistsException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.logging.Level;

@Log
@Service("localStepFunctions")
@Profile("local")
public class LocalStepFunctionService {


    @Value("${aws.stepfunctions.role.arn}")
    private String stepfunctionsRoleArn;

    @Autowired
    private AWSStepFunctions stepFunctions;

    private String stateMachineArn;


    @PostConstruct
    public void deployStepFunctionDefinition() {
        try {
            CreateStateMachineRequest createStateMachineRequest = new CreateStateMachineRequest()
                    .withDefinition("{\n" +
                            "  \"Comment\": \"A Hello World example of the Amazon States Language using a Pass state\",\n" +
                            "  \"StartAt\": \"HelloWorld\",\n" +
                            "  \"States\": {\n" +
                            "    \"HelloWorld\": {\n" +
                            "      \"Type\": \"Pass\",\n" +
                            "      \"End\": true\n" +
                            "    }\n" +
                            "  }\n" +
                            "}")
                    .withLoggingConfiguration(new LoggingConfiguration().withLevel(LogLevel.OFF))
                    .withName("test-state-machine")
                    .withRoleArn(stepfunctionsRoleArn);
            CreateStateMachineResult stateMachine = stepFunctions.createStateMachine(createStateMachineRequest);
            this.stateMachineArn = stateMachine.getStateMachineArn();
            log.log(Level.INFO, "The state machine created: " + stateMachine.toString());
        } catch (StateMachineAlreadyExistsException e) {
            log.log(Level.INFO, e.getMessage());
        }


    }

    public String getStateMachineArn() {
        return stateMachineArn;
    }
}
