#/bin/bash
set -e
version=2.4.1
cp -r ../../sql ./sql
docker build -t orion-visor-mysql:${version} .
rm -rf ./sql
docker tag orion-visor-mysql:${version} registry.cn-hangzhou.aliyuncs.com/orionsec/orion-visor-mysql:${version}
docker tag orion-visor-mysql:${version} registry.cn-hangzhou.aliyuncs.com/orionsec/orion-visor-mysql:latest
