package com.example.proyecto.service;

import com.example.proyecto.response.BaseResponse;
import com.example.proyecto.utils.ServiceUtils;
import com.google.gson.Gson;
import javafx.concurrent.Service;
import java.io.IOException;

public class DeleteWorkerService extends Service<BaseResponse> {
    private String idWorker;

    public DeleteWorkerService(String idWorker) {
        this.idWorker = idWorker;
    }

    @Override
    protected javafx.concurrent.Task<BaseResponse> createTask() {
        return new javafx.concurrent.Task<BaseResponse>() {
            @Override
            protected BaseResponse call() throws Exception {
                try {
                    String json = ServiceUtils.getResponse(
                            ServiceUtils.SERVER + "/trabajadores/" + idWorker, null, "DELETE");

                    Gson gson = new Gson();
                    BaseResponse response = gson.fromJson(json, BaseResponse.class);
                    return response;
                } catch (Exception e) {
                    throw new IOException("Error al intentar eliminar el trabajador", e);
                }
            }
        };
    }
}
