'use strict'

const createClient = require('ipfs-http-client')
const globSource = require("./utils/glob-source")

module.exports = (options) => {
  const client = createClient(options)

  const addFile = async function (path) {
    const file = globSource(path)
    return await client.add(file)
  }

  const getAndEncrypt = async function (path, passwd) {
    const options = {}
    options.passwd = passwd
    const file = globSource(path, options)
    return await client.add(file)
  }

  return {
    data: client.add,
    file: addFile,
    encryptedFile: getAndEncrypt
  }
}
