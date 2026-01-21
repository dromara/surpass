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


package org.dromara.surpass.persistence.service;

import java.util.List;
import java.util.Set;

import org.dromara.mybatis.jpa.service.IJpaService;
import org.dromara.surpass.entity.dto.QueryAppResourceDto;
import org.dromara.surpass.entity.idm.UserInfo;
import org.dromara.surpass.entity.permissions.Resources;

public interface AuthzResourceService  extends IJpaService<UserInfo> {

	public Set<Resources> getResourcesBySubject(UserInfo user);

	public List<Resources> queryResourcesByRoleId(QueryAppResourceDto dto);
	
	public Set<Resources> getAppResourcesBySubject(String userId,String appId);
	
	public List<Resources> queryAppResourcesByRoleId(QueryAppResourceDto dto)  ;

}
