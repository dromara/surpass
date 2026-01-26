package org.dromara.surpass.persistence.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.tree.MapTree;
import org.dromara.hutool.core.tree.TreeNode;
import org.dromara.hutool.core.tree.TreeUtil;
import org.dromara.mybatis.jpa.entity.JpaPageResults;
import org.dromara.mybatis.jpa.query.LambdaQuery;
import org.dromara.mybatis.jpa.query.OrderBy;
import org.dromara.mybatis.jpa.service.impl.JpaServiceImpl;
import org.dromara.mybatis.jpa.update.LambdaUpdateWrapper;
import org.dromara.surpass.entity.ClientPermission;
import org.dromara.surpass.entity.Message;
import org.dromara.surpass.entity.RoleAppResourcesPermission;
import org.dromara.surpass.entity.app.App;
import org.dromara.surpass.entity.app.AppResources;
import org.dromara.surpass.entity.app.dto.AppResourcesChangeDto;
import org.dromara.surpass.entity.app.dto.AppResourcesPageDto;
import org.dromara.surpass.enums.ResourceClassify;
import org.dromara.surpass.exception.BusinessException;
import org.dromara.surpass.persistence.mapper.AppMapper;
import org.dromara.surpass.persistence.mapper.AppResourcesMapper;
import org.dromara.surpass.persistence.service.AppResourcesService;
import org.dromara.surpass.persistence.service.ClientPermissionService;
import org.dromara.surpass.persistence.service.RoleAppResourcesPermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.dromara.surpass.enums.ResourceClassify.OPEN_API;

import java.util.*;

/**
 * @description:
 * @author: orangeBabu
 * @time: 2025/12/18 11:15
 */

@Service
@RequiredArgsConstructor
public class AppResourcesServiceImpl extends JpaServiceImpl<AppResourcesMapper, AppResources> implements AppResourcesService {

    private final AppResourcesMapper appResourcesMapper;

    private final AppMapper appMapper;

    private final ClientPermissionService registeredClientRelationService;

    private final RoleAppResourcesPermissionService roleAppResourcesPermissionService;

    @Override
    public Message<String> create(AppResourcesChangeDto dto) {
        AppResources appResources = BeanUtil.copyProperties(dto, AppResources.class);

        ResourceClassify classify = ResourceClassify.from(dto.getClassify());

        switch (classify) {
            case OPEN_API -> validateOpenApi(appResources);
            case BUTTON   -> validateButton(appResources);
            case MENU     -> validateMenu(appResources);
            case API      -> validateApi(appResources);
        }

        boolean result = super.insert(appResources);
        return result ? Message.ok("新增成功") : Message.failed("新增失败");
    }

    @Override
    public Message<String> updateResources(AppResourcesChangeDto dto) {
        AppResources appResources = BeanUtil.copyProperties(dto, AppResources.class);

        ResourceClassify classify = ResourceClassify.from(dto.getClassify());

        switch (classify) {
            case OPEN_API -> validateOpenApi(appResources);
            case BUTTON   -> validateButton(appResources);
            case MENU     -> validateMenu(appResources);
            case API      -> validateApi(appResources);
        }
        validateParent(appResources);

        boolean result = super.update(appResources);
        return result ? Message.ok("修改成功") : Message.failed("修改失败");
    }

    @Override
    public Message<JpaPageResults<AppResources>> page(AppResourcesPageDto dto) {
        dto.build();
        JpaPageResults<AppResources> jpaPageResults = (JpaPageResults<AppResources>) this.buildPageResults(dto, getMapper().pageList(dto));
        return Message.ok(jpaPageResults);
    }

    @Override
    public Map<String, List<MapTree<String>>> tree(AppResourcesPageDto dto) {
        // 1. 一次性查询该应用下的所有资源
        List<AppResources> allResources = super.query(
                new LambdaQuery<AppResources>()
                        .eq(AppResources::getAppId, dto.getAppId())
        );

        // 2. 构建完整资源树
        List<MapTree<String>> resourceTree = buildTree(allResources);

     /*   // 3. 构建菜单资源树（内存中过滤）
        List<MapTree<String>> menuTree = buildTree(
                allResources.stream()
                        .filter(r -> MENU.getCode().equals(r.getClassify()))
                        .toList()
        );*/

        // 4. 返回结果
        Map<String, List<MapTree<String>>> result = new HashMap<>(2);
        result.put("resources", resourceTree);
//        result.put("resourcesMenu", menuTree);
        return result;
    }

    @Override
    public AppResources findByPathAndMethod(String path, String method, String contextPath) {
        App app = appMapper.findByContextPath(contextPath);

        LambdaQuery<AppResources> wrapper = new LambdaQuery<>();
        wrapper.eq(AppResources::getPath, path);
        wrapper.eq(AppResources::getMethod, method);
        wrapper.eq(AppResources::getAppId, app.getId());
        wrapper.eq(AppResources::getClassify, OPEN_API.getCode());
        wrapper.orderBy(AppResources::getCreatedDate, OrderBy.DESC.getOrder());

        return super.query(wrapper).stream().findFirst().orElse(null);
    }

    @Override
    @Transactional
    public Message<String> deleteResources(List<String> resourcesIds) {
        LambdaUpdateWrapper<AppResources> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(AppResources::getParentId, resourcesIds);
        if (super.count(wrapper) > 0) {
            throw new BusinessException(50001, "请先移除资源的子级菜单再进行删除操作");
        }
        //删除资源客户端关联
        LambdaQuery<ClientPermission> registeredClientRelationLambdaQuery = new LambdaQuery<>();
        registeredClientRelationLambdaQuery.in(ClientPermission::getResourceId, resourcesIds);
        registeredClientRelationService.delete(registeredClientRelationLambdaQuery);
        //删除资源角色关联
        LambdaQuery<RoleAppResourcesPermission> roleAppResourcesPermissionLambdaQuery = new LambdaQuery<>();
        roleAppResourcesPermissionLambdaQuery.in(RoleAppResourcesPermission::getResourceId, resourcesIds);
        roleAppResourcesPermissionService.delete(roleAppResourcesPermissionLambdaQuery);
        //删除资源
        boolean result = super.softDelete(resourcesIds);
        return result ? Message.ok("删除成功") : Message.failed("删除失败");
    }

    private List<MapTree<String>> buildTree(List<AppResources> resources) {
        if (ObjectUtils.isEmpty(resources)) {
            return List.of();
        }

        List<TreeNode<String>> nodes = resources.stream()
                .map(r -> new TreeNode<>(
                        r.getId(),
                        String.valueOf(r.getParentId()),
                        r.getName(),
                        r.getSortIndex()
                ).setExtra(Map.of(
                        "classify", r.getClassify(),
                        "resStyle", r.getResStyle() == null ? "" : r.getResStyle()
                )))
                .toList();

        return TreeUtil.build(nodes, "null");
    }


    private void requireNotBlank(String value, int code, String message) {
        if (StringUtils.isBlank(value)) {
            throw new BusinessException(code, message);
        }
    }

    private void validateOpenApi(AppResources r) {
        isExistDuplicate(r);

        requireNotBlank(r.getDatasourceId(), 50001, "请选择OpenApi所属的数据源");
        requireNotBlank(r.getPath(), 50001, "请填写请求地址");
        requireNotBlank(r.getMethod(), 50001, "请填写请求方式");
    }

    private void validateButton(AppResources r) {
        requireNotBlank(r.getActionType(), 50001, "请填写操作类型");
    }

    private void validateMenu(AppResources r) {
        requireNotBlank(r.getPath(), 50001, "请填写请求路径");

    }

    private void validateParent(AppResources r) {
        if (Objects.nonNull(r.getId())) {
            if (r.getParentId() == null || "0".equals(r.getParentId())) {
                return;
            }
            if (r.getId().equals(r.getParentId())) {
                throw new BusinessException(50001, "父级资源不能选择自己");
            }
            List<String> childIds = appResourcesMapper.selectAllDescendantIds(r.getId());
            if (childIds.contains(r.getParentId())) {
                throw new BusinessException(50001, "父级资源不能选择自己的子级");
            }
        }
    }

    private void validateApi(AppResources r) {
        requireNotBlank(r.getPath(), 50001, "请填写请求地址");
        requireNotBlank(r.getMethod(), 50001, "请填写请求方式");
    }


    private boolean isExistDuplicate(AppResources apiDefinition) {
        LambdaUpdateWrapper<AppResources> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AppResources::getPath, apiDefinition.getPath());
        wrapper.eq(AppResources::getMethod, apiDefinition.getMethod());
        wrapper.eq(AppResources::getAppId, apiDefinition.getAppId());
        if (StringUtils.isNotBlank(apiDefinition.getId())) {
            wrapper.notEq(AppResources::getId, apiDefinition.getId());
        }

        return super.count(wrapper) > 0;
    }
}
