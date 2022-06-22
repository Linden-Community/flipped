package test

import (
	"encoding/hex"
	"errors"
	"fmt"
	"io/ioutil"
	"liblindencloud/src/config"
	"math/rand"
	"net/http"
	"os"
	"path/filepath"
	"sync"
	"testing"
	"time"
)

func TestInt(t *testing.T) {
	encryptData := []byte("123456789asdfghjklqwertyuiopzxcvbnm1")
	fmt.Println("len:", len(encryptData))
	var (
		afterEncrypt = 5
	)
	// 加密文件字节总长
	encryptDataLen := len(encryptData)
	// 分割次数
	num := encryptDataLen / afterEncrypt
	if encryptDataLen%afterEncrypt != 0 {
		num += 1
	}
	// 分割几次
	for i := 0; i < num; i++ {
		if i == num-1 {
			fmt.Println(encryptData[afterEncrypt*i : encryptDataLen])
			break
		}
		fmt.Println(encryptData[afterEncrypt*i : afterEncrypt*i+afterEncrypt])
	}
}

func TestHexDecode(t *testing.T) {
	decodeString, err := hex.DecodeString(config.EOSPrivateKey)
	if err != nil {
		fmt.Println("error")
	}
	fmt.Println(decodeString)
}

func TestGetRandomString(t *testing.T) {
	length := 16
	charset := "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

	var seededRand *rand.Rand = rand.New(rand.NewSource(time.Now().UnixNano()))
	b := make([]byte, length)
	for i := range b {
		b[i] = charset[seededRand.Intn(len(charset))]
	}

	fmt.Println(string(b))
}

func CheckNetWork() (string, error) {
	resp, err := http.Get("http://www.baidu.com/")
	if err != nil {
		return "", err
	}
	defer resp.Body.Close()

	data, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return "", err
	}

	return string(data), nil
}

func GetFileByPath(filePath string) (size int64, err error) {
	obj, err := os.Stat(filePath)
	if nil != err {
		return
	}

	return obj.Size(), nil
}

func ConcurrentWriteFile(filePath string) (err error) {
	fmt.Println(filePath)
	if !filepath.IsAbs(filePath) {
		return errors.New("file path must absolute path")
	}

	filename := filePath + "/123.txt"
	var fileObj *os.File
	fileObj, err = os.OpenFile(filename, os.O_APPEND|os.O_WRONLY|os.O_CREATE, 0644)
	if nil != err {
		return
	}

	/* data, err := ioutil.ReadAll(fileObj)
	if nil != err {
		return
	}
	fmt.Println(string(data)) */
	var wg = &sync.WaitGroup{}
	for i := 0; i < 20; i++ {
		wg.Add(1)
		go func(i int) {
			fileObj.WriteString(fmt.Sprintf("%d\n", i))
			wg.Done()
		}(i)
	}

	wg.Wait()
	fmt.Println("go here")
	/*
		finfo, err := fileObj.Stat()
		if nil != err {
			return
		}
		fmt.Println(finfo.Size(), finfo.Mode()) */
	return fileObj.Close()
}

func TestSegmentation(t *testing.T) {

	num := 9856464567

	num100 := num / 10

	flag := 1
	flag2 := 1

	// 最多执行100次

	for {

		if flag == 1 || flag == num || flag == num100*flag2 {
			fmt.Println(flag)
			flag2++

			if flag == num {
				break
			}
		}

		flag++
	}

}
