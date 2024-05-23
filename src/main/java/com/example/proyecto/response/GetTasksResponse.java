package com.example.proyecto.response;

import com.example.proyecto.model.Task;

import java.util.List;

public class GetTasksResponse extends BaseResponse{
    List<Task> result;

    public List<Task> getTasks() {
        return result;
    }
}
