package com.weceng.cece.engine;

import cn.hutool.core.map.MapUtil;
import com.weceng.cece.operator.OperatorContext;
import com.weceng.cece.operator.OperatorManager;
import com.weceng.cece.operator.bd.BigDecimalOpFunction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@DisplayName("单元测试-类型计算引擎")
class BaseExpressionEngineTest {

    @Test
    @SuppressWarnings("all")
    @DisplayName("属性获取")
    void handleNestedProperty() {
        OperatorContext operatorContext = new OperatorContext();
        BaseExpressionEngine expressionEngine = new BaseExpressionEngine(new OperatorManager(BigDecimal.class), operatorContext) {
            /**
             * 数值转换
             *
             * @param numStr 数值字串
             * @return 数值
             */
            @Override
            public BigDecimal convertToNumber(String numStr) {
                return new BigDecimal(numStr);
            }
        };
        operatorContext.putBean("list", Arrays.asList(
                PropertyBean.builder().number(BigDecimal.ONE).build(),
                PropertyBean.builder().number(BigDecimal.TEN).build()
        ));
        LinkedList<Object> values = new LinkedList<>();
        operatorContext.putBean("map", MapUtil.builder().put("key1", BigDecimal.ZERO).put("key2", BigDecimal.ONE).build());
        VarMeta varMeta = VarMeta.original("map.key1");
        Object value = expressionEngine.extractValue(operatorContext.getContext(), varMeta, ((o, s) -> true));
        assertEquals(BigDecimal.ZERO, value);
        LinkedList<Object> listValue = new LinkedList<>();
        VarMeta varMeta1 = VarMeta.original("list.number");
        Object value1 = expressionEngine.extractValue(operatorContext.getContext(), varMeta1, ((o, s) -> true));
        assertIterableEquals(Arrays.asList(BigDecimal.ONE, BigDecimal.TEN), (List) value1);
    }

    @Test
    @DisplayName("多维数组平铺展开")
    void flatten() {
        OperatorContext operatorContext = new OperatorContext();
        BaseExpressionEngine<BigDecimal> expressionEngine = new BaseExpressionEngine<>(new OperatorManager(BigDecimal.class), operatorContext) {
            /**
             * 数值转换
             *
             * @param numStr 数值字串
             * @return 数值
             */
            @Override
            public BigDecimal convertToNumber(String numStr) {
                return new BigDecimal(numStr);
            }
        };
        List<List<BigDecimal>> arg1 = new ArrayList<>();
        arg1.add(null);
        arg1.add(BigDecimalOpFunction.createList(BigDecimal.ONE, 10));
        List<List<BigDecimal>> arg2 = new ArrayList<>();
        arg2.add(BigDecimalOpFunction.createList(BigDecimal.ONE, 5));
        arg2.add(BigDecimalOpFunction.createList(BigDecimal.TEN, 10));
        Object[] array = new Object[]{arg1, arg2};
        List<?> arg1Flatten = expressionEngine.flatten(array, arg1);
        List<?> arg2Flatten = expressionEngine.flatten(array, arg2);
        assertEquals(15, arg1Flatten.size());
        assertEquals(15, arg2Flatten.size());
        assertIterableEquals(null, (List<?>) arg1Flatten.get(0));

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PropertyBean {
        private BigDecimal number;
    }
}