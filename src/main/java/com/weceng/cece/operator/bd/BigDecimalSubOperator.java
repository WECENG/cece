package com.weceng.cece.operator.bd;


import com.weceng.cece.operator.BaseSubOperator;

import java.math.BigDecimal;

/**
 * <p>
 * {@link  BigDecimal} 类型减法操作器
 * </p>
 *
 * @author chenwc@tsintergy.com
 * @since 2024/5/28 15:07
 */
public class BigDecimalSubOperator extends BaseSubOperator<BigDecimal> {

    /**
     * 减法操作
     *
     * @param op1 操作数1
     * @param op2 操作数2
     * @return 结果
     */
    @Override
    public BigDecimal sub(BigDecimal op1, BigDecimal op2) {
        return BigDecimalOpFunction.subtract(op1, op2);
    }
}
