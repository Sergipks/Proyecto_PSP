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
                /*String json = ServiceUtils.getResponse(
                        ServiceUtils.SERVER + "/tasks" + filter, null, "GET");*/
                String json = "[\n" +
                        "  {\n" +
                        "    \"cod\": 1,\n" +
                        "    \"categoria\": \"Trabajo\",\n" +
                        "    \"descripcion\": \"Preparar informe mensual\",\n" +
                        "    \"fecIni\": \"2024-05-01T08:00:00Z\",\n" +
                        "    \"fecFin\": \"2024-05-05T17:00:00Z\",\n" +
                        "    \"tiempo\": 20,\n" +
                        "    \"prioridad\": 2\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"cod\": 2,\n" +
                        "    \"categoria\": \"Personal\",\n" +
                        "    \"descripcion\": \"Hacer ejercicio\",\n" +
                        "    \"fecIni\": \"2024-05-03T06:30:00Z\",\n" +
                        "    \"fecFin\": \"2024-05-03T07:30:00Z\",\n" +
                        "    \"tiempo\": 1,\n" +
                        "    \"prioridad\": 3\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"cod\": 3,\n" +
                        "    \"categoria\": \"Trabajo\",\n" +
                        "    \"descripcion\": \"Reunión de equipo\",\n" +
                        "    \"fecIni\": \"2024-05-04T10:00:00Z\",\n" +
                        "    \"fecFin\": \"2024-05-04T11:00:00Z\",\n" +
                        "    \"tiempo\": 1,\n" +
                        "    \"prioridad\": 1\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"cod\": 4,\n" +
                        "    \"categoria\": \"Personal\",\n" +
                        "    \"descripcion\": \"Comprar víveres\",\n" +
                        "    \"fecIni\": \"2024-05-04T18:00:00Z\",\n" +
                        "    \"fecFin\": \"2024-05-04T19:00:00Z\",\n" +
                        "    \"tiempo\": 1,\n" +
                        "    \"prioridad\": 2\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"cod\": 5,\n" +
                        "    \"categoria\": \"Trabajo\",\n" +
                        "    \"descripcion\": \"Enviar correos electrónicos\",\n" +
                        "    \"fecIni\": \"2024-05-05T09:00:00Z\",\n" +
                        "    \"fecFin\": \"2024-05-05T11:00:00Z\",\n" +
                        "    \"tiempo\": 2,\n" +
                        "    \"prioridad\": 2\n" +
                        "  }\n" +
                        "]";

                Gson gson = new Gson();
                return gson.fromJson(json, GetTasksResponse.class);
            }
        };
    }
}
