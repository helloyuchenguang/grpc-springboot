```shell
# 安装 maven 插件
MAVEN_OPTS="-Dhttps.protocols=TLSv1.2" mvn clean install

# 编译(更新覆盖)proto文件
maven clean compile 
```