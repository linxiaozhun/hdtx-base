package com.hdtx.base.common.spring.ds;

import java.lang.annotation.*;


/**
 * @Author liubin
 * @Date 2017/7/13 17:04
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface DataSource {

	/**
	 * 1、对于接口里的方法自定义注解AOP是失效的
	 * 2、加了@Override或重载的方法也同样AOP失效
	 * 3、解决方案特定的方法可以使用手动代码的方式即：DataSourceHolder.putDataSource(datasourceKey);
	/**
	 * data Source key
	 * @return
	 */
	String value();


}
