'use strict'

const createClient = require('ipfs-http-client')
const { globSource } = createClient

module.exports = (options) => {
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
