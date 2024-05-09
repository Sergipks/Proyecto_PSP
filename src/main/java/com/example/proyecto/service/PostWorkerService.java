package com.example.proyecto.service;

import com.example.proyecto.model.Worker;
import com.example.proyecto.response.GetWorkerResponse;
import com.example.proyecto.utils.ServiceUtils;
import com.google.gson.Gson;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class PostWorkerService extends Service<GetWorkerResponse> {
    Worker worker;

    public PostWorkerService(Worker worker)
    {
        this.worker = worker;
    }

    @Override
    protected Task<GetWorkerResponse> createTask() {
        return new Task<GetWorkerResponse>() {
            @Override
            protected GetWorkerResponse call() throws Exception {
                Gson gson = new Gson();
                System.out.println(gson.toJson(worker));
                String json = ServiceUtils.getResponse(
                        ServiceUtils.SERVER + "/workers", gson.toJson(worker), "POST");
                GetWorkerResponse response = gson.fromJson(json, GetWorkerResponse.class);
                return response;
            }
        };
    }
}
