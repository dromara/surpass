package org.dromara.surpass.enums;

import lombok.Getter;

/**
 * 接口SQL操作类型
 */

@Getter
public enum ApiSupportPagingEnum {
    PAGE("1", "分页"),
    LIST("2", "列表"),
    SINGLE("3", "单条"),
    INSERT("4", "增加"),
    UPDATE("5", "修改"),
    DELETE("6", "删除");

    private final String code;
    private final String description;

    ApiSupportPagingEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

}
