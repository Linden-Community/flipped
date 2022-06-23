package src

import (
	"io"
)

type PossClient interface {
	AddByPath(path string) (string, error)

	AddByReader(reader io.Reader) (string, error)

	AddEncryptByPath(path, privateKey, randomFileID string) (string, error)

	Get(cid string) ([]byte, error)

	GetEncrypt(proofCid, privateKey string) ([]byte, error)

	Grant(proofCid, privateKey, publicKey string) (string, error)

	AddProof(cid, name, privateKey, aesKey string, size int64) (string, error)

	GetProof(proofCid string) (string, error)

	UploadProgress(randomFileID string) string

	GetRandomFileID() string
}
