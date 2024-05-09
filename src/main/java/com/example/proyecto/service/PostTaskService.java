package com.example.proyecto.service;

import com.example.proyecto.model.Task;
import com.example.proyecto.response.GetTaskResponse;
import com.example.proyecto.utils.ServiceUtils;
import com.google.gson.Gson;
import javafx.concurrent.Service;

public class PostTaskService extends Service<GetTaskResponse> {
    Task task;

    public PostTaskService(Task task)
    {
        this.task = task;
    }

    @Override
    protected javafx.concurrent.Task<GetTaskResponse> createTask() {
        return new javafx.concurrent.Task<GetTaskResponse>() {
            @Override
            protected GetTaskResponse call() throws Exception {
                Gson gson = new Gson();
                System.out.println(gson.toJson(task));
                String json = ServiceUtils.getResponse(
                        ServiceUtils.SERVER + "/tasks", gson.toJson(task), "POST");
                GetTaskResponse response = gson.fromJson(json, GetTaskResponse.class);
                return response;
            }
        };
    }
}
