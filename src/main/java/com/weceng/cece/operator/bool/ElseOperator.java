package com.weceng.cece.operator.bool;

import cn.hutool.dfa.StopChar;

import java.util.Optional;

/**
 * <p>
 * else运算
 * </p>
 *
 * @author WECENG
 * @since 2024/10/24 15:13
 */
public class ElseOperator extends BaseElseOptionalOperator<Object, Object> {

    /**
     * 名称
     *
     * @return 名称
     */
    @Override
    public String name() {
        return "ELSE";
    }

    /**
     * 符号
     *
     * @return 符号
     * {@link StopChar}可用
     */
    @Override
    public String symbol() {
        return ":";
    }

    /**
     * 优先级
     *
     * @return 优先级
     */
    @Override
    public int precedence() {
        return -2;
    }

    /**
     * bool 条件选取
     *
     * @param op1 bool值
     * @param op2 参数
     * @return 根据bool值选取的参数
     * @implNote 结合IF, 如果第一个操作数为null，则意味着条件不成立，返回第二个操作数。否则返回第一个操作数
     * @see IfOperator#optional(Boolean, Object)
     */
    @Override
    public Object optional(Object op1, Object op2) {
        return Optional.ofNullable(op1).orElse(op2);
    }

}
