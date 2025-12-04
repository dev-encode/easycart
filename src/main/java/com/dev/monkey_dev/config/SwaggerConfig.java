package com.dev.monkey_dev.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger/OpenAPI configuration for API documentation.
 * Configures security schemes for JWT authentication.
 */
@Configuration
@Profile("!test")
public class SwaggerConfig {

        @Value("${server.port:8888}")
        private String serverPort;

        @Value("${file.base-image-url:http://localhost:${server.port:8888}}")
        private String baseUrl;

        private final Environment environment;

        public SwaggerConfig(Environment environment) {
                this.environment = environment;
        }

        private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

        /**
         * Configures the OpenAPI specification with security schemes.
         *
         * @return configured OpenAPI instance
         */
        @Bean
        public OpenAPI customOpenAPI() {
                List<Server> servers = new ArrayList<>();
                
                // Add production server if prod profile is active
                String[] activeProfiles = environment.getActiveProfiles();
                boolean isProd = java.util.Arrays.asList(activeProfiles).contains("prod");
                
                if (isProd) {
                        servers.add(new Server()
                                        .url("https://easycart-api.up.railway.app")
                                        .description("Production Server"));
                }
                
                // Always add localhost server
                servers.add(new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"));

                return new OpenAPI()
                                .info(new Info()
                                                .title("Monkey Dev API")
                                                .description("REST API documentation for Monkey Dev application with JWT authentication")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("Monkey Dev Team")
                                                                .email("dev@monkeydev.com")
                                                                .url("https://monkeydev.com"))
                                                .license(new License()
                                                                .name("Apache 2.0")
                                                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                                .servers(servers)
                                .addSecurityItem(new SecurityRequirement()
                                                .addList(SECURITY_SCHEME_NAME))
                                .components(new Components()
                                                .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                                                new SecurityScheme()
                                                                                .name(SECURITY_SCHEME_NAME)
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")
                                                                                .description("Enter JWT Bearer token (without 'Bearer' prefix)")));
        }
}