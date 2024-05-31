package com.weceng.cece.operator.bd;


import com.weceng.cece.operator.BaseAddOperator;

import java.math.BigDecimal;

/**
 * <p>
 * {@link  BigDecimal} 类型加法操作器
 * </p>
 *
 * @author chenwc@tsintergy.com
 * @since 2024/5/27 19:55
 */
public class BigDecimalAddOperator extends BaseAddOperator<BigDecimal> {

    /**
     * 加法操作
     *
     * @param op1 操作数1
     * @param op2 操作数2
     * @return 结果
     */
    @Override
    public BigDecimal add(BigDecimal op1, BigDecimal op2) {
        return BigDecimalOpFunction.add(op1, op2);
    }
}
