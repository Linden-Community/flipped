package test

import (
	"encoding/hex"
	"fmt"
	"github.com/eoscanada/eos-go/ecc"
	"github.com/stretchr/testify/require"
	"liblindencloud/src"
	"liblindencloud/src/config"
	"testing"

	"github.com/enceve/crypto/dh/ecdh"
	"github.com/ethereum/go-ethereum/crypto/secp256k1"
	"github.com/stretchr/testify/assert"
)

func TestEncrypt(t *testing.T) {
	secret, err := src.GetSharedSecret(config.Pri1, config.Pub2)
	if nil != err {
		t.Fatal(err)
	}

	t.Log(hex.EncodeToString(secret))

	crypted, err := src.EcdhEncrypt(secret, []byte("123"))
	if nil != err {
		t.Fatal(err)
	}

	t.Log(crypted)
}

func TestGetPublicKeyByPrivateKey(t *testing.T) {
	kk := ecdh.GenericCurve(secp256k1.S256())
	priv1_bytes, err := hex.DecodeString(config.Pri1)
	if nil != err {
		t.Log(err)
	}

	t.Log(hex.EncodeToString(kk.PublicKey(priv1_bytes)))
	assert.Equal(t, config.Pub1, hex.EncodeToString(kk.PublicKey(priv1_bytes)))

	priv2_bytes, err := hex.DecodeString(config.Pri2)
	if nil != err {
		t.Log(err)
	}
	t.Log(hex.EncodeToString(kk.PublicKey(priv2_bytes)))
	assert.Equal(t, config.Pub2, hex.EncodeToString(kk.PublicKey(priv2_bytes)))
}

func TestResolveLong(t *testing.T) {
	high := 384 << 32
	t.Logf("%b\n", high)

	/* b_bits := bits.Len32(1694634785)
	t.Log(b_bits) */
	low := 1694634785
	t.Logf("%b\n", low)

	t.Logf("%b\n", high|low)

	entropy := 64125 % 0xFFFF
	entropy = entropy + 1
	t.Log(entropy)
}

func TestResolveLong2(t *testing.T) {
	var cell int64 = 1 << 32
	// compnents key: high, value: low
	components := map[int64]int64{
		25191556: -677065606,
		25191720: 1116978792,
		25192043: -671583464,
		25192044: 590659439,
	}
	nonce_res := make([]int64, 0)
	for high, low := range components {
		var nonce_ori int64
		if low < 0 {
			nonce_ori = int64(high+1)*cell + int64(low)
		} else {
			nonce_ori = int64(high)*cell + int64(low)
		}
		t.Log("low: ", low, "high: ", high, "nonce_ori: ", nonce_ori)
		nonce_res = append(nonce_res, nonce_ori)
	}

	for _, nonce := range nonce_res {
		var low int32 = int32(nonce % cell)
		var high int32 = int32(nonce / cell)
		t.Log("nonce: ", nonce, "low: ", low, "high: ", high)
	}

}

func TestPrivateKeyToPublicKey(t *testing.T) {

	privateK := "4e228512d7ae73520f06ff8bbb90c292ed9a0bca7f0a4ce742f6f5cedc8b56ca"
	key, err2 := ecc.NewPrivateKey(privateK)
	if err2 != nil {
		fmt.Println("sdsdsds")
	}

	fmt.Println(key)

	public, err := src.PrivateKeyToPublic(privateK)
	if err != nil {
		fmt.Println("转化错误")
	}

	fmt.Println(public)
}

func TestEosPrivateKeyToPublicKey(t *testing.T) {

	wif := "5KYZdUEo39z3FPrtuX2QbbwGnNP5zTd7yyr2SC1j299sBCnWjss"
	privKey, err := ecc.NewPrivateKey(wif)
	require.NoError(t, err)

	pubKey := privKey.PublicKey()

	pubKeyString := pubKey.String()

	//assert.Equal(t, ecc.PublicKeyPrefixCompat+"859gxfnXyUriMgUeThh1fWv3oqcpLFyHa3TfFYC4PK2HqhToVM", pubKeyString)
	fmt.Println(pubKeyString)
}

// 获取共享密钥
func TestGetSharedSecret(t *testing.T) {

	eos, err := src.GetSharedSecretByEos(config.EOSPrivateKey, config.EOSPubicKey)

	if err != nil {
		fmt.Println("error")
	}
	fmt.Println(eos)

}
