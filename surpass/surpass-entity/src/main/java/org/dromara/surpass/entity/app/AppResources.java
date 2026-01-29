package org.dromara.surpass.entity.app;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.mybatis.jpa.annotations.SoftDelete;
import org.dromara.mybatis.jpa.entity.JpaEntity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author: orangeBabu
 * @time: 2025/12/18 9:54
 */

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Table(name = "surpass_app_resources")
@Entity
public class AppResources extends JpaEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -5551633673516662324L;

    @Id
    @Column
    @GeneratedValue
    String id;

    /**
     * 资源类型
     */
    @Column
    private String classify;

    /**
     * 资源名称
     */
    @Column
    private String name;

    /**
     * 资源路径
     */
    @Column
    private String path;

    /**
     * http方法
     */
    @Column
    private String method;

    /**
     * 参数
     */
    @Column
    private String params;

    @Column
    private String datasourceId;

    @Column
    private String appId;

    @Column
    private String permission;

    @Column
    private String actionType;

    @Column
    private String resStyle;

    @Column
    private String parentId;

    @Column
    private String isOpen;

    @Column
    private String isFrame;

    @Column
    private String isCache;

    @Column
    private String isVisible;

    @Column
    private Integer sortIndex;

    @Column
    private String description;

    @SoftDelete
    @Column
    String deleted;

    @Column
    String status;

    @Column
    private String createdBy;

    @Column
    private Date createdDate;

    @Column
    private String modifiedBy;

    @Column
    private Date modifiedDate;

    private String parentName;

    private String contextPath;

    private String belongApp;

    private String datasourceName;
}
