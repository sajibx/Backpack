package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.web.filter.CommonsRequestLoggingFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@SpringBootApplication
class DemoApplication : SpringBootServletInitializer(){

    override fun configure(builder: SpringApplicationBuilder?): SpringApplicationBuilder {
        return builder?.sources(DemoApplication::class.java)!!
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}




@Bean
fun requestLoggingFilter(): CommonsRequestLoggingFilter {
    val loggingFilter = CommonsRequestLoggingFilter()
    loggingFilter.setIncludeClientInfo(true)
    loggingFilter.setIncludeQueryString(true)
    loggingFilter.setIncludePayload(true)
    return loggingFilter
}

@EnableJpaRepositories
class Config {

}

@Configuration
@EnableWebMvc
class WebConfig : WebMvcConfigurerAdapter() {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
    }
}



