package com.codeclash.service;

import com.codeclash.dto.CodeExecutionRequest;
import com.codeclash.dto.CodeExecutionResponse;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class DockerCodeExecutionService {
    
    private static final long TIMEOUT_SECONDS = 10;
    private static final String MEMORY_LIMIT = "128m";
    private static final String CPU_LIMIT = "0.5";
    
    // Docker images for each language
    private static final String PYTHON_IMAGE = "python:3.11-slim";
    private static final String NODE_IMAGE = "node:18-slim";
    private static final String GCC_IMAGE = "gcc:latest";
    private static final String OPENJDK_IMAGE = "openjdk:17-slim";
    
    public CodeExecutionResponse executeCode(CodeExecutionRequest request) {
        try {
            long startTime = System.currentTimeMillis();
            String output = executeInDocker(request.getLanguage(), request.getCode(), request.getInput());
            long executionTime = System.currentTimeMillis() - startTime;
            
            return CodeExecutionResponse.success(output, executionTime);
        } catch (Exception e) {
            return CodeExecutionResponse.error("Execution error: " + e.getMessage());
        }
    }
    
    private String executeInDocker(String language, String code, String input) throws Exception {
        Path tempDir = Files.createTempDirectory("docker_exec_" + UUID.randomUUID());
        try {
            switch (language.toLowerCase()) {
                case "python":
                    return executePythonInDocker(tempDir, code, input);
                case "javascript":
                    return executeJavaScriptInDocker(tempDir, code, input);
                case "java":
                    return executeJavaInDocker(tempDir, code, input);
                case "cpp":
                    return executeCppInDocker(tempDir, code, input);
                case "c":
                    return executeCInDocker(tempDir, code, input);
                default:
                    throw new IllegalArgumentException("Unsupported language: " + language);
            }
        } finally {
            cleanupDirectory(tempDir);
        }
    }
    
    private String executePythonInDocker(Path tempDir, String code, String input) throws Exception {
        Path codeFile = tempDir.resolve("code.py");
        Files.write(codeFile, code.getBytes());
        
        if (input != null && !input.trim().isEmpty()) {
            Path inputFile = tempDir.resolve("input.txt");
            Files.write(inputFile, input.getBytes());
            return runDockerContainer(PYTHON_IMAGE, tempDir, "sh", "-c", "python3 /code/code.py < /code/input.txt");
        }
        
        return runDockerContainer(PYTHON_IMAGE, tempDir, "python3", "/code/code.py");
    }
    
    private String executeJavaScriptInDocker(Path tempDir, String code, String input) throws Exception {
        Path codeFile = tempDir.resolve("code.js");
        Files.write(codeFile, code.getBytes());
        
        if (input != null && !input.trim().isEmpty()) {
            Path inputFile = tempDir.resolve("input.txt");
            Files.write(inputFile, input.getBytes());
            return runDockerContainer(NODE_IMAGE, tempDir, "sh", "-c", "node /code/code.js < /code/input.txt");
        }
        
        return runDockerContainer(NODE_IMAGE, tempDir, "node", "/code/code.js");
    }
    
    private String executeJavaInDocker(Path tempDir, String code, String input) throws Exception {
        String className = extractJavaClassName(code);
        if (className == null) {
            throw new IllegalArgumentException("No public class found in Java code");
        }
        
        Path codeFile = tempDir.resolve(className + ".java");
        Files.write(codeFile, code.getBytes());
        
        String compileAndRun;
        if (input != null && !input.trim().isEmpty()) {
            Path inputFile = tempDir.resolve("input.txt");
            Files.write(inputFile, input.getBytes());
            compileAndRun = String.format(
                "javac /code/%s.java && java -cp /code %s < /code/input.txt",
                className, className
            );
        } else {
            compileAndRun = String.format(
                "javac /code/%s.java && java -cp /code %s",
                className, className
            );
        }
        
        return runDockerContainer(OPENJDK_IMAGE, tempDir, "sh", "-c", compileAndRun);
    }
    
    private String executeCppInDocker(Path tempDir, String code, String input) throws Exception {
        Path codeFile = tempDir.resolve("code.cpp");
        Files.write(codeFile, code.getBytes());
        
        String compileAndRun;
        if (input != null && !input.trim().isEmpty()) {
            Path inputFile = tempDir.resolve("input.txt");
            Files.write(inputFile, input.getBytes());
            compileAndRun = "g++ -o /code/code /code/code.cpp && /code/code < /code/input.txt";
        } else {
            compileAndRun = "g++ -o /code/code /code/code.cpp && /code/code";
        }
        
        return runDockerContainer(GCC_IMAGE, tempDir, "sh", "-c", compileAndRun);
    }
    
    private String executeCInDocker(Path tempDir, String code, String input) throws Exception {
        Path codeFile = tempDir.resolve("code.c");
        Files.write(codeFile, code.getBytes());
        
        String compileAndRun;
        if (input != null && !input.trim().isEmpty()) {
            Path inputFile = tempDir.resolve("input.txt");
            Files.write(inputFile, input.getBytes());
            compileAndRun = "gcc -o /code/code /code/code.c && /code/code < /code/input.txt";
        } else {
            compileAndRun = "gcc -o /code/code /code/code.c && /code/code";
        }
        
        return runDockerContainer(GCC_IMAGE, tempDir, "sh", "-c", compileAndRun);
    }
    
    private String runDockerContainer(String image, Path codeDir, String... command) throws Exception {
        List<String> dockerCommand = new ArrayList<>();
        dockerCommand.add("docker");
        dockerCommand.add("run");
        dockerCommand.add("--rm");                          // Auto-remove container
        dockerCommand.add("--network=none");                // No network access
        dockerCommand.add("--memory=" + MEMORY_LIMIT);      // Memory limit
        dockerCommand.add("--cpus=" + CPU_LIMIT);           // CPU limit
        dockerCommand.add("--pids-limit=50");               // Limit processes (prevent fork bombs)
        dockerCommand.add("--read-only");                   // Read-only filesystem
        dockerCommand.add("--tmpfs=/tmp:rw,size=64m");      // Writable /tmp with size limit
        dockerCommand.add("-v");
        dockerCommand.add(codeDir.toAbsolutePath() + ":/code:ro");  // Mount code as read-only
        dockerCommand.add(image);
        
        for (String cmd : command) {
            dockerCommand.add(cmd);
        }
        
        ProcessBuilder pb = new ProcessBuilder(dockerCommand);
        pb.redirectErrorStream(true);
        
        Process process = pb.start();
        
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        
        if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            // Also try to kill any lingering container
            killDockerContainer(process);
            throw new RuntimeException("Execution timeout (max " + TIMEOUT_SECONDS + " seconds)");
        }
        
        String result = output.toString().trim();
        
        if (process.exitValue() != 0 && result.isEmpty()) {
            throw new RuntimeException("Execution failed with exit code: " + process.exitValue());
        }
        
        return result;
    }
    
    private void killDockerContainer(Process process) {
        // Docker containers are auto-removed with --rm flag
        // This is just a safety measure
        process.destroyForcibly();
    }
    
    private String extractJavaClassName(String code) {
        String[] lines = code.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("public class ")) {
                String className = line.substring("public class ".length()).split("\\s+")[0];
                return className.replaceAll("[{};]", "").trim();
            }
        }
        return null;
    }
    
    private void cleanupDirectory(Path dir) {
        try {
            Files.walk(dir)
                .sorted((a, b) -> -a.compareTo(b))
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        // Ignore cleanup errors
                    }
                });
        } catch (IOException e) {
            // Ignore cleanup errors
        }
    }
}
