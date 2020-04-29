package com.example.springboot.content.guidance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StepFunctionRestController {

    @Autowired
    private StepFunctionDefinitionService stepFunctionService;

    @RequestMapping(method = RequestMethod.POST, value = "/start-execution")
    @ResponseBody
    private String startExecution(@RequestBody String payload) {
        return stepFunctionService.startExecution(payload);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/describe-execution/{executionUuid}")
    @ResponseBody
    private String describeExecution(@PathVariable String executionUuid) {
        return stepFunctionService.getExecutionState(executionUuid);
    }
}
