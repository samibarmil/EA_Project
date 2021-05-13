package com.eaProject.demo.filter;

import com.eaProject.demo.domain.ErrorData;
import com.eaProject.demo.exceptions.UnauthorizedAccessException;
import com.eaProject.demo.services.PersonDetailService;
import com.eaProject.demo.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    @Autowired
    JWTUtil jwtUtil;
    @Autowired
    PersonDetailService personDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = httpServletRequest.getHeader("Authorization");
        String jwt = null;
        String username = null;
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
           jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (UnauthorizedAccessException e) {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            setAuthentication(httpServletRequest, username);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void setAuthentication(HttpServletRequest httpServletRequest, String username) {
        UserDetails userDetails = personDetailService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
}
