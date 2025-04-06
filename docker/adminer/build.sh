#/bin/bash
set -e
version=2.3.8
docker build -t orion-visor-adminer:${version} .
docker tag orion-visor-adminer:${version} registry.cn-hangzhou.aliyuncs.com/orionsec/orion-visor-adminer:${version}
docker tag orion-visor-adminer:${version} registry.cn-hangzhou.aliyuncs.com/orionsec/orion-visor-adminer:latest
