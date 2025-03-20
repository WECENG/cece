package com.weceng.cece.operator.bool.logic;

import cn.hutool.dfa.StopChar;
import com.weceng.cece.operator.Operator;

import java.util.Arrays;

/**
 * <p>
 * 与运算
 * </p>
 *
 * @author chenwc@tsintergy.com
 * @since 2024/10/23 16:41
 */
public class AndOperator implements Operator<Boolean, Boolean> {
    /**
     * 名称
     *
     * @return 名称
     */
    @Override
    public String name() {
        return "且";
    }

    /**
     * 符号
     *
     * @return 符号
     * {@link StopChar}可用
     */
    @Override
    public String symbol() {
        return "&";
    }

    /**
     * 优先级
     *
     * @return 优先级
     */
    @Override
    public int precedence() {
        return 1;
    }

    /**
     * 操作
     *
     * @param op 操作数
     * @return 操作
     */
    @Override
    public Boolean apply(Boolean... op) {
        return Arrays.stream(op).allMatch(item -> item);
    }
}
