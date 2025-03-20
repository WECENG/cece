package com.weceng.cece.engine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 变量元信息
 * </p>
 *
 * @author WECENG
 * @since 2025/3/14 09:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VarMeta implements Serializable {

    /**
     * 变量keys
     */
    private List<String> varKeys;

    /**
     * 变量
     */
    private String varName;

    public static VarMeta original(String var) {
        return VarMeta.builder()
                .varName(var)
                .build();
    }


}
