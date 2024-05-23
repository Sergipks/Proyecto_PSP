package com.example.proyecto.response;

import com.example.proyecto.model.Task;

public class GetTaskResponse extends BaseResponse {
    private Task result;

    public Task getResult() { return result; }

    public void setResult(Task result) {
        this.result = result;
    }
}
