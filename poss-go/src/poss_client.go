package src

import (
	"io"
	"liblindencloud/src/model"
)

type PossClient interface {
	AddByPath(path string) (string, error)

	AddByReader(reader io.Reader) (string, error)

	AddEncryptByPath(path, privateKey, randomFileID string) (string, error)

	Get(cid string) ([]byte, error)

	GetEncrypt(proofCid, privateKey string) ([]byte, error)

	Grant(proofCid, privateKey, publicKey string) (string, error)

	AddProof(links model.FileInfo, privateKey, aesKey string) (string, error)

	GetProof(proofCid string) (model.ProofInfo, error)

	UploadProgress(randomFileID string) string

	GetRandomFileID() string
}
