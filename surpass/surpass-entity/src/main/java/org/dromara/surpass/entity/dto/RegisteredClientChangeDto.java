package org.dromara.surpass.entity.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import org.dromara.surpass.validate.AddGroup;
import org.dromara.surpass.validate.EditGroup;

/**
 * @description:
 * @author: orangeBabu
 * @time: 2025/12/9 17:38
 */

@Data
public class RegisteredClientChangeDto {

    @NotEmpty(message = "ID不能为空", groups = EditGroup.class)
    private String id;

    @NotEmpty(message = "客户端名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String clientName;

    @NotNull(message = "客户端类型不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer clientType;

    private String description;

    private String contactName;

    private String contactPhone;

    private String contactEmail;

    private String department;

    private String ipWhiteList;


    private Integer status;
}
