import React from 'react';
import { FaCode } from 'react-icons/fa';

interface LanguageSelectorProps {
  selectedLanguage: string;
  onLanguageChange: (language: string) => void;
}

const languages = [
  { id: 'python', name: 'Python', monacoId: 'python' },
  { id: 'javascript', name: 'JavaScript', monacoId: 'javascript' },
  { id: 'java', name: 'Java', monacoId: 'java' },
  { id: 'cpp', name: 'C++', monacoId: 'cpp' },
  { id: 'c', name: 'C', monacoId: 'c' },
  { id: 'csharp', name: 'C#', monacoId: 'csharp' },
  { id: 'go', name: 'Go', monacoId: 'go' },
  { id: 'rust', name: 'Rust', monacoId: 'rust' },
];

const LanguageSelector: React.FC<LanguageSelectorProps> = ({ 
  selectedLanguage, 
  onLanguageChange 
}) => {
  return (
    <div className="flex items-center space-x-2">
      <FaCode className="w-5 h-5 text-gray-400" />
      <select
        value={selectedLanguage}
        onChange={(e) => onLanguageChange(e.target.value)}
        className="bg-sidebar text-gray-300 border border-gray-600 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
      >
        {languages.map((language) => (
          <option key={language.id} value={language.id}>
            {language.name}
          </option>
        ))}
      </select>
    </div>
  );
};

export default LanguageSelector;