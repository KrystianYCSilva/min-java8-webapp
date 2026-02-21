package br.gov.inep.censo.config;

import br.gov.inep.censo.repository.UsuarioRepository;
import br.gov.inep.censo.spring.security.SessionUsuarioAuthenticationFilter;
import br.gov.inep.censo.spring.security.UsuarioAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Configuracao de segurança Spring Security (substitui security-context.xml).
 * Mantem o filtro de sessao legado e o provider customizado baseado em tabela de usuarios.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/api/relatorios/**").authenticated()
                .antMatchers("/api/**").permitAll()
                .and()
            .httpBasic()
                .and()
            .csrf().disable()
            .addFilterBefore(sessionUsuarioAuthenticationFilter(), BasicAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(usuarioAuthenticationProvider());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public SessionUsuarioAuthenticationFilter sessionUsuarioAuthenticationFilter() {
        return new SessionUsuarioAuthenticationFilter();
    }

    @Bean
    public UsuarioAuthenticationProvider usuarioAuthenticationProvider() {
        UsuarioAuthenticationProvider provider = new UsuarioAuthenticationProvider();
        provider.setUsuarioRepository(usuarioRepository);
        return provider;
    }
}
