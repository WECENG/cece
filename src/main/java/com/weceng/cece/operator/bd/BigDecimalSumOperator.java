package com.weceng.cece.operator.bd;


import com.weceng.cece.operator.BaseSumOperator;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * {@link  BigDecimal} 类型合计操作器
 * </p>
 *
 * @author WECENG
 * @since 2024/5/29 09:20
 */
public class BigDecimalSumOperator extends BaseSumOperator<BigDecimal> {

    /**
     * 合计操作
     *
     * @param op 操作数
     * @return 合计结果
     */
    @Override
    public BigDecimal sum(List<BigDecimal> op) {
        return BigDecimalOpFunction.listSum(op);
    }
}
