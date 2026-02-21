package br.gov.inep.censo.config;

import br.gov.inep.censo.spring.datasource.ConnectionFactoryDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Configuracao principal do contexto Spring (substitui applicationContext.xml).
 * Define o DataSource e habilita varredura de componentes (@Service, @Component) e gerenciamento de transacoes.
 * A varredura e deliberadamente limitada a service e repository para evitar ativar @EnableWebMvc
 * no contexto raiz (que e responsabilidade de MvcConfig no contexto do DispatcherServlet).
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"br.gov.inep.censo.service", "br.gov.inep.censo.repository"})
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        return new ConnectionFactoryDataSource();
    }
}
