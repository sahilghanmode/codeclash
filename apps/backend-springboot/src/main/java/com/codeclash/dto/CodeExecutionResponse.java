package com.codeclash.dto;

import java.time.LocalDateTime;

public class CodeExecutionResponse {
    
    private boolean success;
    private String output;
    private String error;
    private long executionTime;
    private LocalDateTime timestamp;
    
    public CodeExecutionResponse() {}
    
    public CodeExecutionResponse(boolean success, String output, String error, long executionTime) {
        this.success = success;
        this.output = output;
        this.error = error;
        this.executionTime = executionTime;
        this.timestamp = LocalDateTime.now();
    }
    
    public static CodeExecutionResponse success(String output, long executionTime) {
        return new CodeExecutionResponse(true, output, null, executionTime);
    }
    
    public static CodeExecutionResponse error(String error) {
        return new CodeExecutionResponse(false, null, error, 0);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getOutput() {
        return output;
    }
    
    public void setOutput(String output) {
        this.output = output;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public long getExecutionTime() {
        return executionTime;
    }
    
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}