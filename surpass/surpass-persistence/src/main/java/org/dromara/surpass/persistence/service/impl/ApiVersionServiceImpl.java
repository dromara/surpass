package org.dromara.surpass.persistence.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dromara.mybatis.jpa.query.LambdaQuery;
import org.dromara.mybatis.jpa.query.OrderBy;
import org.dromara.mybatis.jpa.service.impl.JpaServiceImpl;
import org.dromara.surpass.entity.Message;
import org.dromara.surpass.entity.api.ApiVersion;
import org.dromara.surpass.enums.ApiVersionStatus;
import org.dromara.surpass.exception.BusinessException;
import org.dromara.surpass.persistence.mapper.ApiVersionMapper;
import org.dromara.surpass.persistence.service.ApiVersionService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @description:
 * @author: orangeBabu
 * @time: 2025/11/19 10:01
 */

@Service
@RequiredArgsConstructor
public class ApiVersionServiceImpl extends JpaServiceImpl<ApiVersionMapper, ApiVersion> implements ApiVersionService {

   private final ApiVersionMapper apiVersionMapper;

    private ApiVersion findByApiIdAndStatus(String apiId, Integer status) {
        LambdaQuery<ApiVersion> wrapper = new LambdaQuery<>();
        wrapper.eq(ApiVersion::getApiId, apiId);
        wrapper.eq(ApiVersion::getStatus, status);
        wrapper.orderBy(ApiVersion::getCreatedDate, OrderBy.DESC.getOrder());
        return super.query(wrapper).stream().findFirst().orElse(null);
    }

    @Override
    public ApiVersion findPublishedVersionByApiId(String apiId) {
        return findByApiIdAndStatus(apiId, ApiVersionStatus.PUBLISHED.getCode());
    }

    @Override
    public Message<ApiVersion> createNewVersion(ApiVersion apiVersion) {
        // 获取当前最大版本号
        int maxVersion = apiVersionMapper.findMaxVersionByApiId(apiVersion.getApiId());
        int newVersion = maxVersion + 1;

        apiVersion.setVersion(newVersion);
        apiVersion.setStatus(ApiVersionStatus.DRAFT.getCode());

        return super.insert(apiVersion) ? Message.ok(apiVersion) : Message.failed("新增失败");
    }

    @Override
    public void updateStatus(String id, ApiVersionStatus status) {
        ApiVersion apiVersion = super.get(id);
        if (Objects.isNull(apiVersion)) {
            throw new BusinessException(50001, "API版本不存在");
        }
        apiVersion.setStatus(status.getCode());
        super.update(apiVersion);
    }

    @Override
    public boolean updateVersion(ApiVersion apiVersion) {
        checkSaveOrUpdate(apiVersion);
        return super.update(apiVersion);
    }

    @Override
    public String generateDiff(String oldVersionId, String newVersionId) {
        // 简化的diff实现，实际项目中可以使用更复杂的diff算法
        ApiVersion oldVersion = super.get(oldVersionId);
        if (Objects.isNull(oldVersion)) {
            throw new BusinessException(50001, "旧版本不存在");
        }
        ApiVersion newVersion = super.get(newVersionId);
        if (Objects.isNull(newVersion)) {
            throw new BusinessException(50001, "新版本不存在");
        }

        StringBuilder diff = new StringBuilder();

        if (!oldVersion.getSqlTemplate().equals(newVersion.getSqlTemplate())) {
            diff.append("SQL模板有变化\n");
        }

        if (!oldVersion.getParamDefinition().equals(newVersion.getParamDefinition())) {
            diff.append("请求参数定义有变化\n");
        }

        if (!oldVersion.getResponseDefinition().equals(newVersion.getResponseDefinition())) {
            diff.append("响应参数有变化\n");
        }

        if (!oldVersion.getResponseTemplate().equals(newVersion.getResponseTemplate())) {
            diff.append("响应模板有变化\n");
        }

        return diff.toString();
    }

    @Override
    public Map<String, Long> getVersionStatusStatistics(String apiId) {
        LambdaQuery<ApiVersion> wrapper = new LambdaQuery<>();
        wrapper.eq(ApiVersion::getApiId, apiId);
        List<ApiVersion> versions = super.query(wrapper);

        Map<String, Long> statistics = new HashMap<>();
        statistics.put("total", (long) versions.size());
        statistics.put("draft", versions.stream().filter(v -> v.getStatus().equals(ApiVersionStatus.DRAFT.getCode())).count());
        statistics.put("pending", versions.stream().filter(v -> v.getStatus().equals(ApiVersionStatus.PENDING.getCode())).count());
        statistics.put("published", versions.stream().filter(v -> v.getStatus().equals(ApiVersionStatus.PUBLISHED.getCode())).count());
        statistics.put("offline", versions.stream().filter(v -> v.getStatus().equals(ApiVersionStatus.OFFLINE.getCode())).count());

        return statistics;
    }

    private void checkSaveOrUpdate(ApiVersion apiVersion) {
        LambdaQuery<ApiVersion> wrapper = new LambdaQuery<>();
        wrapper.eq(ApiVersion::getApiId, apiVersion.getApiId());
        wrapper.eq(ApiVersion::getVersion, apiVersion.getVersion());
        if (StringUtils.isNotBlank(apiVersion.getId())) {
            wrapper.notEq(ApiVersion::getId, apiVersion.getId());
        }

        if (super.count(wrapper) > 0) {
            throw new BusinessException(50001, "该API的版本号已存在");
        }

        if (StringUtils.isNotBlank(apiVersion.getId())) {
            // 检查是否允许修改已提交审核或已发布的版本
            ApiVersion currentVersion = super.get(apiVersion.getId());
            if (Objects.isNull(currentVersion)) {
                throw new BusinessException(50001, "API版本不存在");
            }

            if (currentVersion.getStatus().equals(ApiVersionStatus.PENDING.getCode()) ||
                    currentVersion.getStatus().equals(ApiVersionStatus.PUBLISHED.getCode())) {
                throw new BusinessException(50001, "已提交审核或已发布的版本不允许修改");
            }
        }
    }
}
