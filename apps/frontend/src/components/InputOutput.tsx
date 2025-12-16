import React from 'react';
import { FaTerminal } from 'react-icons/fa';

interface InputOutputProps {
  input: string;
  output: string;
  onInputChange: (value: string) => void;
  isRunning: boolean;
}

const InputOutput: React.FC<InputOutputProps> = ({ 
  input, 
  output, 
  onInputChange, 
  isRunning 
}) => {
  return (
    <div className="flex flex-col h-full space-y-2">
      {/* Input Section */}
      <div className="flex-1 flex flex-col bg-code-editor rounded-lg border border-gray-700">
        <div className="flex items-center px-4 py-2 bg-sidebar border-b border-gray-700">
          <FaTerminal className="w-4 h-4 text-gray-400 mr-2" />
          <span className="text-sm text-gray-300 font-medium">Input</span>
        </div>
        <textarea
          value={input}
          onChange={(e) => onInputChange(e.target.value)}
          className="flex-1 p-4 bg-code-bg text-gray-300 font-mono text-sm resize-none focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="Enter input for your program..."
          disabled={isRunning}
        />
      </div>

      {/* Output Section */}
      <div className="flex-1 flex flex-col bg-code-editor rounded-lg border border-gray-700">
        <div className="flex items-center px-4 py-2 bg-sidebar border-b border-gray-700">
          {isRunning ? (
            <>
              <div className="w-4 h-4 mr-2">
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-500"></div>
              </div>
              <span className="text-sm text-blue-400 font-medium">Running...</span>
            </>
          ) : (
            <>
              <FaTerminal className="w-4 h-4 text-gray-400 mr-2" />
              <span className="text-sm text-gray-300 font-medium">Output</span>
            </>
          )}
        </div>
        <div className="flex-1 p-4 bg-code-bg text-gray-300 font-mono text-sm overflow-auto">
          {output || 'Output will appear here...'}
        </div>
      </div>
    </div>
  );
};

export default InputOutput;