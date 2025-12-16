package com.codeclash.controller;

import com.codeclash.config.RateLimitConfig;
import com.codeclash.dto.CodeExecutionRequest;
import com.codeclash.dto.CodeExecutionResponse;
import com.codeclash.service.CodeExecutionRouter;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CodeExecutionController {

    @Autowired
    private CodeExecutionRouter codeExecutionRouter;
    
    @Autowired
    private RateLimitConfig rateLimitConfig;

    @PostMapping("/execute")
    public ResponseEntity<CodeExecutionResponse> executeCode(
            @RequestBody CodeExecutionRequest request,
            HttpServletRequest httpRequest) {
        
        // Rate limiting: 10 requests per minute per IP
        String clientIp = getClientIp(httpRequest);
        Bucket bucket = rateLimitConfig.resolveBucket(clientIp);
        
        if (!bucket.tryConsume(1)) {
            CodeExecutionResponse rateLimitResponse = CodeExecutionResponse.error(
                "Rate limit exceeded. Please wait before making another request.");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(rateLimitResponse);
        }
        
        try {
            CodeExecutionResponse response = codeExecutionRouter.executeCode(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            CodeExecutionResponse errorResponse = CodeExecutionResponse.error("Server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK - Execution mode: " + codeExecutionRouter.getExecutionMode());
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}