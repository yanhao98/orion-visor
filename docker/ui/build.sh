#/bin/bash
set -e
version=2.4.1
mv ../../orion-visor-ui/dist ./dist
docker build -t orion-visor-ui:${version} .
rm -rf ./orion-visor-launch.jar
rm -rf ./dist
docker tag orion-visor-ui:${version} registry.cn-hangzhou.aliyuncs.com/orionsec/orion-visor-ui:${version}
docker tag orion-visor-ui:${version} registry.cn-hangzhou.aliyuncs.com/orionsec/orion-visor-ui:latest
