package com.example.proyecto.service;

import com.example.proyecto.response.GetTaskResponse;
import com.example.proyecto.response.GetTasksResponse;
import com.example.proyecto.utils.ServiceUtils;
import com.google.gson.Gson;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class GetTaskService extends Service<GetTaskResponse> {
    private String codTrabajo;

    public GetTaskService(String codTrabajo){
        this.codTrabajo = codTrabajo;
    }

    @Override
    protected Task<GetTaskResponse> createTask() {
        return new Task<GetTaskResponse>() {
            @Override
            protected GetTaskResponse call() throws Exception {
                try {
                    String json = ServiceUtils.getResponse(
                            ServiceUtils.SERVER + "/trabajos/" + codTrabajo, null, "GET");

                    Gson gson = new Gson();
                    GetTaskResponse respuesta = gson.fromJson(json, GetTaskResponse.class);

                    return respuesta;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Error al deserializar la respuesta del servidor", e);
                }
            }
        };
    }
}
