package br.gov.inep.censo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
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
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:db/schema.sql")
                .addScript("classpath:db/seed.sql")
                .addScript("classpath:db/seed_layout.sql")
                .addScript("classpath:db/seed_layout_ies_docente.sql")
                .addScript("classpath:db/seed_municipio.sql")
                .build();
    }
}
