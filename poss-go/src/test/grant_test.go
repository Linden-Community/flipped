package test

import (
	"fmt"
	"liblindencloud/src/config"
	"testing"
)

func TestGrant(t *testing.T) {
	proofCid := "bafyreif5j5atpexdspwmuifwu5hsqlbv63ihggwkfzckhpncihpvb5uyqm"
	grant, err := client.Grant(proofCid, config.EOSPrivateKey, config.EOSPubicKey2)
	fmt.Println(err)
	fmt.Println(grant)
}
