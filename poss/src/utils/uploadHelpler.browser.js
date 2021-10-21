'use strict'

const axios = require('axios');
const crypto = require("./crypto")

function buildUrl(url, options) {
    let subUrl = ""
    for (let key in options) {
        let link = subUrl ? "&" : ""
        subUrl += link + key + "=" + options[key]
    }
    if (url.includes('?')) {
        url += '&' + subUrl
    } else {
        url += '?' + subUrl
    }
    return url
}

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

    const uploadFile = async function (file, options = {}, onUploadProgress) {
        const form = new FormData();
        form.append("path", file);
        return await upload(form, options, onUploadProgress);
    }

    const uploadDir = async function (path) {
        console.error("browsers are not support uploading dir.")
    }

    const uploadEncryptedFile = async function (file, aesKey, options = {}, onUploadProgress) {
        const form = await getEncryptedForm(file, aesKey)
        return await upload(form, options, onUploadProgress);
    }

    const upload = async function (form, options = {}, onUploadProgress) {
        try {
            let info = await axios({
                method: "post",
                url: buildUrl(url, options),
                processData: false,
                mimeType: "multipart/form-data",
                contentType: false,
                data: form,
                onUploadProgress: progressEvent => {
                    (typeof onUploadProgress != 'undefined') && onUploadProgress(progressEvent.loaded / progressEvent.total)
                }
            });
            return info.data;
        } catch (error) {
            console.error(error);
            throw error
        }
    }

    return {
        uploadFile: uploadFile,
        uploadDir: uploadDir,
        uploadEncryptedFile: uploadEncryptedFile
    }
}