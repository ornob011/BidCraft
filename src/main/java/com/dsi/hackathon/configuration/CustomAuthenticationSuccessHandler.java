package com.dsi.hackathon.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.Objects;

@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        String targetUrl = "/";

        if (Objects.nonNull(savedRequest)) {
            String requestedUrl = savedRequest.getRedirectUrl();
            if (!isErrorPage(requestedUrl)) {
                targetUrl = requestedUrl;
            }
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    private boolean isErrorPage(String url) {
        return !ObjectUtils.isEmpty(url) && (url.contains("/error") || url.contains("/access-denied"));
    }
}
