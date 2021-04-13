FROM node:10-buster

RUN mkdir -p /opt/node
WORKDIR /opt/node
COPY pinner/src src 
COPY pinner/node_modules node_modules
COPY mq mq

CMD node src/pinHelper ${url} ${mqServer}
# CMD sleep 36000