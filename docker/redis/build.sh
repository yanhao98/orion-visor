#/bin/bash
set -e
version=2.4.1
docker build -t orion-visor-redis:${version} .
docker tag orion-visor-redis:${version} registry.cn-hangzhou.aliyuncs.com/orionsec/orion-visor-redis:${version}
docker tag orion-visor-redis:${version} registry.cn-hangzhou.aliyuncs.com/orionsec/orion-visor-redis:latest
