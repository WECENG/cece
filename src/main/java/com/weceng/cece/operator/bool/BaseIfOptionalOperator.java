package com.weceng.cece.operator.bool;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.weceng.cece.operator.Operator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <p>
 * if optional 运算器
 * </p>
 *
 * @author WECENG
 * @since 2024/12/11 17:27
 */
public abstract class BaseIfOptionalOperator<T, R> implements Operator<T, R> {

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
            if (CollUtil.isEmpty((List) op1) || CollUtil.isEmpty((List) op2)) {
                return (R) CollUtil.newArrayList();
            }
            if (Boolean.class.equals(CollUtil.getElementType((List) op1)) ){
                return (R) optional((List<Boolean>) op1, (List<Object>) op2);
            }
        }
        Assert.isTrue(op1 instanceof Boolean, "IF前置必须为条件表达式，示例 1 > 0 IF X ELSE Y");
        return (R) optional((Boolean) op1, op2);
    }

    /**
     * bool 条件选取
     *
     * @param op1 bool值
     * @param op2 参数
     * @return 根据bool值选取的参数
     */
    public abstract Object optional(Boolean op1, Object op2);

    /**
     * bool 条件选取
     *
     * @param op1 bool值
     * @param op2 参数
     * @return 根据bool值选取的参数
     */
    public List<Object> optional(List<Boolean> op1, List<Object> op2) {
        Assert.isTrue(CollUtil.size(op1) == CollUtil.size(op2), "操作数类型为数组，且长度不等");
        return IntStream.range(0, CollUtil.size(op1))
                .mapToObj(idx -> optional(op1.get(idx), op2.get(idx)))
                .collect(Collectors.toList());
    }

}
