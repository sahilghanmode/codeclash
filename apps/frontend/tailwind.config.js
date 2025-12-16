/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'code-bg': '#1e1e1e',
        'code-editor': '#2d2d30',
        'sidebar': '#252526',
        'output': '#1e1e1e',
      },
    },
  },
  plugins: [],
}