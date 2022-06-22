package test

import (
	"bytes"
	"crypto/aes"
	"crypto/cipher"
	"crypto/sha512"
	"fmt"
	"io/ioutil"
	"os"
	"testing"
)

// 加密
func TestEncrypt001(t *testing.T) {
	aesKey := "1nZVMriaGFfiEdR4"
	sum512 := sha512.Sum512([]byte(aesKey))

	fmt.Println(sum512)

	data := []byte("12345612131321313132132131212132133111111111111111111111111111233333333312222222222222222222222222")

	// 加密
	encryptData := aesEncrypt(data, sum512[:])
	// 解密
	decryptData := aesDecrypt(encryptData, sum512[:])

	fmt.Println("加密结果为:", encryptData)
	fmt.Println("加密之前为:", data)
	fmt.Println("解密之后为:", decryptData)
}

func aesEncrypt(origData, encrypt_key []byte) []byte {
	key := encrypt_key[0:32]
	iv := encrypt_key[32:48]

	fmt.Println("iv", iv)
	fmt.Println("key", key)
	// 分组秘钥
	// NewCipher该函数限制了输入k的长度必须为16, 24或者32
	block, _ := aes.NewCipher(key)
	// 获取秘钥块的长度
	blockSize := block.BlockSize()
	// 补全码
	origData = pKCS7Padding(origData, blockSize)
	// 加密模式
	blockMode := cipher.NewCBCEncrypter(block, iv)
	// 创建数组
	cryted := make([]byte, len(origData))
	// 加密
	blockMode.CryptBlocks(cryted, origData)
	return cryted
}

func aesDecrypt(crytedByte, key []byte) []byte {

	keys := key[0:32]
	iv := key[32:48]
	// 分组秘钥
	block, _ := aes.NewCipher(keys)
	// 获取秘钥块的长度
	//blockSize := block.BlockSize()
	// 加密模式
	blockMode := cipher.NewCBCDecrypter(block, iv)
	// 创建数组
	orig := make([]byte, len(crytedByte))
	// 解密
	blockMode.CryptBlocks(orig, crytedByte)
	// 去补全码
	orig = pKCS7UnPadding(orig)
	return orig
}

//补码
//AES加密数据块分组长度必须为128bit(byte[16])，密钥长度可以是128bit(byte[16])、192bit(byte[24])、256bit(byte[32])中的任意一个。
func pKCS7Padding(ciphertext []byte, blocksize int) []byte {
	padding := blocksize - len(ciphertext)%blocksize
	padtext := bytes.Repeat([]byte{byte(padding)}, padding)
	return append(ciphertext, padtext...)
}

//去码
func pKCS7UnPadding(origData []byte) []byte {
	length := len(origData)
	unpadding := int(origData[length-1])
	return origData[:(length - unpadding)]
}

func getFileByte(path string) []byte {

	file, err := os.Open(path)
	if err != nil {
		fmt.Println("读取文件错误")
	}
	defer file.Close()
	content, err := ioutil.ReadAll(file)
	//fmt.Println(string(content))

	return content
}
