FROM node:18-alpine
WORKDIR /app
COPY atelier-local-next/package*.json ./
RUN npm install
COPY atelier-local-next/ .
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]
