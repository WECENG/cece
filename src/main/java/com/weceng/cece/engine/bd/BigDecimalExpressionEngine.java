package com.weceng.cece.engine.bd;

import com.weceng.cece.engine.BaseExpressionEngine;
import com.weceng.cece.operator.OperatorContext;
import com.weceng.cece.operator.OperatorManager;

import java.math.BigDecimal;

/**
 * <p>
 * {@link BigDecimal }List类型计算引擎
 * </p>
 *
 * @author WECENG
 * @since 2024/5/27 16:30
 */
public class BigDecimalExpressionEngine extends BaseExpressionEngine<BigDecimal> {

    public BigDecimalExpressionEngine(OperatorManager operatorManager,
                                      OperatorContext operatorContext) {
        super(operatorManager, operatorContext);
    }

    @Override
    public BigDecimal convertToNumber(String numStr) {
        return new BigDecimal(numStr);
    }
}
