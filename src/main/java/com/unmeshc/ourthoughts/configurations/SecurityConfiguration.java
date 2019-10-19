package com.unmeshc.ourthoughts.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

/**
 * Created by uc on 10/13/2019
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;
    private final LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler;
    private final RememberMeAuthenticationSuccessHandler rememberMeAuthenticationSuccessHandler;

    @Value("${spring.queries.users-query}")
    private String usersQuery;

    @Value("${spring.queries.roles-query}")
    private String rolesQuery;

    @Value("${secret.and.unique}")
    private String secretAndUnique;

    public SecurityConfiguration(DataSource dataSource,
                                 PasswordEncoder passwordEncoder,
                                 LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler,
                                 RememberMeAuthenticationSuccessHandler rememberMeAuthenticationSuccessHandler) {
        this.dataSource = dataSource;
        this.passwordEncoder = passwordEncoder;
        this.loginAuthenticationSuccessHandler = loginAuthenticationSuccessHandler;
        this.rememberMeAuthenticationSuccessHandler = rememberMeAuthenticationSuccessHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
            .usersByUsernameQuery(usersQuery)
            .authoritiesByUsernameQuery(rolesQuery)
            .dataSource(dataSource)
            .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/password/reset/update/form", "/password/reset/update")
                .hasAuthority("CHANGE_PASSWORD_PRIVILEGE")
            .antMatchers("/user/**")
                .hasAuthority("USER")
            .antMatchers(PUBLIC).permitAll()
            .antMatchers("/login").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/login").failureUrl("/login?error=true")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(loginAuthenticationSuccessHandler)
            .and()
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/index.html")
            .and()
            .exceptionHandling()
                .accessDeniedPage("/access/denied")
            .and()
            .rememberMe()
                .key(secretAndUnique)
                .authenticationSuccessHandler(rememberMeAuthenticationSuccessHandler);

        // for accessing H2 database console
        http.csrf().disable();
        http.headers().frameOptions().disable();
    }

    private String[] PUBLIC = {
            "/h2-console/**",
            "/webjars/**",
            "/css/**",
            "/js/**",
            "/img/**",
            "/index.html",
            "/registration/**",
            "/password/**"
    };
}
