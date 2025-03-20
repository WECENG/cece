package com.weceng.cece.operator.bool.compare;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.dfa.StopChar;
import com.weceng.cece.operator.bool.BaseBoolOptionalOperator;

/**
 * <p>
 * 等于运算
 * </p>
 *
 * @author chenwc@tsintergy.com
 * @since 2024/10/23 18:31
 */
public class EqualOperator extends BaseBoolOptionalOperator<Object, Object> {

    /**
     * 名称
     *
     * @return 名称
     */
    @Override
    public String name() {
        return "等于";
    }

    /**
     * 符号
     *
     * @return 符号
     * {@link StopChar}可用
     */
    @Override
    public String symbol() {
        return "=";
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
     * bool 运算
     *
     * @param op1 操作数
     * @param op2 操作数
     * @return ture or false
     */
    @Override
    protected Boolean test(Object op1, Object op2) {
        return ObjectUtil.equals(op1, op2);
    }
}
