/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      colors: {
        blue: '#0C0451',
        gold: '#EBC789',
        goldLight: '#FEF1DA',
        silver: '#C0C0C0',
        white: '#FDFDFD'
      },
      fontFamily: {
        cabin: ['Cabin']
      },
    },
  },
  plugins: [],
};

