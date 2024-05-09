package com.example.proyecto.response;

import com.example.proyecto.model.Task;

public class GetTaskResponse extends BaseResponse {
    private Task task;

    public Task getTask() { return task; }
}
