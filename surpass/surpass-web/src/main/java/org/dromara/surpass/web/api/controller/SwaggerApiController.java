package org.dromara.surpass.web.api.controller;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dromara.mybatis.jpa.query.LambdaQuery;
import org.dromara.surpass.entity.api.ApiVersion;
import org.dromara.surpass.entity.app.AppResources;
import org.dromara.surpass.persistence.service.ApiVersionService;
import org.dromara.surpass.persistence.service.AppResourcesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 动态API文档控制器，提供符合SpringDoc规范的API文档接口
 * 完全按照springdoc-api-res-demo.json的结构自定义响应
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v3/api-docs")
public class SwaggerApiController {

    private final AppResourcesService appResourcesService;
    private final ApiVersionService apiVersionService;

    @Value("${server.port:2154}")
    private String serverPort;

    @Value("${server.servlet.context-path:/surpass-api}")
    private String contextPath;

    @Value("${application.title:Surpass}")
    private String applicationTitle;

    @Value("${application.formatted-version:v1.2.0 GA}")
    private String applicationVersion;

    /**
     * 返回所有已发布动态API的OpenAPI文档定义
     * 完全按照springdoc-api-res-demo.json的结构
     */
    @GetMapping(value = "/dynamic-apis")
    public OpenAPI getDynamicApiDocs(HttpServletRequest request) {
        OpenAPI openAPI = new OpenAPI();

        // 1. 设置OpenAPI版本 - 与demo文件一致
        openAPI.setOpenapi("3.0.1");

        // 2. 设置基本信息 - 与demo文件格式一致
        Info info = new Info()
                .title(applicationTitle)
                .description(applicationTitle + "API文档")
                .version(applicationVersion);

        Contact contact = new Contact()
                .name("Surpass")
                .email("Surpass@Surpass.com");
        info.setContact(contact);

        openAPI.setInfo(info);

        // 3. 添加服务器信息 - 与demo文件格式一致
        List<Server> servers = new ArrayList<>();
        Server server = new Server();
        server.setUrl("http://127.0.0.1:" + serverPort + contextPath);
        server.setDescription("Generated server url");
        servers.add(server);
        openAPI.setServers(servers);

        // 4. 设置安全方案 - 与demo文件一致
        List<SecurityRequirement> securityRequirements = new ArrayList<>();
        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList("apiKey");
        securityRequirements.add(securityRequirement);
        openAPI.setSecurity(securityRequirements);

        // 5. 添加标签 - 使用动态API标签
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag().name("动态API").description("动态生成的API接口"));
        openAPI.setTags(tags);

        // 6. 构建路径信息 - 只包含动态API
        Paths paths = buildDynamicApiPaths();
        openAPI.setPaths(paths);

        // 7. 设置组件 - 完全按照demo文件的结构
        Components components = new Components();

        // 7.1 安全方案
        SecurityScheme apiKey = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .name("maxkey")
                .in(SecurityScheme.In.HEADER);
        components.addSecuritySchemes("apiKey", apiKey);

        // 7.2 Schema定义 - 完全按照demo文件
        addDemoSchemas(components);

        openAPI.setComponents(components);

        return openAPI;
    }

    /**
     * 添加demo文件中的Schema定义
     */
    private void addDemoSchemas(Components components) {
        // 1. ApiVersion Schema
        ObjectSchema apiVersionSchema = new ObjectSchema();
        apiVersionSchema.addRequiredItem("apiId");
        apiVersionSchema.addRequiredItem("sqlTemplate");
        apiVersionSchema.addRequiredItem("status");
        apiVersionSchema.addRequiredItem("supportsPaging");
        apiVersionSchema.addRequiredItem("version");

        apiVersionSchema.addProperty("id", new StringSchema());
        apiVersionSchema.addProperty("apiId", new StringSchema());
        apiVersionSchema.addProperty("version", new IntegerSchema().format("int32"));
        apiVersionSchema.addProperty("sqlTemplate", new StringSchema().minLength(1));
        apiVersionSchema.addProperty("paramDefinition", new StringSchema());
        apiVersionSchema.addProperty("responseTemplate", new StringSchema());
        apiVersionSchema.addProperty("status", new IntegerSchema().format("int32"));
        apiVersionSchema.addProperty("description", new StringSchema());
        apiVersionSchema.addProperty("supportsPaging", new IntegerSchema().format("int32"));
        apiVersionSchema.addProperty("pageSizeMax", new IntegerSchema().format("int32"));
        apiVersionSchema.addProperty("rateLimit", new IntegerSchema().format("int32"));
        apiVersionSchema.addProperty("deleted", new StringSchema());
        apiVersionSchema.addProperty("createdBy", new StringSchema());
        apiVersionSchema.addProperty("createdDate", new StringSchema().format("date-time"));
        apiVersionSchema.addProperty("modifiedBy", new StringSchema());
        apiVersionSchema.addProperty("modifiedDate", new StringSchema().format("date-time"));
        components.addSchemas("ApiVersion", apiVersionSchema);

        // 2. MessageApiVersion Schema
        ObjectSchema messageApiVersionSchema = new ObjectSchema();
        messageApiVersionSchema.addProperty("code", new IntegerSchema().format("int32"));
        messageApiVersionSchema.addProperty("message", new StringSchema());
        messageApiVersionSchema.addProperty("timestamp", new StringSchema().format("date-time"));
        messageApiVersionSchema.addProperty("data", new Schema<>().$ref("#/components/schemas/ApiVersion"));
        components.addSchemas("MessageApiVersion", messageApiVersionSchema);

        // 3. DataSource Schema
        ObjectSchema dataSourceSchema = new ObjectSchema();
        dataSourceSchema.addRequiredItem("driverClassName");
        dataSourceSchema.addRequiredItem("name");
        dataSourceSchema.addRequiredItem("password");
        dataSourceSchema.addRequiredItem("status");
        dataSourceSchema.addRequiredItem("url");
        dataSourceSchema.addRequiredItem("username");

        dataSourceSchema.addProperty("id", new StringSchema());
        dataSourceSchema.addProperty("name", new StringSchema().minLength(1));
        dataSourceSchema.addProperty("driverClassName", new StringSchema().minLength(1));
        dataSourceSchema.addProperty("url", new StringSchema().minLength(1));
        dataSourceSchema.addProperty("username", new StringSchema().minLength(1));
        dataSourceSchema.addProperty("password", new StringSchema().minLength(1));
        dataSourceSchema.addProperty("status", new IntegerSchema().format("int32"));
        dataSourceSchema.addProperty("testSql", new StringSchema());
        dataSourceSchema.addProperty("deleted", new StringSchema());
        dataSourceSchema.addProperty("createdBy", new StringSchema());
        dataSourceSchema.addProperty("createdDate", new StringSchema().format("date-time"));
        dataSourceSchema.addProperty("modifiedBy", new StringSchema());
        dataSourceSchema.addProperty("modifiedDate", new StringSchema().format("date-time"));
        components.addSchemas("DataSource", dataSourceSchema);

        // 4. MessageString Schema
        ObjectSchema messageStringSchema = new ObjectSchema();
        messageStringSchema.addProperty("code", new IntegerSchema().format("int32"));
        messageStringSchema.addProperty("message", new StringSchema());
        messageStringSchema.addProperty("timestamp", new StringSchema().format("date-time"));
        messageStringSchema.addProperty("data", new StringSchema());
        components.addSchemas("MessageString", messageStringSchema);

        // 5. ApiDefinition Schema
        ObjectSchema apiDefinitionSchema = new ObjectSchema();
        apiDefinitionSchema.addRequiredItem("datasourceId");
        apiDefinitionSchema.addRequiredItem("method");
        apiDefinitionSchema.addRequiredItem("name");
        apiDefinitionSchema.addRequiredItem("path");

        apiDefinitionSchema.addProperty("id", new StringSchema());
        apiDefinitionSchema.addProperty("name", new StringSchema().minLength(1));
        apiDefinitionSchema.addProperty("path", new StringSchema().minLength(1));
        apiDefinitionSchema.addProperty("method", new StringSchema().minLength(1));
        apiDefinitionSchema.addProperty("description", new StringSchema());
        apiDefinitionSchema.addProperty("datasourceId", new StringSchema());
        apiDefinitionSchema.addProperty("deleted", new StringSchema());
        apiDefinitionSchema.addProperty("createdBy", new StringSchema());
        apiDefinitionSchema.addProperty("createdDate", new StringSchema().format("date-time"));
        apiDefinitionSchema.addProperty("modifiedBy", new StringSchema());
        apiDefinitionSchema.addProperty("modifiedDate", new StringSchema().format("date-time"));
        components.addSchemas("ApiDefinition", apiDefinitionSchema);

        // 6. MessageListDataSource Schema
        ObjectSchema messageListDataSourceSchema = new ObjectSchema();
        messageListDataSourceSchema.addProperty("code", new IntegerSchema().format("int32"));
        messageListDataSourceSchema.addProperty("message", new StringSchema());
        messageListDataSourceSchema.addProperty("timestamp", new StringSchema().format("date-time"));

        ArraySchema dataSourceArray = new ArraySchema();
        dataSourceArray.setItems(new Schema<>().$ref("#/components/schemas/DataSource"));
        messageListDataSourceSchema.addProperty("data", dataSourceArray);
        components.addSchemas("MessageListDataSource", messageListDataSourceSchema);

        // 7. Message Schema (通用响应)
        ObjectSchema messageSchema = new ObjectSchema();
        messageSchema.addProperty("code", new IntegerSchema().format("int32"));
        messageSchema.addProperty("message", new StringSchema());
        messageSchema.addProperty("timestamp", new StringSchema().format("date-time"));
        messageSchema.addProperty("data", new ObjectSchema());
        components.addSchemas("Message", messageSchema);

        // 8. UserInfo Schema
        ObjectSchema userInfoSchema = new ObjectSchema();
        userInfoSchema.addProperty("sessionId", new StringSchema());
        userInfoSchema.addProperty("id", new StringSchema());
        userInfoSchema.addProperty("username", new StringSchema());
        userInfoSchema.addProperty("password", new StringSchema());
        userInfoSchema.addProperty("decipherable", new StringSchema());
        userInfoSchema.addProperty("sharedSecret", new StringSchema());
        userInfoSchema.addProperty("sharedCounter", new StringSchema());
        userInfoSchema.addProperty("userType", new StringSchema());
        userInfoSchema.addProperty("userState", new StringSchema());
        userInfoSchema.addProperty("displayName", new StringSchema());
        userInfoSchema.addProperty("nickName", new StringSchema());
        userInfoSchema.addProperty("sortIndex", new IntegerSchema().format("int32"));
        userInfoSchema.addProperty("nameZhSpell", new StringSchema().description("名字中文拼音"));
        userInfoSchema.addProperty("nameZhShortSpell", new StringSchema().description("名字中文拼音简称"));
        userInfoSchema.addProperty("pictureId", new StringSchema());
        userInfoSchema.addProperty("email", new StringSchema());
        userInfoSchema.addProperty("emailVerified", new IntegerSchema().format("int32"));
        userInfoSchema.addProperty("mobile", new StringSchema());
        userInfoSchema.addProperty("mobileVerified", new IntegerSchema().format("int32"));
        userInfoSchema.addProperty("passwordQuestion", new StringSchema());
        userInfoSchema.addProperty("passwordAnswer", new StringSchema());
        userInfoSchema.addProperty("passwordLastSetTime", new StringSchema().format("date-time"));
        userInfoSchema.addProperty("badPasswordCount", new IntegerSchema().format("int32"));
        userInfoSchema.addProperty("badPasswordTime", new StringSchema().format("date-time"));
        userInfoSchema.addProperty("unLockTime", new StringSchema().format("date-time"));
        userInfoSchema.addProperty("isLocked", new IntegerSchema().format("int32"));
        userInfoSchema.addProperty("lastLoginTime", new StringSchema().format("date-time"));
        userInfoSchema.addProperty("lastLoginIp", new StringSchema());
        userInfoSchema.addProperty("lastLogoffTime", new StringSchema().format("date-time"));
        userInfoSchema.addProperty("passwordSetType", new IntegerSchema().format("int32"));
        userInfoSchema.addProperty("loginCount", new IntegerSchema().format("int32"));
        userInfoSchema.addProperty("regionHistory", new StringSchema());
        userInfoSchema.addProperty("passwordHistory", new StringSchema());
        userInfoSchema.addProperty("loginFailedCount", new IntegerSchema().format("int32"));
        userInfoSchema.addProperty("loginFailedTime", new StringSchema().format("date-time"));
        userInfoSchema.addProperty("locale", new StringSchema());
        userInfoSchema.addProperty("timeZone", new StringSchema());
        userInfoSchema.addProperty("preferredLanguage", new StringSchema());
        userInfoSchema.addProperty("isOnline", new IntegerSchema().format("int32"));
        userInfoSchema.addProperty("ldapDn", new StringSchema());
        userInfoSchema.addProperty("status", new IntegerSchema().format("int32"));
        userInfoSchema.addProperty("deleted", new StringSchema());
        userInfoSchema.addProperty("description", new StringSchema());
        userInfoSchema.addProperty("createdBy", new StringSchema());
        userInfoSchema.addProperty("createdDate", new StringSchema().format("date-time"));
        userInfoSchema.addProperty("modifiedBy", new StringSchema());
        userInfoSchema.addProperty("modifiedDate", new StringSchema().format("date-time"));
        userInfoSchema.addProperty("instId", new StringSchema());
        userInfoSchema.addProperty("syncId", new StringSchema());
        userInfoSchema.addProperty("syncName", new StringSchema());
        userInfoSchema.addProperty("originId", new StringSchema());
        userInfoSchema.addProperty("originId2", new StringSchema());
        userInfoSchema.addProperty("gradingUserId", new StringSchema());
        components.addSchemas("UserInfo", userInfoSchema);

        // 9. ImageCaptcha Schema
        ObjectSchema imageCaptchaSchema = new ObjectSchema();
        imageCaptchaSchema.addProperty("state", new StringSchema());
        imageCaptchaSchema.addProperty("image", new StringSchema());
        components.addSchemas("ImageCaptcha", imageCaptchaSchema);

        // 10. MessageImageCaptcha Schema
        ObjectSchema messageImageCaptchaSchema = new ObjectSchema();
        messageImageCaptchaSchema.addProperty("code", new IntegerSchema().format("int32"));
        messageImageCaptchaSchema.addProperty("message", new StringSchema());
        messageImageCaptchaSchema.addProperty("timestamp", new StringSchema().format("date-time"));
        messageImageCaptchaSchema.addProperty("data", new Schema<>().$ref("#/components/schemas/ImageCaptcha"));
        components.addSchemas("MessageImageCaptcha", messageImageCaptchaSchema);
    }

    /**
     * 构建动态API的paths
     */
    private Paths buildDynamicApiPaths() {
        Paths paths = new Paths();

        // 获取所有API定义
        LambdaQuery<AppResources> wrapper = new LambdaQuery<>();
        wrapper.eq(AppResources::getClassify, "openApi");
        List<AppResources> apiDefinitions = appResourcesService.query(wrapper);

        for (AppResources apiDefinition : apiDefinitions) {
            // 获取已发布的版本
            ApiVersion publishedVersion = apiVersionService.findPublishedVersionByApiId(apiDefinition.getId());

            if (publishedVersion != null) {
                // 构建路径项
                io.swagger.v3.oas.models.PathItem pathItem = buildPathItem(apiDefinition, publishedVersion);
                paths.addPathItem("/api-v1" + apiDefinition.getPath(), pathItem);
            }
        }

        return paths;
    }

    /**
     * 构建单个路径项
     */
    private io.swagger.v3.oas.models.PathItem buildPathItem(AppResources apiDefinition, ApiVersion apiVersion) {
        io.swagger.v3.oas.models.PathItem pathItem = new io.swagger.v3.oas.models.PathItem();

        // 构建操作
        io.swagger.v3.oas.models.Operation operation = new io.swagger.v3.oas.models.Operation();
        operation.setOperationId("dynamic_" + apiDefinition.getId() + "_" + apiDefinition.getMethod().toLowerCase());
        operation.setSummary(apiDefinition.getName());
        operation.setDescription(apiDefinition.getDescription());
        operation.addTagsItem("动态API");

        // 构建参数
        List<Parameter> parameters = buildParameters(apiDefinition, apiVersion);
        if (!parameters.isEmpty()) {
            operation.setParameters(parameters);
        }

        // 构建响应 - 使用Message Schema
        ApiResponses responses = new ApiResponses();
        ApiResponse successResponse = new ApiResponse();
        successResponse.setDescription("OK");

        Content content = new Content();
        MediaType mediaType = new MediaType();

        // 使用Message Schema
        Schema<?> messageSchema = new Schema<>().$ref("#/components/schemas/Message");
        mediaType.setSchema(messageSchema);
        content.addMediaType("application/json", mediaType);
        successResponse.setContent(content);

        responses.addApiResponse("200", successResponse);
        operation.setResponses(responses);

        // 设置HTTP方法
        String method = apiDefinition.getMethod().toUpperCase();
        switch (method) {
            case "GET":
                pathItem.setGet(operation);
                break;
            case "POST":
                pathItem.setPost(operation);
                // 为POST方法添加请求体
                operation.setRequestBody(buildRequestBody());
                break;
            case "PUT":
                pathItem.setPut(operation);
                // 为PUT方法添加请求体
                operation.setRequestBody(buildRequestBody());
                break;
            case "DELETE":
                pathItem.setDelete(operation);
                break;
        }

        return pathItem;
    }

    /**
     * 构建参数列表
     */
    private List<Parameter> buildParameters(AppResources apiDefinition, ApiVersion apiVersion) {
        List<Parameter> parameters = new ArrayList<>();

        // 提取路径参数
        Pattern pathPattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher pathMatcher = pathPattern.matcher(apiDefinition.getPath());
        while (pathMatcher.find()) {
            String paramName = pathMatcher.group(1);
            PathParameter pathParam = new PathParameter();
            pathParam.setName(paramName);
            pathParam.setRequired(true);
            pathParam.setSchema(new StringSchema());
            parameters.add(pathParam);
        }

        // 如果是GET请求，添加查询参数
        if ("GET".equalsIgnoreCase(apiDefinition.getMethod())) {
            // 添加分页参数
            if (apiVersion.getSupportsPaging() != null && "1".equals(apiVersion.getSupportsPaging())) {
                QueryParameter pageNum = new QueryParameter();
                pageNum.setName("_pageNum");
                pageNum.setSchema(new IntegerSchema());
                parameters.add(pageNum);

                QueryParameter pageSize = new QueryParameter();
                pageSize.setName("_pageSize");
                pageSize.setSchema(new IntegerSchema());
                parameters.add(pageSize);
            }

            // 从SQL模板提取参数
            extractQueryParametersFromSql(apiVersion.getSqlTemplate(), parameters);
        }

        return parameters;
    }

    /**
     * 从SQL模板提取查询参数
     */
    private void extractQueryParametersFromSql(String sqlTemplate, List<Parameter> parameters) {
        if (sqlTemplate == null) return;

        Pattern pattern = Pattern.compile("#\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(sqlTemplate);
        Set<String> paramNames = new HashSet<>();

        while (matcher.find()) {
            String paramName = matcher.group(1);
            // 跳过已处理的分页参数
            if (!"_pageNum".equals(paramName) && !"_pageSize".equals(paramName)) {
                paramNames.add(paramName);
            }
        }

        for (String paramName : paramNames) {
            QueryParameter param = new QueryParameter();
            param.setName(paramName);
            param.setSchema(new StringSchema());
            parameters.add(param);
        }
    }

    /**
     * 构建请求体
     */
    private io.swagger.v3.oas.models.parameters.RequestBody buildRequestBody() {
        io.swagger.v3.oas.models.parameters.RequestBody requestBody = new io.swagger.v3.oas.models.parameters.RequestBody();
        requestBody.setRequired(true);

        Content content = new Content();
        MediaType mediaType = new MediaType();

        // 使用ObjectSchema作为默认请求体
        mediaType.setSchema(new ObjectSchema());
        content.addMediaType("application/json", mediaType);
        requestBody.setContent(content);

        return requestBody;
    }
}
