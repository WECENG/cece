package com.weceng.cece.operator;


import cn.hutool.core.lang.Assert;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 加权均值操作器
 * </p>
 *
 * @author WECENG
 * @since 2024/5/28 18:04
 */
public abstract class BaseWeightAvgOperator<T> implements Operator<T,T> {

    /**
     * 名称
     *
     * @return 名称
     */
    @Override
    public String name() {
        return "加权平均";
    }

    /**
     * 符号
     *
     * @return 符号
     */
    @Override
    public String symbol() {
        return "ω";
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
        List<T> op1 = opList.get(0);
        List<T> op2 = opList.get(1);
        return Collections.singletonList(div(sum(multi(op1, op2)), sum(op2)));
    }

    /**
     * 乘法
     *
     * @param op1 操作数1
     * @param op2 操作数2
     * @return 乘法结果
     */
    public abstract List<T> multi(List<T> op1, List<T> op2);

    /**
     * 合计
     *
     * @param op 操作数
     * @return 合计
     */
    public abstract T sum(List<T> op);

    /**
     * 除法
     *
     * @param op1 操作数1
     * @param op2 操作数2
     * @return 结果
     */
    public abstract T div(T op1, T op2);


}
