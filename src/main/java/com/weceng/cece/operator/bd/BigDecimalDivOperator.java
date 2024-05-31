package com.weceng.cece.operator.bd;


import com.weceng.cece.operator.BaseDivOperator;

import java.math.BigDecimal;

/**
 * <p>
 * {@link  BigDecimal} 类型除法操作器
 * </p>
 *
 * @author chenwc@tsintergy.com
 * @since 2024/5/28 17:31
 */
public class BigDecimalDivOperator extends BaseDivOperator<BigDecimal> {
    /**
     * 除法操作
     *
     * @param op1 操作数1
     * @param op2 操作数2
     * @return 结果
     */
    @Override
    public BigDecimal div(BigDecimal op1, BigDecimal op2) {
        return BigDecimalOpFunction.divide(op1, op2);
    }
}
