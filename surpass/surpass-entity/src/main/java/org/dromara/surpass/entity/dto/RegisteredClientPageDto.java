package org.dromara.surpass.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.dromara.mybatis.jpa.entity.JpaPage;

/**
 * @description:
 * @author: orangeBabu
 * @time: 2025/12/9 17:51
 */

@Data
@EqualsAndHashCode(callSuper=false)
public class RegisteredClientPageDto extends JpaPage {
    private String clientName;

    private String clientId;

    private Integer clientType;
}
