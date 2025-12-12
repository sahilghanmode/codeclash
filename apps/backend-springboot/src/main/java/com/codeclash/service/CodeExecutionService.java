package com.codeclash.service;

import com.codeclash.dto.CodeExecutionRequest;
import com.codeclash.dto.CodeExecutionResponse;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Service
public class CodeExecutionService {
    
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final long TIMEOUT_SECONDS = 10;
    
    public CodeExecutionResponse executeCode(CodeExecutionRequest request) {
        try {
            long startTime = System.currentTimeMillis();
            String output = executeByLanguage(request.getLanguage(), request.getCode(), request.getInput());
            long executionTime = System.currentTimeMillis() - startTime;
            
            return CodeExecutionResponse.success(output, executionTime);
        } catch (Exception e) {
            return CodeExecutionResponse.error("Execution error: " + e.getMessage());
        }
    }
    
    private String executeByLanguage(String language, String code, String input) throws Exception {
        switch (language.toLowerCase()) {
            case "python":
                return executePython(code, input);
            case "java":
                return executeJava(code, input);
            case "javascript":
                return executeJavaScript(code, input);
            case "cpp":
                return executeCpp(code, input);
            case "c":
                return executeC(code, input);
            default:
                throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }
    
    private String executePython(String code, String input) throws Exception {
        Path tempFile = Files.createTempFile("code_", ".py");
        try {
            Files.write(tempFile, code.getBytes());
            
            ProcessBuilder pb = new ProcessBuilder("python3", tempFile.toString());
            if (input != null && !input.trim().isEmpty()) {
                Process process = pb.start();
                try (OutputStream os = process.getOutputStream()) {
                    os.write(input.getBytes());
                    os.flush();
                }
                return readProcessOutput(process);
            }
            
            return executeProcess(pb);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
    
    private String executeJava(String code, String input) throws Exception {
        Path tempDir = Files.createTempDirectory("java_exec_");
        try {
            // Extract class name from public class
            String className = extractJavaClassName(code);
            if (className == null) {
                throw new IllegalArgumentException("No public class found in Java code");
            }
            
            Path javaFile = tempDir.resolve(className + ".java");
            Files.write(javaFile, code.getBytes());
            
            // Compile
            ProcessBuilder compilePb = new ProcessBuilder("javac", javaFile.toString());
            Process compileProcess = compilePb.start();
            if (!compileProcess.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                compileProcess.destroyForcibly();
                throw new RuntimeException("Compilation timeout");
            }
            
            if (compileProcess.exitValue() != 0) {
                String error = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()))
                    .lines().reduce("", String::concat);
                throw new RuntimeException("Compilation failed: " + error);
            }
            
            // Run
            ProcessBuilder runPb = new ProcessBuilder("java", "-cp", tempDir.toString(), className);
            if (input != null && !input.trim().isEmpty()) {
                Process process = runPb.start();
                try (OutputStream os = process.getOutputStream()) {
                    os.write(input.getBytes());
                    os.flush();
                }
                return readProcessOutput(process);
            }
            
            return executeProcess(runPb);
        } finally {
            // Clean up
            try {
                Files.walk(tempDir)
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
    
    private String executeJavaScript(String code, String input) throws Exception {
        Path tempFile = Files.createTempFile("code_", ".js");
        try {
            Files.write(tempFile, code.getBytes());
            
            ProcessBuilder pb = new ProcessBuilder("node", tempFile.toString());
            if (input != null && !input.trim().isEmpty()) {
                Process process = pb.start();
                try (OutputStream os = process.getOutputStream()) {
                    os.write(input.getBytes());
                    os.flush();
                }
                return readProcessOutput(process);
            }
            
            return executeProcess(pb);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
    
    private String executeCpp(String code, String input) throws Exception {
        Path tempDir = Files.createTempDirectory("cpp_exec_");
        try {
            Path cppFile = tempDir.resolve("code.cpp");
            Path exeFile = tempDir.resolve("code");
            
            Files.write(cppFile, code.getBytes());
            
            // Compile
            ProcessBuilder compilePb = new ProcessBuilder("g++", "-o", exeFile.toString(), cppFile.toString());
            Process compileProcess = compilePb.start();
            if (!compileProcess.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                compileProcess.destroyForcibly();
                throw new RuntimeException("Compilation timeout");
            }
            
            if (compileProcess.exitValue() != 0) {
                String error = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()))
                    .lines().reduce("", String::concat);
                throw new RuntimeException("Compilation failed: " + error);
            }
            
            // Run
            ProcessBuilder runPb = new ProcessBuilder(exeFile.toString());
            if (input != null && !input.trim().isEmpty()) {
                Process process = runPb.start();
                try (OutputStream os = process.getOutputStream()) {
                    os.write(input.getBytes());
                    os.flush();
                }
                return readProcessOutput(process);
            }
            
            return executeProcess(runPb);
        } finally {
            // Clean up
            try {
                Files.walk(tempDir)
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
    
    private String executeC(String code, String input) throws Exception {
        Path tempDir = Files.createTempDirectory("c_exec_");
        try {
            Path cFile = tempDir.resolve("code.c");
            Path exeFile = tempDir.resolve("code");
            
            Files.write(cFile, code.getBytes());
            
            // Compile
            ProcessBuilder compilePb = new ProcessBuilder("gcc", "-o", exeFile.toString(), cFile.toString());
            Process compileProcess = compilePb.start();
            if (!compileProcess.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                compileProcess.destroyForcibly();
                throw new RuntimeException("Compilation timeout");
            }
            
            if (compileProcess.exitValue() != 0) {
                String error = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()))
                    .lines().reduce("", String::concat);
                throw new RuntimeException("Compilation failed: " + error);
            }
            
            // Run
            ProcessBuilder runPb = new ProcessBuilder(exeFile.toString());
            if (input != null && !input.trim().isEmpty()) {
                Process process = runPb.start();
                try (OutputStream os = process.getOutputStream()) {
                    os.write(input.getBytes());
                    os.flush();
                }
                return readProcessOutput(process);
            }
            
            return executeProcess(runPb);
        } finally {
            // Clean up
            try {
                Files.walk(tempDir)
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
    
    private String executeProcess(ProcessBuilder pb) throws Exception {
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
            throw new RuntimeException("Execution timeout");
        }
        
        if (process.exitValue() != 0) {
            throw new RuntimeException("Process exited with code: " + process.exitValue());
        }
        
        return output.toString().trim();
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
    
    private String readProcessOutput(Process process) throws Exception {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        
        if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new RuntimeException("Execution timeout");
        }
        
        if (process.exitValue() != 0) {
            throw new RuntimeException("Process exited with code: " + process.exitValue());
        }
        
        return output.toString().trim();
    }
}