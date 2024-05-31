package com.weceng.cece.operator;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 操作器管理器
 * </p>
 *
 * @author chenwc@tsintergy.com
 * @since 2024/5/27 10:39
 */
@Getter
public class OperatorManager<T extends Number> {

    /**
     *  操作列表
     *
     */
    private final List<Operator<T>> operatorList = new ArrayList<>();

    /**
     * 注册
     *
     * @param operator 操作
     */
    public void register(Operator<T> operator) {
        operatorList.add(operator);
    }

    /**
     * 注销
     *
     * @param operator 操作
     */
    public void unregister(Operator<T> operator) {
        operatorList.remove(operator);
    }

    /**
     * 注销
     *
     * @param symbol 符号
     */
    public void unregister(String symbol) {
        operatorList.removeIf(operator -> operator.symbol().equals(symbol));
    }

    /**
     * 根据符号获取操作
     *
     * @param symbol 符号
     * @return 操作
     */
    public Operator<T> getOperator(String symbol) {
        return operatorList.stream()
                .filter(operator -> symbol.equals(operator.symbol()))
                .findAny()
                .orElse(null);
    }

    /**
     * 是否包含该符号的操作
     *
     * @param symbol 符号
     * @return 操作
     */
    public boolean contain(String symbol) {
        return operatorList.stream()
                .map(Operator::symbol)
                .collect(Collectors.toList())
                .contains(symbol);
    }

}
