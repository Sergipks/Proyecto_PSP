package com.example.proyecto.service;

import com.example.proyecto.response.GetTasksResponse;
import com.example.proyecto.utils.ServiceUtils;
import com.google.gson.Gson;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class GetTasksService extends Service<GetTasksResponse> {
    private String filter;

    public GetTasksService(String filter){
        this.filter = filter;
    }

    public GetTasksService(){
        this.filter = "";
    }

    @Override
    protected Task<GetTasksResponse> createTask() {
        return new Task<GetTasksResponse>() {
            @Override
            protected GetTasksResponse call() throws Exception {
                String json = ServiceUtils.getResponse(
                        ServiceUtils.SERVER + "/trabajos" + filter, null, "GET");

                Gson gson = new Gson();
                GetTasksResponse respuesta =  gson.fromJson(json, GetTasksResponse.class);
                return respuesta;
            }
        };
    }
}
