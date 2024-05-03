package com.example.proyecto.response;

public class BaseResponse {
    private boolean error;
    private String errorMessage;

    public boolean isError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
