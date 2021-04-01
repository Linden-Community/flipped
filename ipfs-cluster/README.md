# flipped-IPFS-cluster

[![standard-readme compliant](https://img.shields.io/badge/standard--readme-OK-green.svg?style=flat-square)](https://github.com/RichardLitt/standard-readme)
TODO: Put more badges here.

IPFS cluster based on slice and pin Technology.

## Table of Contents

- [Install](#install)
- [Usage](#usage)
- [Maintainers](#maintainers)
- [Contributing](#contributing)
- [License](#license)

## Install

```
cd ~
wget https://raw.githubusercontent.com/Linden-Community/flipped/main/ipfs-cluster/docker-compose.yml
docker-compose up -d

# set system command alias
echo "alias ipfs0='docker exec ipfs0 ipfs'" >> .bashrc
echo "alias ipfs1='docker exec ipfs1 ipfs'" >> .bashrc
echo "alias ipfs2='docker exec ipfs2 ipfs'" >> .bashrc
. .bashrc
```

## Usage

```
ipfs0 id
ipfs0 swarm peers
```

## Maintainers

[@do-nothing](https://github.com/do-nothing)

## Contributing

PRs accepted.

Small note: If editing the README, please conform to the [standard-readme](https://github.com/RichardLitt/standard-readme) specification.

## License

MIT © 2021 lindensys.com
