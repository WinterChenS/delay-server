#!/bin/bash

if [ X$1 = X ]; then
        read -p "请输入镜像版本号(按回车默认latest)：" version
else
        version=$1
fi

if [ X$version = X ]; then
version=latest
fi

echo -e "\n"
echo "------------------------"
echo "镜像版本为：$version"
echo "------------------------"


mvn clean package -Dmaven.test.skip=true &&

cd src/main/docker/ &&

cp ../../../target/delay-server-0.0.1-SNAPSHOT.jar ./ &&

docker build -f="Dockerfile-push" -t="winterchen/delay-server:$version" . &&

docker push winterchen/delay-server:$version
echo "[上传完成]"
