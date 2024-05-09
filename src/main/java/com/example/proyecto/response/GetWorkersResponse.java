package com.example.proyecto.response;

import com.example.proyecto.model.Task;
import com.example.proyecto.model.Worker;

import java.util.List;

public class GetWorkersResponse extends BaseResponse{
    List<Worker> workers;

    public List<Worker> getWorkers() {
        return workers;
    }
}
