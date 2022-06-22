package src

import (
	"bytes"
	"crypto/sha512"
	"errors"
	"fmt"
	chunk "github.com/ipfs/go-ipfs-chunker"
	"io"
	"liblindencloud/src/cache"
	"liblindencloud/src/config"
	"liblindencloud/src/global"
	"liblindencloud/src/model"
	"os"
	"path/filepath"
	"sync"
)

type Resource struct {
}

// add 上传文件
func add(reader io.Reader) (cid string, error error) {

	cid, err := global.GetShell().Add(reader)

	if err != nil {
		PrintLog("UploadFile file", "上传文件异常")
		os.Exit(1)
		return "", err
	}

	return cid, nil
}

// uploadEncryptedFile 上传加密文件
func uploadEncryptedFile(path, aesKey, randomFileID string) (cid string, fileInfo model.FileInfo, error error) {

	if !filepath.IsAbs(path) {
		return "", fileInfo, errors.New("file path must absolute path")
	}
	fileObj, err := os.Open(path)
	if nil != err {
		return "", fileInfo, err
	}

	// 获取文件信息
	info := getFileFormat(path)
	info.Name += ".encrypted"
	totalSize := info.TSize

	// 加密文件缓冲器
	encryptedFile := new(bytes.Buffer)

	splitter := chunk.DefaultSplitter(fileObj)
	dataCh, errs := chunk.Chan(splitter)

	// 文件需经几次加密
	num := encryptNum(totalSize) // float64
	var progress float64
	var flag float64 = 1
	flag2 := 1
	num100 := int(num / config.MaxWriterNum)

FOR:
	for {
		select {
		case err = <-errs:
			break FOR
		case data := <-dataCh:
			// len(data) == 0 , 这个channel有时也会返回，有时走上面的分支EOF
			PrintLog("data len", len(data))
			if len(data) == 0 {
				break FOR
			}

			encrypted := processData(data, aesKey)
			n, err := encryptedFile.Write(encrypted)

			// 进度
			progress = (flag / num) * 100
			fmt.Println("加密进度", progress)

			// 异步写入
			mutex := sync.WaitGroup{}

			if flag == 1 || flag == num || int(flag) == num100*flag2 {
				mutex.Add(1)
				fmt.Println("写入", flag2)
				go func(randomStr string, progress float64, group *sync.WaitGroup) {
					// 将进度写入db
					cache.Update(randomFileID, progress, group)
				}(randomFileID, progress, &mutex)
				flag2++
			}

			mutex.Wait()
			flag++

			PrintLog("each encrypt len", n)
			if nil != err {
				return "", fileInfo, err
			}
		}
	}

	// 如果len(data) == 0 , 此处的err有两种情况，EOF 或 nil
	if nil != err && io.EOF != err {
		return "", fileInfo, err
	}

	PrintLog("total encrypt len", len(encryptedFile.Bytes()))

	// 加密后文件上传ipfs
	cid, err1 := add(encryptedFile)
	if err1 != nil {
		PrintLog("UploadFile fail", "上传加密文件失败")
	}

	return cid, info, nil
}

// processData 处理分片数据
// TODO 加密分片写入内存 OR 写入磁盘临时文件 ？？？
// TODO 流式上传 OR 所有分片加密完成再上传 ？？？
func processData(data []byte, aesKey string) []byte {
	var encryptKey = sha512.Sum512([]byte(aesKey))
	encrypted := aesEncrypt(data, encryptKey[:])
	return encrypted
}

// 文件需要加密的次数
func encryptNum(total int64) float64 {
	num := total / config.EncryptBLock
	if total%config.EncryptBLock != 0 {
		num++
	}
	return float64(num)
}
