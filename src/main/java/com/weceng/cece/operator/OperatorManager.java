package com.weceng.cece.operator;

import com.weceng.cece.operator.bool.ElseOperator;
import com.weceng.cece.operator.bool.IfOperator;
import com.weceng.cece.operator.bool.compare.*;
import com.weceng.cece.operator.bool.logic.AndOperator;
import com.weceng.cece.operator.bool.logic.NotOperator;
import com.weceng.cece.operator.bool.logic.OrOperator;
import com.weceng.cece.utils.ReflectUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * <p>
 * 操作器管理器
 * </p>
 *
 * @author WECENG
 * @since 2024/5/27 10:39
 */
@Getter
public class OperatorManager {

    /**
     * 操作列表
     */
    private final List<Operator<?, ?>> operatorList;

    public OperatorManager(Class<?> clazz) {
        operatorList = load(clazz);
    }

    public OperatorManager(Class<?> clazz, List<Operator<?, ?>> operatorList) {
        this.operatorList = load(clazz);
        this.operatorList.addAll(operatorList);
    }

    private void loadDefault(List<Operator<?, ?>> operatorList) {
        operatorList.add(new LeftBracketOperator<>());
        operatorList.add(new RightBracketOperator<>());
        operatorList.add(new IfOperator());
        operatorList.add(new ElseOperator());
        operatorList.add(new AndOperator());
        operatorList.add(new OrOperator());
        operatorList.add(new NotOperator());
        operatorList.add(new EqualOperator());
        operatorList.add(new GeOperator());
        operatorList.add(new GtOperator());
        operatorList.add(new LeOperator());
        operatorList.add(new LtOperator());
        operatorList.add(new InOperator());
    }

    @SuppressWarnings("all")
    private List<Operator<?, ?>> load(Class<?> clazz) {
        Iterator<Operator> operatorIterator = ServiceLoader.load(Operator.class, Thread.currentThread().getContextClassLoader()).iterator();
        List<Operator<?, ?>> operatorList = new CopyOnWriteArrayList<>();
        loadDefault(operatorList);
        while (operatorIterator.hasNext()) {
            Operator operator = operatorIterator.next();
            Class<?> operatorTypeArgument = ReflectUtils.getGenericsClass(operator.getClass());
            if (operatorTypeArgument == clazz) {
                operatorList.add((Operator<?, ?>) operator);
            }
        }
        return operatorList;
    }

    /**
     * 注册
     *
     * @param operator 操作
     */
    public void register(Operator<?,?> operator) {
        operatorList.add(operator);
    }

    /**
     * 注销
     *
     * @param operator 操作
     */
    public void unregister(Operator<?,?> operator) {
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
    @SuppressWarnings("unchecked")
    public <T,R> Operator<T,R> getOperator(String symbol) {
        return (Operator<T, R>) operatorList.stream()
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
                .toList()
                .contains(symbol);
    }

}