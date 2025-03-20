package com.weceng.cece.operator;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * <p>
 * 计算器上下文
 * </p>
 *
 * @author WECENG
 * @since 2024/5/28 11:42
 */
@Getter
@Slf4j
public class OperatorContext {

    private final Map<String, Object> context = new LazyConcurrentHashMap();

    public void putIfAbsent(String key, Object value) {
        context.putIfAbsent(key, value);
    }

    public void put(String key, Object value) {
        context.put(key, value);
    }

    public void putAll(Map<String, Object> map) {
        context.putAll(map);
    }

    public void putListIfAbsent(String key, List<Object> value) {
        context.putIfAbsent(key, value);
    }

    public void putList(String key, List<?> value) {
        context.put(key, value);
    }

    public void putAllList(Map<String, List<?>> map) {
        context.putAll(map);
    }

    public void putBean(String beanName, Object bean) {
        context.put(beanName, bean);
    }

    public void putLazy(String key, Supplier<Object> function) {
        context.put(key, function);
    }

    public static class LazyConcurrentHashMap extends ConcurrentHashMap<String, Object> {
        @Override
        public Object get(Object key) {
            return super.computeIfPresent((String) key, (varKey, value) ->
                    (value instanceof Supplier) ? ((Supplier<?>) value).get() : value
            );
        }
    }

}
