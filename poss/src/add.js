'use strict'

const createClient = require('ipfs-http-client')
const { globSource } = createClient

module.exports = (options) => {
  const url = JSON.stringify(options) == "{}" ? {} : options.url || 'http://poss.cipfs.cn/v1/add/' + options.clientID + '/api/v0'
  const client = createClient(url)
  const addFile = async function (path) {
    const file = globSource(path)
    return await client.add(file)
  }
  return {
    data: client.add,
    file: addFile
  }
}
