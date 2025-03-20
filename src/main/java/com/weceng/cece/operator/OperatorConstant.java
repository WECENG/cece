package com.weceng.cece.operator;

/**
 * <p>
 * 操作常量类
 * </p>
 *
 * @author WECENG
 * @since 2024/5/27 11:22
 */
public final class OperatorConstant {

    private OperatorConstant() {
    }

    /**
     * 小数点
     */
    public static final char DOT = '.';

    /**
     * 点
     */
    public static String ESCAPED_DOT = "\\.";

    /**
     * 负号
     */
    public static final char NEG = '-';

    /**
     * 左括号
     */
    public static final char LEFT_BRACKET = '(';

    /**
     * 右括号
     */
    public static final char RIGHT_BRACKET = ')';

    public static final char LEFT_CONTEXT = '[';

    public static final char RIGHT_CONTEXT = ']';

    /**
     * 变量正则
     */
    public static final String WORD_VARIABLE_REGEX = "[a-zA-Z](?:[a-zA-Z0-9_.-]*[a-zA-Z0-9])?";

}
