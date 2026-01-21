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




package org.dromara.surpass.entity.dto;

import java.util.ArrayList;
import java.util.List;

public class QueryAppResourceDto {

	String userId;
	
	String appId;

	List<String> orgIds;


	List<String> roleIds;

	public QueryAppResourceDto(String userId,String appId) {
		super();
		this.userId = userId;
		this.appId = appId;
		roleIds = new ArrayList<>();
		orgIds = new ArrayList<>();
	}


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<String> getOrgIds() {
		return orgIds;
	}

	public void setOrgIds(List<String> orgIds) {
		this.orgIds = orgIds;
	}

	public List<String> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<String> roleIds) {
		this.roleIds = roleIds;
	}

}
