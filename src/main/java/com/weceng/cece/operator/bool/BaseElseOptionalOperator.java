package com.weceng.cece.operator.bool;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.weceng.cece.operator.Operator;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * <p>
 * else optional 运算器
 * </p>
 *
 * @author WECENG
 * @since 2024/12/11 18:03
 */
public abstract class BaseElseOptionalOperator<T, R> implements Operator<T, R> {
    /**
     * 操作
     *
     * @param op 操作数
     * @return 操作
     */
    @Override
    @SuppressWarnings("all")
    public R apply(T... op) {
        if (op == null || op.length != ops()) {
            Assert.isTrue(op.length == ops(), "操作符:{}({}),操作数不符!", name(), symbol());
        }
        Object op1 = op[0];
        Object op2 = op[1];
        if (op1 instanceof List && op2 instanceof List) {
            if (((List<?>) op1).stream().filter(Objects::nonNull).anyMatch(item -> item instanceof Collection)
                    || ((List<?>) op2).stream().filter(Objects::nonNull).anyMatch(item ->  item instanceof Collection)) {
                return (R) optional(op1, op2);
            }
            return (R) optional((List<Object>) op1, (List<Object>) op2);
        }
        return (R) optional(op1, op2);
    }

    /**
     * bool 条件选取
     *
     * @param op1 bool值
     * @param op2 参数
     * @return 根据bool值选取的参数
     */
    public abstract Object optional(Object op1, Object op2);

    /**
     * bool 条件选取
     *
     * @param op1 bool值
     * @param op2 参数
     * @return 根据bool值选取的参数
     */
    public List<Object> optional(List<Object> op1, List<Object> op2) {
        Assert.isTrue(CollUtil.size(op1) == CollUtil.size(op2), "操作数类型为数组，且长度不等");
        IntStream.range(0, CollUtil.size(op1)).forEach(idx -> op1.set(idx, optional(op1.get(idx), op2.get(idx))));
        return op1;
    }

}
