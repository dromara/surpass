package org.dromara.surpass.entity.app.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.dromara.surpass.validate.AddGroup;
import org.dromara.surpass.validate.EditGroup;

/**
 * @description:
 * @author: orangeBabu
 * @time: 2025/12/17 16:54
 */

@Data
public class AppResourcesChangeDto {

    @NotEmpty(message = "ID不能为空", groups = {EditGroup.class})
    String id;

    /**
     * 资源类型
     */
    @NotEmpty(message = "所属类型不能为空", groups = {AddGroup.class, EditGroup.class})
    private String classify;

    /**
     * 资源名称
     */
    @NotEmpty(message = "资源名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String name;

    /**
     * 资源路径
     */
    private String path;

    /**
     * http方法
     */
    private String method;

    /**
     * 参数
     */
    private String params;

    private String datasourceId;

    @NotEmpty(message = "所属应用不能为空", groups = {AddGroup.class, EditGroup.class})
    private String appId;

    private String permission;

    private String actionType;

    private String resStyle;

    private String parentId;

    private String isOpen;

    private String isFrame;

    private String isCache;

    private String isVisible;

    private Integer sortIndex;

    private String description;

    String status;
}
