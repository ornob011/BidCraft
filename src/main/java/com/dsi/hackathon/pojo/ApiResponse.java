package com.dsi.hackathon.pojo;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
public class ApiResponse<T> {
    private int statusCode;
    private String message;
    private T data;

    public ApiResponse(int statusCode) {
        this.statusCode = statusCode;
    }

    public ApiResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> response;
        response = new ApiResponse<>(HttpStatus.OK.value());
        response.setData(data);

        return response;
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        ApiResponse<T> response;
        response = new ApiResponse<>(HttpStatus.OK.value(), message);
        response.setData(data);

        return response;
    }

    public static <T> ApiResponse<T> badRequest(T data, String message) {
        ApiResponse<T> response;
        response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), message);
        response.setData(data);

        return response;
    }

    public static <T> ApiResponse<T> forbidden(T data, String message) {
        ApiResponse<T> response;
        response = new ApiResponse<>(HttpStatus.FORBIDDEN.value(), message);
        response.setData(data);

        return response;
    }

    public static <T> ApiResponse<T> tooManyRequests(T data, String message) {
        ApiResponse<T> response;
        response = new ApiResponse<>(HttpStatus.TOO_MANY_REQUESTS.value(), message);
        response.setData(data);

        return response;
    }

    public static <T> ApiResponse<T> internalServerError(T data, String message) {
        ApiResponse<T> response;
        response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
        response.setData(data);

        return response;
    }
}
