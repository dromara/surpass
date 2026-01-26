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


package org.dromara.surpass.persistence.service.impl;

import org.dromara.mybatis.jpa.query.LambdaQuery;
import org.dromara.surpass.entity.report.vo.ApiAccessTrendVo;
import org.dromara.surpass.entity.report.vo.ApiTopVo;
import org.dromara.surpass.entity.report.vo.DashboardStatisticsVo;
import org.dromara.surpass.entity.report.vo.RegionAccessVo;
import org.dromara.surpass.persistence.mapper.HistoryOpenapiMapper;
import org.dromara.surpass.persistence.service.ApiDataSourceService;
import org.dromara.surpass.persistence.service.AppResourcesService;
import org.dromara.surpass.persistence.service.AppService;
import org.dromara.surpass.persistence.service.DashboardStatisticsService;
import org.dromara.surpass.persistence.service.RegisteredClientService;
import org.dromara.surpass.persistence.service.RolesService;
import org.dromara.surpass.persistence.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class DashboardStatisticsServiceImpl implements DashboardStatisticsService {

    private static final Random random = new Random();
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private AppService appService;

    @Autowired
    private AppResourcesService appResourcesService;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RegisteredClientService registeredClientService;

    @Autowired
    private ApiDataSourceService apiDataSourceService;

    @Autowired
    private HistoryOpenapiMapper historyOpenapiMapper;

    @Override
    public DashboardStatisticsVo getDashboardStatistics() {
        DashboardStatisticsVo data = new DashboardStatisticsVo();
        
//        data.setAppCount(getAppCount());
//        data.setApiCount(getApiCount());
//        data.setRoleCount(getRoleCount());
//        data.setUserCount(getUserCount());
//        data.setClientCount(getClientCount());
//        data.setDatasourceCount(getDatasourceCount());
//
//        data.setTodayApiCalls(getTodayApiCalls());
//        data.setWeekApiCalls(getWeekApiCalls());
//        data.setMonthApiCalls(getMonthApiCalls());
//        data.setApiSuccessRate(getApiSuccessRate());
        
//        data.setApiAccessTrend(getApiAccessTrend(null, null, "day"));
//        data.setRegionAccessData(getRegionAccessData(null, null, "month"));
//        data.setApiTopList(getApiTopList(null, null, "month"));
        
        return data;
    }

    @Override
    public int getAppCount() {
        try {
            return Math.toIntExact(appService.count(new LambdaQuery<>()));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getApiCount() {
        try {
            return Math.toIntExact(appResourcesService.count(new LambdaQuery<>()));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getRoleCount() {
        try {
            return Math.toIntExact(rolesService.count(new LambdaQuery<>()));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getUserCount() {
        try {
            return Math.toIntExact(userInfoService.count(new LambdaQuery<>()));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getClientCount() {
        try {
            return Math.toIntExact(registeredClientService.count(new LambdaQuery<>()));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getDatasourceCount() {
        try {
            return Math.toIntExact(apiDataSourceService.count(new LambdaQuery<>()));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getTodayApiCalls() {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
            
            Map<String, Object> params = new HashMap<>();
            params.put("startTime", startOfDay.format(datetimeFormatter));
            params.put("endTime", endOfDay.format(datetimeFormatter));
            
            Long count = historyOpenapiMapper.selectCountByCondition(params);
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getWeekApiCalls() {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfWeek = today.minusDays(6).atStartOfDay();
            LocalDateTime endOfWeek = today.plusDays(1).atStartOfDay();
            
            Map<String, Object> params = new HashMap<>();
            params.put("startTime", startOfWeek.format(datetimeFormatter));
            params.put("endTime", endOfWeek.format(datetimeFormatter));
            
            Long count = historyOpenapiMapper.selectCountByCondition(params);
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getMonthApiCalls() {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = today.plusDays(1).atStartOfDay();
            
            Map<String, Object> params = new HashMap<>();
            params.put("startTime", startOfMonth.format(datetimeFormatter));
            params.put("endTime", endOfMonth.format(datetimeFormatter));
            
            Long count = historyOpenapiMapper.selectCountByCondition(params);
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public double getApiSuccessRate() {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
            
            Map<String, Object> params = new HashMap<>();
            params.put("startTime", startOfDay.format(datetimeFormatter));
            params.put("endTime", endOfDay.format(datetimeFormatter));
            
            Long totalCount = historyOpenapiMapper.selectCountByCondition(params);
            if (totalCount == null || totalCount == 0) {
                return 100;
            }
            
            params.put("access", "y");
            Long successCount = historyOpenapiMapper.selectCountByCondition(params);
            
            if (successCount == null) {
                return 100;
            }
            
            return (successCount.doubleValue() / totalCount.doubleValue()) * 100.0;
        } catch (Exception e) {
            return 100;
        }
    }

    @Override
    public List<ApiAccessTrendVo> getApiAccessTrend(String startTime, String endTime, String type) {
        List<ApiAccessTrendVo> trendData = new ArrayList<>();
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        
        if ("day".equals(type)) {
            // 按小时统计：今天按小时的数据
            if (startTime == null || endTime == null) {
                LocalDate today = LocalDate.now();
                startTime = today.format(dateFormatter);

                params.put("startTime", startTime + " 00:00:00");
                params.put("endTime", today.plusDays(1).format(dateFormatter) + " 00:00:00");
            } else {
                params.put("startTime", startTime + " 00:00:00");
                params.put("endTime", endTime + " 23:59:59");
            }
            
            List<Map<String, Object>> hourStats = historyOpenapiMapper.selectApiAccessTrendByHour(params);
            trendData = fillMissingHourData(hourStats);
            
        } else if ("week".equals(type)) {
            LocalDate today = LocalDate.now();
            // 获取本周一作为开始日期
            LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            // 获取本周日作为结束日期
            LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            // 按天统计：本周的数据
            if (startTime == null || endTime == null) {
                startTime = startOfWeek.format(dateFormatter);
                endTime = endOfWeek.format(dateFormatter);
                params.put("startTime", startTime + " 00:00:00");
                params.put("endTime", endOfWeek.plusDays(1).format(dateFormatter) + " 00:00:00");
            } else {
                params.put("startTime", startTime + " 00:00:00");
                params.put("endTime", endTime + " 23:59:59");
            }
            
            List<Map<String, Object>> dayStats = historyOpenapiMapper.selectApiAccessTrendByDay(params);
            int days = (int) ChronoUnit.DAYS.between(startOfWeek, endOfWeek) + 1;
            trendData = fillMissingDayData(dayStats, startOfWeek, days);
            
        } else if ("month".equals(type)) {
            // 按天统计：本月数据
            if (startTime == null || endTime == null) {
                LocalDate today = LocalDate.now();
                LocalDate startOfMonth = today.withDayOfMonth(1);
                // 获取本月最后一天
                LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
                
                startTime = startOfMonth.format(dateFormatter);
                endTime = endOfMonth.format(dateFormatter);
                
                params.put("startTime", startTime + " 00:00:00");
                params.put("endTime", endOfMonth.plusDays(1).format(dateFormatter) + " 00:00:00");
            } else {
                params.put("startTime", startTime + " 00:00:00");
                params.put("endTime", endTime + " 23:59:59");
            }
            
            List<Map<String, Object>> dayStats = historyOpenapiMapper.selectApiAccessTrendByDay(params);
            // 计算从开始日期到结束日期的天数
            LocalDate start = LocalDate.parse(startTime.substring(0, 10));
            LocalDate end = LocalDate.parse(endTime.substring(0, 10));
            int days = (int) ChronoUnit.DAYS.between(start, end) + 1;
            trendData = fillMissingDayData(dayStats, start, days);
        } else if ("month_stat".equals(type)) {
            // 按月统计：多个月的数据
            if (startTime == null || endTime == null) {
                LocalDate today = LocalDate.now();
                LocalDate startOfLastMonth = today.minusMonths(5).withDayOfMonth(1); // 最近6个月
                startTime = startOfLastMonth.format(dateFormatter);
                endTime = today.format(dateFormatter);
                
                params.put("startTime", startTime + " 00:00:00");
                params.put("endTime", today.plusDays(1).format(dateFormatter) + " 00:00:00");
            } else {
                params.put("startTime", startTime + " 00:00:00");
                params.put("endTime", endTime + " 23:59:59");
            }
            
            List<Map<String, Object>> monthStats = historyOpenapiMapper.selectApiAccessTrendByMonth(params);
            trendData = fillMissingMonthData(monthStats);
        }
        
        return trendData;
    }
    
    private List<ApiAccessTrendVo> fillMissingHourData(List<Map<String, Object>> stats) {
        List<ApiAccessTrendVo> result = new ArrayList<>();
        Map<String, ApiAccessTrendVo> statMap = new HashMap<>();
        
        // 将数据库查询结果转换为Map，便于查找
        for (Map<String, Object> stat : stats) {
            ApiAccessTrendVo trend = new ApiAccessTrendVo();
            trend.setDate((String) stat.get("date"));
            trend.setCount(((Number) stat.get("count")).intValue());
            trend.setSuccess(((Number) stat.get("success")).intValue());
            trend.setFailed(((Number) stat.get("failed")).intValue());
            trend.setAvgResponseTime(Integer.parseInt(stat.getOrDefault("avgResponseTime", "0").toString()));
            
            statMap.put(trend.getDate(), trend);
        }
        
        // 补充缺失的小时数据
        for (int hour = 0; hour < 24; hour++) {
            String hourStr = String.format("%02d:00", hour);
            if (statMap.containsKey(hourStr)) {
                result.add(statMap.get(hourStr));
            } else {
                // 缺失数据使用0值
                ApiAccessTrendVo trend = new ApiAccessTrendVo();
                trend.setDate(hourStr);
                trend.setCount(0);
                trend.setSuccess(0);
                trend.setFailed(0);
                trend.setAvgResponseTime(0);
                result.add(trend);
            }
        }
        
        return result;
    }
    
    private List<ApiAccessTrendVo> fillMissingDayData(List<Map<String, Object>> stats, LocalDate startDate, int days) {
        List<ApiAccessTrendVo> result = new ArrayList<>();
        Map<String, ApiAccessTrendVo> statMap = new HashMap<>();
        
        // 将数据库查询结果转换为Map，便于查找
        for (Map<String, Object> stat : stats) {
            ApiAccessTrendVo trend = new ApiAccessTrendVo();
            trend.setDate(stat.get("date").toString());
            trend.setCount(((Number) stat.get("count")).intValue());
            trend.setSuccess(((Number) stat.get("success")).intValue());
            trend.setFailed(((Number) stat.get("failed")).intValue());
            trend.setAvgResponseTime(Integer.parseInt(stat.getOrDefault("avgResponseTime", "0").toString()));
            
            statMap.put(trend.getDate(), trend);
        }
        
        // 根据传入的起始日期和天数计算需要填充的数据
        if (days > 0) {
            for (int i = 0; i < days; i++) {
                LocalDate date = startDate.plusDays(i);
                String dateStr = date.format(dateFormatter);
                
                if (statMap.containsKey(dateStr)) {
                    result.add(statMap.get(dateStr));
                } else {
                    // 缺失数据使用0值
                    ApiAccessTrendVo trend = new ApiAccessTrendVo();
                    trend.setDate(dateStr);
                    trend.setCount(0);
                    trend.setSuccess(0);
                    trend.setFailed(0);
                    trend.setAvgResponseTime(0);
                    result.add(trend);
                }
            }
        }
        
        return result;
    }
    
    private List<ApiAccessTrendVo> fillMissingMonthData(List<Map<String, Object>> stats) {
        List<ApiAccessTrendVo> result = new ArrayList<>();
        Map<String, ApiAccessTrendVo> statMap = new HashMap<>();
        
        // 将数据库查询结果转换为Map，便于查找
        for (Map<String, Object> stat : stats) {
            ApiAccessTrendVo trend = new ApiAccessTrendVo();
            trend.setDate((String) stat.get("date"));
            trend.setCount(((Number) stat.get("count")).intValue());
            trend.setSuccess(((Number) stat.get("success")).intValue());
            trend.setFailed(((Number) stat.get("failed")).intValue());
            trend.setAvgResponseTime(Integer.parseInt(stat.getOrDefault("avgResponseTime", "0").toString()));
            
            statMap.put(trend.getDate(), trend);
        }
        
        // 补充最近6个月的数据
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(5);
        
        for (int i = 0; i < 6; i++) {
            LocalDate date = startDate.plusMonths(i);
            String monthStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            
            if (statMap.containsKey(monthStr)) {
                result.add(statMap.get(monthStr));
            } else {
                // 缺失数据使用0值
                ApiAccessTrendVo trend = new ApiAccessTrendVo();
                trend.setDate(monthStr);
                trend.setCount(0);
                trend.setSuccess(0);
                trend.setFailed(0);
                trend.setAvgResponseTime(0);
                result.add(trend);
            }
        }
        
        return result;
    }

    @Override
    public List<RegionAccessVo> getRegionAccessData(String startTime, String endTime, String type) {
        try {
            Map<String, Object> params = new HashMap<>();
            
            if (startTime != null && endTime != null) {
                params.put("startTime", startTime + " 00:00:00");
                params.put("endTime", endTime + " 23:59:59");
            }
            
            List<Map<String, Object>> regionStats = historyOpenapiMapper.selectRegionAccessData(params);
            List<RegionAccessVo> regionData = new ArrayList<>();
            
            for (Map<String, Object> stat : regionStats) {
                String province = (String) stat.get("province");
                Long totalCount = (Long) stat.get("totalCount");
                Long userCount = (Long) stat.get("userCount");
                Long successCount = (Long) stat.get("successCount");
                
                int count = totalCount != null ? totalCount.intValue() : 0;
                int users = userCount != null ? userCount.intValue() : 0;
                int success = successCount != null ? successCount.intValue() : 0;
                double successRate = count > 0 ? (success * 100.0 / count) : 100.0;
                
                RegionAccessVo region = new RegionAccessVo();
                region.setProvince(province);
                region.setCount(count);
                region.setUsers(users);
                region.setSuccessRate(successRate);
                
                regionData.add(region);
            }
            
            // 如果没有查询到数据，返回空列表
            if (regionData.isEmpty()) {
                return new ArrayList<>();
            }
            
            return regionData;
        } catch (Exception e) {
            // 如果查询失败，返回空列表
            return new ArrayList<>();
        }
    }

    @Override
    public List<ApiTopVo> getApiTopList(String startTime, String endTime, String type) {
        try {
            Map<String, Object> params = new HashMap<>();
            if (startTime != null && endTime != null) {
                LocalDateTime start = LocalDateTime.parse(startTime + " 00:00:00", datetimeFormatter);
                LocalDateTime end = LocalDateTime.parse(endTime + " 23:59:59", datetimeFormatter);
                params.put("startTime", start);
                params.put("endTime", end);
            }
            
            List<Map<String, Object>> apiStats = historyOpenapiMapper.selectApiStatistics(params);
            List<ApiTopVo> topData = new ArrayList<>();
            
            if (apiStats != null && !apiStats.isEmpty()) {
                for (Map<String, Object> stat : apiStats) {
                    String resourceName = (String) stat.get("resourceName");
                    String resourceUri = (String) stat.get("resourceUri");
                    Long count = (Long) stat.get("count");
                    Long totalCost = (Long) stat.get("totalCost");
                    Long successCount = (Long) stat.get("successCount");
                    
                    if (resourceName != null && count != null) {
                        ApiTopVo api = new ApiTopVo();
                        api.setApiName(resourceName);
                        api.setPath(resourceUri != null ? resourceUri : "/api/unknown");
                        api.setCount(count.intValue());
                        api.setAvgResponseTime(totalCost != null && count > 0 ? 
                            (int) (totalCost / count) : random.nextInt(400) + 50);
                        api.setSuccessRate(successCount != null && count > 0 ? 
                            (successCount * 100.0 / count) : 95.0);
                        
                        topData.add(api);
                    }
                }
            }
            
            if (topData.isEmpty()) {
                return generateMockApiTopData();
            }
            
            topData.sort((a, b) -> Integer.compare(b.getCount(), a.getCount()));
            return topData;
        } catch (Exception e) {
            return generateMockApiTopData();
        }
    }
    
    private List<ApiTopVo> generateMockApiTopData() {
        return new ArrayList<>();
    }
}