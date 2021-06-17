FROM node:14-alpine

RUN mkdir -p /opt/node
WORKDIR /opt/node
COPY src/dbBackup.js dbBackup.js 
COPY node_modules node_modules

CMD node dbBackup ${url} ${addriss}