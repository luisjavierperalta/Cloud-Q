package com.cloudq.cloudq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket apiDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.optimization.controller"))
                .paths(PathSelectors.any()) // Adjust to specify or limit documented paths if needed
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(List.of(apiKey())); // Adds token-based security scheme
    }

    // API Information for Swagger UI
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Optimization Engine API")
                .description("API documentation for the Optimization Engine platform, enabling cloud optimization solutions.")
                .version("1.0")
                .contact(new Contact("Your Company", "https://www.example.com", "support@example.com"))
                .license("Apache License 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                .build();
    }

    // Security scheme definition for JWT or API Key-based security (customize as needed)
    private ApiKey apiKey() {
        return new ApiKey("API Key", "Authorization", "header");
    }
}