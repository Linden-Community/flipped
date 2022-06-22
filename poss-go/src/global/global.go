package global

import shell "github.com/ipfs/go-ipfs-api"

const (
	DefaultHost    = "csg.lindensys.cn"
	DefaultPort    = "80"
	DefaultBaseUrl = "/ipfs/"
)

func GetShell() (sh *shell.Shell) {
	return shell.NewShell(DefaultHost + ":" + DefaultPort)
}
