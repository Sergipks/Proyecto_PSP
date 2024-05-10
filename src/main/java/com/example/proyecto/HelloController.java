package com.example.proyecto;

import com.example.proyecto.model.Task;
import com.example.proyecto.model.Worker;
import com.example.proyecto.service.GetTasksService;
import com.example.proyecto.service.GetWorkersService;
import com.example.proyecto.utils.MessageUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private ListView<Task> lvTaskMg;

    @FXML
    private RadioButton rbAll;

    @FXML
    private RadioButton rbAssigned;

    @FXML
    private RadioButton rbUnassigned;

    private ToggleGroup toggleGroup;

    @FXML
    private Button btnCreateT;

    @FXML
    private Button btnUpdateT;

    @FXML
    private Button btnDeleteT;

    @FXML
    private ListView<Worker> lvWorkersMg;

    @FXML
    private Button btnCreateW;

    @FXML
    private Button btnUpdateW;

    @FXML
    private Button btnDeleteW;

    @FXML
    private Button btnCreateP;

    @FXML
    private ListView<String> lvTasksA;

    @FXML
    private ListView<Worker> lvWorkersA;

    @FXML
    private ListView<String> lvAssignments;

    @FXML
    private ChoiceBox<String> cbJobs;

    @FXML
    private Button btnDeleteA;

    @FXML
    private Button btnConfirmA;

    private GetWorkersService getWorkersService;
    private GetTasksService getTasksService;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Seleccionar un radiobutton
        toggleGroup = new ToggleGroup();
        rbAll.setToggleGroup(toggleGroup);
        rbAssigned.setToggleGroup(toggleGroup);
        rbUnassigned.setToggleGroup(toggleGroup);

        // Mostrar lista de trabajadores
        getWorkersService = new GetWorkersService();
        getWorkersService.start();

        getWorkersService.setOnSucceeded(e-> {
            if(!getWorkersService.getValue().isError())
            {
                System.out.println(getWorkersService.getValue().getWorkers());
                lvWorkersMg.setItems(FXCollections.observableArrayList(getWorkersService.getValue().getWorkers()));
            }
            else{
                MessageUtils.showError("Error getting workers", getWorkersService.getValue().getErrorMessage());
            }
        });
        getWorkersService.setOnFailed(e-> {
            MessageUtils.showError("Error", "Error connecting to server");
        });

        // Mostrar lista de tareas
        getTasksService = new GetTasksService();
        getTasksService.start();

        getTasksService.setOnSucceeded(e-> {
            if(!getTasksService.getValue().isError())
            {
                System.out.println(getWorkersService.getValue().getWorkers());
                lvTaskMg.setItems(FXCollections.observableArrayList(getTasksService.getValue().getTasks()));
            }
            else{
                MessageUtils.showError("Error getting tasks", getTasksService.getValue().getErrorMessage());
            }
        });
        getTasksService.setOnFailed(e-> {
            MessageUtils.showError("Error", "Error connecting to server");
        });
        // Configurar listeners para los radio buttons
        rbAll.setOnAction(event -> updateTaskList());
        rbAssigned.setOnAction(event -> updateTaskList());
        rbUnassigned.setOnAction(event -> updateTaskList());


        /* Botones
        btnCreateT.setOnAction(event -> createTask());
        btnUpdateT.setOnAction(event -> updateTask());
        btnDeleteT.setOnAction(event -> deleteTask());
        btnCreateW.setOnAction(event -> createWorker());
        btnUpdateW.setOnAction(event -> updateWorker());
        btnDeleteW.setOnAction(event -> deleteWorker());
        btnCreateP.setOnAction(event -> createPayroll());
        btnDeleteA.setOnAction(event -> deleteAssignment());
        btnConfirmA.setOnAction(event -> confirmAssignment());

         */
    }

    private void updateTaskList() {
        if (rbAll.isSelected()) {
            getTasksService = new GetTasksService();
        } else if (rbAssigned.isSelected()) {
            // Mostrar tareas asignadas (con worker no nulo)
            getTasksService = new GetTasksService("/assigned");
        } else if (rbUnassigned.isSelected()) {
            // Mostrar tareas no asignadas (con worker nulo)
            getTasksService = new GetTasksService("/notassigned");
        }

        List<Task> tasks = getTasksService.getValue().getTasks();
        // Actualizar la lista de tareas en la ListView
        lvTaskMg.setItems(FXCollections.observableArrayList(tasks));
    }


    @FXML
    private void onRadioButtonSelected(){
        RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();

        if (selectedRadioButton == rbAll) {
            rbAssigned.setSelected(false);
            rbUnassigned.setSelected(false);
        } else if (selectedRadioButton == rbAssigned) {
            rbAll.setSelected(false);
            rbUnassigned.setSelected(false);
        } else if (selectedRadioButton == rbUnassigned) {
            rbAll.setSelected(false);
            rbAssigned.setSelected(false);
        }
    }

}