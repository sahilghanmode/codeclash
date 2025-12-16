import React, { useState } from 'react';
import { FaPlay, FaRedo } from 'react-icons/fa';
import axios from 'axios';
import CodeEditor from './CodeEditor';
import InputOutput from './InputOutput';
import LanguageSelector from './LanguageSelector';

interface CodeExecutionProps {}

const CodeExecution: React.FC<CodeExecutionProps> = () => {
  const [code, setCode] = useState(`// Welcome to CodeClash - Online Code Executor
// Select your language and start coding!

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}`);

  const [input, setInput] = useState('');
  const [output, setOutput] = useState('');
  const [selectedLanguage, setSelectedLanguage] = useState('java');
  const [isRunning, setIsRunning] = useState(false);

  const getMonacoLanguageId = (languageId: string): string => {
    const languageMap: { [key: string]: string } = {
      'python': 'python',
      'javascript': 'javascript',
      'java': 'java',
      'cpp': 'cpp',
      'c': 'c',
    };
    return languageMap[languageId] || 'plaintext';
  };

  const getDefaultCode = (languageId: string): string => {
    const defaultCodes: { [key: string]: string } = {
      'python': `# Welcome to CodeClash - Python
print("Hello, World!")`,
      'javascript': `// Welcome to CodeClash - JavaScript
console.log("Hello, World!");`,
      'java': `// Welcome to CodeClash - Java
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}`,
      'cpp': `// Welcome to CodeClash - C++
#include <iostream>
using namespace std;

int main() {
    cout << "Hello, World!" << endl;
    return 0;
}`,
      'c': `// Welcome to CodeClash - C
#include <stdio.h>

int main() {
    printf("Hello, World!\\n");
    return 0;
}`,
    };
    return defaultCodes[languageId] || '// Start coding here...';
  };

  const handleLanguageChange = (languageId: string) => {
    setSelectedLanguage(languageId);
    setCode(getDefaultCode(languageId));
    setOutput('');
  };

  const handleRunCode = async () => {
    setIsRunning(true);
    setOutput('Compiling and running your code...\n');

    try {
      const response = await axios.post('http://localhost:4000/api/execute', {
        code,
        language: selectedLanguage,
        input
      });

      if (response.data.success) {
        setOutput(response.data.output + `\n\n\nExecution time: ${response.data.executionTime}ms`);
      } else {
        setOutput('Error: ' + response.data.error);
      }
    } catch (error: any) {
      if (error.response) {
        setOutput('Error: ' + (error.response.data.error || error.response.data.message));
      } else if (error.request) {
        setOutput('Error: Failed to connect to backend server');
      } else {
        setOutput('Error: ' + error.message);
      }
    } finally {
      setIsRunning(false);
    }
  };

  const handleReset = () => {
    setCode(getDefaultCode(selectedLanguage));
    setInput('');
    setOutput('');
  };

  return (
    <div className="h-screen bg-code-bg flex flex-col">
      {/* Header */}
      <header className="bg-sidebar border-b border-gray-700 px-4 py-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-4">
            <h1 className="text-xl font-bold text-white">CodeClash</h1>
            <span className="text-sm text-gray-400">Online Code Executor</span>
          </div>
          
          <div className="flex items-center space-x-4">
            <LanguageSelector 
              selectedLanguage={selectedLanguage}
              onLanguageChange={handleLanguageChange}
            />
            
            <div className="flex items-center space-x-2">
              <button
                onClick={handleRunCode}
                disabled={isRunning}
                className="flex items-center space-x-2 bg-green-600 hover:bg-green-700 disabled:bg-gray-600 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
              >
                <FaPlay className="w-4 h-4" />
                <span>{isRunning ? 'Running...' : 'Run'}</span>
              </button>
              
              <button
                onClick={handleReset}
                disabled={isRunning}
                className="flex items-center space-x-2 bg-gray-600 hover:bg-gray-700 disabled:bg-gray-700 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
              >
                <FaRedo className="w-4 h-4" />
                <span>Reset</span>
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <div className="flex-1 flex p-4 space-x-4">
        {/* Code Editor */}
        <div className="flex-1 flex flex-col">
          <div className="bg-sidebar px-4 py-2 rounded-t-lg border border-gray-700 border-b-0">
            <span className="text-sm text-gray-300 font-medium">Code Editor</span>
          </div>
          <div className="flex-1">
            <CodeEditor
              language={getMonacoLanguageId(selectedLanguage)}
              value={code}
              onChange={setCode}
            />
          </div>
        </div>

        {/* Input/Output Panel */}
        <div className="w-96 flex flex-col">
          <div className="bg-sidebar px-4 py-2 rounded-t-lg border border-gray-700 border-b-0">
            <span className="text-sm text-gray-300 font-medium">Input & Output</span>
          </div>
          <div className="flex-1">
            <InputOutput
              input={input}
              output={output}
              onInputChange={setInput}
              isRunning={isRunning}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default CodeExecution;