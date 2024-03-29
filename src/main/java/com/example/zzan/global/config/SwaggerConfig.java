package com.example.zzan.global.config;

import com.example.zzan.global.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Tag(name = "My Controller", description = "This is swagger")
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version("v2.0.0")
                .title("zzan")
                .description("Api Description");

        String token_header = JwtUtil.AUTHORIZATION_HEADER;

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(token_header);

        Components components = new Components()
                .addSecuritySchemes(token_header, new SecurityScheme()
                        .name(token_header)
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER));

        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
