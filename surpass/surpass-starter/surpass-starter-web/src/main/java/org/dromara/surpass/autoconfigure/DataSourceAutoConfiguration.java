/*
 * Copyright [2025] [MaxKey of copyright http://www.maxkey.top]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.dromara.surpass.autoconfigure;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.dromara.mybatis.jpa.datasource.DynamicRoutingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import java.util.HashMap;
import java.util.Map;

@AutoConfiguration
public class DataSourceAutoConfiguration {
    private static final Logger _logger = LoggerFactory.getLogger(DataSourceAutoConfiguration.class);

    @Bean
    @ConfigurationProperties("spring.datasource")
    DruidDataSource druidDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name="dataSource")
    DynamicRoutingDataSource dynamicRoutingDataSource(DruidDataSource druidDataSource) {
        DynamicRoutingDataSource dynamicDataSource = new DynamicRoutingDataSource();
        // 创建多个测试数据源
        Map<Object, Object> targetDataSources = new HashMap<>();
        // 默认数据源
        targetDataSources.put(DynamicRoutingDataSource.DEFAULT_DATASOURCE_KEY, druidDataSource);

        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(druidDataSource);
        _logger.debug("init dynamicRoutingDataSource");
        return dynamicDataSource;
    }
}
