package com.example.proyecto.service;

import com.example.proyecto.model.Task;
import com.example.proyecto.response.GetTaskResponse;
import com.example.proyecto.utils.ServiceUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.concurrent.Service;

public class UpdateTaskService extends Service<GetTaskResponse> {
    Task task;
    String filter;

    public UpdateTaskService(Task task){
        this.task = task;
        this.filter = "";
    }

    public UpdateTaskService(Task task, String filter){
        this.task = task;
        this.filter = filter;
    }

    @Override
    protected javafx.concurrent.Task<GetTaskResponse> createTask() {
        return new javafx.concurrent.Task<GetTaskResponse>() {
            @Override
            protected GetTaskResponse call() throws Exception {
                Gson gson = new GsonBuilder().serializeNulls().create();

                String json = ServiceUtils.getResponse(
                        ServiceUtils.SERVER + "/trabajos/" + task.getCodTrabajo() + filter, gson.toJson(task), "PUT");

                GetTaskResponse response = gson.fromJson(json, GetTaskResponse.class);
                return response;
            }
        };
    }
}
