package com.example.springboot.content.guidance.service;

import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.DescribeExecutionRequest;
import com.amazonaws.services.stepfunctions.model.LogLevel;
import com.amazonaws.services.stepfunctions.model.LoggingConfiguration;
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest;
import com.amazonaws.services.stepfunctions.model.StartExecutionResult;
import com.amazonaws.services.stepfunctions.model.UpdateStateMachineRequest;
import com.amazonaws.services.stepfunctions.model.UpdateStateMachineResult;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.UUID;
import java.util.logging.Level;

@Log
@Service
public class StepFunctionDefinitionService {

    @Value("classpath:workflow.json")
    Resource resourceFile;

    @Value("${aws.stepfunctions.arn}")
    private String stepfunctionsArn;

    @Value("${aws.stepfunctions.role.arn}")
    private String stepfunctionsRoleArn;

    @Autowired
    private AWSStepFunctions stepFunctions;

    @Autowired(required = false)
    private LocalStepFunctionService localStepFunctionService;


    @PostConstruct
    public void deployStepFunctionDefinition() {
        String definition = asString(resourceFile);

        String stateMachineArn = getStateMachineArn();

        UpdateStateMachineRequest updateStateMachineRequest = new UpdateStateMachineRequest()
                .withStateMachineArn(stateMachineArn)
                .withLoggingConfiguration(new LoggingConfiguration().withLevel(LogLevel.OFF))
                .withRoleArn(stepfunctionsRoleArn)
                .withDefinition(definition);

        UpdateStateMachineResult updateStateMachineResult = stepFunctions.updateStateMachine(updateStateMachineRequest);
        log.log(Level.INFO, "The state machine definition deployed: " + updateStateMachineResult.toString());


    }

    private String getStateMachineArn() {
        return localStepFunctionService == null || localStepFunctionService.getStateMachineArn() == null
                ? stepfunctionsArn : localStepFunctionService.getStateMachineArn();
    }

    public String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String startExecution(String payload) {
        StartExecutionRequest startExecutionRequest = new StartExecutionRequest()
                .withStateMachineArn(getStateMachineArn())
                .withInput(payload)
                .withName(UUID.randomUUID().toString() + "_local");
        StartExecutionResult startExecutionResult = stepFunctions.startExecution(startExecutionRequest);
        return startExecutionResult.getExecutionArn();
    }

    public String getExecutionState(String executionArn) {
        DescribeExecutionRequest describeExecutionRequest =
                new DescribeExecutionRequest().withExecutionArn(executionArn);

        return stepFunctions.describeExecution(describeExecutionRequest).toString();
    }


}
