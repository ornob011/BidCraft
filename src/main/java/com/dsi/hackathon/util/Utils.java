package com.dsi.hackathon.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Locale;

public class Utils {
    private Utils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static boolean isLoggedIn(Authentication authentication) {
        return authentication != null
               && !(authentication instanceof AnonymousAuthenticationToken)
               && authentication.isAuthenticated();
    }

    public static boolean isLoggedIn() {
        return isLoggedIn(getAuthentication());
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static String getMessageFromMessageSource(MessageSource messageSource, String msg) {
        return messageSource.getMessage(msg, null, Locale.getDefault());
    }

    public static String getMessageFromMessageSource(MessageSource messageSource, String msg, Object[] args) {
        return messageSource.getMessage(msg, args, Locale.getDefault());
    }

    public static void setSuccessMessageCode(HttpServletRequest request, MessageSource messageSource, String msgCode) {
        request.getSession().setAttribute(Constants.FLUSH_SUCCESS_MSG_CODE, getMessageFromMessageSource(messageSource, msgCode));
    }

    public static void setInfoMessageCode(HttpServletRequest request, MessageSource messageSource, String msgCode) {
        request.getSession().setAttribute(Constants.FLUSH_INFO_MSG_CODE, getMessageFromMessageSource(messageSource, msgCode));
    }

    public static void setErrorMessageCode(HttpServletRequest request, MessageSource messageSource, String msgCode) {
        request.getSession().setAttribute(Constants.FLUSH_ERROR_MSG_CODE, getMessageFromMessageSource(messageSource, msgCode));
    }

    public static void setErrorMessageCode(HttpServletRequest request, MessageSource messageSource, String msgCode, Object[] args) {
        request.getSession().setAttribute(Constants.FLUSH_ERROR_MSG_CODE, getMessageFromMessageSource(messageSource, msgCode, args));
    }

    public static void setWarnMessageCode(HttpServletRequest request, MessageSource messageSource, String msgCode) {
        request.getSession().setAttribute(Constants.FLUSH_WARNING_MSG_CODE, getMessageFromMessageSource(messageSource, msgCode));
    }

}
