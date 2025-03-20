package com.weceng.cece.operator.bool.compare;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.dfa.StopChar;
import com.weceng.cece.operator.bool.BaseBoolOptionalOperator;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 包含于运算
 * </p>
 *
 * @author chenwc@tsintergy.com
 * @since 2025/1/9 15:46
 */
public class InOperator extends BaseBoolOptionalOperator<Object, Object> {

    /**
     * 名称
     *
     * @return 名称
     */
    @Override
    public String name() {
        return "包含于";
    }

    /**
     * 符号
     *
     * @return 符号
     * {@link StopChar}可用
     */
    @Override
    public String symbol() {
        return "∈";
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
        @SuppressWarnings("unchecked")
        List<Object> opList = op2 instanceof List ? (List<Object>) op2 : Collections.singletonList(op2);
        return opList.stream().anyMatch(o2 -> ObjectUtil.equals(op1, o2));
    }
}
