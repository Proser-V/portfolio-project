/** @type {import('tailwindcss').Config} */
const defaultTheme = require('tailwindcss/defaultTheme');

module.exports = {
  content: [
    "./src/app/**/*.{js,ts,jsx,tsx}",
    "./src/components/**/*.{js,ts,jsx,tsx}"
  ],
  safelist: [
    'bg-gradient-to-br',
    'from-transparent',
    'to-white/70',
  ],
  theme: {
    extend: {
      colors: {
        blue: '#0C0451',
        gold: '#EBC789',
        goldLight: '#FEF1DA',
        silver: '#C0C0C0',
        white: '#FDFDFD',
      },
      fontFamily: {
        cabin: ['Cabin', 'sans-serif'],
      },
    },
  },
  plugins: [],
};
