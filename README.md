[使用h2数据库](https://jingyan.baidu.com/article/c275f6ba607282e33d756784.html)

[浏览器访问h2数据库](http://localhost:8080/h2-console)

配置文件
```
spring:
  datasource:
    driverClassName: org.h2.Driver
    url: jdbc:h2:~/crawler
    username: root
    password: 123456
  jpa:
    database: MySQL
    show-sql: true
  h2:
    console:
      path: /h2-console
      enabled: true

imgPath: src/main/resources/img/
```