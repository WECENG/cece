package com.weceng.cece.engine.bd;

import com.weceng.cece.engine.BaseExpressionEngine;
import com.weceng.cece.operator.OperatorContext;
import com.weceng.cece.operator.OperatorManager;
import com.weceng.cece.operator.bd.BigDecimalOpFunction;
import com.weceng.cece.operator.bd.BigDecimalSumOperator;
import com.weceng.cece.operator.bd.BigDecimalWeightAvgOperator;
import com.weceng.cece.operator.bool.ElseOperator;
import com.weceng.cece.operator.bool.IfOperator;
import com.weceng.cece.operator.bool.compare.*;
import com.weceng.cece.operator.bool.logic.AndOperator;
import com.weceng.cece.operator.bool.logic.NotOperator;
import com.weceng.cece.operator.bool.logic.OrOperator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("单元测试-BigDecimal类型计算引擎")
class BigDecimalExpressionEngineTest {

    private BaseExpressionEngine<BigDecimal> expressionEngine;

    @BeforeEach
    void init() {
        OperatorManager operatorManager = new OperatorManager(BigDecimal.class);
        operatorManager.register(new BigDecimalSumOperator());
        operatorManager.register(new BigDecimalWeightAvgOperator());
        OperatorContext operatorContext = new OperatorContext();
        operatorContext.putList("ele", IntStream.range(0, 96).mapToObj(i -> new BigDecimal(i + 1)).collect(Collectors.toList()));
        operatorContext.putListIfAbsent("price", IntStream.range(0, 96).mapToObj(i -> new BigDecimal(i + 100)).collect(Collectors.toList()));
        operatorContext.putList("fee", BigDecimalOpFunction.createList(BigDecimal.valueOf(1000), 96));
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
        List<BigDecimal> expected = BigDecimalOpFunction.listAddValue((List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("price"), (BigDecimal) expressionEngine.getOperatorContext().getContext().get("cost"));
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
        List<BigDecimal> expected = BigDecimalOpFunction.listSubtract((List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("price"), (List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("dyCost"));
        assertIterableEquals(expected, result);
    }

    @Test
    @SuppressWarnings("all")
    @DisplayName("乘法")
    void multiplyTest() {
        String express = " ele * price ";
        List<BigDecimal> result = expressionEngine.evaluates(express);
        List<BigDecimal> expected = BigDecimalOpFunction.listMultiply((List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("ele"), (List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("price"));
        assertIterableEquals(expected, result);
    }

    @Test
    @SuppressWarnings("all")
    @DisplayName("除法")
    void divideTest() {
        String express = " fee / price ";
        List<BigDecimal> result = expressionEngine.evaluates(express);
        List<BigDecimal> expected = BigDecimalOpFunction.listDivide((List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("fee"), (List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("price"));
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
        List<BigDecimal> fee_price = BigDecimalOpFunction.listDivide((List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("fee"), (List<BigDecimal>) expressionEngine.getOperatorContext().getContext().get("price"));
        List<BigDecimal> expected = BigDecimalOpFunction.listAdd(weightAvg_ele_cost, fee_price);
        assertIterableEquals(expected, result);

    }

    @Test
    @DisplayName("验证表达式")
    void verifyExpressionTest() {
        String correctExpress = " (ele ω price) * ele - cost + fee/price";
        String errorExpress = "(* price) * ele - cost + fee/price";
        String errorExpress2 = "(ele ω ) * ele - cost + fee/price";
        String errorExpress3 = "(ele * ) * ele - cost + ";
        boolean verify = expressionEngine.verify(correctExpress);
        Assertions.assertTrue(verify);
        Assertions.assertThrows(Exception.class, () -> expressionEngine.verify(errorExpress));
        Assertions.assertThrows(Exception.class, () -> expressionEngine.verify(errorExpress2));
        Assertions.assertThrows(Exception.class, () -> expressionEngine.verify(errorExpress3));
    }

    @Test
    @DisplayName("bean属性数据计算")
    void nestedPropertyTest() {
        List<BigDecimal> eleList = IntStream.range(0, 96).mapToObj(i -> new BigDecimal(i + 1)).collect(Collectors.toList());
        List<BigDecimal> priceList = IntStream.range(0, 96).mapToObj(i -> new BigDecimal(100 + i)).collect(Collectors.toList());
        BigDecimal cost = BigDecimal.valueOf(100);
        DataBean data = DataBean.builder().ep(DataBean.DataEP.builder().ele(eleList).price(priceList).build()).cost(cost).build();
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
        DataBean data = DataBean.builder().ep(DataBean.DataEP.builder().ele(eleList).price(priceList).build()).cost(cost).other("other").build();
        expressionEngine.getOperatorContext().putBean("data", data);
        assertThrows(RuntimeException.class, () -> expressionEngine.evaluate("data.other + data.cost"));
    }

    @Test
    @DisplayName("等于")
    void equalTest() {
        String express = "type = type";
        expressionEngine.getOperatorManager().register(new EqualOperator());
        expressionEngine.getOperatorContext().put("type", "fire");
        Assertions.assertTrue(expressionEngine.verify(express));
        boolean condition = expressionEngine.evaluate(express);
        Assertions.assertTrue(condition);
    }

    @Test
    @DisplayName("大于等于")
    void geTest() {
        expressionEngine.getOperatorManager().register(new GeOperator());
        String express1 = "1 ≥ 2";
        String express2 = "1 ≥ 1";
        String express3 = "2 ≥ 1";
        String express4 = "\"aa\" ≥ \"a\"";
        String express5 = "'b' ≥ 'a'";
        Assertions.assertFalse(expressionEngine.<Boolean>evaluate(express1));
        Assertions.assertTrue(expressionEngine.<Boolean>evaluate(express2));
        Assertions.assertTrue(expressionEngine.<Boolean>evaluate(express3));
        Assertions.assertTrue(expressionEngine.<Boolean>evaluate(express4));
        Assertions.assertTrue(expressionEngine.<Boolean>evaluate(express5));
    }

    @Test
    @DisplayName("大于")
    void gtTest() {
        expressionEngine.getOperatorManager().register(new GtOperator());
        String express1 = "1 > 2";
        String express2 = "1 > 1";
        String express3 = "2 > 1";
        String express4 = "\"aa\" > \"a\"";
        String express5 = "'b' > 'a'";
        Assertions.assertFalse(expressionEngine.<Boolean>evaluate(express1));
        Assertions.assertFalse(expressionEngine.<Boolean>evaluate(express2));
        Assertions.assertTrue(expressionEngine.<Boolean>evaluate(express3));
        Assertions.assertTrue(expressionEngine.<Boolean>evaluate(express4));
        Assertions.assertTrue(expressionEngine.<Boolean>evaluate(express5));
    }

    @Test
    @DisplayName("小于等于")
    void leTest() {
        expressionEngine.getOperatorManager().register(new LeOperator());
        String express1 = "1 ≤ 2";
        String express2 = "1 ≤ 1";
        String express3 = "2 ≤ 1";
        String express4 = "\"aa\" ≤ \"a\"";
        String express5 = "'b' ≤ 'a'";
        Assertions.assertTrue(expressionEngine.<Boolean>evaluate(express1));
        Assertions.assertTrue(expressionEngine.<Boolean>evaluate(express2));
        Assertions.assertFalse(expressionEngine.<Boolean>evaluate(express3));
        Assertions.assertFalse(expressionEngine.<Boolean>evaluate(express4));
        Assertions.assertFalse(expressionEngine.<Boolean>evaluate(express5));
    }

    @Test
    @DisplayName("小于")
    void ltTest() {
        expressionEngine.getOperatorManager().register(new LtOperator());
        String express1 = "1 < 2";
        String express2 = "1 < 1";
        String express3 = "2 < 1";
        String express4 = "\"aa\" < \"a\"";
        String express5 = "'b' < 'a'";
        Assertions.assertTrue(expressionEngine.<Boolean>evaluate(express1));
        Assertions.assertFalse(expressionEngine.<Boolean>evaluate(express2));
        Assertions.assertFalse(expressionEngine.<Boolean>evaluate(express3));
        Assertions.assertFalse(expressionEngine.<Boolean>evaluate(express4));
        Assertions.assertFalse(expressionEngine.<Boolean>evaluate(express5));
    }

    @Test
    @DisplayName("包含于")
    void inTest() {
        expressionEngine.getOperatorManager().register(new InOperator());
        String express1 = "a ∈ a";
        String express2 = "a ∈ b";
        String express3 = "a ∈ list";
        expressionEngine.getOperatorContext().getContext().put("a", "a");
        expressionEngine.getOperatorContext().getContext().put("list", Arrays.asList(Arrays.asList("a", "b"), Collections.singletonList("c")));
        Assertions.assertEquals(true, expressionEngine.<Boolean>evaluate(express1));
        Assertions.assertEquals(false, expressionEngine.<Boolean>evaluate(express2));
        Assertions.assertIterableEquals(Arrays.asList(true, false), expressionEngine.<Boolean>evaluates(express3));
    }

    @Test
    @DisplayName("与")
    void andTest() {
        expressionEngine.getOperatorManager().register(new GeOperator());
        expressionEngine.getOperatorManager().register(new GtOperator());
        expressionEngine.getOperatorManager().register(new LeOperator());
        expressionEngine.getOperatorManager().register(new LtOperator());
        expressionEngine.getOperatorManager().register(new EqualOperator());
        expressionEngine.getOperatorManager().register(new AndOperator());
        String express1 = "2 > 1 & 1 ≥ 2";
        String express2 = "1 ≥ 1 & 2 > 1";
        String express3 = "\"aa\" ≥ \"a\" & \"b\" ≥ \"a\"";
        Assertions.assertFalse(expressionEngine.<Boolean>evaluate(express1));
        Assertions.assertTrue(expressionEngine.<Boolean>evaluate(express2));
        Assertions.assertTrue(expressionEngine.<Boolean>evaluate(express3));
    }

    @Test
    @DisplayName("或")
    void orTest() {
        expressionEngine.getOperatorManager().register(new GeOperator());
        expressionEngine.getOperatorManager().register(new GtOperator());
        expressionEngine.getOperatorManager().register(new LeOperator());
        expressionEngine.getOperatorManager().register(new LtOperator());
        expressionEngine.getOperatorManager().register(new EqualOperator());
        expressionEngine.getOperatorManager().register(new OrOperator());
        String express1 = "2 > 1 | 1 ≥ 2";
        String express2 = "1 > 1 | 2 < 1";
        String express3 = "\"aa\" ≥ \"a\" | \"b\" ≥ \"a\"";
        Assertions.assertTrue(expressionEngine.<Boolean>evaluate(express1));
        Assertions.assertFalse(expressionEngine.<Boolean>evaluate(express2));
        Assertions.assertTrue(expressionEngine.<Boolean>evaluate(express3));
    }

    @Test
    @DisplayName("非")
    void notTest() {
        expressionEngine.getOperatorManager().register(new GeOperator());
        expressionEngine.getOperatorManager().register(new GtOperator());
        expressionEngine.getOperatorManager().register(new LeOperator());
        expressionEngine.getOperatorManager().register(new LtOperator());
        expressionEngine.getOperatorManager().register(new EqualOperator());
        expressionEngine.getOperatorManager().register(new NotOperator());
        expressionEngine.getOperatorManager().register(new AndOperator());
        expressionEngine.getOperatorManager().register(new OrOperator());
        String express1 = "!(2 > 1)";
        String express2 = "!(1 > 1) & !(2 < 1)";
        String express3 = "!(\"aa\" ≥ \"a\")";
        Assertions.assertFalse(expressionEngine.<Boolean>evaluate(express1));
        Assertions.assertTrue(expressionEngine.<Boolean>evaluate(express2));
        Assertions.assertFalse(expressionEngine.<Boolean>evaluate(express3));
    }

    @Test
    @DisplayName("if else")
    void ifElseTest() {
        expressionEngine.getOperatorManager().register(new GeOperator());
        expressionEngine.getOperatorManager().register(new GtOperator());
        expressionEngine.getOperatorManager().register(new LeOperator());
        expressionEngine.getOperatorManager().register(new LtOperator());
        expressionEngine.getOperatorManager().register(new EqualOperator());
        expressionEngine.getOperatorManager().register(new NotOperator());
        expressionEngine.getOperatorManager().register(new AndOperator());
        expressionEngine.getOperatorManager().register(new OrOperator());
        expressionEngine.getOperatorManager().register(new IfOperator());
        expressionEngine.getOperatorManager().register(new ElseOperator());
        String express = " 2 > 1 ? 1 + 1 : 0 + 0 ";
        String express1 = " 'b' > 'a' & 1 > 2 ? 1 : -1 ";
        Assertions.assertEquals(BigDecimal.valueOf(2), expressionEngine.<BigDecimal>evaluate(express));
        Assertions.assertEquals(BigDecimal.valueOf(-1), expressionEngine.<BigDecimal>evaluate(express1));
        // list
        List<String> list1 = Arrays.asList("a", "b", "c");
        List<String> list2 = Arrays.asList("c", "b", "a");
        expressionEngine.getOperatorContext().getContext().put("lA", list1);
        expressionEngine.getOperatorContext().getContext().put("lB", list2);
        String express2 = " lA = lB ";
        List<Object> evaluates = expressionEngine.evaluates(express2);
        Assertions.assertIterableEquals(evaluates, Arrays.asList(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE));
        List<BigDecimal> r1 = Arrays.asList(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
        List<BigDecimal> r2 = Arrays.asList(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        expressionEngine.getOperatorContext().getContext().put("rA", r1);
        expressionEngine.getOperatorContext().getContext().put("rB", r2);
        String express3 = " lA = lB ? rA : rB";
        List<Object> evaluates1 = expressionEngine.evaluates(express3);
        Assertions.assertIterableEquals(evaluates1, Arrays.asList(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ZERO));
        String express4 = " 'a' = lB ? rA : rB";
        List<Object> evaluates2 = expressionEngine.evaluates(express4);
        Assertions.assertIterableEquals(evaluates2, Arrays.asList(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ONE));
        String express5 = " 'a' = lB ? rA ";
        List<Object> evaluates3 = expressionEngine.evaluates(express5);
        Assertions.assertIterableEquals(evaluates3, Arrays.asList(null, null, BigDecimal.ONE));

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