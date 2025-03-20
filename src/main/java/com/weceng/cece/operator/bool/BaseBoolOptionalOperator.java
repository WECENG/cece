package com.weceng.cece.operator.bool;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.weceng.cece.operator.Operator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <p>
 * bool option 运算器
 * </p>
 *
 * @author WECENG
 * @since 2024/12/11 14:08
 */
public abstract class BaseBoolOptionalOperator<T, R> implements Operator<T, R> {

    /**
     * 操作数数量
     *
     * @return 数量
     * @implNote bool运算必须是2个操作数
     */
    @Override
    public final int ops() {
        return 2;
    }

    /**
     * 操作
     *
     * @param op 操作数
     * @return 操作
     */
    @Override
    @SuppressWarnings("all")
    public final R apply(T... op) {
        if (op == null || op.length != ops()) {
            Assert.isTrue(op.length == ops(), "操作符:{}({}),操作数不符!", name(), symbol());
        }
        Object op1 = op[0];
        Object op2 = op[1];
        if (op1 instanceof List && op2 instanceof List) {
            return (R) test((List<Object>) op1, (List<Object>) op2);
        }
        if (op1 instanceof List) {
            return (R) test((List<Object>) op1, op2);
        }
        if (op2 instanceof List) {
            return (R) test(op1, (List<Object>) op2);
        }
        return (R) Boolean.valueOf(test(op1, op2));
    }

    /**
     * bool 运算
     *
     * @param op1 操作数
     * @param op2 操作数
     * @return ture or false
     */
    protected abstract Boolean test(Object op1, Object op2);


    /**
     * bool 运算
     *
     * @param op1 操作数
     * @param op2 操作数
     * @return ture or false
     */
    protected List<Boolean> test(List<Object> op1, Object op2) {
        return CollUtil.emptyIfNull(op1).stream()
                .map(o1 -> test(o1, op2))
                .collect(Collectors.toList());
    }

    /**
     * bool 运算
     *
     * @param op1 操作数
     * @param op2 操作数
     * @return ture or false
     */
    protected List<Boolean> test(Object op1, List<Object> op2) {
        return CollUtil.emptyIfNull(op2).stream()
                .map(o2 -> test(op1, o2))
                .collect(Collectors.toList());
    }

    /**
     * bool 运算
     *
     * @param op1 操作数
     * @param op2 操作数
     * @return ture or false
     */
    protected List<Boolean> test(List<Object> op1, List<Object> op2) {
        Assert.isTrue(CollUtil.size(op1) == CollUtil.size(op2), "操作数类型为数组，且长度不等");
        return IntStream.range(0, CollUtil.size(op1))
                .mapToObj(idx -> test(op1.get(idx), op2.get(idx)))
                .collect(Collectors.toList());
    }

}
