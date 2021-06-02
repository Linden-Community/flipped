const util = require('util');
TextDecoder = util.TextDecoder
TextEncoder = util.TextEncoder
globalThis = global;
const args = process.argv.splice(2)

const consumer = require('../mq/consumer');

const createClient = require('ipfs-http-client')
const ipfsServer = args[0] || 'http://192.168.0.72:5001'
const client = createClient(ipfsServer)

consumer.init(args[1] || "111.19.254.170:9876", async function (msg, ack) {
  console.log(msg);
  if (!msg.keys) {
    ack.done()
    return
  }
  const obj = await client.pin.add(msg.keys)
  console.log('pin', obj, "at", ipfsServer)
  ack.done()
});
