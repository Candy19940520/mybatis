

# Mybatis

## 一、Mybatis得全局配置文件(严格遵循以下顺序配置)

### 1.properties配置

> ***作用：Mybatis来引入外部properties的配置文件内容；***

```
<properties resource="" url=""></properties>
```

resource：引入类路径下的资源

url：引入磁盘或者网络的资源

### 2.settings配置

> ***这是 MyBatis 中极为重要的调整设置，它们会改变 MyBatis 的运行时行为。 下表描述了设置中各项设置的含义、默认值等。***

**具体设置项查看Mybatis官方文档：https://mybatis.org/mybatis-3/zh/configuration.html**

### 3.typeAliases类型别名配置

> ***类型别名可为 Java 类型设置一个缩写名字。 它仅用于 XML 配置，意在降低冗余的全限定类名书写，默认类名小写***

```
<typeAliases>
  <package name="domain.blog"/>
</typeAliases>
```

**具体配置查看Mybatis官方文档**

如果类名重复，可以使用@Alias("XXX")指定新的别名

### 4.typeHandlers自定义类型处理器配置

作用：数据库数据类型和Java类型的转换



### 5.plugins插件配置

- Executor (update, query, flushStatements, commit, rollback, getTransaction, close, isClosed)
- ParameterHandler (getParameterObject, setParameters)
- ResultSetHandler (handleResultSets, handleOutputParameters)
- StatementHandler (prepare, parameterize, batch, update, query)

### 6.environments环境配置

MyBatis 可以配置成适应多种环境，这种机制有助于将 SQL 映射应用于多种数据库之中；**不过要记住：尽管可以配置多个环境，但每个 SqlSessionFactory 实例只能选择一种环境**

```
<environments default="development">
    <environment id="development">
        <transactionManager type="JDBC"/>
        <dataSource type="POOLED">
            <property name="driver" value="${pool.driverClassName}"/>
            <property name="url" value="${pool.url}"/>
            <property name="username" value="${pool.username}"/>
            <property name="password" value="${pool.password}"/>
        </dataSource>
    </environment>
</environments>
```

如果你正在使用 Spring + MyBatis，则没有必要配置事务管理器，因为 Spring 模块会使用自带的管理器来覆盖前面的配置。数据源也是

### 7.多数据库支持databaseIdProvider

查看视频即可

### 8.mappers映射器

```
<mappers>
    <mapper resource="xml/EmployeeMapper.xml"></mapper>
</mappers>
```



## 二、MybatisXML映射文件

### 1.多个参数传递

**多个参数会被封装为一个map；并且键值对为：**

**param1-参数值**

**...**

**paramN-参数值**

**他的键默认是paramN，N为自增的数字**

- 使用命名参数，明确指定封装参数中Map的Key值：@Param("XXX")

```
Employee selectEmployee(@Param("employeeId") Integer employeeId,@Param("lastName") String lastName);
```

- 直接传入Map，因为Mybais最终还是会将参数封装为Map；直接指定键名即可；

```
public Employee getEmp(@Param("id")Integer id,String lastName);
	取值：id==>#{id/param1}   lastName==>#{param2}

public Employee getEmp(Integer id,@Param("e")Employee emp);
	取值：id==>#{param1}    lastName===>#{param2.lastName/e.lastName}

##特别注意：如果是Collection（List、Set）类型或者是数组，
		 也会特殊处理。也是把传入的list或者数组封装在map中。
			key：Collection（collection）,如果是List还可以使用这个key(list)
				数组(array)
public Employee getEmpById(List<Integer> ids);
	取值：取出第一个id的值：   #{list[0]}
```

**总结：参数多时会封装map，为了不混乱，我们可以使用@Param来指定封装时使用的key；**
**#{key}就可以取出map中的值；**

### 2.占位符${}和#{}的区别

```
#{}：可以获取map中的值或者pojo对象属性的值；
${}：可以获取map中的值或者pojo对象属性的值；


select * from tbl_employee where id=${id} and last_name=#{lastName}
Preparing: select * from tbl_employee where id=2 and last_name=?
	区别：
		#{}:是以预编译的形式，将参数设置到sql语句中；PreparedStatement；防止sql注入
		${}:取出的值直接拼装在sql语句中；会有安全问题；
		大多情况下，我们去参数的值都应该去使用#{}；
		
		原生jdbc不支持占位符的地方我们就可以使用${}进行取值
		比如分表、排序。。。；按照年份分表拆分
			select * from ${year}_salary where xxx;
			select * from tbl_employee order by ${f_name} ${order}
```

### 3.多条记录封装Map

键是主键ID(也可以是别的字段)，值是Bean对象

```
Mapper:
//键是主键ID(也可以是别的字段)，值是Bean对象
@MapKey("lastName")
Map<Integer,Employee> getEmployeeMaps();

XML:
<select id="getEmployeeMaps" resultType="java.util.Map">
	SELECT * FROM `employee`
</select>
```



### 4.使用ResultMap封装关联查询

#### 1.**对象类型的元素，结果集封装规则：association**

- 级联属性，嵌套结果集

  ```
  <resultMap id="EmployeeAndDepartmentTwo" type="com.candy.bean.Employee">
      <id column="employee_id" property="employeeId"></id>
      <result column="last_name" property="lastName"></result>
      <result column="email" property="email"></result>
      <result column="gender" property="gender"></result>
      <result column="age" property="age"></result>
  
      <!--使用association指定内联的Bean对象 -->
      <association property="department" javaType="com.candy.bean.Department">
          <id column="department_id" property="departmentId"></id>
          <id column="department_name" property="departmentName"></id>
      </association>
  </resultMap>
  ```

- 级联属性，嵌套结果集,分步查询

  ```
  <resultMap id="EmployeeAndDepartmentThree" type="com.candy.bean.Employee">
      <id column="employee_id" property="employeeId"></id>
      <result column="last_name" property="lastName"></result>
      <result column="email" property="email"></result>
      <result column="gender" property="gender"></result>
      <result column="age" property="age"></result>
  
      <!--使用association指定调用另一个Mapper中的方法封装对象 -->
      <association property="department"
                   select="com.candy.mapper.DepartmentMapper.getDepartmentById"
                   column="department_id">
      </association>
  </resultMap>
  ```

- 级联属性，嵌套结果集,分步查询开启懒加载

  ```
  <resultMap id="EmployeeAndDepartmentThree" type="com.candy.bean.Employee">
      <id column="employee_id" property="employeeId"></id>
      <result column="last_name" property="lastName"></result>
      <result column="email" property="email"></result>
      <result column="gender" property="gender"></result>
      <result column="age" property="age"></result>
  
      <!--使用association指定调用另一个Mapper中的方法封装对象 -->
      <association property="department"
                   select="com.candy.mapper.DepartmentMapper.getDepartmentById"
                   column="department_id">
      </association>
  </resultMap>
  ```

  ```
  <!-- 开启懒加载，按需加载(有个坑，打断点也算按需加载了) -->
  <setting name="lazyLoadingEnabled" value="true"/>
  <setting name="aggressiveLazyLoading" value="false"/>
  ```

#### 2.**集合类型的元素，结果集封装规则：collection**

```
<resultMap id="DepartmentMapTwo" type="com.candy.bean.Department">
        <id column="department_id" property="departmentId"></id>
        <result column="department_name" property="departmentName"></result>
        <collection property="employeeList"
                    select="com.candy.mapper.EmployeeMapper.getDepartmentIds"
                    column="{departmentId=department_id}">
        </collection>
    </resultMap>
```

```
<resultMap id="DepartmentMap" type="com.candy.bean.Department">
        <id column="department_id" property="departmentId"></id>
        <result column="department_name" property="departmentName"></result>
        <collection property="employeeList" ofType="com.candy.bean.Employee">
            <id column="employee_id" property="employeeId"></id>
            <result column="last_name" property="lastName"></result>
            <result column="email" property="email"></result>
            <result column="gender" property="gender"></result>
            <result column="age" property="age"></result>
            <association property="department"
                         select="com.candy.mapper.DepartmentMapper.getDepartmentById"
                         column="department_id"></association>
        </collection>
    </resultMap>
```

**column属性可以传递多个参数：键是方法对应的参数key**

```
column="{departmentId=department_id}"
```

#### 3.**鉴别器discriminator判断某一列的值，改变封装行为**

例如：如果age等于30岁时，关联查询部门信息

```
<resultMap id="EmployeeAndDepartmentFour" type="com.candy.bean.Employee">
    <id column="employee_id" property="employeeId"></id>
    <result column="last_name" property="lastName"></result>
    <result column="email" property="email"></result>
    <result column="gender" property="gender"></result>
    <result column="age" property="age"></result>
    <discriminator javaType="int" column="age">
        <case value="30" resultType="com.candy.bean.Employee">
            <!--使用association指定调用另一个Mapper中的方法封装对象 -->
            <association property="department"
                         select="com.candy.mapper.DepartmentMapper.getDepartmentById"
                         column="department_id">
            </association>
        </case>
    </discriminator>
</resultMap>
```

### 5.动态SQL

#### 1.choose标签

```
<select id="selectEmployeeList" resultType="com.candy.bean.Employee">
    SELECT * FROM `employee`
    <where>
        <!--
            如果带了employeeId，就使用employeeId查询；如果带了lastName，就是要lastName查询；
            只会进入一个when，拼接一个条件;
            when相当于if，otherwise相当于else
        -->
        <choose>
            <when test="employeeId != null">
                employee_id=#{employeeId}
            </when>
            <when test="lastName != null and lastName != ''">
                last_name=#{lastName}
            </when>
            <otherwise>
                gender=1
            </otherwise>
        </choose>
    </where>
</select>
```

#### 2.foreach标签

```
collection：指定要遍历的集合，list类型的参数会特殊处理，封装到map中，默认的key就是：list
item：将当前遍历出的元素赋值给指定的变量，之后使用#{变量名}取出值
separator：每个元素之间的分隔符
open：遍历出所有结果后，拼接的前缀
close：拼接的后缀
index：遍历list的时候就是索引；
       遍历map的时候，就是key，item是值value
```

```
<select id="getForeach" resultType="com.candy.bean.Employee">
    SELECT * FROM `employee` WHERE employee_id IN
    <foreach collection="employeeIds" item="employeeId" separator="," open="(" close=")" index="index">
      #{employeeId}
    </foreach>
</select>

<insert id="addEmployeeList" parameterType="com.candy.bean.Employee">
        INSERT INTO `employee`(last_name,email,gender,age,create_time,department_id)
        VALUES
        <foreach collection="employeeList" item="employee" separator=",">
            (#{employee.lastName},#{employee.email},#{employee.gender}
            ,#{employee.age},#{employee.createTime},#{employee.departmentId})
        </foreach>
    </insert>
```

#### 3.Mybatis中内置的参数_parameter

**_parameter代表的就是当前参数，无论是引用还是值**

#### 4.bind参数

**将OGNL表达式的值，绑定到一个变量中，方便后续引用；可以有拼字符串的效果**

```
<select id="selectBind" resultType="com.candy.bean.Employee">
    <bind name="_lastName" value="'%'+lastName+'%'"></bind>
    SELECT * FROM `employee`
    <if test="_parameter != null and _parameter != ''">
        WHERE last_name LIKE #{_lastName}
    </if>
</select>
```



## 三、Mybatis缓存机制

### 一级缓存(本地缓存,默认开启,存放到map中)

**概念：sqlSession级别的缓存；与数据库同一次会话期间，查询到的数据会放在本地缓存中；之后获取相同的数据，直接从缓存中获取即可;**

- 不同sqlSession会话则不会共享一级缓存
- 同一个sqlSession会话，两次相同的查询之间有增、删、改操作则一级缓存失效
- 手动清除一级缓存：sqlSession.clearCache();

### 二级缓存(全局缓存)

**一个XML的namespace对应一个二级缓存**

#### 1.开启全局二级缓存配置：

<setting name="cacheEnabled" value="true"></setting>

#### 2.在XML中配置使用二级缓存：

<cache eviction="" blocking="" size="" type="" readOnly="" flushInterval=""/>

eviction：缓存的回收策略

```
LRU – 最近最少使用：移除最长时间不被使用的对象。
FIFO – 先进先出：按对象进入缓存的顺序来移除它们。
SOFT – 软引用：基于垃圾回收器状态和软引用规则移除对象。
WEAK – 弱引用：更积极地基于垃圾收集器状态和弱引用规则移除对象。
默认是：LRU
```

size：缓存存放多少元素，**默认值是 1024**

type：自定义缓存时使用，需要指定自定义缓存的全类名；**实现Cache接口即可**

readOnly：是否只读

- true：只读；所有从缓存中获取数据的操作都只是只读操作，不会修改数据。

- false：非只读；从缓存中获取的数据可能会被修改

  **默认是false**

flushInterval：缓存刷新间隔，缓存多长时间清空一次；**默认不清空；单位毫秒**

#### 3.Bean需要实现序列化接口

```
public class Employee implements Serializable {

    private static final long serialVersionUID = 5290991031819996566L;
}
```

***查出的数据会先默认放到一级缓存中，只有当前的会话关闭后，才会移到二级缓存中***

#### 4.在XML中的select标签中指定是否使用二级缓存

```
<select id="getEmployeeAndDepartmentTwo" resultMap="EmployeeAndDepartmentThree" useCache="true">
```

**useCache="true"**

#### 5.使用flushCache标签清空一二级缓存

```
<update id="updateEmployee" parameterType="com.candy.bean.Employee" flushCache="true">
```

**默认增、删、改的标签中flushCache值默认是true**

**select标签中flushCache值默认是false**

#### 6.sqlSession.clearCache()方法

**只能清空一级缓存的数据**

#### 7.禁用一级缓存配置

```
<setting name="localCacheScope" value="STATEMENT"></setting>
```

### 整合第三方缓存ehcache

#### 1.导入jar包

```
<dependency>
    <groupId>net.sf.ehcache</groupId>
    <artifactId>ehcache-core</artifactId>
    <version>2.6.8</version>
</dependency>
<dependency>
    <groupId>org.mybatis.caches</groupId>
    <artifactId>mybatis-ehcache</artifactId>
    <version>1.2.1</version>
</dependency>
```

#### 2.再XML中引用

```
<mapper namespace="org.acme.FooMapper">
  <cache type="org.mybatis.caches.ehcache.EhcacheCache"/>
  ...
</mapper>
```

#### 3.引用ehcache.xml

# Mybatis运行原理

## 1.创建DefaultSqlSessionFactory

**解析Mybatis配置和xml配置，保存在Configuration对象，最后返回DefaultSqlSessionFactory对象;**

## 2.创建DefaultSqlSession

**主要负责创建Executor对象，来执行增、删、改操作**

1. 根据不同的ExecutorType创建不同的Executor对象(BatchExecutor,ReuseExecutor,SimpleExecutor)
2. 如果开启二级缓存，会包装Executor对象
3. 拦截器Interceptor包装Executor对象

## 3.创建Mapper的代理对象MapperProxy

**通过Mapper接口类型，创建MapperProxy代理对象，基于JDK的代理类创建**



1.通过Executor执行增、删、改操作

2.通过StatementHandler进行SQL语句预编译操作

3.在StatementHandler中通过ParameterHandler设置参数(通过TypeHandler)

4.在StatementHandler中通过ResultSetHandler封装处理结果(通过TypeHandler)

# Mybatis插件plugin开发



**Mybatis中四大对象，每一个都有Interceptor的调用**

Executor：(update, query, flushStatements, commit, rollback, getTransaction, close, isClosed) 

ParameterHandler：(getParameterObject, setParameters)

ResultSetHandler：(handleResultSets, handleOutputParameters)

StatementHandler： (prepare, parameterize, batch, update, query)

```
interceptorChain.pluginAll(parameterHandler);
```

**插件其实就是基于拦截器的原理**

## 自定义Plugin插件开发

1. 编写Interceptor的实现类

2. 使用注解@Intercepts完成插件签名

   ```
   @Intercepts({@Signature(type = StatementHandler.class,method = "parameterize",args = Statement.class)})
   ```

3. 将写好的插件注册到全局配置文件中

   ```
   <plugins>
       <plugin interceptor="com.candy.plugin.MyPlugin">
           <property name="username" value="root"></property>
       </plugin>
   </plugins>
   ```

**如果使用多个插件包装同一个对象，就会出现多层代理套用的情况**

**执行目标方法的时候，会从最外层开始执行；和声明插件倒序执行**



## 自定义类型处理器TypeHandler

- **实现接口TypeHandler即可**

  ```
  //定义当前数据如何保存到数据库中
  @Override
  public void setParameter(PreparedStatement ps, int i, EmpStatus parameter, JdbcType jdbcType) throws SQLException {
      ps.setString(i,parameter.getCode().toString());
  }
  
  //设置参数返回一个枚举对象;因为Bean封装的是一个枚举对象属性
  @Override
  public EmpStatus getResult(ResultSet rs, String columnName) throws SQLException {
      int code = rs.getInt(columnName);
      EmpStatus empStatusByCode = EmpStatus.getEmpStatusByCode(code);
      return empStatusByCode;
  }
  
  @Override
  public EmpStatus getResult(ResultSet rs, int columnIndex) throws SQLException {
      int code = rs.getInt(columnIndex);
      EmpStatus empStatusByCode = EmpStatus.getEmpStatusByCode(code);
      return empStatusByCode;
  }
  
  //存储过程
  @Override
  public EmpStatus getResult(CallableStatement cs, int columnIndex) throws SQLException {
      int code = cs.getInt(columnIndex);
      EmpStatus empStatusByCode = EmpStatus.getEmpStatusByCode(code);
      return empStatusByCode;
  }
  ```

- **全局配置文件指明自定义类型处理器**

  ```
  <typeHandlers>
      <!-- 指定自定义类型处理器 -->
      <typeHandler handler="com.candy.TypeHandler.MyTypeHandler" javaType="com.candy.bean.EmpStatus"></typeHandler>
  </typeHandlers>
  ```



