package com.weceng.cece.operator;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 计算器上下文
 * </p>
 *
 * @author chenwc@tsintergy.com
 * @since 2024/5/28 11:42
 */
@Getter
public class OperatorContext<T extends Number> {

    private final Map<String, Object> context = new ConcurrentHashMap<>();

    public void putIfAbsent(String key, T value) {
        context.putIfAbsent(key, value);
    }

    public void put(String key, T value) {
        context.put(key, value);
    }

    public void putAll(Map<String, T> map) {
        context.putAll(map);
    }

    public void putListIfAbsent(String key, List<T> value) {
        context.putIfAbsent(key, value);
    }

    public void putList(String key, List<T> value) {
        context.put(key, value);
    }

    public void putAllList(Map<String, List<T>> map) {
        context.putAll(map);
    }

    public void putBean(String beanName, Object bean) {
        context.put(beanName, bean);
    }

}
