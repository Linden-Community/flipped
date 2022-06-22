package test

import (
	"fmt"
	"liblindencloud/src"
	"liblindencloud/src/config"
	"os"
	"testing"
)

var (
	client src.DefaultPossClient
)

func TestAddByPath(t *testing.T) {
	filePath := "F:\\file\\audit\\demo.txt"
	cid, err := client.AddByPath(filePath)
	fmt.Println(err)
	fmt.Println(cid)
}

func TestAddByReader(t *testing.T) {
	filePath := "F:\\file\\audit\\demo.txt"
	reader, _ := os.Open(filePath)
	cid, err := client.AddByReader(reader)
	fmt.Println(err)
	fmt.Println(cid)
}

func TestAddEncryptByPath(t *testing.T) {
	filePath := "F:\\BaiduNetdiskDownload\\test.zip"
	//str := liblindencloud.GetTimeNowStr()
	str := "1750860335000TtxNVN"
	fmt.Println("加密文件id", str)
	cid, err := client.AddEncryptByPath(filePath, config.EOSPrivateKey, str)
	fmt.Println(err)
	fmt.Println(cid)
}
