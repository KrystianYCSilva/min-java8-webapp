package br.gov.inep.censo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Configuracao Spring MVC (substitui mvc-context.xml).
 * Habilita anotacoes MVC e varre os controllers REST do pacote web.spring.
 */
@Configuration
@EnableWebMvc
@ComponentScan("br.gov.inep.censo.web.spring")
public class MvcConfig extends WebMvcConfigurerAdapter {
}
