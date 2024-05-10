package com.example.proyecto.service;

import com.example.proyecto.response.GetWorkersResponse;
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
                /*String json = ServiceUtils.getResponse(
                        ServiceUtils.SERVER + "/workers" + filter, null, "GET");*/
                String json = "[\n" +
                        "  {\n" +
                        "    \"id\": 1,\n" +
                        "    \"dni\": \"12345678A\",\n" +
                        "    \"nombre\": \"Juan\",\n" +
                        "    \"apellidos\": \"Pérez Gómez\",\n" +
                        "    \"especialidad\": \"Programador\",\n" +
                        "    \"contrasenya\": \"password123\",\n" +
                        "    \"email\": \"juan@example.com\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"id\": 2,\n" +
                        "    \"dni\": \"98765432B\",\n" +
                        "    \"nombre\": \"María\",\n" +
                        "    \"apellidos\": \"González López\",\n" +
                        "    \"especialidad\": \"Diseñador\",\n" +
                        "    \"contrasenya\": \"mypass123\",\n" +
                        "    \"email\": \"maria@example.com\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"id\": 3,\n" +
                        "    \"dni\": \"45678901C\",\n" +
                        "    \"nombre\": \"Pedro\",\n" +
                        "    \"apellidos\": \"Martínez Rodríguez\",\n" +
                        "    \"especialidad\": \"Analista\",\n" +
                        "    \"contrasenya\": \"securepass\",\n" +
                        "    \"email\": \"pedro@example.com\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"id\": 4,\n" +
                        "    \"dni\": \"56789012D\",\n" +
                        "    \"nombre\": \"Ana\",\n" +
                        "    \"apellidos\": \"Sánchez Fernández\",\n" +
                        "    \"especialidad\": \"Ingeniero\",\n" +
                        "    \"contrasenya\": \"engineer123\",\n" +
                        "    \"email\": \"ana@example.com\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"id\": 5,\n" +
                        "    \"dni\": \"34567890E\",\n" +
                        "    \"nombre\": \"David\",\n" +
                        "    \"apellidos\": \"López Pérez\",\n" +
                        "    \"especialidad\": \"Consultor\",\n" +
                        "    \"contrasenya\": \"consultingpass\",\n" +
                        "    \"email\": \"david@example.com\"\n" +
                        "  }\n" +
                        "]";

                Gson gson = new Gson();
                GetWorkersResponse response = gson.fromJson(json, GetWorkersResponse.class);
                return response;
            }
        };
    }
}
