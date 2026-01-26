package org.dromara.surpass.web.app.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.mybatis.jpa.entity.JpaPageResults;
import org.dromara.surpass.crypto.password.PasswordReciprocal;
import org.dromara.surpass.entity.ClientPermission;
import org.dromara.surpass.entity.Message;
import org.dromara.surpass.entity.RegisteredClient;
import org.dromara.surpass.entity.dto.RegisteredClientChangeDto;
import org.dromara.surpass.entity.dto.RegisteredClientPageDto;
import org.dromara.surpass.persistence.service.ClientPermissionService;
import org.dromara.surpass.persistence.service.RegisteredClientService;
import org.dromara.surpass.validate.AddGroup;
import org.dromara.surpass.validate.EditGroup;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:
 * @author: orangeBabu
 * @time: 2025/12/9 16:51
 */

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class RegisteredClientController {

    private final RegisteredClientService clientService;

    private final ClientPermissionService clientRelationService;

    @PostMapping("/add")
    public Message<String> addApp(@Validated(value = AddGroup.class) @RequestBody RegisteredClientChangeDto dto) {
        return clientService.create(dto);
    }

    @PutMapping("/update")
    public Message<String> updateApp(@Validated(value = EditGroup.class) @RequestBody RegisteredClientChangeDto dto) {
        return clientService.updateApp(dto);
    }

    @GetMapping("/get/{id}")
    public Message<RegisteredClient> get(@PathVariable("id") String id) {
    	RegisteredClient client = clientService.get(id);
    	String clientSecret = PasswordReciprocal.getInstance().decoder(client.getClientSecret());
    	client.setClientSecret(clientSecret);
    	return Message.ok(client);
    }

    @GetMapping("/generate/{id}")
    public Message<RegisteredClient> generate(@PathVariable("id") String id) {
    	RegisteredClient client = clientService.generate(id);
    	return Message.ok(client);
    }

    @DeleteMapping(value = {"/delete"}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Message<String> delete(@RequestParam("ids") List<String> ids) {
       return clientService.deleteClient(ids);
    }

    @GetMapping("/list")
    public Message<JpaPageResults<RegisteredClient>> pageList(@ParameterObject RegisteredClientPageDto dto) {
        return Message.ok(clientService.fetchPageResults(dto));
    }

    @GetMapping("/relate-app/{clientId}")
    public Message<List<ClientPermission>> getClientApps(@PathVariable("clientId") String clientId) {
        return Message.ok(clientRelationService.getClientApps(clientId));
    }
}
