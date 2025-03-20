package com.weceng.cece.engine;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.impl.CollectionConverter;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.dfa.StopChar;
import com.alibaba.fastjson.JSON;
import com.weceng.cece.operator.Operator;
import com.weceng.cece.operator.OperatorConstant;
import com.weceng.cece.operator.OperatorContext;
import com.weceng.cece.operator.OperatorManager;
import com.weceng.cece.utils.ContextVarUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.weceng.cece.operator.OperatorConstant.*;


/**
 * <p>
 * 表达式计算引擎
 * </p>
 *
 * @author WECENG
 * @since 2024/5/27 11:08
 */
@Getter
@Slf4j
public abstract class BaseExpressionEngine<N> {


    private final OperatorManager operatorManager;

    private final OperatorContext operatorContext;

    protected BaseExpressionEngine(OperatorManager operatorManager, OperatorContext operatorContext) {
        this.operatorManager = operatorManager;
        this.operatorContext = operatorContext;
    }

    public boolean verify(String expression) {
        evaluates(expression, VarMeta::original, true, (v1, v2) -> true);
        return true;
    }

    /**
     * 表达式计算
     *
     * @param expression 表达式
     * @param <R>        结果类型
     * @return 计算结果
     */
    @SuppressWarnings("unchecked")
    public <R> R evaluate(String expression) {
        return (R) evaluates(expression).stream().findAny().orElse(null);
    }

    /**
     * 表达式计算
     *
     * @param expression      表达式
     * @param varExprFunction 变量表达式转换方法
     * @param <R>             结果类型
     * @return 计算结果
     */
    @SuppressWarnings("unchecked")
    public <R> R evaluate(String expression, Function<String, VarMeta> varExprFunction) {
        return (R) evaluates(expression, varExprFunction, (v1, v2) -> true).stream().findAny().orElse(null);
    }

    /**
     * 表达式计算
     *
     * @param expression 表达式
     * @param <R>        结果类型
     * @return 计算结果
     */
    public <R> List<R> evaluates(String expression) {
        return evaluates(expression, VarMeta::original, (v1, v2) -> true);
    }

    /**
     * 表达式计算
     *
     * @param expression      表达式
     * @param varExprFunction 变量表达式转换方法
     * @param valueFilter     值过滤器
     * @param <R>             结果类型
     * @return 计算结果
     */
    public <R> List<R> evaluates(String expression, Function<String, VarMeta> varExprFunction, BiPredicate<Object, String> valueFilter) {
        return evaluates(expression, varExprFunction, false, valueFilter);
    }

    /**
     * 表达式计算
     *
     * @param expression         表达式
     * @param varExprFunction    变量表达式转换方法
     * @param valueFilter        值过滤器
     * @param contextVarFunction 上下文变量转换方法
     * @param <R>                结果类型
     * @return 计算结果
     */
    public <R> List<R> evaluates(String expression, Function<String, VarMeta> varExprFunction, BiPredicate<Object, String> valueFilter, UnaryOperator<String> contextVarFunction) {
        return evaluates(expression, varExprFunction, false, valueFilter, contextVarFunction);
    }

    /**
     * 表达式计算
     *
     * @param expression      表达式
     * @param varExprFunction 变量表达式转换方法
     * @param verify          是否为校验表达式
     * @param valueFilter     值过滤器
     * @param <R>             结果类型
     * @return 计算结果
     */
    public <R> List<R> evaluates(String expression, Function<String, VarMeta> varExprFunction, boolean verify, BiPredicate<Object, String> valueFilter) {
        return evaluates(expression, varExprFunction, verify, valueFilter, UnaryOperator.identity());
    }

    /**
     * 表达式计算
     *
     * @param expression         表达式
     * @param varExprFunction    变量表达式转换方法
     * @param verify             是否为校验表达式
     * @param valueFilter        值过滤器
     * @param contextVarFunction 上下文变量转换方法
     * @param <R>                结果类型
     * @return 计算结果
     */
    public <R> List<R> evaluates(String expression, Function<String, VarMeta> varExprFunction, boolean verify, BiPredicate<Object, String> valueFilter, UnaryOperator<String> contextVarFunction) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> context = operatorContext.getContext();
        LinkedList<Object> values = new LinkedList<>();
        LinkedList<String> ops = new LinkedList<>();
        List<Step> stepList = new ArrayList<>();
        for (AtomicInteger i = new AtomicInteger(); i.get() < expression.length(); i.getAndIncrement()) {
            char ch = expression.charAt(i.get());
            if (Character.isWhitespace(ch)) {
                continue;
            }
            if (Character.isDigit(ch) || ch == DOT ||
                    (ch == NEG && Character.isDigit(expression.charAt(i.get() + 1)))) {
                i.set(parseNumber(expression, i.get(), values));
            } else if (ch == OperatorConstant.LEFT_BRACKET) {
                ops.push(String.valueOf(ch));
            } else if (ch == OperatorConstant.RIGHT_BRACKET) {
                //操作栈非空且非空括号，先行计算
                while (!ops.isEmpty() && !ops.peek().equals(String.valueOf(OperatorConstant.LEFT_BRACKET))) {
                    values.push(applyOp(ops.pop(), values, stepList));
                }
                ops.pop();
            } else if (ch == '\'' || ch == '\"') {
                i.set(parseString(expression, i.get(), values, ch));
            } else if (ch == LEFT_CONTEXT) {
                i.set(ContextVarUtil.parseContextVariable(expression, i.get(), values, context, contextVarFunction));
            } else if (Character.isLetter(ch) && StopChar.isNotStopChar(ch)) {
                i.set(parseVariable(expression, i.get(), values, context, varExprFunction, verify, valueFilter));
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
        List<R> result = wrapList(values.pop());
        long endTime = System.currentTimeMillis();
        if (log.isTraceEnabled()) {
            log.trace("原表达式:{}, 计算步骤:{}, 计算结果:{}, 耗时:{}", expression, JSON.toJSONString(stepList), result, endTime - startTime);
        }
        return result;
    }

    /**
     * 数值转换
     *
     * @param expression 表达式
     * @param index      下标
     * @param values     值队列
     * @return 下标
     */
    private int parseNumber(String expression, int index, Deque<Object> values) {
        StringBuilder buffer = new StringBuilder();
        while (index < expression.length() && (Character.isDigit(expression.charAt(index)) ||
                expression.charAt(index) == DOT || expression.charAt(index) == NEG)) {
            buffer.append(expression.charAt(index++));
        }
        values.push(convertToNumber(buffer.toString()));
        return index - 1;
    }

    /**
     * 原始字串解析
     *
     * @param expression 表达式
     * @param index      下标
     * @param values     值队列
     * @param quoteChar  字串标识
     * @return 下标
     */
    private int parseString(String expression, int index, LinkedList<Object> values, char quoteChar) {
        StringBuilder str = new StringBuilder();
        // 跳过起始引号
        index++;
        // 循环直到找到匹配的结束引号
        while (index < expression.length() && expression.charAt(index) != quoteChar) {
            str.append(expression.charAt(index));
            index++;
        }
        // 如果是正常的结束引号，则 index++ 跳过结束引号
        if (index < expression.length() && expression.charAt(index) == quoteChar) {
            index++;
        }
        // 将字符串加入 values 列表
        values.push(str.toString());
        // 返回当前处理后的索引
        return index - 1;
    }

    /**
     * 变量解析
     *
     * @param expression      表达式
     * @param index           下标
     * @param values          值队列
     * @param context         上下文对象
     * @param varExprFunction 变量表达式自定义处理方法
     * @param isMock          是否模拟数据
     * @return 下标
     */
    @SuppressWarnings("all")
    public int parseVariable(String expression, int startIndex, LinkedList<Object> values,
                             Map<String, Object> context, Function<String, VarMeta> varExprFunction, boolean isMock, BiPredicate<Object, String> valueFilter) {
        Pattern pattern = Pattern.compile(WORD_VARIABLE_REGEX);
        Matcher matcher = pattern.matcher(expression.substring(startIndex));
        String variable = "";
        if (matcher.find() && matcher.start() == 0) {
            variable = matcher.group();
            VarMeta varMeta = varExprFunction.apply(variable);
            Object value = extractValue(context, varMeta, valueFilter);
            values.push(value);
        } else {
            throw new RuntimeException("表达式：" + expression + "解析变量失败");
        }
        return startIndex + variable.length() - 1;
    }

    public static Object extractValue(Object context, VarMeta varMeta, BiPredicate<Object, String> valueFilter) {
        if (context == null || varMeta == null || varMeta.getVarName().isEmpty()) {
            return Collections.emptyList();
        }
        String varName = varMeta.getVarName();
        List<String> metaVarKeys = varMeta.getVarKeys();
        Object prefixContext = extractRecursive(context, varName, metaVarKeys, 0, valueFilter);
        List<String> keys = Arrays.asList(varName.split(ESCAPED_DOT));
        return extractRecursive(prefixContext, varName, keys, 0, valueFilter);
    }

    private static Object extractRecursive(Object current, String varName, List<String> keys, int index, BiPredicate<Object, String> valueFilter) {
        if (current == null || CollUtil.isEmpty(keys) || index >= keys.size()) {
            return current;
        }
        String key = keys.get(index);
        if (current instanceof Collection<?> list) {
            return list.stream()
                    .map(item -> extractRecursive(item, varName, keys, index, valueFilter))
                    .filter(item -> !(item instanceof IgnoreValue))
                    .collect(Collectors.toList());
        } else if (current instanceof Map) {
            Object val = ((Map<?, ?>) current).get(key);
            return extractRecursive(val, varName, keys, index + 1, valueFilter);
        } else {
            try {
                if (valueFilter.test(current, varName)) {
                    return extractRecursive(PropertyUtils.getProperty(current, key), varName, keys, index + 1, valueFilter);
                } else {
                    return IgnoreValue.builder().build();
                }
            } catch (Exception e) {
                return null;
            }
        }
    }


    private boolean hasPrecedence(String op1, String op2) {
        if (op2.equals(String.valueOf(OperatorConstant.LEFT_BRACKET)) || op2.equals(String.valueOf(OperatorConstant.RIGHT_BRACKET))) {
            return false;
        }
        return operatorManager.getOperator(op1).precedence() <= operatorManager.getOperator(op2).precedence();
    }


    @SuppressWarnings("all")
    private <T, R> Object applyOp(String op, Deque<Object> values, List<Step> stepList) {
        Operator<T, R> operator = operatorManager.getOperator(op);
        Object[] args = IntStream.range(0, Integer.min(operator.ops(), values.size()))
                .mapToObj(i -> values.pop())
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    Collections.reverse(list);
                    return list.toArray();
                }));
        stepList.add(Step.<T>builder()
                .op(op)
                .values(Arrays.stream(args).collect(Collectors.toList()))
                .build());
        Type type = TypeUtil.getTypeArgument(operator.getClass());
        Class<?> clazz = TypeUtil.getClass(type);
        CollectionConverter collectionConverter = new CollectionConverter(List.class, type);
        if (Arrays.stream(args).anyMatch(item -> item instanceof List && Number.class.isAssignableFrom(clazz))) {
            List<List<T>> argList = Arrays.stream(args)
                    .map(itemList -> collectionConverter.convert(flatten(args, itemList), new ArrayList<>()))
                    .map(item -> this.<T>wrapList(item))
                    .collect(Collectors.toList());
            return operator.apply(argList);
        } else {
            Object[] convertArgs = (List.class.isAssignableFrom(clazz))
                    ? collectionConverter.convert(args, new ArrayList<>()).toArray()
                    : args;
            T[] argList = Arrays.stream(convertArgs).toArray(length -> (T[]) Array.newInstance(clazz, length));
            return operator.calc(argList);
        }
    }

    @SuppressWarnings("all")
    private <T> List<T> wrapList(Object obj) {
        List<T> wrapList;
        if (Objects.isNull(obj)) {
            return null;
        }
        if (obj instanceof List) {
            wrapList = (List<T>) obj;
        } else {
            wrapList = Collections.singletonList((T) obj);
        }
        return wrapList;
    }

    /**
     * 平铺展开
     *
     * @param sources 原数组
     * @param obj     对象
     * @return 如果是集合对象将其平铺展开
     */
    public List<Object> flatten(Object[] sources, Object obj) {
        return flattenHelper(sources, obj, true).collect(Collectors.toList());
    }

    /**
     * 平铺展开
     *
     * @param sources  原数组
     * @param element  元素
     * @param nullable 可空
     * @return 展开元素
     * @implNote 如果元素为null，且参数nullable为false,
     * 则会初始化一个长度为sources对应下标元素的一个所有元素为null的List
     * 其目的是保证数组展开后最终长度保持一致
     */
    private Stream<?> flattenHelper(Object[] sources, Object element, boolean nullable) {
        // 如果元素为 null 且不允许为空
        if (!nullable && element == null && hasListElement(sources)) {
            int length = getElementLength(sources);
            return length > 0 ? Stream.generate(() -> null).limit(length) : Stream.of();
        }
        // 如果元素是 List 类型
        if (element instanceof List<?> list) {
            return IntStream.range(0, list.size())
                    .mapToObj(idx -> {
                        Object[] eSources = filterAndExtractElements(sources, list.size(), idx);
                        return flattenHelper(eSources, list.get(idx), false);
                    })
                    .flatMap(Function.identity());
        }
        // 否则返回单个元素的 Stream
        return Stream.of(element);
    }

    /**
     * 判断数组中是否包含 List 类型元素
     *
     * @param sources 原数组
     * @return 是否包含 List 类型元素
     */
    private boolean hasListElement(Object[] sources) {
        return Arrays.stream(sources).anyMatch(e -> e instanceof List<?>);
    }

    /**
     * 筛选并提取对应索引的元素
     *
     * @param sources 原数组
     * @param size    List 的大小
     * @param index   索引
     * @return 筛选出的元素数组
     */
    private Object[] filterAndExtractElements(Object[] sources, int size, int index) {
        return Arrays.stream(sources, 0, sources.length)
                .filter(e -> e instanceof List<?>)
                .map(e -> (List<?>) e)
                .filter(e -> e.size() == size)
                .map(e -> e.get(index))
                .toArray();
    }

    /**
     * 获取数组中元素最大长度
     *
     * @param sources 数组
     * @return 元素最大长度
     */
    private Integer getElementLength(Object[] sources) {
        return Arrays.stream(sources)
                .filter(List.class::isInstance)
                .map(item -> (List<?>) item)
                .map(List::size)
                .max(Integer::compareTo)
                .orElse(null);
    }

    /**
     * 数值转换
     *
     * @param numStr 数值字串
     * @return 数值
     */
    public abstract N convertToNumber(String numStr);


    @Data
    @NoArgsConstructor
    @Builder
    public static final class IgnoreValue implements Serializable {

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Step implements Serializable {

        private String op;

        private List<Object> values;

    }


}
