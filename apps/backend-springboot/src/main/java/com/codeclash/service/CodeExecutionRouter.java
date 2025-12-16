package com.codeclash.service;

import com.codeclash.dto.CodeExecutionRequest;
import com.codeclash.dto.CodeExecutionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CodeExecutionRouter {
    
    @Value("${codeclash.execution.mode:direct}")
    private String executionMode;
    
    @Autowired
    private CodeExecutionService directExecutionService;
    
    @Autowired
    private DockerCodeExecutionService dockerExecutionService;
    
    public CodeExecutionResponse executeCode(CodeExecutionRequest request) {
        if ("docker".equalsIgnoreCase(executionMode)) {
            return dockerExecutionService.executeCode(request);
        } else {
            return directExecutionService.executeCode(request);
        }
    }
    
    public String getExecutionMode() {
        return executionMode;
    }
}
