package org.dromara.surpass.persistence.service;

import java.util.Map;

import org.dromara.surpass.entity.ApiRequestUri;

/**
 * API执行服务
 */
public interface ApiExecuteService{

    public Object execute(ApiRequestUri apiRequestUri, String method, Map<String, Object> params, boolean isTest);
    
}
