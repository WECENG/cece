package com.weceng.cece.operator.bool;

import cn.hutool.dfa.StopChar;

/**
 * <p>
 * if运算
 * </p>
 *
 * @author WECENG
 * @since 2024/10/24 15:11
 */
public class IfOperator extends BaseIfOptionalOperator<Object, Object> {

    /**
     * 名称
     *
     * @return 名称
     */
    @Override
    public String name() {
        return "IF";
    }

    /**
     * 符号
     *
     * @return 符号
     * {@link StopChar}可用
     */
    @Override
    public String symbol() {
        return "?";
    }

    /**
     * 优先级
     *
     * @return 优先级
     */
    @Override
    public int precedence() {
        return -1;
    }

    /**
     * bool 条件选取
     *
     * @param op1 bool值
     * @param op2 参数
     * @return 根据bool值选取的参数
     * @implNote  满足条件返回IF后的结果，否则返回null(配合ELSE后续处理）
     * //todo 如果原本为true对应的值为null，则会被else的值覆盖。
     */
    @Override
    public Object optional(Boolean op1, Object op2) {
        return Boolean.TRUE.equals(op1) ? op2 : null;
    }
}
