//package com.bigking.springcloud.config;
//
//import com.bigking.springcloud.pojo.ServersEx;
//import com.google.common.collect.Lists;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.bind.annotation.RestController;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.ParameterBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.schema.ModelRef;
//import springfox.documentation.service.*;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author devwang
// */
//@Configuration
//@EnableSwagger2
//public class SwaggerConfig {
//    @Bean
//    public Docket createRestAPI() {
//        List<Parameter> parameters = new ArrayList<>();
//        ParameterBuilder builder = new ParameterBuilder();
//        builder.name("Authorization")
//                .description("Authorization")
//                .modelRef(new ModelRef("string"))
//                .parameterType("header")
//                .required(true)
//                .build();
//        parameters.add(builder.build());
//
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .extensions(Lists.newArrayList(ServersEx.getInstance().servers()))
//                .select()
//                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
//                .paths(PathSelectors.any())
//                .build()
//                .securitySchemes(securitySchemes());
//
//    }
//
//    private ApiInfo apiInfo() {
//        ObjectVendorExtension logo = new ObjectVendorExtension("x-logo");
//        logo.addProperty(new StringVendorExtension("url", "/fas/doc/images/mats-log.png"));
//        logo.addProperty(new StringVendorExtension("backgroundColor", "#fff"));
//        return new ApiInfoBuilder()
//                .extensions(Lists.newArrayList(logo))
//                .description("MATS API for Webex Meeting, Webex Site and Webex MeetingData. For internal troubleshot" +
//                        " and tools development. API listed here are supported by https://mats.webex.com/.")
//                .build();
//    }
//
//    private List<ApiKey> securitySchemes() {
//        return Lists.newArrayList(
//                new ApiKey("Authorization", "Authorization", "header"));
//    }
//
//    public static Boolean enable;
//    public static String username;
//    public static String password;
//
//    @Value("${common.swagger.admin.enable}")
//    public void setEnable(Boolean enable) {
//        SwaggerConfig.enable = enable;
//    }
//
//    @Value("${common.swagger.admin.username}")
//    public void setUsername(String username) {
//        SwaggerConfig.username = username;
//    }
//
//    @Value("${common.swagger.admin.password}")
//    public void setPassword(String password) {
//        SwaggerConfig.password = password;
//    }
//}
