package com.hh.excel.engine.common;

import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 公共util类，为了不引用其他的util类（除必须要引入的之外）
 *
 * @author huanghao
 * @date 2017年4月6日上午11:19:07
 */
public class CommonUtil {

	public static boolean isEmpty(CharSequence cs) {
		return ((cs == null) || (cs.length() == 0));
	}

	public static boolean isNotEmpty(CharSequence cs) {
		return (!(isEmpty(cs)));
	}

	public static void assertTrue(boolean flag, String msg) {
		if (!flag) {
			throw new IllegalArgumentException(msg);
		}
	}

	public static void assertArgumentNotNull(Object param, String msg) {
		CommonUtil.assertTrue(param != null, msg);
	}

	public static void assertArgumentNotEmpty(String str, String msg) {
		CommonUtil.assertTrue(CommonUtil.isNotEmpty(str), msg);
	}

	public static void throwArgument(String msg) {
		throw new IllegalArgumentException(msg);
	}

	public static void closeAutoCloseable(AutoCloseable autoCloseable) {
		if (autoCloseable != null) {
			try {
				autoCloseable.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取setter方法，不使用 beanutils包的原因是有些set方法的参数始终是String
	 *
	 * @param attrName 属性名称
	 * @param isSetter 是否返回setter名称
	 * @return
	 * @author huanghao
	 * @date 2017年4月25日下午1:08:57
	 */
	public static String getSetter(String attrName, Boolean isSetter) {
		if (CommonUtil.isEmpty(attrName)) {
			return attrName;
		}
		return (Boolean.TRUE.equals(isSetter) ? "set" : "get") + attrName.substring(0, 1).toUpperCase()
				+ attrName.substring(1);
	}

	/**
	 * 获取class实例
	 *
	 * @param className
	 * @return
	 * @author huanghao
	 * @date 2017年4月10日上午11:31:18
	 */
	public static Class<? extends Object> getClassInstance(String className) {
		Class<? extends Object> clz;
		try {
			clz = Class.forName(className);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			throw new IllegalArgumentException("没有找到类（'" + className + "'），请确认类名称是否正确");
		}
		return clz;
	}

	public static Date string2Date(String src, String pattern) throws ParseException {
		if (CommonUtil.isEmpty(src) || CommonUtil.isEmpty(pattern)) {
			throw new IllegalArgumentException("字符串或pattern不合规");
		}
		DateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.parse(src);
	}

	public static List<Integer> getIgnoreColumnIndex(String ignoreColumnIndex) {
		List<Integer> ignores = new ArrayList<>();
		if (CommonUtil.isNotEmpty(ignoreColumnIndex)) {
			for (String s : ignoreColumnIndex.split(",")) {
				ignores.add(CoreUtil.getColumnIndexFormExcelLocation(s));
			}
		}
		return ignores;
	}

	/**
	 * 如果嵌套属性为对象且其为null时，就实例化
	 *
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @author huanghao
	 * @date 2017年4月10日下午2:54:16
	 */
	public static void instanceNestPropertyIfNull(Object obj, String fieldName)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
		if (obj == null || CommonUtil.isEmpty(fieldName)) {
			throw new IllegalArgumentException("指定对象或其字段名不能为null或\"\".");
		}
		String[] fieldNames = fieldName.split("\\.");
		if (fieldNames.length > 1) {
			StringBuilder nestedProperty = new StringBuilder();
			for (int i = 0; i < fieldNames.length - 1; i++) {
				String fn = fieldNames[i];
				if (i != 0) {
					nestedProperty.append(".");
				}
				nestedProperty.append(fn);
				Object value = PropertyUtils.getProperty(obj, nestedProperty.toString());
				if (value == null) {
					PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(obj, nestedProperty.toString());
					Class<?> propertyType = propertyDescriptor.getPropertyType();
					Object newInstance = propertyType.newInstance();
					PropertyUtils.setProperty(obj, nestedProperty.toString(), newInstance);
				}
			}
		}
	}
}
