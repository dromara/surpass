package org.dromara.surpass.persistence.service;

import javax.sql.DataSource;

import org.dromara.mybatis.jpa.service.IJpaService;
import org.dromara.surpass.entity.Message;
import org.dromara.surpass.entity.api.ApiDataSource;
import org.dromara.surpass.enums.DataSourceStatus;

public interface ApiDataSourceService extends IJpaService<ApiDataSource> {
    
    Message<String> saveDataSource(ApiDataSource dataSource);

    Message<String> updateDataSource(ApiDataSource dataSource);

    Message<String> deleteDataSource(String id);

    void updateStatus(String id, DataSourceStatus status);

    /**
     * 测试数据源是否连通
     * @param dataSource
     * @return
     */
    boolean testConnection(ApiDataSource dataSource);
    
    /**
     * 根据配置参数构建数据源
     * @param cfg
     * @return
     */
    DataSource buildDataSource(ApiDataSource cfg);
    
    /**
     * 切换数据源，如果没有则先创建再切换
     * @param dataSourceId
     */
    void switchDataSource(String dataSourceId);
}
