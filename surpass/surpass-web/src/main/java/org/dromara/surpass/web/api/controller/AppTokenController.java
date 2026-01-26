package org.dromara.surpass.web.api.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.mybatis.jpa.entity.JpaPageResults;
import org.dromara.surpass.entity.Message;
import org.dromara.surpass.entity.app.App;
import org.dromara.surpass.entity.app.dto.AppChangeDto;
import org.dromara.surpass.entity.app.dto.AppPageDto;
import org.dromara.surpass.persistence.service.AppService;
import org.dromara.surpass.validate.AddGroup;
import org.dromara.surpass.validate.EditGroup;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: orangeBabu
 * @time: 2025/11/26 15:29
 */

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class AppTokenController {

    private final AppService appService;

    @PostMapping("/token")
    public Message<Map<String, Object>> getToken(@RequestParam String clientId,
                                                        @RequestParam String clientSecret) {

    /*    App app = appService.findByClientId(clientId);
        if(app == null || !BCrypt.checkpw(clientSecret, app.getClientSecret())){
            return new Message<>(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }

        String token = appService.issueToken(app);*/

        Map<String,Object> result = new HashMap<>();
//        result.put("access_token", token);
        result.put("token_type", "Bearer");
//        result.put("expires_in", app.getTtlSeconds());
        return Message.ok(result);
    }

    @GetMapping("/list")
    public Message<JpaPageResults<App>> pageList(@ParameterObject AppPageDto dto) {
        return Message.ok(appService.fetchPageResults(dto));
    }

    @PostMapping("/add")
    public Message<String> addApp(@Validated(value = AddGroup.class) @RequestBody AppChangeDto dto) {
        return appService.create(dto);
    }

    @PutMapping("/update")
    public Message<String> updateApp(@Validated(value = EditGroup.class) @RequestBody AppChangeDto dto) {
        return appService.updateApp(dto);
    }

    @GetMapping("/get/{id}")
    public Message<App> get(@PathVariable("id") String id) {
        return Message.ok(appService.get(id));
    }

    @DeleteMapping(value = {"/delete"}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Message<String> delete(@RequestParam("ids") List<String> ids) {
       return appService.deleteApp(ids);
    }

}
