package com.weceng.cece.operator.bd;


import com.weceng.cece.operator.BaseWeightAvgOperator;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * {@link  BigDecimal} 类型加权均值操作器
 * </p>
 *
 * @author chenwc@tsintergy.com
 * @since 2024/5/29 09:24
 */
public class BigDecimalWeightAvgOperator extends BaseWeightAvgOperator<BigDecimal> {

    /**
     * 乘法
     *
     * @param op1 操作数1
     * @param op2 操作数2
     * @return 乘法结果
     */
    @Override
    public List<BigDecimal> multi(List<BigDecimal> op1, List<BigDecimal> op2) {
        return BigDecimalOpFunction.listMultiply(op1, op2);
    }

    /**
     * 合计
     *
     * @param op 操作数
     * @return 合计
     */
    @Override
    public BigDecimal sum(List<BigDecimal> op) {
        return BigDecimalOpFunction.listSum(op);
    }

    /**
     * 除法
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
