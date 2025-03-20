package com.weceng.cece.operator.bd;


import com.weceng.cece.operator.BaseMultiOperator;

import java.math.BigDecimal;

/**
 * <p>
 * {@link  BigDecimal} 类型乘法操作器
 * </p>
 *
 * @author WECENG
 * @since 2024/5/28 17:33
 */
public class BigDecimalMultiOperator extends BaseMultiOperator<BigDecimal> {

    /**
     * 乘法操作
     *
     * @param op1 操作数1
     * @param op2 操作数2
     * @return 结果
     */
    @Override
    public BigDecimal multi(BigDecimal op1, BigDecimal op2) {
        return BigDecimalOpFunction.multiply(op1, op2);
    }

}
