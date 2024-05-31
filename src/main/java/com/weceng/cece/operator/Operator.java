package com.weceng.cece.operator;

import cn.hutool.core.lang.Assert;
import cn.hutool.dfa.StopChar;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <p>
 * 操作器
 * </p>
 *
 * @param <T> 操作数值类型
 * @author chenwc@tsintergy.com
 * @since 2024/5/27 10:17
 */
public interface Operator<T> {

    /**
     * 符号
     *
     * @return 符号
     * {@link StopChar}可用
     */
    String symbol();

    /**
     * 优先级
     *
     * @return 优先级
     */
    int precedence();

    /**
     * 操作数数量
     *
     * @return 数量
     */
    default int ops() {
        return 2;
    }

    /**
     * 操作
     *
     * @param op 操作数
     * @return 操作
     */
    @SuppressWarnings("all")
    T apply(T... op);

    /**
     * 操作
     *
     * @param opList 操作数
     * @return 操作
     * @implNote 数量([1, n], ...)可计算，(n,m,...)不可计算, op.size()为参数数量 op.get(idx)为参数值
     */
    @SuppressWarnings("all")
    default List<T> apply(List<List<T>> opList) {
        Integer size = opList.stream()
                .max(Comparator.comparingInt(List::size))
                .map(List::size)
                .orElseThrow(() -> new IllegalArgumentException("参数不可为空"));
        Assert.isTrue(opList.stream().allMatch(item -> item.size() == 1 || item.size() == size), "操作数长度不相等且不为1");
        return IntStream.range(0, size)
                .mapToObj(i -> {
                    T[] ops = (T[]) opList.stream().map(item -> item.size() == 1 ? item.get(0) : item.get(i)).toArray();
                    return apply(ops);
                })
                .collect(Collectors.toList());
    }


}
