package com.hdtx.base.common.utils;

import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.List;


/** 对象转换工具
 * @param null
 * @return
 * @author xiaoLin
 * @creed: Talk is cheap,show me the code
 * @date 2021/8/20 0020 10:45
 */
public class BeanUtil {



	public static final <E, T> E from(T t, Class<E> clazz) {
		try {
			E d = clazz.newInstance();
			BeanUtils.copyProperties(t, d);
			return d;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	
	// 拷贝特定属性
	public static final <B> void copy(B f, B t, String... props) {

		if(props != null) {
			
			for(String p: props) {
				
				try {
					Field field = f.getClass().getDeclaredField(p);
					field.setAccessible(true);
					field.set(t, field.get(f));
					
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
			}
		}
		
	}
	
	// 属性拷贝忽略
	public static final <F,T> void copyIgnore(F f, T t, String... ignoreProperties) {
		
		BeanUtils.copyProperties(f, t, ignoreProperties);
		
	}
	
	
	
	public static final <E, T> E from(T t, Class<E> clazz, String... ignoreProps) {
		try {
			E d = clazz.newInstance();
			BeanUtils.copyProperties(t, d, ignoreProps);
			return d;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T, E> List<E> fromTb(List<T> l, Class<E> clazz) {
		if (l == null) {
			return null;
		}
		List<E> ret = Lists.newArrayList();
		for (T t : l) {
			ret.add((E) from(t, clazz));
		}
		return ret;
	}
	

}
