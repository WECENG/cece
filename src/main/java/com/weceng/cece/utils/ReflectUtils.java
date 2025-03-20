package com.weceng.cece.utils;

import cn.hutool.core.collection.CollUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * <p>
 * 反射工具类
 * </p>
 *
 * @author chenwc@tsintergy.com
 * @since 2024/12/6 12:36
 */
public class ReflectUtils {


    /**
     * 获取泛型类类型
     *
     * @param clazz 泛型类
     * @return 具体范型类
     */
    public static Class<?> getGenericsClass(Class<?> clazz) {
        if (CollUtil.isNotEmpty(Arrays.asList(clazz.getGenericInterfaces()))){
            return findActualType(clazz.getGenericInterfaces());
        }
        if (clazz.getGenericSuperclass() instanceof Class){
            return getGenericsClass((Class<?>) clazz.getGenericSuperclass());
        }
        if (clazz.getGenericSuperclass() instanceof ParameterizedType){
            Type[] typeArguments = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
            return findActualType(typeArguments);
        }
        return null;
    }

    /**
     * 获取类型
     *
     * @param typeArguments 类型数组
     * @return 真实范型类
     */
    public static Class<?> findActualType(Type[] typeArguments) {
        for (Type type : typeArguments) {
            if (type instanceof Class) {
                return (Class<?>) type;
            }
            if (type instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
                return findActualType(actualTypeArguments);
            }
        }
        return null;
    }

}
