package org.dromara.surpass.entity.app.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.dromara.mybatis.jpa.entity.JpaPage;

/**
 * @description:
 * @author: orangeBabu
 * @time: 2025/12/17 16:53
 */

@Data
@EqualsAndHashCode(callSuper=false)
public class AppResourcesPageDto extends JpaPage {
    private String name;

    private String appId;

    private String classify;

    private String clientId;

    private String roleId;
}
