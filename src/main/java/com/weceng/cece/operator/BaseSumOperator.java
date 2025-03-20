package com.weceng.cece.operator;


import cn.hutool.core.lang.Assert;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 合计操作器
 * </p>
 *
 * @author WECENG
 * @since 2024/5/28 17:42
 */
public abstract class BaseSumOperator<T> implements Operator<T,T> {

    /**
     * 名称
     *
     * @return 名称
     */
    @Override
    public String name() {
        return "合计";
    }

    /**
     * 符号
     *
     * @return 符号
     */
    @Override
    public String symbol() {
        return "∑";
    }

    /**
     * 优先级
     *
     * @return 优先级
     */
    @Override
    public int precedence() {
        return 3;
    }

    /**
     * 操作数数量
     *
     * @return 数量
     */
    @Override
    public int ops() {
        return 1;
    }

    /**
     * 操作
     *
     * @param op 操作数
     * @return 操作
     */
    @SafeVarargs
    @Override
    public final T apply(T... op) {
        Assert.isTrue(op.length == ops(), "操作数不符");
        return op[0];
    }

    /**
     * 操作
     *
     * @param opList 操作数
     * @return 操作
     * @implNote 数量([1, n], ...)可计算，(n,m,...)不可计算, op.size()为参数数量 op.get(idx)为参数值
     */
    @Override
    public List<T> apply(List<List<T>> opList) {
        Assert.isTrue(opList.size() == ops(), "操作数不符");
        return Collections.singletonList(sum(opList.get(0)));
    }

    /**
     * 合计操作
     *
     * @param op 操作数
     * @return 合计结果
     */
    public abstract T sum(List<T> op);
}
