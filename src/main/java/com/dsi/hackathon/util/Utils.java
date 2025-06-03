package com.dsi.hackathon.util;

import com.dsi.hackathon.entity.User;
import com.dsi.hackathon.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.stream.Collectors;

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

    public static User getLoggedInUserDetails(Authentication authentication) {
        User loggedInUserDetails = null;

        if (!(authentication instanceof AnonymousAuthenticationToken)
            && authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            loggedInUserDetails = userDetails.getUser();
        }

        return Objects.requireNonNull(
            loggedInUserDetails,
            "User details is null in authentication principle. User not logged in properly."
        );
    }

    public static User getLoggedInUserDetails() {
        return getLoggedInUserDetails(getAuthentication());
    }

    public static Integer getLoggedInUserId() {
        return Objects.requireNonNull(
            getLoggedInUserDetails().getId(),
            "User id is null in authentication principle. User not logged in properly."
        );
    }

    public static String getMessageFromMessageSource(MessageSource messageSource, MessageSourceResolvable messageSourceResolvable) {
        return messageSource.getMessage(messageSourceResolvable, Locale.getDefault());
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

    public static List<String> getErrorStrList(MessageSource messageSource, BindingResult bindingResult) {
        List<String> errors;
        errors = bindingResult.getAllErrors()
                              .stream()
                              .map(error -> Utils.getMessageFromMessageSource(messageSource, error))
                              .toList();
        return errors;
    }

    public static Map<String, String> convertMetaDataToStringMap(Map<String, Object> metaData) {
        if (metaData == null) return Collections.emptyMap();

        return metaData.entrySet().stream()
                       .collect(
                           Collectors.toMap(
                               Map.Entry::getKey,
                               entry -> String.valueOf(entry.getValue())
                           )
                       );
    }

}
