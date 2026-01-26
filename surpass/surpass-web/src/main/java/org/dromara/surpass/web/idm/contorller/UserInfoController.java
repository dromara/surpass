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

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.mybatis.jpa.entity.JpaPageResults;
import org.dromara.mybatis.jpa.query.LambdaQuery;
import org.dromara.surpass.authn.annotation.CurrentUser;
import org.dromara.surpass.authn.secretkey.SecretKeyManager;
import org.dromara.surpass.authn.session.SessionManager;
import org.dromara.surpass.constants.*;
import org.dromara.surpass.entity.*;
import org.dromara.surpass.entity.idm.UserInfo;
import org.dromara.surpass.entity.idm.dto.UserInfoPageDto;
import org.dromara.surpass.persistence.service.*;
import org.dromara.surpass.validate.AddGroup;
import org.dromara.surpass.validate.EditGroup;
import org.dromara.surpass.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Crystal.Sea
 *
 */
@RestController
@RequestMapping(value = { "/users" })
public class UserInfoController {
	static final Logger logger = LoggerFactory.getLogger(UserInfoController.class);

	@Autowired
	UserInfoService userInfoService;

	@Autowired
	UserInfoExcelService userInfoExcelService;

	@Autowired
	SecretKeyManager secretKeyManager;

	@Autowired
	SessionManager sessionManager;

	@Autowired
	HistorySystemLogsService historySystemLogsService;

	@GetMapping(value = { "/fetch" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<JpaPageResults<UserInfo>> fetch(@ParameterObject UserInfoPageDto dto, @CurrentUser UserInfo currentUser) {
		logger.debug("fetch {}",dto);
		return userInfoService.fetchPageResults(dto);
	}

	@GetMapping(value={"/query"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<UserInfo> query(@ParameterObject UserInfoPageDto dto, @CurrentUser UserInfo currentUser) {
		logger.debug("-query  : {}" , dto);

		LambdaQuery<UserInfo> wrapper = new LambdaQuery<>();
		if (ObjectUtils.isNotEmpty(userInfoService.query(wrapper))) {
			 return new Message<>(Message.SUCCESS);
		} else {
			 return new Message<>(Message.FAIL);
		}
	}

	/**
	 * 获取登录用户信息
	 * 需要token头，获取当前token对应的用户对象
	 * @return
	 */
	@GetMapping(value = { "/currentUser" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<UserInfo> currentUser(@CurrentUser UserInfo currentUser) {
		if (Objects.isNull(currentUser)) {
			return new Message<>(Message.FAIL);
		}
		UserInfo userInfo = userInfoService.get(currentUser.getId());
		userInfo.clearSensitive();
		return new Message<>(userInfo);
	}

	@GetMapping(value = { "/get/{id}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<UserInfo> get(@PathVariable("id") String id) {
		UserInfo userInfo=userInfoService.get(id);
		userInfo.clearSensitive();
		return new Message<>(userInfo);
	}

	@GetMapping(value = { "/getByUsername/{username}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<UserInfo> getByUsername(@PathVariable("username") String username) {
		UserInfo userInfo=userInfoService.findByUsername(username);
		userInfo.clearSensitive();
		return new Message<>(userInfo);
	}

	@PostMapping(value={"/add"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<UserInfo> insert(@Validated(value = AddGroup.class) @RequestBody UserInfo userInfo,@CurrentUser UserInfo currentUser) {
		logger.debug("-Add  : {}" , userInfo);
		userInfo.setId(WebContext.genId());
		userInfo.setInstId(currentUser.getInstId());
		userInfo.setCreatedBy(currentUser.getId());
		userInfo.setCreatedDate(new Date());
		if (userInfoService.saveOneUser(userInfo)) {
			historySystemLogsService.log(
					ConstsEntryType.USERINFO,
					userInfo,
					ConstsAct.CREATE,
					ConstsActResult.SUCCESS,
					currentUser);
			return new Message<>(Message.SUCCESS);
		} else {
			return new Message<>(Message.FAIL);
		}
	}

	@PutMapping(value={"/update"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<UserInfo> update(@Validated(value = EditGroup.class) @RequestBody  UserInfo userInfo, @CurrentUser UserInfo currentUser) {
		logger.debug("-update  : {}" , userInfo);

		if (userInfoService.updateOneUser(userInfo)) {
			historySystemLogsService.log(
					ConstsEntryType.USERINFO,
					userInfo,
					ConstsAct.UPDATE,
					ConstsActResult.SUCCESS,
					currentUser);
		    return new Message<>(Message.SUCCESS);
		} else {
			return new Message<>(Message.FAIL);
		}
	}

	@DeleteMapping(value={"/delete"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<UserInfo> delete(@RequestParam("ids") List<String> ids,@CurrentUser UserInfo currentUser) {
		logger.debug("-delete  ids : {} " , ids);

		if (userInfoService.deleteBatch(ids)) {
			historySystemLogsService.log(
					ConstsEntryType.USERINFO,
					ids,
					ConstsAct.DELETE,
					ConstsActResult.SUCCESS,
					currentUser);
			 return new Message<>(Message.SUCCESS);
		} else {
			return new Message<>(Message.FAIL);
		}
	}

    @GetMapping(value = "/randomPassword", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Message<String> randomPassword() {
        return new Message<>(userInfoService.randomPassword());
    }


	@PutMapping(value="/changePassword", produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<UserInfo> changePassword(
			@Validated(value = EditGroup.class)
			@RequestBody ChangePassword changePassword,
			@CurrentUser UserInfo currentUser) {
		logger.debug("UserId {}",changePassword.getUserId());
		if(StringUtils.isNotBlank(changePassword.getSecretKey())) {
			try {
				changePassword.setPassword( secretKeyManager.decrypt(changePassword.getSecretKey(), changePassword.getPassword()));
				changePassword.setConfirmPassword( secretKeyManager.decrypt(changePassword.getSecretKey(), changePassword.getConfirmPassword()));
			} catch (Exception e) {
				logger.error("changePassword Exception",e);
			}
		}
		changePassword.setPasswordSetType(ConstsPasswordSetType.PASSWORD_NORMAL);
		if(userInfoService.changePassword(changePassword,true)) {
			historySystemLogsService.log(
					ConstsEntryType.USERINFO,
					changePassword,
					ConstsAct.CHANGE_PASSWORD,
					ConstsActResult.SUCCESS,
					currentUser);
			return new Message<>(Message.SUCCESS);
		} else {
			return new Message<>(Message.FAIL);
		}
	}

	@GetMapping(value = { "/updateStatus" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<UserInfo> updateStatus(@ModelAttribute UserInfo userInfo,@CurrentUser UserInfo currentUser) {
		logger.debug("updateStatus {}",userInfo);
		UserInfo loadUserInfo = userInfoService.get(userInfo.getId());
		userInfo.setUsername(loadUserInfo.getUsername());
		userInfo.setDisplayName(loadUserInfo.getDisplayName());
		if(userInfoService.updateStatus(userInfo)) {
			historySystemLogsService.log(
					ConstsEntryType.USERINFO,
					userInfo,
					ConstsAct.statusActon.get(userInfo.getStatus()),
					ConstsActResult.SUCCESS,
					currentUser);
			return new Message<>(Message.SUCCESS);
		} else {
			return new Message<>(Message.FAIL);
		}
	}

    @RequestMapping(value = "/import")
    public Message<UserInfo> importUsers(
    		@ModelAttribute("excelImportFile")ExcelImport excelImportFile,
    		@CurrentUser UserInfo currentUser)  {
    	userInfoExcelService.importFromExcel(excelImportFile,currentUser);
        return new Message<>(Message.FAIL);

    }


	@GetMapping(value = "/export/{type}")
	public void exportOrganizations(@ModelAttribute UserInfo userInfo,
									@PathVariable("type") String type,
									HttpServletResponse response,
									@CurrentUser UserInfo currentUser)  {
		userInfoExcelService.exportToExcel(type,userInfo,response);
	}

	@InitBinder
	public void binder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
		    @Override
			public void setAsText(String value) {
		        	if(StringUtils.isEmpty(value)){
		        		setValue(null);
		        	}else{
		        		setValue(value);
		        	}
		    }

		});
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        dateFormat.setLenient(false);
	        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
}
