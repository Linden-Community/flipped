'use strict'

const createClient = require('ipfs-http-client')
const { globSource } = createClient

module.exports = (options) => {
  options.url = options.url || (options.clientID ? 'http://poss.cipfs.cn/v1/add/' + options.clientID + '/api/v0' : null)
  
  const client = createClient(options)
  const addFile = async function (path) {
    const file = globSource(path)
    return await client.add(file)
  }
  return {
    data: client.add,
    file: addFile
  }
}