# delay-server
使用RabbitMQ作为延迟任务的公共服务

## 简介

![](https://img.shields.io/badge/springboot-2.3.0-green)  ![](https://img.shields.io/badge/license-Apache%202-blue)

依赖于RabiitMQ死信队列实现的延迟任务处理服务，极大的保证消息的可用

## 项目依赖

- jdk8
- maven
- springboot 2.3.0
- Redis
- RabbitMQ
- docker (可选）

## 实现功能

- [X] 可通用的延时任务服务
- [X] 可定制的超时时间以及重试次数，确保消息的可靠
- [X] 回调异常重试
- [X] 可拓展的失败消息存储策略

## 如何使用
1.拉取代码
```
git clone https://github.com/WinterChenS/delay-server.git
```

2.修改application.properties

3.编译

> 以下方式选一即可

3.1 普通编译

```
cd delay-server &&
mvn clean package
```

3.2 docker编译

```
cd delay-server &&
mvn clean package docker:build
```

4.运行

> 运行方式选一即可

4.1 普通运行
```
cd target &&
nohup java -jar delay-server-0.0.1-SNAPSHOT.jar &
```

4.2 docker运行

```
docker run -p 8088:8088 -d  winterchen/delay-server
```


5.调用

OkHttp
```java
OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("application/json");
RequestBody body = RequestBody.create(mediaType, "{\n    \"id\": \"0923840293429384023\",\n    \"expireTime\": 3,\n    \"message\": \"hello\",\n    \"callbackPath\": \"http://127.0.0.1:8088/test/success\",\n    \"currentTime\": 29387492384,\n    \"retryCount\": 3\n}");
Request request = new Request.Builder()
  .url("http://127.0.0.1:8088/api/v1/default/delay")
  .post(body)
  .addHeader("Content-Type", "application/json")
  .addHeader("cache-control", "no-cache")
  .build();

Response response = client.newCall(request).execute();
```
接口参数描述：

参数名 | 类型| 解释 | 是否必须 | 示例
---|---|---|---|---
id | String | 请求唯一ID | 是 | 982739492347932
expireTime | Long | 过期时间(秒) | 是 | 30
message | String | 消息体（随意字符串) | 是 | {"name":"ston","message":"23424"}
callbackPath | String | 过期回调地址 | 是 | http://127.0.0.1:8088/test/success
currentTime | Long | 当前系统时间 | 是 | 29387492384
retryCount | Integer | 失败重试次数（默认无限重试）| 否 | 0

## 使用注意事项

- 该项目仅供学习，请勿在生产中使用
- 使用该服务需要保证回调接口的幂等性


