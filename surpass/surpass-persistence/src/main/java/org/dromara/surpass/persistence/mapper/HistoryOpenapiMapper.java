/*
 * Copyright [2025] [Surpass of copyright http://www.surpass.com]
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
 *
 */






package org.dromara.surpass.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dromara.mybatis.jpa.IJpaMapper;
import org.dromara.surpass.entity.history.HistoryOpenapi;
import org.dromara.surpass.entity.history.dto.HistoryOpenapiPageDto;

import java.util.List;
import java.util.Map;

/**
 * @author Crystal.sea
 *
 */
@Mapper
public interface HistoryOpenapiMapper extends IJpaMapper<HistoryOpenapi> {
    List<HistoryOpenapi> pageList(HistoryOpenapiPageDto dto);
    
    Long selectCountByCondition(@Param("params") Map<String, Object> params);
    
    List<Map<String, Object>> selectApiStatistics(@Param("params") Map<String, Object> params);
    
    List<Map<String, Object>> selectApiAccessTrendByHour(@Param("params") Map<String, Object> params);
    
    List<Map<String, Object>> selectApiAccessTrendByDay(@Param("params") Map<String, Object> params);
    
    List<Map<String, Object>> selectApiAccessTrendByMonth(@Param("params") Map<String, Object> params);
    
    List<Map<String, Object>> selectRegionAccessData(@Param("params") Map<String, Object> params);
}
