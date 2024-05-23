package com.example.proyecto.response;

import com.example.proyecto.model.Task;
import com.example.proyecto.model.Worker;

import java.util.List;

public class GetWorkersResponse extends BaseResponse{
    List<Worker> result;

    public List<Worker> getResult() {
        return result;
    }

    public void setResult(List<Worker> result) {
        this.result = result;
    }
}
