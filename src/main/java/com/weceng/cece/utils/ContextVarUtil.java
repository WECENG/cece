package com.weceng.cece.utils;


import com.weceng.cece.operator.OperatorConstant;

import java.util.LinkedList;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * <p>
 *
 * </p>
 *
 * @author chenwc@tsintergy.com
 * @since 2025/2/28 19:23
 */
public class ContextVarUtil {

    /**
     * 识别上下文变量
     *
     * @param expression         表达式
     * @param index              索引
     * @param values             值列表
     * @param context            上下文
     * @param contextVarFunction 上下文变量转换方法
     * @return 识别后索引
     */
    public static int parseContextVariable(String expression, int index, LinkedList<Object> values, Map<String, Object> context, UnaryOperator<String> contextVarFunction) {
        StringBuilder varName = new StringBuilder();
        int i = index + 1;
        while (i < expression.length() && expression.charAt(i) != OperatorConstant.RIGHT_CONTEXT) {
            varName.append(expression.charAt(i));
            i++;
        }
        if (i == expression.length()) {
            throw new IllegalArgumentException("未闭合的变量: " + varName);
        }
        String key = varName.toString();
        String contextVar = contextVarFunction.apply(key);
        Object varValue = context.getOrDefault(contextVar, null);
        values.push(varValue);
        return i;
    }

    public static String wrapContextVariable(String variable) {
        return OperatorConstant.LEFT_CONTEXT + variable + OperatorConstant.RIGHT_CONTEXT;
    }

}
