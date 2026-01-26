package org.dromara.surpass.entity.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: orangeBabu
 * @time: 2025/12/11 11:13
 */

@Data
public class RegisteredClientPermissionDto {

    @NotEmpty(message = "客户端ID不能为空")
    private String clientId;

    private List<String> appIds;
}
