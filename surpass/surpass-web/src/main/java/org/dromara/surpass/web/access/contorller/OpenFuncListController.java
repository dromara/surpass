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






package org.dromara.surpass.web.access.contorller;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import org.dromara.surpass.authn.jwt.service.AuthTokenService;
import org.dromara.surpass.authn.session.SessionManager;
import org.dromara.surpass.authn.web.AuthorizationUtils;
import org.dromara.surpass.entity.Message;
import org.dromara.surpass.entity.idm.UserInfo;
import org.dromara.surpass.entity.permissions.Resources;
import org.dromara.surpass.entity.vo.AppResourcesVo;
import org.dromara.surpass.persistence.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "获取应用功能权限 API文档模块")
@RestController
@RequestMapping(value={"/open/func"})
public class OpenFuncListController {

	/**
	 * 会话管理
	 */
	@Autowired
	SessionManager sessionManager;
	/**
	 * 认证令牌服务
	 */
	@Autowired
	AuthTokenService authTokenService ;

	@Autowired
    LoginService loginService;

    @Operation(summary = "获取应用功能权限", description = "传递参数appId和token",method="GET")
    @GetMapping(value = "/list")
    public Message<AppResourcesVo> getFunctionsList(@RequestParam String appId,HttpServletRequest request) throws ParseException {
    	AuthorizationUtils.authenticate(request, authTokenService, sessionManager);
    	UserInfo user = AuthorizationUtils.getUserInfo();
    	if(user != null) {
	    	Set<Resources> functions  = loginService.getResourcesBySubject(user);
	    	return new Message<>(new AppResourcesVo(functions));
    	}else {
    		return new Message<>(new AppResourcesVo(new HashSet<>()));
    	}
    }

}
