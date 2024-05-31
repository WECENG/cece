package com.weceng.cece.engine;

import cn.hutool.dfa.StopChar;
import com.alibaba.fastjson2.JSON;
import com.weceng.cece.operator.Operator;
import com.weceng.cece.operator.OperatorConstant;
import com.weceng.cece.operator.OperatorContext;
import com.weceng.cece.operator.OperatorManager;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.weceng.cece.operator.OperatorConstant.DOT;


/**
 * <p>
 * 表达式计算引擎
 * </p>
 *
 * @author chenwc@tsintergy.com
 * @since 2024/5/27 11:08
 */
@Getter
@Slf4j
public abstract class BaseExpressionEngine<T extends Number> {


    private final OperatorManager<T> operatorManager;

    private final OperatorContext<T> operatorContext;

    protected BaseExpressionEngine(OperatorManager<T> operatorManager, OperatorContext<T> operatorContext) {
        this.operatorManager = operatorManager;
        this.operatorContext = operatorContext;
    }

    public T evaluate(String expression) {
        return evaluates(expression).stream().findAny().orElse(null);
    }

    public List<T> evaluates(String expression) {
        Map<String, Object> context = operatorContext.getContext();
        Deque<Object> values = new ArrayDeque<>();
        Deque<String> ops = new ArrayDeque<>();
        List<Step> stepList = new ArrayList<>();
        for (AtomicInteger i = new AtomicInteger(); i.get() < expression.length(); i.getAndIncrement()) {
            char ch = expression.charAt(i.get());
            if (Character.isWhitespace(ch)) {
                continue;
            }
            if (Character.isDigit(ch) || ch == DOT) {
                i.set(parseNumber(expression, i.get(), values));
            } else if (ch == OperatorConstant.LEFT_BRACKET) {
                ops.push(String.valueOf(ch));
            } else if (ch == OperatorConstant.RIGHT_BRACKET) {
                //操作栈非空且非空括号，先行计算
                while (!ops.isEmpty() && !ops.peek().equals(String.valueOf(OperatorConstant.LEFT_BRACKET))) {
                    values.push(applyOp(ops.pop(), values, stepList));
                }
                ops.pop();
            } else if (Character.isLetter(ch) && StopChar.isNotStopChar(ch)) {
                i.set(parseVariable(expression, i.get(), values, context));
            } else if (operatorManager.contain(String.valueOf(ch))) {
                //如果操作栈非空，且计算优先级不低于当前操作，则先行计算
                while (!ops.isEmpty() && hasPrecedence(String.valueOf(ch), ops.peek())) {
                    values.push(applyOp(ops.pop(), values, stepList));
                }
                ops.push(String.valueOf(ch));
            }
        }
        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values, stepList));
        }
        log.info("原表达式:{}, 计算步骤:{}", expression, JSON.toJSONString(stepList));
        return wrapList(values.pop());
    }

    private int parseNumber(String expression, int index, Deque<Object> values) {
        StringBuilder buffer = new StringBuilder();
        while (index < expression.length() && (Character.isDigit(expression.charAt(index)) || expression.charAt(index) == DOT)) {
            buffer.append(expression.charAt(index++));
        }
        values.push(convertToNumber(buffer.toString()));
        return index - 1;
    }

    @SuppressWarnings("all")
    public int parseVariable(String expression, int index, Deque<Object> values, Map<String, Object> context) {
        StringBuilder buffer = new StringBuilder();
        while (index < expression.length() && (Character.isLetterOrDigit(expression.charAt(index)) || DOT == expression.charAt(index))) {
            buffer.append(expression.charAt(index++));
        }
        String varName = buffer.toString();
        if (varName.contains(String.valueOf(DOT))) {
            handleNestedProperty(varName, values, context);
        } else if (context.containsKey(varName)) {
            values.push(context.get(varName));
        } else {
            throw new IllegalArgumentException("Unknown variable: " + varName);
        }
        return index - 1;
    }

    private void handleNestedProperty(String varName, Deque<Object> values, Map<String, Object> context) {
        int firstDotIdx = varName.indexOf(DOT);
        String prefixVarName = varName.substring(0, firstDotIdx);
        String suffixVarName = varName.substring(firstDotIdx + 1);
        if (context.containsKey(prefixVarName)) {
            try {
                values.push(PropertyUtils.getProperty(context.get(prefixVarName), suffixVarName));
            } catch (Exception e) {
                throw new IllegalArgumentException("Unknown property: " + suffixVarName + " in variable: " + prefixVarName, e);
            }
        } else {
            throw new IllegalArgumentException("Unknown variable: " + prefixVarName);
        }
    }

    private boolean hasPrecedence(String op1, String op2) {
        if (op2.equals(String.valueOf(OperatorConstant.LEFT_BRACKET)) || op2.equals(String.valueOf(OperatorConstant.RIGHT_BRACKET))) {
            return false;
        }
        return operatorManager.getOperator(op1).precedence() <= operatorManager.getOperator(op2).precedence();
    }


    @SuppressWarnings("all")
    private Object applyOp(String op, Deque<Object> values, List<Step> stepList) {
        Operator<T> operator = operatorManager.getOperator(op);
        Object[] args = IntStream.range(0, operator.ops())
                .mapToObj(i -> values.pop())
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    Collections.reverse(list);
                    return list.toArray();
                }));
        stepList.add(Step.<T>builder()
                .op(op)
                .values(Arrays.stream(args).collect(Collectors.toList()))
                .build());
        if (Arrays.stream(args).anyMatch(item -> item instanceof List)) {
            List<List<T>> argList = Arrays.stream(args).map(this::wrapList).collect(Collectors.toList());
            return operator.apply(argList);
        } else {
            T[] argList = (T[]) Arrays.stream(args).toArray(Number[]::new);
            return operator.apply(argList);
        }
    }

    @SuppressWarnings("all")
    private List<T> wrapList(Object obj) {
        List<T> wrapList;
        if (obj instanceof List) {
            wrapList = (List<T>) obj;
        } else {
            wrapList = Collections.singletonList((T) obj);
        }
        return wrapList;
    }

    /**
     * 数值转换
     *
     * @param numStr 数值字串
     * @return 数值
     */
    public abstract T convertToNumber(String numStr);


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Step implements Serializable {

        private String op;

        private List<Object> values;

    }


}
