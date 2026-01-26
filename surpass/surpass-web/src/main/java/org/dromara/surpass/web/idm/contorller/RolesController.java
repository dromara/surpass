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






package org.dromara.surpass.web.idm.contorller;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.mybatis.jpa.entity.JpaPageResults;
import org.dromara.mybatis.jpa.query.LambdaQuery;
import org.dromara.surpass.authn.annotation.CurrentUser;
import org.dromara.surpass.constants.ConstsAct;
import org.dromara.surpass.constants.ConstsActResult;
import org.dromara.surpass.constants.ConstsEntryType;
import org.dromara.surpass.entity.Message;
import org.dromara.surpass.entity.RoleAppResourcesPermission;
import org.dromara.surpass.entity.idm.UserInfo;
import org.dromara.surpass.entity.permissions.Roles;
import org.dromara.surpass.entity.permissions.dto.RolesPageDto;
import org.dromara.surpass.persistence.service.HistorySystemLogsService;
import org.dromara.surpass.persistence.service.RoleAppResourcesPermissionService;
import org.dromara.surpass.persistence.service.RolesService;
import org.dromara.surpass.validate.AddGroup;
import org.dromara.surpass.validate.EditGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value={"/idm/groups"})
public class RolesController {
	static final Logger logger = LoggerFactory.getLogger(RolesController.class);

	@Autowired
	RolesService groupsService;

	@Autowired
	HistorySystemLogsService historySystemLogsService;

	@Autowired
	RoleAppResourcesPermissionService roleAppResourcesPermissionService;

	@GetMapping(value = { "/fetch" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<JpaPageResults<Roles>> fetch(
			@ParameterObject RolesPageDto dto,
			@CurrentUser UserInfo currentUser) {

		LambdaQuery<Roles> wrapper = new LambdaQuery<>();

		if (StringUtils.isNotEmpty(dto.getRoleName())) {
			wrapper.like(Roles::getRoleName, dto.getRoleName());
		}
		dto.build();
		return new Message<>(Message.SUCCESS, groupsService.fetch(dto, wrapper));
	}

	@GetMapping(value={"/query"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<Roles> query(@ModelAttribute Roles group,@CurrentUser UserInfo currentUser) {
		logger.debug("-query  : {}" , group);
		LambdaQuery<Roles> wrapper = new LambdaQuery<>();
		if (ObjectUtils.isNotEmpty(groupsService.query(wrapper))) {
			 return new Message<>(Message.SUCCESS);
		} else {
			 return new Message<>(Message.FAIL);
		}

	}

	@GetMapping(value = { "/get/{id}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<Roles> get(@PathVariable("id") String id,@CurrentUser UserInfo currentUser) {
		Roles group = groupsService.get(id);
		return new Message<>(group);
	}

	@PostMapping(value={"/add"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<Roles> insert(@Validated(value = AddGroup.class) @RequestBody Roles group, @CurrentUser UserInfo currentUser) {
		logger.debug("-Add  : {}" , group);
		group.setId(group.generateId());
		if(StringUtils.isBlank(group.getRoleCode())) {
			group.setRoleCode(group.getId());
		}
		group.setCreatedBy(currentUser.getId());
		if (groupsService.insert(group)) {
			groupsService.refreshDynamicRoles(group);
		    historySystemLogsService.log(
					ConstsEntryType.ROLE,
					group,
					ConstsAct.CREATE,
					ConstsActResult.SUCCESS,
					currentUser);
		    return new Message<>(Message.SUCCESS);
		} else {
			return new Message<>(Message.FAIL);
		}
	}

	@PutMapping(value={"/update"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<Roles> update(@Validated(value = EditGroup.class) @RequestBody Roles group,@CurrentUser UserInfo currentUser) {
		logger.debug("-update  group : {}" , group);
		if(group.getId().equalsIgnoreCase("ROLE_ALL_USER")) {
			group.setDefaultAllUser();
		}
		group.setModifiedBy(currentUser.getId());
		if (groupsService.update(group)) {
			groupsService.refreshDynamicRoles(group);
		    historySystemLogsService.log(
					ConstsEntryType.ROLE,
					group,
					ConstsAct.UPDATE,
					ConstsActResult.SUCCESS,
					currentUser);
		    return new Message<>(Message.SUCCESS);
		} else {
			return new Message<>(Message.FAIL);
		}
	}

	@DeleteMapping(value={"/delete"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<Roles> delete(@RequestParam("ids") List<String> ids,@CurrentUser UserInfo currentUser) {
		logger.debug("-delete ids : {}" , ids);
		ids.removeAll(Arrays.asList("ROLE_ALL_USER","ROLE_ADMINISTRATORS","-1"));
		//删除角色应用资源关联
		LambdaQuery<RoleAppResourcesPermission> wrapper = new LambdaQuery<>();
		wrapper.in(RoleAppResourcesPermission::getId, ids);
		roleAppResourcesPermissionService.delete(wrapper);
		//删除角色
		if (groupsService.deleteBatch(ids)) {
			historySystemLogsService.log(
					ConstsEntryType.ROLE,
					ids,
					ConstsAct.DELETE,
					ConstsActResult.SUCCESS,
					currentUser);
			 return new Message<>(Message.SUCCESS);
		} else {
			return new Message<>(Message.FAIL);
		}
	}
}
