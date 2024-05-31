package com.weceng.cece.engine.bd;

import com.weceng.cece.engine.BaseExpressionEngine;
import com.weceng.cece.operator.OperatorContext;
import com.weceng.cece.operator.OperatorManager;
import com.weceng.cece.operator.bd.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("单元测试-BigDecimal类型计算引擎")
class BigDecimalExpressionEngineTest {

    private BaseExpressionEngine<BigDecimal> expressionEngine;

    @BeforeEach
    void init() {
        OperatorManager<BigDecimal> operatorManager = new OperatorManager<>();
        operatorManager.register(new BigDecimalAddOperator());
        operatorManager.register(new BigDecimalSubOperator());
        operatorManager.register(new BigDecimalMultiOperator());
        operatorManager.register(new BigDecimalDivOperator());
        operatorManager.register(new BigDecimalSumOperator());
        operatorManager.register(new BigDecimalAvgOperator());
        operatorManager.register(new BigDecimalWeightAvgOperator());
        OperatorContext<BigDecimal> operatorContext = new OperatorContext<>();
        operatorContext.putList("ele", IntStream.range(0, 96).mapToObj(i -> new BigDecimal(i + 1)).collect(Collectors.toList()));
        operatorContext.putListIfAbsent("price", IntStream.range(0, 96).mapToObj(i -> new BigDecimal(i + 100)).collect(Collectors.toList()));
        operatorContext.putList("fee", IntStream.range(0, 96).mapToObj(i -> new BigDecimal(1000)).collect(Collectors.toList()));
        operatorContext.put("cost", BigDecimal.valueOf(20));
        operatorContext.putIfAbsent("income", BigDecimal.valueOf(30));
        operatorContext.putList("dyCost", IntStream.range(0, 96).mapToObj(i -> new BigDecimal(100 - i)).collect(Collectors.toList()));
        expressionEngine = new BigDecimalExpressionEngine(operatorManager, operatorContext);
    }

    @Test
    @SuppressWarnings("all")
    @DisplayName("加法")
    void addTest() {
        String express = " price + cost ";
        List<BigDecimal> result = expressionEngine.evaluates(express);
        List<BigDecimal> expected = BigDecimalOpFunction.listAddValue((List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("price"),
                (BigDecimal) expressionEngine.getOperatorContext().getContext().get("cost"));
        assertIterableEquals(expected, result);
        express = "cost + income";
        BigDecimal evaluated = expressionEngine.evaluate(express);
        assertEquals(new BigDecimal(50), evaluated);
    }

    @Test
    @SuppressWarnings("all")
    @DisplayName("减法")
    void subTest() {
        String express = " price - dyCost ";
        List<BigDecimal> result = expressionEngine.evaluates(express);
        List<BigDecimal> expected = BigDecimalOpFunction.listSubtract((List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("price"),
                (List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("dyCost"));
        assertIterableEquals(expected, result);
    }

    @Test
    @SuppressWarnings("all")
    @DisplayName("乘法")
    void multiplyTest() {
        String express = " ele * price ";
        List<BigDecimal> result = expressionEngine.evaluates(express);
        List<BigDecimal> expected = BigDecimalOpFunction.listMultiply((List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("ele"),
                (List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("price"));
        assertIterableEquals(expected, result);
    }

    @Test
    @SuppressWarnings("all")
    @DisplayName("除法")
    void divideTest() {
        String express = " fee / price ";
        List<BigDecimal> result = expressionEngine.evaluates(express);
        List<BigDecimal> expected = BigDecimalOpFunction.listDivide((List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("fee"),
                (List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("price"));
        assertIterableEquals(expected, result);
    }


    @Test
    @SuppressWarnings("all")
    @DisplayName("合计")
    void sumTest() {
        String express = " ∑(ele) ";
        String lexpress = " ∑ ele ";
        String rexpress = " ele ∑ ";
        BigDecimal result = expressionEngine.evaluate(express);
        BigDecimal lresult = expressionEngine.evaluate(lexpress);
        BigDecimal rresult = expressionEngine.evaluate(rexpress);
        BigDecimal expected = BigDecimalOpFunction.listSum((List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("ele"));
        assertEquals(expected, result);
        assertEquals(expected, lresult);
        assertEquals(expected, rresult);
    }

    @Test
    @SuppressWarnings("all")
    @DisplayName("平均")
    void avgTest() {
        String express = " μ(price) ";
        String lexpress = " μ price ";
        String rexpress = " price μ ";
        BigDecimal result = expressionEngine.evaluate(express);
        BigDecimal lresult = expressionEngine.evaluate(lexpress);
        BigDecimal rresult = expressionEngine.evaluate(rexpress);
        BigDecimal expected = BigDecimalOpFunction.listAvg((List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("price"));
        assertEquals(expected, result);
        assertEquals(expected, lresult);
        assertEquals(expected, rresult);
    }

    @Test
    @SuppressWarnings("all")
    @DisplayName("加权平均")
    void weightAvgTest() {
        String express = " (ele ω price) ";
        String lexpress = " ele ω price ";
        BigDecimal result = expressionEngine.evaluate(express);
        BigDecimal lresult = expressionEngine.evaluate(lexpress);
        List<BigDecimal> eleList = (List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("ele");
        List<BigDecimal> priceList = (List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("price");
        BigDecimal expected = BigDecimalOpFunction.divide(BigDecimalOpFunction.listSum(BigDecimalOpFunction.listMultiply(eleList, priceList)), BigDecimalOpFunction.listSum(priceList));
        assertEquals(expected, result);
        assertEquals(expected, lresult);
    }

    @Test
    @SuppressWarnings("all")
    @DisplayName("复杂表达式")
    void complexExpressionTest() {
        String express = " (ele ω price) * ele - cost + fee/price";
        List<BigDecimal> result = expressionEngine.evaluates(express);
        List<BigDecimal> eleList = (List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("ele");
        List<BigDecimal> priceList = (List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("price");
        BigDecimal weightAvg = BigDecimalOpFunction.divide(BigDecimalOpFunction.listSum(BigDecimalOpFunction.listMultiply(eleList, priceList)), BigDecimalOpFunction.listSum(priceList));
        List<BigDecimal> weightAvg_ele = BigDecimalOpFunction.listMultiplyValue(eleList, weightAvg);
        BigDecimal cost = (BigDecimal) expressionEngine.getOperatorContext().getContext().get("cost");
        List<BigDecimal> weightAvg_ele_cost = BigDecimalOpFunction.listSubtractValue(weightAvg_ele, cost);
        List<BigDecimal> fee_price = BigDecimalOpFunction.listDivide((List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("fee"),
                (List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("price"));
        List<BigDecimal> expected = BigDecimalOpFunction.listAdd(weightAvg_ele_cost, fee_price);
        assertIterableEquals(expected, result);

    }

    @Test
    @DisplayName("bean属性数据计算")
    void nestedPropertyTest() {
        List<BigDecimal> eleList = IntStream.range(0, 96).mapToObj(i -> new BigDecimal(i + 1)).collect(Collectors.toList());
        List<BigDecimal> priceList = IntStream.range(0, 96).mapToObj(i -> new BigDecimal(100 + i)).collect(Collectors.toList());
        BigDecimal cost = BigDecimal.valueOf(100);
        DataBean data = DataBean.builder()
                .ep(DataBean.DataEP.builder()
                        .ele(eleList)
                        .price(priceList)
                        .build())
                .cost(cost)
                .build();
        expressionEngine.getOperatorContext().putBean("data", data);
        BigDecimal evaluates = expressionEngine.evaluate("∑(data.ep.ele * data.ep.price - data.cost)");
        BigDecimal expected = BigDecimalOpFunction.listSum(BigDecimalOpFunction.listSubtractValue(BigDecimalOpFunction.listMultiply(eleList, priceList), cost));
        assertEquals(expected, evaluates);
    }

    @Test
    @DisplayName("异常数据类型")
    void errorTypeTest() {
        assertThrows(RuntimeException.class, () -> expressionEngine.evaluate("data.other + data.cost"));
        List<BigDecimal> eleList = IntStream.range(0, 96).mapToObj(i -> new BigDecimal(i + 1)).collect(Collectors.toList());
        List<BigDecimal> priceList = IntStream.range(0, 96).mapToObj(i -> new BigDecimal(100 + i)).collect(Collectors.toList());
        BigDecimal cost = BigDecimal.valueOf(100);
        DataBean data = DataBean.builder()
                .ep(DataBean.DataEP.builder()
                        .ele(eleList)
                        .price(priceList)
                        .build())
                .cost(cost)
                .other("other")
                .build();
        expressionEngine.getOperatorContext().putBean("data", data);
        assertThrows(RuntimeException.class, () -> expressionEngine.evaluate("data.other + data.cost"));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DataBean {

        private DataEP ep;

        private BigDecimal cost;

        private String other;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class DataEP {

            private List<BigDecimal> ele;

            private List<BigDecimal> price;
        }
    }


}