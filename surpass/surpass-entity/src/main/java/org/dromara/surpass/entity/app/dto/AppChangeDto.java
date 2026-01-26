package org.dromara.surpass.entity.app.dto;

import org.dromara.surpass.validate.AddGroup;
import org.dromara.surpass.validate.EditGroup;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @description:
 * @author: orangeBabu
 * @time: 2025/12/4 16:19
 */

@Data
public class AppChangeDto {

    @NotEmpty(message = "ID不能为空", groups = EditGroup.class)
    private String id;

    @NotEmpty(message = "应用名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String appName;

    @NotEmpty(message = "应用编码不能为空", groups = {AddGroup.class, EditGroup.class})
    private String appCode;

    private Integer status;

    @NotEmpty(message = "应用上下文路径不能为空", groups = {AddGroup.class, EditGroup.class})
    private String contextPath;

    private String loginUrl;
}
