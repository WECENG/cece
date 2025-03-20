package com.weceng.cece.operator.bd;

import cn.hutool.core.lang.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.expression.EvaluationException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <p>
 * BigDecimal计算类
 * </p>
 *
 * @author WECENG
 * @since 2024/5/30 18:32
 */
public class BigDecimalOpFunction {

    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    public static final int SCALE = 10;
    public static final MathContext MATH_CONTEXT = new MathContext(SCALE, ROUNDING_MODE);

    public static final BigDecimal ZERO = new BigDecimal("0");
    public static final BigDecimal ONE = new BigDecimal("1");
    /**
     * -1
     */
    public static final BigDecimal MINUS_ONE = new BigDecimal("-1");
    public static final BigDecimal HUNDREND = new BigDecimal(100);

    /**
     * 设置保留指定小数位，并四舍五入
     *
     * @param values
     * @param scale
     * @return
     */
    public static final List<BigDecimal> listScale(List<BigDecimal> values, int scale) {
        if (values == null || values.isEmpty()) {
            return values;
        } else {
            return values.stream().map(item -> item == null ? null : item.setScale(scale, ROUNDING_MODE)).collect(Collectors.toList());
        }
    }

    /**
     * 列表求和
     *
     * @param values
     * @return
     */
    public static BigDecimal listSum(List<BigDecimal> values) {
        return listStatistics(values, (v1, v2) -> valueCalc(v1, v2, BigDecimal::add)).orElse(new BigDecimal("0"));
    }

    /**
     * v1 operator v2, operator: + - * / 等。如果v1 == null，返回v2，如果v2 == null，返回v1，否则 v1 operator v2
     *
     * @param v1
     * @param v2
     * @param operator
     * @return
     */
    public static BigDecimal valueCalc(BigDecimal v1, BigDecimal v2, BiFunction<BigDecimal, BigDecimal, BigDecimal> operator) {
        if (v1 == null) {
            return v2;
        } else if (v2 == null) {
            return v1;
        } else {
            return operator.apply(v1, v2);
        }
    }

    /**
     * 列表最大值, 如果列表所有元素为null，则返回null
     *
     * @param values
     * @return
     */
    public static BigDecimal listMax(List<BigDecimal> values) {
        return listStatistics(values, (v1, v2) -> valueCalc(v1, v2, BigDecimal::max)).orElse(null);
    }

    /**
     * 列表最小值, 如果列表所有元素为null，则返回null
     *
     * @param values
     * @return
     */
    public static BigDecimal listMin(List<BigDecimal> values) {
        return listStatistics(values, (v1, v2) -> valueCalc(v1, v2, BigDecimal::min)).orElse(null);
    }

    /**
     * 平均值, 如果列表所有元素为null，则返回null
     *
     * @param values
     * @return
     */
    public static BigDecimal listAvg(List<BigDecimal> values) {
        //除不尽会抛出异常，需设置四舍五入
        return listSum(values).divide(new BigDecimal(values.size()), MATH_CONTEXT);
    }

    /**
     * @param values
     * @param function 统计函数
     * @param <T>
     * @return
     */
    public static <T> Optional<T> listStatistics(List<T> values, BiFunction<T, T, T> function) {
        if (CollectionUtils.isEmpty(values)) {
            return Optional.empty();
        } else {
            return values.stream().filter(Objects::nonNull).reduce(function::apply);
        }
    }

    /**
     * @param values
     * @param function 统计函数
     * @param identity 初值
     * @param <T>
     * @return
     */
    public static <T> T listStatistics(List<T> values, BiFunction<T, T, T> function, T identity) {
        if (CollectionUtils.isEmpty(values)) {
            return identity;
        } else {
            return values.stream().filter(Objects::nonNull).reduce(identity, function::apply);
        }
    }

    public static final int SIZE_NOT_SET = 0;

    public static <T> List<T> listCalc(List<T> values1, List<T> values2, BiFunction<T, T, T> function) {
        Assert.isTrue(values1.size() == values2.size(), "长度不等");
        List<T> resultList = new ArrayList<>();
        ListIterator<T> v1Iter = values1.listIterator();
        ListIterator<T> v2Iter = values2.listIterator();
        while (v1Iter.hasNext()) {
            T v = function.apply(v1Iter.next(), v2Iter.next());
            resultList.add(v);
        }
        return resultList;
    }

    /**
     * 两个列表对应元素相加
     *
     * @param values1
     * @param values2
     * @return
     */
    public static List<BigDecimal> listAdd(List<BigDecimal> values1, List<BigDecimal> values2) {
        return listCalc(values1, values2, BigDecimalOpFunction::valueAdd);
    }

    /**
     * 两个列表对应元素相减
     *
     * @param values1
     * @param values2
     * @return
     */
    public static List<BigDecimal> listSubtract(List<BigDecimal> values1, List<BigDecimal> values2) {
        return listCalc(values1, values2, BigDecimalOpFunction::valueSubtract);
    }

    /**
     * 两个列表对应元素相乘
     *
     * @param values1
     * @param values2
     * @return
     */
    public static List<BigDecimal> listMultiply(List<BigDecimal> values1, List<BigDecimal> values2) {
        return listCalc(values1, values2, BigDecimalOpFunction::valueMultiply);
    }

    /**
     * 两个列表对应元素相除
     *
     * @param values1
     * @param values2
     * @return
     */
    public static List<BigDecimal> listDivide(List<BigDecimal> values1, List<BigDecimal> values2) {
        //除不尽会抛出异常，需设置四舍五入
        return listCalc(values1, values2, BigDecimalOpFunction::valueDivide);
    }

    /**
     * list平方
     *
     * @param values
     * @return
     */
    public static List<BigDecimal> listPow2(List<BigDecimal> values) {
        if (values == null || values.isEmpty()) {
            throw new EvaluationException("values 为 null 或 空");
        }
        return values.stream().map(BigDecimalOpFunction::valuePow2).collect(Collectors.toList());
    }


    /**
     * 常量转list
     *
     * @param value
     * @param size
     * @return
     */
    public static List<BigDecimal> constantToList(BigDecimal value, Integer size) {
        if (size == null) {
            return null;
        }
        List<BigDecimal> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(value);
        }
        return result;
    }

    /**
     * 获取列表的大小
     *
     * @param values
     * @param <T>
     * @return
     */
    public static <T> Integer getListSize(List<T> values) {
        return values != null ? values.size() : null;
    }

    /**
     * 两个列表对应元素相加
     *
     * @param values
     * @param itemValue
     * @return
     */
    public static List<BigDecimal> listAddValue(List<BigDecimal> values, BigDecimal itemValue) {
        return listAdd(values, constantToList(itemValue, getListSize(values)));
    }

    /**
     * 两个列表对应元素相减
     *
     * @param values
     * @param itemValue
     * @return
     */
    public static List<BigDecimal> listSubtractValue(List<BigDecimal> values, BigDecimal itemValue) {
        return listSubtract(values, constantToList(itemValue, getListSize(values)));
    }

    /**
     * 两个列表对应元素相乘，如果values为null则抛出异常
     *
     * @param values
     * @param itemValue
     * @return
     */
    public static List<BigDecimal> listMultiplyValue(List<BigDecimal> values, BigDecimal itemValue) {
        return listMultiply(values, constantToList(itemValue, getListSize(values)));
    }

    /**
     * 两个列表对应元素相除
     *
     * @param values
     * @param itemValue
     * @return
     */
    public static List<BigDecimal> listDivideValue(List<BigDecimal> values, BigDecimal itemValue) {
        return listDivide(values, constantToList(itemValue, getListSize(values)));
    }

    /**
     * 两个元素相加
     *
     * @param value1
     * @param value2
     * @return
     */
    public static BigDecimal valueAdd(BigDecimal value1, BigDecimal value2) {
        if (value1 == null || value2 == null) {
            return null;
        }
        return value1.add(value2);
    }

    /**
     * 两个元素相减
     *
     * @param value1
     * @param value2
     * @return
     */
    public static BigDecimal valueSubtract(BigDecimal value1, BigDecimal value2) {
        if (value1 == null || value2 == null) {
            return null;
        }
        return value1.subtract(value2);
    }

    /**
     * 两个元素相乘
     *
     * @param value1
     * @param value2
     * @return
     */
    public static BigDecimal valueMultiply(BigDecimal value1, BigDecimal value2) {
        if (value1 == null || value2 == null) {
            return null;
        }
        return value1.multiply(value2);
    }

    /**
     * 两个元素相除
     *
     * @param value1
     * @param value2
     * @return
     */
    public static BigDecimal valueDivide(BigDecimal value1, BigDecimal value2) {
        if (value1 == null || value2 == null) {
            return null;
        }
        if (ZERO.compareTo(value2) == 0) {
            return null;
        }
        return value1.divide(value2, MATH_CONTEXT);
    }

    /**
     * 平方
     *
     * @param value
     * @return
     */
    public static BigDecimal valuePow2(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.multiply(value);
    }

    /**
     * a == b
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean eq(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) == 0;
    }

    /**
     * a >= b
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean ge(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) >= 0;
    }

    /**
     * a > b
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean gt(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) > 0;
    }

    /**
     * a <= b
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean le(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) <= 0;
    }

    /**
     * a < b
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean lt(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) < 0;
    }

    /**
     * a, b 返回较小的那个
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal min(BigDecimal a, BigDecimal b) {
        return ge(a, b) ? b : a;
    }

    /**
     * a, b 返回较大的那个
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal max(BigDecimal a, BigDecimal b) {
        return ge(a, b) ? a : b;
    }

    /**
     * 取负数
     *
     * @param value
     * @return
     */
    public static BigDecimal neg(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.multiply(MINUS_ONE);
    }

    /**
     * 设置初值，如果value为null，那么返回defaultValue
     *
     * @param value
     * @param defaultValue
     * @return
     */
    public static BigDecimal initValue(BigDecimal value, BigDecimal defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * a + b
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) {
            return null;
        }
        return a.add(b);
    }

    /**
     * a - b
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) {
            return null;
        }
        return a.subtract(b);
    }

    /**
     * a * b
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) {
            return null;
        }
        return a.multiply(b);
    }

    /**
     * a / b
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal divide(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) {
            return null;
        }
        return a.divide(b, MATH_CONTEXT);
    }

    /**
     * 创建 list
     *
     * @param value 值
     * @param size 长度
     * @return list
     */
    public static List<BigDecimal> createList(BigDecimal value, int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> value)
                .collect(Collectors.toList());
    }

}
