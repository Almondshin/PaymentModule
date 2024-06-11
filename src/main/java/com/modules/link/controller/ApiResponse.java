/*
package com.modules.link.controller;

public class ApiResponse<T> {

    private T body;
    private String message;

    private ApiResponse(T body){
        this.body = body;
    }

    private ApiResponse(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public T getBody() {
        return body;
    }

    public static <T> ApiResponse<T> success(T body) {
        return new ApiResponse<>(body);
    }

    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>(message);
    }

}

*/
