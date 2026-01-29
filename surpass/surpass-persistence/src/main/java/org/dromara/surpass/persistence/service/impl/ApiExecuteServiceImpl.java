package org.dromara.surpass.persistence.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.dromara.mybatis.jpa.datasource.DataSourceSwitch;
import org.dromara.mybatis.jpa.entity.JpaPage;
import org.dromara.surpass.entity.ApiRequestUri;
import org.dromara.surpass.entity.api.ApiParam;
import org.dromara.surpass.entity.api.ApiVersion;
import org.dromara.surpass.entity.api.dto.ApiParamList;
import org.dromara.surpass.entity.app.AppResources;
import org.dromara.surpass.enums.ApiFieldDataTypeEnum;
import org.dromara.surpass.enums.ApiSupportPagingEnum;
import org.dromara.surpass.exception.BusinessException;
import org.dromara.surpass.persistence.service.ApiDataSourceService;
import org.dromara.surpass.persistence.service.ApiExecuteService;
import org.dromara.surpass.persistence.service.ApiVersionService;
import org.dromara.surpass.persistence.service.AppResourcesService;
import org.dromara.surpass.persistence.service.ISqlRepository;
import org.dromara.surpass.util.JsonUtils;
import org.springframework.stereotype.Service;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class ApiExecuteServiceImpl implements ApiExecuteService {
    private static final String DEFAULT_PAGE_NUM_KEY = "_pageNum";
    private static final String DEFAULT_PAGE_SIZE_KEY = "_pageSize";

    private final ISqlRepository sqlRepository;

    private final ApiVersionService apiVersionService;

    private final AppResourcesService appResourcesService;

    private final ApiDataSourceService dataSourceService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 动态执行API
     *
     * @param apiRequestUri 请求路径信息
     * @param method        请求方法
     * @param params        参数
     * @param isTest        测试
     * @return 执行结果
     */
    public Object execute(ApiRequestUri apiRequestUri, String method, Map<String, Object> params, boolean isTest) {
        try {
            AppResources byPathAndMethod;
            ApiVersion apiVersion;
            if (isTest) {
                apiVersion = getApiVersion(params);
                byPathAndMethod = appResourcesService.get(apiVersion.getApiId());
                params = getRequestParams(params);
            } else {
                byPathAndMethod = appResourcesService.findByPathAndMethod(apiRequestUri.getResourcePath(), method, apiRequestUri.getContextPath());
                if (Objects.isNull(byPathAndMethod)) {
                    throw new BusinessException(50001, "API不存在");
                }
                apiVersion = apiVersionService.findPublishedVersionByApiId(byPathAndMethod.getId());
                if (Objects.isNull(apiVersion)) {
                    throw new BusinessException(50001, "API未发布");
                }
            }

            // 3、参数验证和转换
            Map<String, Object> parseredParams = validateAndConvert(params, apiVersion.getParamDefinition());
            // 4. 切换数据源
            dataSourceService.switchDataSource(byPathAndMethod.getDatasourceId());
            // 5. 获取SQL
            String sql = apiVersion.getSqlTemplate().trim();
            // 判断是否分页
            if (ApiSupportPagingEnum.PAGE.getCode().equals(apiVersion.getSupportsPaging())) {
                int pageNum = Integer.parseInt(params.getOrDefault(DEFAULT_PAGE_NUM_KEY, "1").toString());
                int pageSize = Integer.parseInt(params.getOrDefault(DEFAULT_PAGE_SIZE_KEY, "20").toString());
                JpaPage page = new JpaPage(pageNum, pageSize);
                return sqlRepository.fetch(sql, page, parseredParams);
            } else if (ApiSupportPagingEnum.LIST.getCode().equals(apiVersion.getSupportsPaging())) {
                // 查询操作
                return sqlRepository.selectList(sql, parseredParams);
            } else if (ApiSupportPagingEnum.SINGLE.getCode().equals(apiVersion.getSupportsPaging())) {
                // 查询操作
                JpaPage page = new JpaPage(1, 1);
                List<Map<String, Object>> rows = sqlRepository.fetch(sql, page, parseredParams).getRows();
                if (!rows.isEmpty()) {
                    return rows.get(0);
                }
                return null;
            } else if (ApiSupportPagingEnum.INSERT.getCode().equals(apiVersion.getSupportsPaging())) {
                // 插入操作
                int generatedKey = sqlRepository.insert(sql, parseredParams);
                return Map.of("affectedRows", 1, "generatedKey", generatedKey);
            } else if (ApiSupportPagingEnum.UPDATE.getCode().equals(apiVersion.getSupportsPaging())) {
                // 更新
                int affectedRows = sqlRepository.update(sql, parseredParams);
                return Map.of("affectedRows", affectedRows);
            } else if (ApiSupportPagingEnum.DELETE.getCode().equals(apiVersion.getSupportsPaging())) {
                // 删除操作
                int affectedRows = sqlRepository.delete(sql, parseredParams);
                return Map.of("affectedRows", affectedRows);
            } else {
                throw new BusinessException(50001, "不支持的SQL类型: " + sql);
            }

        } catch (Exception e) {
            log.error("执行API失败: {} {}", method, apiRequestUri.getRequestPath(), e);
            throw new BusinessException(50001, "API执行失败: " + e.getMessage());
        } finally {
            DataSourceSwitch.switchToDefault();
        }
    }

    /**
     * 参数验证和转换
     *
     * @param params        参数
     * @param paramRuleJson 参数规则定义
     */
    public Map<String, Object> validateAndConvert(Map<String, Object> params, String paramRuleJson) {
        log.debug("params : {}", params);
        log.debug("paramJson : {}", paramRuleJson);
        Map<String, Object> convertedParams = new HashMap<>();
        String json = "{\"paramList\":" + paramRuleJson.replace("\n", "") + "}";
        ApiParamList paramList = JsonUtils.stringToObject(json, ApiParamList.class);
        log.debug("paramList : {}", paramList);
        for (ApiParam apiParam : paramList.getParamList()) {
            Object value = params.get(apiParam.getName());
            if (value != null) {
                ApiFieldDataTypeEnum typeEnum =
                        ApiFieldDataTypeEnum.fromCode(apiParam.getType());
                Object convertedValue;
                if (typeEnum != null) {
                    convertedValue = typeEnum.convert(value);
                } else {
                    // 兜底策略：未知类型直接使用原值
                    convertedValue = value;
                }
                convertedParams.put(apiParam.getName(), convertedValue);
            }
        }
        log.debug("converted Params : {}", convertedParams);
        return convertedParams;
    }

    private ApiVersion getApiVersion(Map<String, Object> params) throws JsonProcessingException {
        return objectMapper.readValue(params.get("apiVersion").toString(), ApiVersion.class);
    }

    private Map<String, Object> getRequestParams(Map<String, Object> params) {
        return (Map<String, Object>) params.getOrDefault("data", new HashMap<String, Object>() {
        });
    }

}
