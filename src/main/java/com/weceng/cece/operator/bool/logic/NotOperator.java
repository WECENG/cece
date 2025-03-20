package com.weceng.cece.operator.bool.logic;

import cn.hutool.dfa.StopChar;
import com.weceng.cece.operator.Operator;

import java.util.Arrays;

/**
 * <p>
 * 非运算
 * </p>
 *
 * @author chenwc@tsintergy.com
 * @since 2024/10/23 16:49
 */
public class NotOperator implements Operator<Boolean,Boolean> {
    /**
     * 名称
     *
     * @return 名称
     */
    @Override
    public String name() {
        return "非";
    }

    /**
     * 符号
     *
     * @return 符号
     * {@link StopChar}可用
     */
    @Override
    public String symbol() {
        return "!";
    }

    /**
     * 优先级
     *
     * @return 优先级
     */
    @Override
    public int precedence() {
        return 2;
    }

    /**
     * 操作数数量
     *
     * @return 数量
     */
    @Override
    public int ops() {
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
        return !Arrays.stream(op).findAny().orElse(false);
    }
}
