FROM node:8.17.0-jessie

RUN mkdir -p /opt/node
WORKDIR /opt/node
COPY src src 
COPY package.json .
COPY node_modules node_modules

CMD node src/gateway ${url} ${mongoUrl}