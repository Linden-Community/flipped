'use strict'

const axios = require('axios');
const crypto = require("./crypto")

module.exports = (options) => {
    const url = options.url + "/add"

    async function getEncryptedForm(file, aesKey) {
        const form = new FormData();
        const size = file.size;
        let buf = Buffer.alloc(0);
        const shardSize = 262144,
            shardCount = Math.ceil(size / shardSize);
        for (let i = 0; i < shardCount; ++i) {
            let start = i * shardSize,
                end = Math.min(size, start + shardSize);
            let tempBuf = await file.slice(start, end).arrayBuffer();
            tempBuf = Buffer.from(tempBuf)
            tempBuf = crypto.encrypt(tempBuf, aesKey)
            buf = Buffer.concat([buf, tempBuf]);
        }
        form.append("data", new Blob([buf]), file.name + ".encrypted");
        return form
    }

    const uploadFile = async function (file) {
        const form = new FormData();
        form.append("path", file);

        try {
            let info = await axios({
                method: "post",
                url: url,
                processData: false,
                mimeType: "multipart/form-data",
                contentType: false,
                data: form,
            });
            return info.data
        } catch (error) {
            console.error(error);
            return error
        }
    }

    const uploadDir = async function (path) {
        console.error("browsers are not support uploading dir.")
    }

    const uploadEncryptedFile = async function (file, aesKey) {
        const form = await getEncryptedForm(file, aesKey)

        try {
            let info = await axios({
                method: "post",
                url: url,
                processData: false,
                mimeType: "multipart/form-data",
                contentType: false,
                data: form,
            });
            return info.data;
        } catch (error) {
            console.error(error);
            return error
        }
    }

    return {
        uploadFile: uploadFile,
        uploadDir: uploadDir,
        uploadEncryptedFile: uploadEncryptedFile
    }
}