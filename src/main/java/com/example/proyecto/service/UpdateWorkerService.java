package com.example.proyecto.service;

import com.example.proyecto.model.Worker;
import com.example.proyecto.response.GetWorkerResponse;
import com.example.proyecto.utils.ServiceUtils;
import com.google.gson.Gson;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class UpdateWorkerService extends Service<GetWorkerResponse> {
    Worker worker;

    public UpdateWorkerService(Worker worker)
    {
        this.worker = worker;
    }

    @Override
    protected Task<GetWorkerResponse> createTask() {
        return new Task<GetWorkerResponse>() {
            @Override
            protected GetWorkerResponse call() throws Exception {
                Gson gson = new Gson();

                String json = ServiceUtils.getResponse(
                        ServiceUtils.SERVER + "/trabajadores/" +worker.getIdTrabajador(), gson.toJson(worker), "PUT");

                GetWorkerResponse response = gson.fromJson(json, GetWorkerResponse.class);
                return response;
            }
        };
    }
}
