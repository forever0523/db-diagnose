

##接入说明

```xml
<!-- 只接入预警打印功能 -->
<dependency>
  <groupId>cn.gov.zcy</groupId>
  <artifactId>mybatis-diagnose-interceptor</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>

<!-- 接入预警打印功能+dubbo调试功能 -->
<dependency>
  <groupId>cn.gov.zcy</groupId>
  <artifactId>mybatis-diagnose-dubbo</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>

<!-- 接入预警打印功能+web调试功能 -->
<dependency>
  <groupId>cn.gov.zcy</groupId>
  <artifactId>mybatis-diagnose-web</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>

<!-- 接入预警打印功能+dubbo调试+web调试功能 -->
<dependency>
  <groupId>cn.gov.zcy</groupId>
  <artifactId>mybatis-diagnose-all</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

##配置说明

```properties
sql.threshold.execute.max.time = 500
sql.threshold.execute.max.count = 1000
sql.threshold.execute.print.sql = true
sql.threshold.execute.print.prefix = xx123
sql.threshold.execute.print.times = 10
sql.threshold.execute.print.printType = dubbo
sql.threshold.execute.print.restUrlOrDubboClassMethod = cn.gov.zcy.indenture.api.service.ContractReaderService.checkContractFinish
sql.threshold.execute.print.restOperatorId = 10007324005
sql.threshold.execute.print.paramCheck = true
sql.threshold.execute.print.expression = (arg0.orgId == 1234)
```

### SQL预警配置

- sql.threshold.execute.max.time = 500
  - 当sql执行超过500毫秒时候打印SQL
- sql.threshold.execute.max.count = 1000
  - 当sql执行返回集合数量超过1000的时候打印SQL

### SQL调试配置

- sql.threshold.execute.print.sql = tru
  - 是否开启调试状态
- sql.threshold.execute.print.prefix = xxxxx
  - 日志打印的前缀，可以用来再日志平台里关键字搜索
- sql.threshold.execute.print.times = 1
  - SQL打印的次数，默认是1次。如果要想不限制次数打印，设置成9999
- sql.threshold.execute.print.printType = dubbo
  - 需要调试的接口类型，只有两个取值（rest/dubbo)
- sql.threshold.execute.print.restUrlOrDubboClassMethod = 
  - 需要调试的接口
    - rest :  url地址
    - dubbo: 完整的类路径+方法名
- sql.threshold.execute.print.restOperatorId = 10007324005
  - 如果是rest请求调试，支持指定operatorId
- sql.threshold.execute.print.paramCheck = true
  - 是否校验接口参数
- sql.threshold.execute.print.expression = (arg0.orgId == 1234)
  - 接口参数表达式的支持
  - [表达式详细描述]([http://loveshisong.cn/%E7%BC%96%E7%A8%8B%E6%8A%80%E6%9C%AF/2016-02-24-%E8%A1%A8%E8%BE%BE%E5%BC%8F%E5%BC%95%E6%93%8Eaviator.html#%E7%AE%80%E4%BB%8B](http://loveshisong.cn/编程技术/2016-02-24-表达式引擎aviator.html#简介))



#### 表达式的特殊说明

```markdown
# 当调试的是rest接口的时候
- get请求参数会被包装成一个Map对象，可以根据key取值判断。例如 (orgId == 1234)
- post请求json类型的被转换成一个对象支持层级取值。例如(user.operatorID == 1234)
# 当调试的是dubbo请求的时候
- 参数会被包装成一个固定的前缀，规则为arg + 参数序号 。arg0,arg1,arg2...
- 例如(String name,User user)。包装后的结构如下
{
	"arg0": "jim",
	"arg1": {
			"name":"jim",
			"age":20
	}
}

- 使用的时候就需要：	
- (arg0 == "jim") 
- (arg1.name == "jim")
- ...
```



#### 参数调试的具体设置

场景一：当需要调试的是rest请求，既需要过滤operatorId，也需要过滤参数

```properties
sql.threshold.execute.print.printType = rest
sql.threshold.execute.print.restUrlOrDubboClassMethod = /api/xxxx
sql.threshold.execute.print.restOperatorId = 10007324005
# 这个必须要开启
sql.threshold.execute.print.paramCheck = true
# 这里必须要设置条件
sql.threshold.execute.print.expression = (orgId == 1234)
```

场景二：

- 当需要调试的是rest请求，只需要根据operatorId过滤的
- 当需要调试的是rest请求，是个无参数的接口，必须指定operatorId

```properties
sql.threshold.execute.print.printType = rest
sql.threshold.execute.print.restUrlOrDubboClassMethod = /api/xxxx
sql.threshold.execute.print.restOperatorId = 10007324005
# 这个关闭
sql.threshold.execute.print.paramCheck = false
# 这里无所谓设不设置值
sql.threshold.execute.print.expression = (orgId == 1234)
```

场景三：当需要调试的是rest请求，不需要根据operatorId过滤，根据条件过滤

```properties
sql.threshold.execute.print.printType = rest
sql.threshold.execute.print.restUrlOrDubboClassMethod = /api/xxxx
# 这里必须设置成-1
sql.threshold.execute.print.restOperatorId = -1
# 这个必须打开
sql.threshold.execute.print.paramCheck = false
# 这里无所谓设不设置值
sql.threshold.execute.print.expression = (orgId == 1234)
```

场景四：当需要调试的是dubbo接口，不需要根据条件过滤，或者是无参数接口时候

```properties
sql.threshold.execute.print.printType = dubbo
sql.threshold.execute.print.restUrlOrDubboClassMethod = cn.gov.zcy.vienna.ReadContract.list
# 这个关闭
sql.threshold.execute.print.paramCheck = false
# 这里无所谓设不设置值
sql.threshold.execute.print.expression = (arg0 == 1234)
```

场景五：当需要调试的是dubbo接口，需要根据条件过滤

```properties
sql.threshold.execute.print.printType = dubbo
sql.threshold.execute.print.restUrlOrDubboClassMethod = cn.gov.zcy.vienna.ReadContract.list
# 这个打开
sql.threshold.execute.print.paramCheck = true
# 这里无所谓设不设置值
sql.threshold.execute.print.expression = (arg0 == 1234)
```

