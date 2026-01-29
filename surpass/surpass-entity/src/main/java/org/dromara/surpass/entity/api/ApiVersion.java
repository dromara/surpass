package org.dromara.surpass.entity.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.mybatis.jpa.annotations.SoftDelete;
import org.dromara.mybatis.jpa.entity.JpaEntity;
import org.dromara.surpass.entity.app.AppResources;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "surpass_api_version")
@Data
public class ApiVersion extends JpaEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -8361763544848242839L;

    @Id
    @Column
    @GeneratedValue
    private String id;

    @NotNull(message = "API ID不能为空")
    @Column(nullable = false)
    private String apiId;

    @NotNull(message = "版本号不能为空")
    @Column(nullable = false)
    private Integer version;

    @NotBlank(message = "SQL模板不能为空")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String sqlTemplate;

    @Column(columnDefinition = "TEXT")
    private String paramDefinition;

    @Column(columnDefinition = "TEXT")
    private String responseTemplate;

    @Column(columnDefinition = "TEXT")
    private String responseDefinition;

    @NotNull(message = "状态不能为空")
    @Column(nullable = false)
    private Integer status = 0;

    @Column(length = 500)
    private String description;

    /**
     * 是否分页
     */
    @Column(length = 1)
    @NotNull(message = "操作类型不能为空")
    private String supportsPaging;

    /**
     * 允许的页记录最大值
     */
    @Column
    private Integer pageSizeMax;

    /**
     * 访问限流
     */
    @Column
    private Integer rateLimit;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_id", insertable = false, updatable = false)
    private AppResources apiDefinition;

    @SoftDelete
    @Column
    String deleted;

    @Column
    private String createdBy;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    @Column
    private String modifiedBy;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifiedDate;
}
