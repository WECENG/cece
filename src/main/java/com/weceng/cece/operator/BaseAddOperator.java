package com.weceng.cece.operator;


import cn.hutool.core.lang.Assert;

/**
 * <p>
 * 加法操作器
 * </p>
 *
 * @author chenwc@tsintergy.com
 * @since 2024/5/27 10:33
 */
public abstract class BaseAddOperator<T> implements Operator<T> {

    @Override
    public String symbol() {
        return "+";
    }


    @Override
    public int precedence() {
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
        T op1 = op[0];
        T op2 = op[1];
        return add(op1, op2);
    }

    /**
     * 加法操作
     *
     * @param op1 操作数1
     * @param op2 操作数2
     * @return 结果
     */
    public abstract T add(T op1, T op2);
}
