package com.example.proyecto;

import com.example.proyecto.model.Task;
import com.example.proyecto.model.Worker;
import com.example.proyecto.service.GetTasksService;
import com.example.proyecto.service.GetWorkersService;
import com.example.proyecto.service.PostTaskService;
import com.example.proyecto.utils.MessageUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
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

    // Campos del formulario
    @FXML
    private TextField categoriaField;

    @FXML
    private TextArea descripcionArea;

    @FXML
    private TextField prioridadField;

    @FXML
    private ChoiceBox<Worker> trabajadorChoiceBox;

    private GetWorkersService getWorkersService;
    private PostTaskService postTaskService;
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
            if(!getWorkersService.getValue().isError()) {
                System.out.println(getWorkersService.getValue().getWorkers());
                lvWorkersMg.setItems(FXCollections.observableArrayList(getWorkersService.getValue().getWorkers()));
            } else {
                MessageUtils.showError("Error getting tasks", getWorkersService.getValue().getErrorMessage());
            }
        });
        getWorkersService.setOnFailed(e-> {
            MessageUtils.showError("Error", "Error connecting to server");
        });

        btnCreateT.setOnAction(event -> createTask());
      
        // Configurar listeners para los radio buttons
        rbAll.setOnAction(event -> updateTaskList());
        rbAssigned.setOnAction(event -> updateTaskList());
        rbUnassigned.setOnAction(event -> updateTaskList());
    }

    private void createTask() {
        // Crear el diseño del formulario
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Inicializar los campos del formulario
        categoriaField = new TextField();
        descripcionArea = new TextArea();
        prioridadField = new TextField();
        trabajadorChoiceBox = new ChoiceBox<>();
        // Crear una lista de trabajadores (de ejemplo)
        ObservableList<Worker> workers = FXCollections.observableArrayList(
                new Worker("12345678A", "Juan", "González", "Programador", "password", "juan@example.com"),
                new Worker("87654321B", "María", "López", "Diseñador", "password", "maria@example.com"),
                new Worker("98765432C", "Pedro", "Martínez", "Tester", "password", "pedro@example.com")
        );
        trabajadorChoiceBox.setItems(workers);
        //trabajadorChoiceBox.setItems(FXCollections.observableArrayList(getWorkersService.getValue().getWorkers()));

        // Agregar los campos al diseño
        gridPane.add(new Label("Categoría:"), 0, 0);
        gridPane.add(categoriaField, 1, 0);
        gridPane.add(new Label("Descripción:"), 0, 1);
        gridPane.add(descripcionArea, 1, 1);
        gridPane.add(new Label("Prioridad:"), 0, 2);
        gridPane.add(prioridadField, 1, 2);
        gridPane.add(new Label("Trabajador:"), 0, 3);
        gridPane.add(trabajadorChoiceBox, 1, 3);

        // Crear el diálogo
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Crear Tarea");
        dialog.getDialogPane().setContent(gridPane);

        // Agregar botones al diálogo
        ButtonType createButtonType = new ButtonType("Crear", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Habilitar el botón de crear cuando el formulario es válido
        Node createButton = dialog.getDialogPane().lookupButton(createButtonType);

        createButton.setDisable(true);
        // Validar el formulario
        categoriaField.textProperty().addListener((observable, oldValue, newValue) -> validateFields(createButton));
        descripcionArea.textProperty().addListener((observable, oldValue, newValue) -> validateFields(createButton));
        prioridadField.textProperty().addListener((observable, oldValue, newValue) -> validateFields(createButton));
        trabajadorChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> validateFields(createButton));

        // Mostrar el diálogo y esperar a que el usuario interactúe
        dialog.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == ButtonType.OK.getButtonData()) {
                // Obtener los valores del formulario y crear la tarea
                String categoria = categoriaField.getText();
                String descripcion = descripcionArea.getText();
                LocalDate fecIniLocalDate = LocalDate.now();
                int prioridad = Integer.parseInt(prioridadField.getText());
                Worker trabajador = trabajadorChoiceBox.getValue();

                // Convertir LocalDate a Date
                Date fecIni = Date.from(fecIniLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

                Task task;
                if (trabajador != null)
                    task = new Task(categoria, descripcion, fecIni, prioridad, trabajador);
                else {
                    task = new Task(categoria, descripcion, fecIni, prioridad);
                }
                //postTaskService = new PostTaskService(task);
                MessageUtils.showMessage("Tarea creada", "Tarea creada con éxito");
                System.out.println("Tarea creada");
            }
        });
    }

    private void validateFields(Node createButton) {
        boolean isPrioridadNumeric = isPrioridadValid(prioridadField.getText());
        boolean allFieldsFilled = !categoriaField.getText().isEmpty() &&
                !descripcionArea.getText().isEmpty() && isPrioridadNumeric;

        // Habilitar el botón si todos los campos están rellenos
        createButton.setDisable(!allFieldsFilled);
    }

    private boolean isPrioridadValid(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        if(Integer.parseInt(str) < 0 || Integer.parseInt(str) > 3){
            return false;
        }

        return true;
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
    private void onRadioButtonSelected() {
        RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();

        if (selectedRadioButton == rbAll) {
            rbAssigned.setSelected(false);
            rbUnassigned.setSelected(false);
        } else if (selectedRadioButton == rbAssigned) {
            rbAll.setSelected(false);
            rbUnassigned.setSelected(false);
        }
    }
}