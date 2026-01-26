package org.dromara.surpass.persistence.service;

import org.dromara.mybatis.jpa.service.IJpaService;
import org.dromara.surpass.entity.ClientPermission;
import org.dromara.surpass.entity.Message;
import org.dromara.surpass.entity.app.AppResources;
import org.dromara.surpass.entity.app.dto.AppResourcesPageDto;
import org.dromara.surpass.entity.app.dto.ClientAuthzDto;

import java.util.List;

public interface ClientPermissionService extends IJpaService<ClientPermission> {
    List<ClientPermission> getClientApps(String clientId);

    Message<String> saveClientAppRelation(ClientAuthzDto dto);

    Message<List<ClientPermission>> getClientAuthz(AppResourcesPageDto dto);
    /**
     * 读取客户端对应应用资源得权限列表
     * @param clientPermission
     * @return
     */
    public List<AppResources> queryPermissions(ClientPermission clientPermission);
}
