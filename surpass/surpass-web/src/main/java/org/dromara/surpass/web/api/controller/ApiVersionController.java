package org.dromara.surpass.web.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dromara.mybatis.jpa.query.LambdaQuery;
import org.dromara.surpass.entity.Message;
import org.dromara.surpass.entity.api.ApiVersion;
import org.dromara.surpass.enums.ApiVersionStatus;
import org.dromara.surpass.persistence.service.ApiVersionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/versions")
@RequiredArgsConstructor
public class ApiVersionController {

    private final ApiVersionService apiVersionService;

    @GetMapping("/api/{apiId}")
    public Message<List<ApiVersion>> getByApiId(@PathVariable String apiId) {
        LambdaQuery<ApiVersion> wrapper = new LambdaQuery<>();
        wrapper.eq(ApiVersion::getApiId, apiId);
        List<ApiVersion> versions = apiVersionService.query(wrapper);
        return Message.ok(versions);
    }

    @GetMapping("/{id}")
    public Message<ApiVersion> getById(@PathVariable String id) {
        return Message.ok(apiVersionService.get(id));
    }

    @GetMapping("/api/{apiId}/published")
    public Message<ApiVersion> getPublishedVersion(@PathVariable String apiId) {
        return Message.ok(apiVersionService.findPublishedVersionByApiId(apiId));
    }

    @PostMapping
    public Message<ApiVersion> create(@Valid @RequestBody ApiVersion apiVersion) {
        return apiVersionService.createNewVersion(apiVersion);
    }

    @PutMapping("/{id}")
    public Message<ApiVersion> update(@PathVariable String id, @Valid @RequestBody ApiVersion apiVersion) {
        apiVersion.setId(id);
        return apiVersionService.updateVersion(apiVersion) ? Message.ok(apiVersion) : Message.failed("修改失败");
    }

    @DeleteMapping("/{id}")
    public Message<String> delete(@PathVariable String id) {

        return apiVersionService.delete(id) ?  Message.ok("删除成功") : Message.failed("删除失败");
    }

    @PutMapping("/{id}/status")
    public Message<String> updateStatus(@PathVariable String id, @RequestParam ApiVersionStatus status) {
        apiVersionService.updateStatus(id, status);
        return Message.ok("修改成功");
    }

    @GetMapping("/diff")
    public Message<String> getDiff(@RequestParam String oldVersionId, @RequestParam String newVersionId) {
        String diff = apiVersionService.generateDiff(oldVersionId, newVersionId);
        return Message.ok(diff);
    }

    @GetMapping("/api/{apiId}/statistics")
    public Message<Map<String, Long>> getVersionStatistics(@PathVariable String apiId) {
        Map<String, Long> statistics = apiVersionService.getVersionStatusStatistics(apiId);
        return Message.ok(statistics);
    }
}
