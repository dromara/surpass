package org.dromara.surpass.entity.app.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.dromara.mybatis.jpa.entity.JpaPage;

/**
 * @description:
 * @author: orangeBabu
 * @time: 2025/12/2 9:44
 */

@Data
@EqualsAndHashCode(callSuper=false)
public class AppPageDto extends JpaPage {
    private String appName;

    private String appCode;
}
