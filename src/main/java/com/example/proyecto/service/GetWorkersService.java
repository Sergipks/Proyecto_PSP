package com.example.proyecto.service;

import com.example.proyecto.response.GetTasksResponse;
import com.example.proyecto.response.GetWorkersResponse;
import com.example.proyecto.utils.ServiceUtils;
import com.google.gson.Gson;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class GetWorkersService extends Service<GetWorkersResponse> {
    private String filter;

    public GetWorkersService(String filter){
        this.filter = filter;
    }

    public GetWorkersService(){
        this.filter = "";
    }

    @Override
    protected Task<GetWorkersResponse> createTask() {
        return new Task<GetWorkersResponse>() {
            @Override
            protected GetWorkersResponse call() throws Exception {
                try {
                    String json = ServiceUtils.getResponse(
                            ServiceUtils.SERVER + "/trabajadores" + filter, null, "GET");

                    Gson gson = new Gson();
                    GetWorkersResponse respuesta = gson.fromJson(json, GetWorkersResponse.class);

                    return respuesta;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Error al deserializar la respuesta del servidor", e);
                }
            }
        };
    }
}
