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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.collections.CollectionUtils;
import org.dromara.mybatis.jpa.service.impl.JpaServiceImpl;
import org.dromara.surpass.entity.dto.QueryAppResourceDto;
import org.dromara.surpass.entity.idm.UserInfo;
import org.dromara.surpass.entity.permissions.Resources;
import org.dromara.surpass.entity.permissions.Roles;
import org.dromara.surpass.persistence.mapper.AuthzResourceMapper;
import org.dromara.surpass.persistence.service.AuthzResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AuthzResourceServiceImpl  extends JpaServiceImpl<AuthzResourceMapper,UserInfo> implements AuthzResourceService {
	private static final Logger logger = LoggerFactory.getLogger(AuthzResourceServiceImpl.class);

	@Autowired
	AuthzServiceImpl authzService;

	@Autowired
	AuthzResourceMapper authzResourceMapper;

	public AuthzResourceMapper getMapper() {
		return authzResourceMapper;
	}

	/**
	 * 根据主体获取用户对应得应用资源清单
	 * @param user
	 * @return 资源清单列表
	 */
	@Override
    public Set<Resources> getResourcesBySubject(UserInfo user){
    	logger.debug("user {} , app {}",user);

    	List<CompletableFuture<List<Resources>>> futures = new ArrayList<>();
    	//根据用户读取应用资源
    	QueryAppResourceDto dto = new QueryAppResourceDto(user.getId(),"");

    	//根据用户组获取应用资源
    	List<Roles> listRole = authzService.queryRolesByMembers(user);
    	for(Roles r : listRole) {
    		dto.getRoleIds().add(r.getId());
    	}
    	if (CollectionUtils.isNotEmpty(dto.getRoleIds())) {
	    	CompletableFuture<List<Resources>> subjectRoleResourcesFuture = CompletableFuture.supplyAsync(() -> {
	    		return queryResourcesByRoleId(dto);
	    	});
	    	futures.add(subjectRoleResourcesFuture);
    	}

        @SuppressWarnings("unchecked")
		CompletableFuture<List<Resources>>[] completableFutures = futures.toArray(new CompletableFuture[futures.size()]);

        //合并数据并去重
        CompletableFuture<Set<Resources>> completableFuture =
        		CompletableFuture.allOf(completableFutures).thenApply(result -> {
        			Set<Resources> resourcesList = new HashSet<>();
                	for (CompletableFuture<List<Resources>> future : completableFutures) {
                		resourcesList.addAll(future.join());
                	}
                	return  resourcesList;
                });

    	return completableFuture.join();
    }


	/**
	 * 根据组列表获取资源清单
	 * @param dto
	 * @return
	 */
	@Override
	public List<Resources> queryResourcesByRoleId(QueryAppResourceDto dto) {
		return getMapper().queryResourcesByRoleId(dto);
	}
	
	
	/**
	 * 根据主体获取用户对应得应用资源清单
	 * @param user
	 * @return 资源清单列表
	 */
	@Override
    public Set<Resources> getAppResourcesBySubject(String userId,String appId){
    	logger.debug("user {} , app {}",userId);

    	UserInfo user = new UserInfo();
    	user.setId(userId);
    	List<CompletableFuture<List<Resources>>> futures = new ArrayList<>();
    	//根据用户读取应用资源
    	QueryAppResourceDto dto = new QueryAppResourceDto(userId,appId);

    	//根据用户组获取应用资源
    	List<Roles> listRole = authzService.queryRolesByMembers(user);
    	for(Roles r : listRole) {
    		dto.getRoleIds().add(r.getId());
    	}
    	if (CollectionUtils.isNotEmpty(dto.getRoleIds())) {
	    	CompletableFuture<List<Resources>> subjectRoleResourcesFuture = CompletableFuture.supplyAsync(() -> {
	    		return queryAppResourcesByRoleId(dto);
	    	});
	    	futures.add(subjectRoleResourcesFuture);
    	}

        @SuppressWarnings("unchecked")
		CompletableFuture<List<Resources>>[] completableFutures = futures.toArray(new CompletableFuture[futures.size()]);

        //合并数据并去重
        CompletableFuture<Set<Resources>> completableFuture =
        		CompletableFuture.allOf(completableFutures).thenApply(result -> {
        			Set<Resources> resourcesList = new HashSet<>();
                	for (CompletableFuture<List<Resources>> future : completableFutures) {
                		resourcesList.addAll(future.join());
                	}
                	return  resourcesList;
                });

    	return completableFuture.join();
    }
	/**
	 * 根据appId和userId组列表获取资源清单
	 * @param dto
	 * @return
	 */
	@Override
	public List<Resources> queryAppResourcesByRoleId(QueryAppResourceDto dto) {
		return getMapper().queryAppResourcesByRoleId(dto);
	}

}
