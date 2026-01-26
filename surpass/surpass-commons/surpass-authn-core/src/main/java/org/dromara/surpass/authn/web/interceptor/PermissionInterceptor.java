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


package org.dromara.surpass.authn.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.dromara.surpass.authn.SignedPrincipal;
import org.dromara.surpass.authn.jwt.service.AuthTokenService;
import org.dromara.surpass.authn.session.SessionManager;
import org.dromara.surpass.authn.web.AuthorizationUtils;
import org.dromara.surpass.configuration.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * 登录认证判断
 *
 * @author Crystal.Sea
 */
@Component
public class PermissionInterceptor implements AsyncHandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(PermissionInterceptor.class);
    /**
     * 系统配置
     */
    @Autowired
    ApplicationConfig applicationConfig;
    /**
     * 会话管理
     */
    @Autowired
    SessionManager sessionManager;
    /**
     * 认证令牌服务
     */
    @Autowired
    AuthTokenService authTokenService;

    /**
     * 是否管理端
     */
    boolean mgmt = false;

    /*
     * 请求前处理
     *  (non-Javadoc)
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.trace("Permission Interceptor .");
        logger.trace("request URL {} ", request.getRequestURI());

        // ① 先走已有用户认证体系（JWT/Congress/Session）
        AuthorizationUtils.authenticate(request, authTokenService, sessionManager);
        SignedPrincipal principal = AuthorizationUtils.getPrincipal();

        // ② 如果现有体系没认出来，再尝试当作第三方 App Token
        String authHeader = request.getHeader("Authorization");
        if (principal == null && StringUtils.isNotBlank(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (!authTokenService.validateAppToken(token)) {
                logger.debug("App token invalid, block request: {}", request.getRequestURI());
                request.getRequestDispatcher("/auth/entrypoint").forward(request, response);
                return false;
            }
            request.getSession().setAttribute("__token", token);

            // 可选：如果你未来要区分 App 身份，可以这里 set principal
            // AuthorizationUtils.setAuthentication(request, new SignedPrincipal(...));
//			principal = AuthorizationUtils.getPrincipal(); // 重新获取一次（如果你塞入了）
        }

        // ③ 仍然没有 principal → 说明用户和第三方都没通过校验
        if (principal == null) {
            logger.trace("No Authentication ... forward to /auth/entrypoint");
            request.getRequestDispatcher("/auth/entrypoint").forward(request, response);
            return false;
        }

        return true;
    }


    /**
     * 设置管理
     *
     * @param mgmt
     */
    public void setMgmt(boolean mgmt) {
        this.mgmt = mgmt;
        logger.debug("Permission for ADMINISTRATORS {}", this.mgmt);
    }

}
