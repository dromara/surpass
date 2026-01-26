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






package org.dromara.surpass.web.historys.contorller;
import org.dromara.mybatis.jpa.entity.JpaPageResults;
import org.dromara.mybatis.jpa.query.LambdaQuery;
import org.dromara.surpass.authn.annotation.CurrentUser;
import org.dromara.surpass.entity.Message;
import org.dromara.surpass.entity.history.HistoryLogin;
import org.dromara.surpass.entity.history.dto.HistoryLoginPageDto;
import org.dromara.surpass.entity.idm.UserInfo;
import org.dromara.surpass.persistence.service.HistoryLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录日志查询
 *
 * @author Crystal.sea
 *
 */

@RestController
@RequestMapping(value={"/historys"})
public class LoginHistoryController {
	static final Logger logger = LoggerFactory.getLogger(LoginHistoryController.class);

	@Autowired
	HistoryLoginService historyLoginService;

	/**
	 * @param dto
	 * @return
	 */
	@GetMapping(value={"/loginHistory/fetch"})
	public Message<JpaPageResults<HistoryLogin>> fetch(
				@ModelAttribute("historyLogin") HistoryLoginPageDto dto,
				@CurrentUser UserInfo currentUser
			){
		logger.debug("historys/loginHistory/fetch/ {}",dto);

		LambdaQuery<HistoryLogin> wrapper = new LambdaQuery<>();

		dto.build();
		return new Message<>(historyLoginService.fetch(dto, wrapper));
	}

}
