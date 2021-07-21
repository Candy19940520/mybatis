package com.candy.plugin;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Statement;
import java.util.Properties;

/**
 * @author Candy
 * @create 2021-06-02 11:06
 */
//指定拦截那个对象？的那个方法？参数类型是？
@Intercepts({@Signature(type = StatementHandler.class,method = "parameterize",args = Statement.class)})
public class MyPlugin implements Interceptor {

    /**
     * 3.
     * 目标方法执行前拦截
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("intercept--------------------------------->"+invocation.getTarget());
        //通过插件改变SQL的参数:StatementHandler--->ParameterHandler--->parameterObject的参数值即可
        //首先需要拿到target元数据的值
        Object target = invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(target);
        Object value = metaObject.getValue("parameterHandler.parameterObject");
        System.out.println("预编译的参数值："+value);
        //修改SQL语句的参数
        metaObject.setValue("parameterHandler.parameterObject",1);
        //执行目标方法
        Object proceed = invocation.proceed();
        return proceed;
    }

    /**
     * 2.
     * 用来包装目标对象，创建代理对象
     */
    @Override
    public Object plugin(Object target) {
        //使用Plugin来让当前的Interceptor包装目标对象
        System.out.println("plugin--------------------->"+target);
        Object wrap = Plugin.wrap(target, this);
        //返回当前target创建的动态代理
        return wrap;
    }

    /**
     * 1.
     * 在Mybatis全局配置文件中配置plugin的时候，可以指定Properties属性内容,然后通过入参获取
     * <plugin interceptor="com.candy.plugin.MyPlugin">
     *     <property name="username" value="root"></property>
     * </plugin>
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {
        System.out.println("插件配置信息："+properties);
    }
}
