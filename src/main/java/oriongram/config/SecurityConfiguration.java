package oriongram.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import oriongram.repos.UserRepository;
import oriongram.services.SSUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity (prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return new SSUserDetailsService(userRepository);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable();
        http
                .authorizeRequests()
                .antMatchers("/", "/register").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login").permitAll().permitAll()
                .and()
                .httpBasic();
    }

   @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user1").password("password").roles("USER")
                               .and().withUser("user2").password("password").roles("ADMIN");
        auth.userDetailsService(userDetailsServiceBean());
    }


}
