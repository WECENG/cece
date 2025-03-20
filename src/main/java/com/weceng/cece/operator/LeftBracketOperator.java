package com.weceng.cece.operator;

import cn.hutool.dfa.StopChar;

/**
 * <p>
 * 左括号
 * </p>
 *
 * @author WECENG
 * @since 2024/10/12 11:54
 */
public class LeftBracketOperator<T> implements Operator<T,T> {
    /**
     * 名称
     *
     * @return 名称
     */
    @Override
    public String name() {
        return "左括号";
    }

    /**
     * 符号
     *
     * @return 符号
     * {@link StopChar}可用
     */
    @Override
    public String symbol() {
        return String.valueOf(OperatorConstant.LEFT_BRACKET);
    }

    /**
     * 优先级
     *
     * @return 优先级
     */
    @Override
    public int precedence() {
        return Integer.MAX_VALUE;
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
        throw new UnsupportedOperationException("不支持该操作运算！");
    }
}
