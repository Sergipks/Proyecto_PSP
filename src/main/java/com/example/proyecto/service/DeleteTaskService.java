package com.example.proyecto.service;

import com.example.proyecto.model.Task;
import com.example.proyecto.response.BaseResponse;
import com.example.proyecto.response.GetTaskResponse;
import com.example.proyecto.utils.ServiceUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.concurrent.Service;

public class DeleteTaskService extends Service<BaseResponse> {
    private String idTask;

    public DeleteTaskService(String idTask){
        this.idTask = idTask;
    }

    @Override
    protected javafx.concurrent.Task<BaseResponse> createTask() {
        return new javafx.concurrent.Task<BaseResponse>() {
            @Override
            protected BaseResponse call() throws Exception {
                String json = ServiceUtils.getResponse(
                        ServiceUtils.SERVER + "/trabajos/" + idTask, null,"DELETE");

                Gson gson = new Gson();
                BaseResponse response = gson.fromJson(json, BaseResponse.class);
                return response;
            }
        };
    }
}
