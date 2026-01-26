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

import java.util.Date;

import org.dromara.mybatis.jpa.entity.JpaPageResults;
import org.dromara.mybatis.jpa.service.impl.JpaServiceImpl;
import org.dromara.surpass.entity.Message;
import org.dromara.surpass.entity.history.HistoryOpenapi;
import org.dromara.surpass.entity.history.dto.HistoryOpenapiPageDto;
import org.dromara.surpass.persistence.mapper.HistoryOpenapiMapper;
import org.dromara.surpass.persistence.service.HistoryOpenapiService;
import org.dromara.surpass.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HistoryOpenapiServiceImpl  extends JpaServiceImpl<HistoryOpenapiMapper,HistoryOpenapi> implements HistoryOpenapiService {

	private static Logger logger = LoggerFactory.getLogger(HistoryOpenapiServiceImpl.class);

	@Override
    public void insertHistory(HistoryOpenapi historyOpenapi) {
		historyOpenapi.setId(WebContext.genId());
        //Thread insert HistoryOpenapi
        new Thread(new HistoryOpenapiRunnable(this,historyOpenapi)).start();
    }

	@Override
	public Message<JpaPageResults<HistoryOpenapi>> page(HistoryOpenapiPageDto dto) {
		dto.build();
		JpaPageResults<HistoryOpenapi> jpaPageResults = (JpaPageResults<HistoryOpenapi>) this.buildPageResults(dto, getMapper().pageList(dto));
		return Message.ok(jpaPageResults);
	}

	public class HistoryOpenapiRunnable implements Runnable{

		HistoryOpenapiServiceImpl service;

		HistoryOpenapi historyOpenapi;

		public HistoryOpenapiRunnable(HistoryOpenapiServiceImpl historyLoginService, HistoryOpenapi historyOpenapi) {
			super();
			this.service = historyLoginService;
			this.historyOpenapi = historyOpenapi;
		}

		@Override
	    public void run() {
			logger.debug(" historyLogin {}" , historyOpenapi);
			historyOpenapi.setAccessTime(new Date());
			service.insert(historyOpenapi);
		}
	}

}
