package src

import (
	"bytes"
	"encoding/hex"
	"fmt"
	"io"
	"liblindencloud/src/model"
	"log"
	"math/rand"
	"os"
	"reflect"
	"strconv"
	"time"
)

const debug bool = false
const charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

// PrintLog 临时的日志输出
func PrintLog(flag string, msg interface{}) {
	if debug {
		log.Println(flag, ": ", msg)
		switch reflect.TypeOf(msg).Kind() {
		case reflect.Slice:
			log.Println(flag, ": ", hex.EncodeToString(msg.([]byte)))
		}
	}
}

func stringWithCharset(length int, charset string) string {
	var seededRand *rand.Rand = rand.New(rand.NewSource(time.Now().UnixNano()))
	b := make([]byte, length)
	for i := range b {
		b[i] = charset[seededRand.Intn(len(charset))]
	}
	return string(b)
}

// randomString 随机string
func randomString(length int) string {
	return stringWithCharset(length, charset)
}

// getProofInfo 获取文件信息
func getFileFormat(path string) (value model.FileInfo) {
	// 获取文件信息
	file, err := os.Stat(path)
	if err != nil {
		fmt.Println("error")
		return
	}
	value.Name = file.Name()
	value.TSize = file.Size()
	//value.IsDir = file.IsDir()
	//value.ModTime = file.ModTime()

	return
}

// byteToReader 将[]byte 转化为 io.reader
func byteToReader(data []byte) io.Reader {
	// byte slice to bytes.Reader, which implements the io.Reader interface
	return bytes.NewReader(data)
}

func getTimeNowStr() string {
	now := time.Now()
	str := strconv.Itoa(now.Hour()) + strconv.Itoa(now.Minute()) + strconv.Itoa(now.Nanosecond())
	s := randomString(6)

	return str + s
}
