package com.weceng.cece.operator;


import cn.hutool.core.lang.Assert;

/**
 * <p>
 * 乘法操作器
 * </p>
 *
 * @author chenwc@tsintergy.com
 * @since 2024/5/28 15:34
 */
public abstract class BaseMultiOperator<T> implements Operator<T> {

    /**
     * 符号
     *
     * @return 符号
     */
    @Override
    public String symbol() {
        return "*";
    }

    /**
     * 优先级
     *
     * @return 优先级
     */
    @Override
    public int precedence() {
        return 2;
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
        return multi(op1, op2);
    }

    /**
     * 乘法操作
     *
     * @param op1 操作数1
     * @param op2 操作数2
     * @return 结果
     */
    public abstract T multi(T op1, T op2);

}
