package org.dromara.surpass.web.app.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.hutool.core.tree.MapTree;
import org.dromara.mybatis.jpa.entity.JpaPageResults;
import org.dromara.mybatis.jpa.query.LambdaQuery;
import org.dromara.surpass.entity.ClientPermission;
import org.dromara.surpass.entity.Message;
import org.dromara.surpass.entity.app.App;
import org.dromara.surpass.entity.app.AppResources;
import org.dromara.surpass.entity.app.dto.AppResourcesChangeDto;
import org.dromara.surpass.entity.app.dto.AppResourcesPageDto;
import org.dromara.surpass.entity.app.dto.ClientAuthzDto;
import org.dromara.surpass.persistence.service.AppResourcesService;
import org.dromara.surpass.persistence.service.AppService;
import org.dromara.surpass.persistence.service.ClientPermissionService;
import org.dromara.surpass.validate.AddGroup;
import org.dromara.surpass.validate.EditGroup;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @GetMapping("/get/{id}")
    public Message<AppResources> get(@PathVariable String id) {
        AppResources appResources = appResourcesService.get(id);
        if (Objects.nonNull(appResources)) {
            App app = appService.get(appResources.getAppId());
            if (Objects.nonNull(app)) {
                appResources.setContextPath(app.getContextPath());
                appResources.setBelongApp(app.getAppName());
            }
        }
        return Message.ok(appResources);
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
}
