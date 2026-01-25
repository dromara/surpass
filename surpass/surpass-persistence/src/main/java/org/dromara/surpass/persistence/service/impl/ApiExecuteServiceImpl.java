package org.dromara.surpass.persistence.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.dromara.mybatis.jpa.datasource.DataSourceSwitch;
import org.dromara.mybatis.jpa.entity.JpaPage;
import org.dromara.surpass.entity.ApiRequestUri;
import org.dromara.surpass.entity.api.ApiParam;
import org.dromara.surpass.entity.api.ApiVersion;
import org.dromara.surpass.entity.api.dto.ApiParamList;
import org.dromara.surpass.entity.app.AppResources;
import org.dromara.surpass.exception.BusinessException;
import org.dromara.surpass.persistence.service.ApiDataSourceService;
import org.dromara.surpass.persistence.service.ApiExecuteService;
import org.dromara.surpass.persistence.service.ApiVersionService;
import org.dromara.surpass.persistence.service.AppResourcesService;
import org.dromara.surpass.persistence.service.ISqlRepository;
import org.dromara.surpass.util.JsonUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
public class ApiExecuteServiceImpl  implements ApiExecuteService{
    private final ISqlRepository sqlRepository;
    
    private final ApiVersionService apiVersionService;

    private final AppResourcesService appResourcesService;

    private final ApiDataSourceService dataSourceService;

    private final String DEFAULT_PAGE_NUM_KEY = "_pageNum";
    private final String DEFAULT_PAGE_SIZE_KEY = "_pageSize";

    public Object execute(ApiRequestUri apiRequestUri, String method,  Map<String, Object> params) {
        try {
            // 1. 根据路径和方法查找API定义
            AppResources byPathAndMethod = appResourcesService.findByPathAndMethod(apiRequestUri.getResourcePath(), method, apiRequestUri.getContextPath());
            if (Objects.isNull(byPathAndMethod)) {
                throw new BusinessException(50001, "API不存在");
            }

            // 2. 查找已发布的版本
            ApiVersion apiVersion = apiVersionService.findPublishedVersionByApiId(byPathAndMethod.getId());
            if (Objects.isNull(apiVersion)) {
                throw new BusinessException(50001, "API未发布");
            }

            // 3、参数验证和转换
            Map<String, Object> parseredParams =validateAndConvert(params,apiVersion.getParamDefinition());
            
            // 4. 切换数据源
            dataSourceService.switchDataSource(byPathAndMethod.getDatasourceId());
            
            // 5. 获取SQL
            String sql = apiVersion.getSqlTemplate().trim();
            String sqlUpperCase = sql.toUpperCase();
            
            // 判断是否分页
            if (sqlUpperCase.startsWith("SELECT") && apiVersion.getSupportsPaging() != null && apiVersion.getSupportsPaging().equals(1)) {
                int pageNum = Integer.parseInt(params.getOrDefault(DEFAULT_PAGE_NUM_KEY, "1").toString());
                int pageSize = Integer.parseInt(params.getOrDefault(DEFAULT_PAGE_SIZE_KEY, "20").toString());
                JpaPage page = new JpaPage(pageNum,pageSize);
                return sqlRepository.fetch(sql, page, parseredParams);
            }else if (sqlUpperCase.startsWith("SELECT")) {
                // 查询操作
                return sqlRepository.selectList(sql, parseredParams);
            } else if (sqlUpperCase.startsWith("INSERT")) {
                // 插入操作
                int generatedKey = sqlRepository.insert(sql, parseredParams);
                return Map.of("affectedRows", 1, "generatedKey", generatedKey);
            } else if (sqlUpperCase.startsWith("UPDATE")) {
                // 更新
                int affectedRows = sqlRepository.update(sql, parseredParams);
                return Map.of("affectedRows", affectedRows);
            }else if (sqlUpperCase.startsWith("DELETE")) {
                // 删除操作
                int affectedRows = sqlRepository.delete(sql, parseredParams);
                return Map.of("affectedRows", affectedRows);
            } else {
                throw new BusinessException(50001, "不支持的SQL类型: " + sql);
            }

        } catch (Exception e) {
            log.error("执行API失败: {} {}", method, apiRequestUri.getRequestPath(), e);
            throw new BusinessException(50001, "API执行失败: " + e.getMessage());
        }finally {
            DataSourceSwitch.switchToDefault();
        }
    }
    
    /**
     * 参数验证和转换
     * @param params
     * @param paramJson
     */
    public Map<String, Object> validateAndConvert(Map<String, Object> params,String paramJson) {
        log.debug("params : {}", params);
        log.debug("paramJson : {}", paramJson);
        Map<String, Object> convertedParams = new HashMap<>();
        String json = "{\"paramList\":"+paramJson.replace("\n", "")+"}";
        ApiParamList paramList = JsonUtils.stringToObject(json, ApiParamList.class);
        log.debug("paramList : {}", paramList);
        for(ApiParam apiParam : paramList.getParamList()) {
            Object value = params.get(apiParam.getName());
            if(value != null) {
                if(apiParam.getType().equalsIgnoreCase("String")) {
                    convertedParams.put(apiParam.getName(), value);
                }else if(apiParam.getType().equalsIgnoreCase("Byte")) {
                    convertedParams.put(apiParam.getName(), Byte.valueOf(value.toString()));
                }else if(apiParam.getType().equalsIgnoreCase("Short")) {
                    convertedParams.put(apiParam.getName(), Short.valueOf(value.toString()));
                }else if(apiParam.getType().equalsIgnoreCase("Integer")) {
                    convertedParams.put(apiParam.getName(), Integer.valueOf(value.toString()));
                }else if(apiParam.getType().equalsIgnoreCase("Long")) {
                    convertedParams.put(apiParam.getName(), Long.valueOf(value.toString()));
                }else if(apiParam.getType().equalsIgnoreCase("Float")) {
                    convertedParams.put(apiParam.getName(), Float.valueOf(value.toString()));
                }else if(apiParam.getType().equalsIgnoreCase("Double")) {
                    convertedParams.put(apiParam.getName(), Double.valueOf(value.toString()));
                }else if(apiParam.getType().equalsIgnoreCase("Boolean")) {
                    convertedParams.put(apiParam.getName(), Boolean.valueOf(value.toString()));
                }else if(apiParam.getType().equalsIgnoreCase("Array[Integer]")) {
                    String [] stringArray = StringUtils.commaDelimitedListToStringArray(value.toString());
                    List<Integer> listValue =new ArrayList<>();
                    for(String v : stringArray) {
                        listValue.add(Integer.valueOf(v));
                    }
                    convertedParams.put(apiParam.getName(), listValue);
                }else if(apiParam.getType().equalsIgnoreCase("Array[String]")) {
                    String [] stringArray = StringUtils.commaDelimitedListToStringArray(value.toString());
                    List<String> listValue =new ArrayList<>();
                    for(String v : stringArray) {
                        listValue.add(v);
                    }
                    convertedParams.put(apiParam.getName(), listValue);
                }
            }
        }
        log.debug("converted Params : {}", convertedParams);
        return convertedParams;
    }

}
