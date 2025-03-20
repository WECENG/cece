package com.weceng.cece.operator.bd;


import com.weceng.cece.operator.BaseAvgOperator;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * {@link  BigDecimal} 类型算术均值操作器
 * </p>
 *
 * @author WECENG
 * @since 2024/5/29 09:23
 */
public class BigDecimalAvgOperator extends BaseAvgOperator<BigDecimal> {

    /**
     * 算术均值操作
     *
     * @param op 操作数
     * @return 算术均值结果
     */
    @Override
    public BigDecimal avg(List<BigDecimal> op) {
        return BigDecimalOpFunction.listAvg(op);
    }
}
