package org.dromara.surpass.web.app.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.tree.MapTree;
import org.dromara.mybatis.jpa.entity.JpaPageResults;
import org.dromara.mybatis.jpa.query.LambdaQuery;
import org.dromara.surpass.entity.ClientPermission;
import org.dromara.surpass.entity.Message;
import org.dromara.surpass.entity.api.ApiDataSource;
import org.dromara.surpass.entity.api.ApiVersion;
import org.dromara.surpass.entity.app.App;
import org.dromara.surpass.entity.app.AppResources;
import org.dromara.surpass.entity.app.dto.AppResourcesChangeDto;
import org.dromara.surpass.entity.app.dto.AppResourcesPageDto;
import org.dromara.surpass.entity.app.dto.ClientAuthzDto;
import org.dromara.surpass.persistence.service.*;
import org.dromara.surpass.validate.AddGroup;
import org.dromara.surpass.validate.EditGroup;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @description:
 * @author: orangeBabu
 * @time: 2025/12/18 17:08
 */

@RestController
@RequestMapping("/app-resources")
@RequiredArgsConstructor
public class AppResourcesController {
    private final AppResourcesService appResourcesService;

    private final AppService appService;

    private final ClientPermissionService clientPermissionService;

    private final ApiDataSourceService apiDataSourceService;

    private final ApiVersionService apiVersionService;

    @PostMapping("/add")
    public Message<String> addResources(@Validated(value = AddGroup.class) @RequestBody AppResourcesChangeDto dto) {
        return appResourcesService.create(dto);
    }

    @GetMapping("/page")
    public Message<JpaPageResults<AppResources>> page(@ParameterObject AppResourcesPageDto dto) {

        return appResourcesService.page(dto);
    }

    @PutMapping("/update")
    public Message<String> updateResources(@Validated(value = EditGroup.class) @RequestBody AppResourcesChangeDto dto) {

        return appResourcesService.updateResources(dto);
    }

    @DeleteMapping(value = {"/delete"})
    public Message<String> delete(@RequestParam("ids") List<String> ids) {
        return appResourcesService.deleteResources(ids);
    }

    @GetMapping(value={"/tree"})
    public Message<Map<String, List<MapTree<String>>>> tree(@ParameterObject AppResourcesPageDto dto) {
       return Message.ok(appResourcesService.tree(dto));
    }

    @GetMapping(value={"/tree/client"})
    public Message<Map<String, List<MapTree<String>>>> treeByClient(@ParameterObject AppResourcesPageDto dto) {
        return Message.ok(appResourcesService.treeByClient(dto));
    }

    @PostMapping("/clientAuthz")
    public Message<String> clientAuthz(@Validated @RequestBody ClientAuthzDto dto) {
        return clientPermissionService.saveClientAppRelation(dto);
    }

    @GetMapping("/getClientAuthz")
    public Message<List<ClientPermission>> getClientAuthz(@ParameterObject AppResourcesPageDto dto) {
        return clientPermissionService.getClientAuthz(dto);
    }

    @GetMapping("/list")
    public ResponseEntity<List<AppResources>> list() {
        LambdaQuery<AppResources> wrapper = new LambdaQuery<>();
        wrapper.eq(AppResources::getClassify, "openApi");
        List<AppResources> apis = appResourcesService.query(wrapper);
        return ResponseEntity.ok(apis);
    }

    @GetMapping("/get/{id}")
    public Message<AppResources> get(@PathVariable String id) {
        AppResources resource = appResourcesService.get(id);
        if (resource == null) {
            return Message.ok(null);
        }

        enrichResourceWithRelatedData(resource);
        return Message.ok(resource);
    }

    /**
     * enriches the resource with related data from various services
     */
    private void enrichResourceWithRelatedData(AppResources resource) {
        enrichAppInfo(resource);
        enrichParentInfo(resource);
        enrichOpenApiVersion(resource);
        enrichDataSourceInfo(resource);
    }

    /**
     * 填充所属应用信息
     */
    private void enrichAppInfo(AppResources resource) {
        Optional.ofNullable(resource.getAppId())
                .map(appService::get)
                .ifPresent(app -> {
                    resource.setContextPath(app.getContextPath());
                    resource.setBelongApp(app.getAppName());
                });
    }

    /**
     * 填充父级资源信息
     */
    private void enrichParentInfo(AppResources resource) {
        Optional.ofNullable(resource.getParentId())
                .filter(StringUtils::isNotBlank)
                .map(appResourcesService::get)
                .ifPresent(parent -> resource.setParentName(parent.getName()));
    }

    /**
     * 填充OpenApi版本定义信息
     */
    private void enrichOpenApiVersion(AppResources resource) {
        if (!"openApi".equals(resource.getClassify())) {
            return;
        }

        Optional.ofNullable(apiVersionService.findPublishedVersionByApiId(resource.getId()))
                .ifPresent(version -> {
                    resource.setParamDefinition(version.getParamDefinition());
                    resource.setResponseDefinition(version.getResponseDefinition());
                });
    }

    /**
     * 填充数据源信息
     */
    private void enrichDataSourceInfo(AppResources resource) {
        Optional.ofNullable(resource.getDatasourceId())
                .filter(StringUtils::isNotBlank)
                .map(apiDataSourceService::get)
                .ifPresent(dataSource -> resource.setDatasourceName(dataSource.getName()));
    }
}
