/*
 * Copyright [2025] [JinBooks of copyright http://www.jinbooks.com]
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

package org.dromara.surpass.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.dromara.surpass.authn.annotation.CurrentUser;
import org.dromara.surpass.entity.Message;
import org.dromara.surpass.entity.idm.UserInfo;
import org.dromara.surpass.entity.report.vo.ApiAccessTrendVo;
import org.dromara.surpass.entity.report.vo.ApiTopVo;
import org.dromara.surpass.entity.report.vo.DashboardStatisticsVo;
import org.dromara.surpass.entity.report.vo.RegionAccessVo;
import org.dromara.surpass.persistence.service.DashboardStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Dashboard Controller
 * @author Crystal.Sea
 *
 */
@RestController
@RequestMapping("/statistics")
@Tag(name = "Dashboard Statistics API", description = "仪表板统计相关接口")
public class DashboardController {

	private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

	@Autowired
	private DashboardStatisticsService dashboardStatisticsService;

	@Operation(summary = "获取仪表板统计数据", description = "获取系统仪表板完整统计数据")
	@ApiResponse(responseCode = "200", description = "成功返回仪表板统计数据",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = Message.class)))
	@GetMapping(value = "/dashboard", produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<DashboardStatisticsVo> getStatisticsDashboard(@CurrentUser UserInfo currentUser) {
		logger.debug("DashboardController /statistics/dashboard.");
		DashboardStatisticsVo data = dashboardStatisticsService.getDashboardStatistics();
		return new Message<>(data);
	}

	@Operation(summary = "获取API访问趋势数据", description = "获取指定时间范围内的API访问趋势数据")
	@ApiResponse(responseCode = "200", description = "成功返回API访问趋势数据",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = Message.class)))
	@GetMapping(value = "/api-access-trend", produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<List<ApiAccessTrendVo>> getApiAccessTrend(
			@Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
			@Parameter(description = "结束时间") @RequestParam(required = false) String endTime,
			@Parameter(description = "时间类型") @RequestParam(required = false) String type) {
		logger.debug("DashboardController /statistics/api-access-trend.");
		List<ApiAccessTrendVo> trendData = dashboardStatisticsService.getApiAccessTrend(startTime, endTime, type);
		return new Message<>(trendData);
	}

	@Operation(summary = "获取区域访问数据", description = "获取指定时间范围内的区域访问统计数据")
	@ApiResponse(responseCode = "200", description = "成功返回区域访问数据",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = Message.class)))
	@GetMapping(value = "/region-access", produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<List<RegionAccessVo>> getRegionAccessData(
			@Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
			@Parameter(description = "结束时间") @RequestParam(required = false) String endTime,
			@Parameter(description = "时间类型") @RequestParam(required = false) String type) {
		logger.debug("DashboardController /statistics/region-access.");
		List<RegionAccessVo> regionData = dashboardStatisticsService.getRegionAccessData(startTime, endTime, type);
		return new Message<>(regionData);
	}

	@Operation(summary = "获取热门API列表", description = "获取指定时间范围内的热门API调用统计")
	@ApiResponse(responseCode = "200", description = "成功返回热门API数据",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = Message.class)))
	@GetMapping(value = "/api-top", produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<List<ApiTopVo>> getApiTopList(
			@Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
			@Parameter(description = "结束时间") @RequestParam(required = false) String endTime,
			@Parameter(description = "时间类型") @RequestParam(required = false) String type) {
		logger.debug("DashboardController /statistics/api-top.");

		List<ApiTopVo> topData = dashboardStatisticsService.getApiTopList(startTime, endTime, type);
		return new Message<>(topData);
	}

	@Operation(summary = "获取应用数量", description = "获取系统应用总数")
	@ApiResponse(responseCode = "200", description = "成功返回应用数量",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = Message.class)))
	@GetMapping(value = "/app-count", produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<Integer> getAppCount() {
		logger.debug("DashboardController /statistics/app-count.");
		return new Message<>(dashboardStatisticsService.getAppCount());
	}

	@Operation(summary = "获取角色数量", description = "获取系统角色总数")
	@ApiResponse(responseCode = "200", description = "成功返回角色数量",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = Message.class)))
	@GetMapping(value = "/role-count", produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<Integer> getRoleCount() {
		logger.debug("DashboardController /statistics/role-count.");
		return new Message<>(dashboardStatisticsService.getRoleCount());
	}

	@Operation(summary = "获取用户数量", description = "获取系统用户总数")
	@ApiResponse(responseCode = "200", description = "成功返回用户数量",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = Message.class)))
	@GetMapping(value = "/user-count", produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<Integer> getUserCount() {
		logger.debug("DashboardController /statistics/user-count.");
		return new Message<>(dashboardStatisticsService.getUserCount());
	}

	@Operation(summary = "获取客户端数量", description = "获取系统客户端总数")
	@ApiResponse(responseCode = "200", description = "成功返回客户端数量",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = Message.class)))
	@GetMapping(value = "/client-count", produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<Integer> getClientCount() {
		logger.debug("DashboardController /statistics/client-count.");
		return new Message<>(dashboardStatisticsService.getClientCount());
	}

	@Operation(summary = "获取数据源数量", description = "获取系统数据源总数")
	@ApiResponse(responseCode = "200", description = "成功返回数据源数量",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = Message.class)))
	@GetMapping(value = "/datasource-count", produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<Integer> getDatasourceCount() {
		logger.debug("DashboardController /statistics/datasource-count.");
		return new Message<>(dashboardStatisticsService.getDatasourceCount());
	}


}
