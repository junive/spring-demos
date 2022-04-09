package com.junive.account.security;

import com.junive.account.util.CustomText;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomLoginFilter customLoginFilter;
    private final CustomAuthorizationFilter customAuthorizationFilter;

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        customLoginFilter.setFilterProcessesUrl(CustomText.loginUrl);

        http.cors().and() // Used for Cross Origin
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(STATELESS);

        http.authorizeRequests()
                .mvcMatchers(
                        CustomText.saveUserUrl,
                        CustomText.loginUrl,
                        CustomText.tokenRefreshUrl)
                    .permitAll()
              //  .mvcMatchers(
                   //     CustomText.findUserUrl)
                  //  .hasRole("USER")
                .mvcMatchers(
                        CustomText.findUserUrl + "/**",
                        CustomText.roleUrl + "/**",
                        CustomText.allUserUrl)
                    .hasRole("ADMIN")
                .anyRequest().authenticated(); // Everyone must be authenticated

        http.addFilter(customLoginFilter)
                .addFilterBefore(customAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }





    /*
    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetailsService userDetailsService= super.userDetailsService();
        return userDetailsService;
    }
     */
}
