package src

import (
	"io"
	"io/ioutil"
	"liblindencloud/src/config"
)

type uploadProgress func()

func readerToBytes(reader io.Reader) (bytes []byte, error error) {
	return ioutil.ReadAll(reader)
}

func decrypt(encryptData, aesKey []byte) (decryptData []byte) {
	decryptBlockLen := config.DecryptBLock

	// 加密文件字节总长
	encryptDataLen := len(encryptData)
	// 分割次数
	num := encryptDataLen / decryptBlockLen
	if encryptDataLen%decryptBlockLen != 0 {
		num += 1
	}

	// 分割几次
	for i := 0; i < num; i++ {
		if i == num-1 {
			deData := aesDecrypt(encryptData[decryptBlockLen*i:encryptDataLen], aesKey)
			decryptData = append(decryptData, deData...)
			break
		}
		deData := aesDecrypt(encryptData[decryptBlockLen*i:decryptBlockLen*i+decryptBlockLen], aesKey)
		decryptData = append(decryptData, deData...)
	}

	return decryptData
}
