package com.fittr.server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;


/**
 *
 * This class contains logic to implement the web security.
 * Currently, it is enabled for one api endpoint for uploading the vouchers using batch file.
 *
 * @author Rakesh Kumar
 *
 * Note: We have commented all endpints except one to disable the backend basic web security since
 * frontend could manage to send tha authorization in request header.
 * This can be enabled in the future once frontend app manages to send the authorizaion
 * in the header.
 *
 */

@EnableWebSecurity
public class CustomSecurity extends WebSecurityConfigurerAdapter {


    @Autowired
    private CustomUserDetailsService userDetailsService;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

//Disabled endpoints security is for future use, if we adapt our fitter app to send the api requests with authorization in the header.
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .cors().and().csrf().disable().authorizeRequests()
//					.antMatchers("/login").hasAnyRole("USER", "ADMIN")
//					.antMatchers("/getoneuser").hasAnyRole("USER", "ADMIN")
//					.antMatchers("/getoneuser").permitAll()
//					.antMatchers("/getallusers").hasRole("ADMIN")
//					.antMatchers("/deleteuser").hasAnyRole("USER", "ADMIN")
//					.antMatchers("/updateuser").hasAnyRole("USER", "ADMIN")
//					.antMatchers("/uploadimage").hasAnyRole("USER", "ADMIN")
//          		.antMatchers("/register").hasRole("USER")
//					.antMatchers("/getonevoucher").hasAnyRole("USER", "ADMIN")
//					.antMatchers("/getallvouchers").hasAnyRole("USER", "ADMIN")
//					.antMatchers("/purchasevoucher").hasAnyRole("USER", "ADMIN")
//					.antMatchers("/getuservoucherhistory").hasAnyRole("USER", "ADMIN")
//					.antMatchers("/uploadvoucherimage").hasAnyRole("USER", "ADMIN")
//					.antMatchers("/deletevoucher").hasAnyRole("ADMIN")
//					.antMatchers("/createvoucher").hasAnyRole("ADMIN")

                .antMatchers("/loadvoucherfile").hasAnyRole("ADMIN")
                .antMatchers("/getoneuser").permitAll()
                .antMatchers("/getallusers").permitAll()
                .antMatchers("/deleteuser").permitAll()
                .antMatchers("/updateuser").permitAll()
                .antMatchers("/updateuser2").permitAll()
                .antMatchers("/uploadimage").permitAll()
                .antMatchers("/getonevoucher").permitAll()
                .antMatchers("/getallvouchers").permitAll()
                .antMatchers("/purchasevoucher").permitAll()
                .antMatchers("/getuservoucherhistory").permitAll()
                .antMatchers("/uploadvoucherimage").permitAll()
                .antMatchers("/deletevoucher").permitAll()
                .antMatchers("/createvoucher").permitAll()
                .antMatchers("/register").permitAll()
                .antMatchers("/login").permitAll()
                .and()
                .httpBasic();


    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}