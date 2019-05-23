package com.dyptan.configuration;

import com.dyptan.service.UserAuthDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@EnableWebSecurity
//@EnableJpaRepositories(basePackageClasses = UserRepository.class)
//@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    @Autowired
    private UserAuthDetailsService userAuthDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userAuthDetailsService)
                .passwordEncoder(getPasswordEncoder());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.authorizeRequests()
                .anyRequest().fullyAuthenticated()
                .and()
                .formLogin().permitAll();
    }

    private PasswordEncoder getPasswordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence charSequence) {
                return charSequence.toString();
            }

            @Override
            public boolean matches(CharSequence charSequence, String s) {
                return true;
            }
        };
    }
}