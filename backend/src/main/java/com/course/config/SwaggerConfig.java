package com.course.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Swagger配置类
 * 配置API文档的基本信息
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Configuration
public class SwaggerConfig {

    /**
     * 配置OpenAPI文档信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8080").description("本地开发环境"),
                        new Server().url("https://api.course.com").description("生产环境")
                ))
                .components(new Components()
                        .addSecuritySchemes("Bearer", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("请输入JWT令牌，格式：Bearer {token}")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("Bearer"));
    }

    /**
     * API基本信息
     */
    private Info apiInfo() {
        return new Info()
                .title("课程管理系统API文档")
                .description("基于Spring Boot + MyBatis Plus + JWT的课程管理系统后端API接口文档\n\n" +
                        "## 功能特性\n" +
                        "- 用户管理：用户注册、登录、权限控制\n" +
                        "- 课程管理：课程创建、编辑、查询、删除\n" +
                        "- 角色管理：管理员、教师、学生角色\n" +
                        "- JWT认证：无状态令牌认证\n\n" +
                        "## 使用说明\n" +
                        "1. 首先调用登录接口获取JWT令牌\n" +
                        "2. 在需要认证的接口中添加Authorization头：Bearer {token}\n" +
                        "3. 默认管理员账户：admin/123456")
                .version("1.0.0")
                .contact(new Contact()
                        .name("课程管理系统开发团队")
                        .email("dev@course.com")
                        .url("https://github.com/course-management")
                )
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT")
                );
    }
}
