package test

import (
	"bufio"
	"fmt"
	"liblindencloud/src"
	"liblindencloud/src/config"
	"os"
	"testing"
)

func TestGet(t *testing.T) {
	cid := "QmViV9WtoJn4RQ8rT1rUozoGikCAHJMYxt91JumLvY68js"
	bytes, err := client.Get(cid)
	fmt.Println(err)
	fmt.Println(bytes)
}

func TestGetEncrypted(t *testing.T) {
	proofCid := "bafyreig2hcjh5x25u7t44iggjs27w33goq223twbe22kt474mmqfowcipe"
	file := getFileByte("F:\\file\\audit\\demo.txt")
	bytes, err := client.GetEncrypt(proofCid, config.EOSPrivateKey)
	fmt.Println(err)
	fmt.Println("原始数据:", file)
	fmt.Println("解密文件:", bytes)
}

func TestDownloadSaveLocal(t *testing.T) {

	cid := "QmRu5L5npXoDGG4Nvd8SmcTZzWLW6UM2kp8cH5DvLi6WwB"
	file, err := client.Get(cid)

	//proofCid := "bafyreid76yyviknadj5np2ybzvjp475sp22gx3axdzqvmjyn627bqpr5dm"
	//encryptFile, err := liblindencloud.DefaultPossClient.GetEncrypt(liblindencloud.DefaultPossClient{}, proofCid,config.EOSPrivateKey)

	value, err := os.OpenFile("F:\\file\\file\\userfiles\\video\\0002.txt", os.O_CREATE|os.O_WRONLY|os.O_APPEND, 0666)
	if err != nil {
		fmt.Printf("文件错误,错误为:%v\n", err)
		return
	}

	defer value.Close()
	writer := bufio.NewWriter(value)
	writer.Write(file)
	writer.Flush()
}

func TestGetSharedSecretByEos(t *testing.T) {

	// 共享密钥
	// 己方私钥对方公钥换算出共享密钥  == 对方公钥己方私钥换算出共享密钥

	eos, _ := src.GetSharedSecretByEos(config.EOSPrivateKey, config.EOSPubicKey2)
	eos2, _ := src.GetSharedSecretByEos(config.EOSPrivateKey2, config.EOSPubicKey)

	// eos == eos2
	fmt.Println(eos)
	fmt.Println(eos2)
}

func TestUploadProgress(t *testing.T) {
	key := "1750860335000TtxNVN"
	for {
		value := client.UploadProgress(key)
		fmt.Println(value)
	}
}

func TestGetProof(t *testing.T) {
	proof, err := client.GetProof("bafyreibizuavmk4bikh5yyns4ujxlxhetbysdq2gscdgqwvjtw5uhbw4l4")
	fmt.Println("proof", proof)
	fmt.Println("err", err)
}
