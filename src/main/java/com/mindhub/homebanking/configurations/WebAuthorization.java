package com.mindhub.homebanking.configurations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@EnableWebSecurity
@Configuration
public class WebAuthorization extends WebSecurityConfigurerAdapter {

    @Autowired
    ClientService clientService;
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.httpBasic().and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/clients","/api/clients/current/accounts",
                        "/api/clients/current/cards", "/api/clients/current/transactions","/api/clients/current/loans",
                        "/api/confirm-account","/api/password-token", "/api/reset-password")
                .permitAll()
                .antMatchers(HttpMethod.GET, "/api/clients").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/clients/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/api/clients/**").hasAnyAuthority("ADMIN", "CLIENT")
                .antMatchers(HttpMethod.GET,"/api/confirm-account").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/clients/current/cards").hasAnyAuthority("ADMIN","CLIENT");

        http.authorizeRequests()
                .antMatchers( "/web/js/**", "/web/assets/**","/web/styles/**", "/web/index.html").permitAll()
                .antMatchers("/rest/**", "/h2-console/**").hasRole("ADMIN")
                .antMatchers("/web/confirm-account.html","/web/accounts.html",
                                "/web/account.html", "/api/clients/{id}", "/web/cards.html"
                                , "/web/loan-application.html").hasAnyAuthority("ADMIN","CLIENT");

        http.formLogin()
                .usernameParameter("email")
                .passwordParameter("password")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");


        // turn off checking for CSRF tokens
        http.csrf().disable();

        //disabling frameOptions so h2-console can be accessed
        http.headers().frameOptions().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            Client client = clientService.findByEmail(email);
            String message = "";
            if(client != null) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                if(!client.isEnabled()) {
                    message = "User hasn't activated their account yet";
                } else if(client.getPassword() != password) {
                        message = "Wrong credentials";
                }
            } else {
                message = "User doesn't exist";
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }

            res.setContentType("application/json");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonMessage = mapper.createObjectNode();
            ((ObjectNode) jsonMessage).put("message", message);

            res.setContentType("application/json");
            res.getWriter().write(jsonMessage.toString());
        });


        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }

}

