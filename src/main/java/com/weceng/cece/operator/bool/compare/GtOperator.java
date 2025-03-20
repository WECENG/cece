package com.weceng.cece.operator.bool.compare;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.dfa.StopChar;
import com.weceng.cece.operator.bool.BaseBoolOptionalOperator;

/**
 * <p>
 * 大于运算
 * </p>
 *
 * @author chenwc@tsintergy.com
 * @since 2024/10/23 16:54
 */
public class GtOperator extends BaseBoolOptionalOperator<Object, Object> {
    /**
     * 名称
     *
     * @return 名称
     */
    @Override
    public String name() {
        return "大于";
    }

    /**
     * 符号
     *
     * @return 符号
     * {@link StopChar}可用
     */
    @Override
    public String symbol() {
        return ">";
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
        if (op1 != null && op2 != null) {
            if (op1 instanceof Number && op2 instanceof Number) {
                return NumberUtil.compare(((Number) op1).doubleValue(), ((Number) op2).doubleValue()) > 0;
            }
            if (op1 instanceof String && op2 instanceof String) {
                return CharSequenceUtil.compare((String) op1, (String) op2, true) > 0;
            }
            throw new IllegalArgumentException("参数类型异常");
        }
        return false;
    }

}
