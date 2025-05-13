package com.dsi.hackathon.filter;

import com.dsi.hackathon.util.Constants;
import com.dsi.hackathon.util.Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
public class LoginPageFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(LoginPageFilter.class);

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (Utils.isLoggedIn() && isLoginRequest(httpRequest)) {
            logger.info("Authenticated user redirected away from the login page.");
            httpResponse.sendRedirect(Constants.HOME_URL);
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        boolean isLoginRequest = Constants.LOGIN_URL.equals(request.getRequestURI());

        if (isLoginRequest) {
            logger.debug("Login page requested.");
        }

        return isLoginRequest;
    }
}
