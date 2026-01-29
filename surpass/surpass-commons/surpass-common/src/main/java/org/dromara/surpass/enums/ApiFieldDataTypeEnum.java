package org.dromara.surpass.enums;

import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;

/**
 * 接口字段数据类型枚举
 */
@Getter
public enum ApiFieldDataTypeEnum {

    STRING("String", "字符串", v -> v),

    BYTE("Byte", "字节", v -> Byte.valueOf(v.toString())),

    SHORT("Short", "短整型", v -> Short.valueOf(v.toString())),

    INTEGER("Integer", "整数", v -> Integer.valueOf(v.toString())),

    LONG("Long", "长整数", v -> Long.valueOf(v.toString())),

    FLOAT("Float", "单精度浮点", v -> Float.valueOf(v.toString())),

    DOUBLE("Double", "双精度浮点", v -> Double.valueOf(v.toString())),

    BOOLEAN("Boolean", "布尔", v -> Boolean.valueOf(v.toString())),

    ARRAY_INTEGER("Array[Integer]", "整型数组", v -> {
        String[] arr = StringUtils.commaDelimitedListToStringArray(v.toString());
        List<Integer> list = new ArrayList<>();
        for (String s : arr) {
            list.add(Integer.valueOf(s));
        }
        return list;
    }),

    ARRAY_FLOAT("Array[Float]", "浮点数组", v -> {
        String[] arr = StringUtils.commaDelimitedListToStringArray(v.toString());
        List<Float> list = new ArrayList<>();
        for (String s : arr) {
            list.add(Float.valueOf(s));
        }
        return list;
    }),

    ARRAY_STRING("Array[String]", "字符串数组", v -> {
        String[] arr = StringUtils.commaDelimitedListToStringArray(v.toString());
        return Arrays.asList(arr);
    });

    private final String code;
    private final String description;

    /**
     * 转换函数
     */
    private final Function<Object, Object> converter;

    ApiFieldDataTypeEnum(String code,
                         String description,
                         Function<Object, Object> converter) {
        this.code = code;
        this.description = description;
        this.converter = converter;
    }

    /**
     * 类型转换
     */
    public Object convert(Object value) {
        return converter.apply(value);
    }

    /**
     * 根据code获取枚举
     */
    public static ApiFieldDataTypeEnum fromCode(String code) {
        for (ApiFieldDataTypeEnum type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
