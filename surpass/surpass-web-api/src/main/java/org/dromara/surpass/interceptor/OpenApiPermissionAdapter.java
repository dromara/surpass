/*
 * Copyright [2025] [MaxKey of copyright http://www.maxkey.top]
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
 */
 

package org.dromara.surpass.interceptor;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.dromara.surpass.authn.token.AccessToken;
import org.dromara.surpass.authn.token.TokenManager;
import org.dromara.surpass.authn.web.AuthorizationUtils;
import org.dromara.surpass.constants.ConstsBoolean;
import org.dromara.surpass.crypto.password.PasswordReciprocal;
import org.dromara.surpass.entity.ApiRequestUri;
import org.dromara.surpass.entity.history.HistoryOpenapi;
import org.dromara.surpass.ip2location.IpLocationParser;
import org.dromara.surpass.ip2location.Region;
import org.dromara.surpass.persistence.service.AuthzClientService;
import org.dromara.surpass.persistence.service.HistoryOpenapiService;
import org.dromara.surpass.util.AuthorizationHeader;
import org.dromara.surpass.util.AuthorizationHeaderUtils;
import org.dromara.surpass.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor token和权限处理.
 * @author Crystal.Sea
 *
 */
@Component
public class OpenApiPermissionAdapter  implements AsyncHandlerInterceptor  {
    private static final Logger logger = LoggerFactory.getLogger(OpenApiPermissionAdapter.class);

    static final String PASSWORD = AuthorizationHeader.Credential.BEARER;
    
    @Autowired
    TokenManager tokenManager;
    
    @Autowired
    AuthzClientService authzClientService;
    
    @Autowired
    HistoryOpenapiService historyOpenapiService;
    
    @Autowired
    PasswordReciprocal passwordReciprocal;
    
    @Autowired
    IpLocationParser ipLocationParser;
    
    /*
     * 请求前处理
     *  (non-Javadoc)
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    @Override
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
        logger.trace("API Permission Adapter pre handle");
        boolean authned = false;
        boolean isAccess = false;
        long startTime = System.currentTimeMillis();
        AuthorizationHeader headerCredential = AuthorizationHeaderUtils.resolve(request);
        ApiRequestUri apiRequestUri = WebContext.explainRequestUri(request);
        String ipAddress = WebContext.getRequestIpAddress(request);
        Region region =ipLocationParser.region(ipAddress);
        HistoryOpenapi history = new HistoryOpenapi();
        history.setRequestMethod(apiRequestUri.getHttpMethod());
        history.setRequestUri(apiRequestUri.getRequestPath());
        history.setResourceUri(apiRequestUri.getResourcePath());
        history.setAccessTime(new Date());
        history.setAuthned(ConstsBoolean.NO);
        history.setAccess(ConstsBoolean.NO);
        history.setRequestId(WebContext.genId());
        history.setIpAddr(ipAddress);
        history.setCountry(region.getCountry());
        history.setProvince(region.getProvince());
        history.setCity(region.getCity());
        history.setLocation(region.getAddr());
        AccessToken token = null;
        //判断Authorization
        if(headerCredential != null && StringUtils.isNotBlank(headerCredential.getCredential()) && headerCredential.isBearer() ){
            UsernamePasswordAuthenticationToken authenticationToken = null;
            String accessToken = passwordReciprocal.decoder(headerCredential.getCredential());
            token = tokenManager.get(accessToken);
            if(token != null ) {
                ArrayList<SimpleGrantedAuthority> grantedAuthoritys = new ArrayList<>();
                grantedAuthoritys.add(new SimpleGrantedAuthority("ROLE_USER"));
                User user = new User(token.getClientId(), PASSWORD, grantedAuthoritys);
                authned = true;
                history.setAuthned(ConstsBoolean.YES);
                authenticationToken= new UsernamePasswordAuthenticationToken(user, PASSWORD, grantedAuthoritys);
            }else {
                logger.trace("accessToken {} not exists . ",accessToken);
            }
            
            if(authenticationToken !=null && authenticationToken.isAuthenticated()) {
            	history.setClientId(token.getClientId());
                AuthorizationUtils.setAuthentication(authenticationToken);
                logger.debug("ApiRequestUri {} ",apiRequestUri);
                if(apiRequestUri.getRequestPath().equals("/func/list")) {
                	isAccess = true;
                }else {
                	isAccess = authzClientService.enforce(apiRequestUri,token.getClientId(),history);
                }
            }
        }
        
        if(!authned){
	        logger.trace("No Authentication ... forward to /login");
	        RequestDispatcher dispatcher = request.getRequestDispatcher("/login");
	        dispatcher.forward(request, response);
        }
        long endTime = System.currentTimeMillis();
        long constTime = endTime - startTime;
        history.setAccessCost(constTime);
        historyOpenapiService.insertHistory(history);
        return authned && isAccess;
    }

}
