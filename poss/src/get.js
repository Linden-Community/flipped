'use strict'

const createClient = require('ipfs-http-client')
const { globSource } = createClient
const fs = require('fs-extra')
const crypto = require("./utils/crypto")

module.exports = (options) => {
    const client = createClient(options)

    const getFile = async (path, cid) => {
        for await (const file of client.get(cid)) {
            if (file.type != "file") console.error("this dag node is not a file")
            let ws = fs.createWriteStream(path);
            for await (const chunk of file.content) {
                ws.write(chunk)
            }
            ws.end()
        }
    }

    const getAndDecrypt = async (path, cid, passwd) => {
        for await (const file of client.get(cid)) {
            if (file.type != "file") console.error("this dag node is not a file")

            const block = 262160
            let buf = Buffer.alloc(0);
            let ws = fs.createWriteStream(path);
            for await (const chunk of file.content) {
                if (buf.length + chunk.length < block) {
                    buf = Buffer.concat([buf, chunk])
                } else {
                    let data = Buffer.concat([buf, chunk], block)
                    data = crypto.decrypt(data, passwd)
                    ws.write(data)
                    buf = chunk.slice(block - buf.length)
                }
            }
            buf = crypto.decrypt(buf, passwd)
            ws.write(buf)
            ws.end()
        }
    }

    return {
        data: client.cat,
        file: getFile,
        encryptedFile: getAndDecrypt
    }
}