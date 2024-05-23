package com.example.proyecto.response;

import com.example.proyecto.model.Task;

import java.util.List;

public class GetTasksResponse extends BaseResponse {
    private List<Task> result;

    public List<Task> getResult() {
        return result;
    }

    public void setResult(List<Task> result) {
        this.result = result;
    }
}
