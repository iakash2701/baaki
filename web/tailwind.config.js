/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        baaki: {
          primary: '#1B4D3E',       // Primary green from Android
          primaryDark: '#12382C',
          primaryLight: '#24614E',
          primaryContainer: '#A3E4D7',
          onPrimaryContainer: '#0A2920',
          secondary: '#00796B',
          amber: '#D97706',
          amberContainer: '#FDE68A',
          bg: '#F8FAF9',
          surface: '#FFFFFF',
          textDark: '#111C16',
          border: '#E2E8F0',
          muted: '#64748B'
        }
      }
    },
  },
  plugins: [],
}
