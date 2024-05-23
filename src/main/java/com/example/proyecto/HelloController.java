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
    private TextField codField;

    @FXML
    private ChoiceBox<Categoria> categoriaChoiceBox;

    @FXML
    private TextArea descripcionArea;

    @FXML
    private TextField prioridadField;

    @FXML
    private ChoiceBox<Worker> trabajadorChoiceBox;

    private GetWorkersService getWorkersService;
    private PostTaskService postTaskService;
    private GetTasksService getTasksService;;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Seleccionar un radiobutton
        toggleGroup = new ToggleGroup();
        rbAll.setToggleGroup(toggleGroup);
        rbAssigned.setToggleGroup(toggleGroup);
        rbUnassigned.setToggleGroup(toggleGroup);

        // Configurar listeners para los radio buttons
        rbAll.setSelected(true);
        rbAll.setOnAction(event -> updateTaskList());
        rbAssigned.setOnAction(event -> updateTaskList());
        rbUnassigned.setOnAction(event -> updateTaskList());

        // Mostrar lista de trabajadores
        updateTaskList();

        btnCreateT.setOnAction(event -> createTask());
    }


    private void createTask() {
        // Crear el diseño del formulario
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Inicializar los campos del formulario
        codField = new TextField();
        categoriaChoiceBox = new ChoiceBox<>();
        categoriaChoiceBox.getItems().addAll(Categoria.values()); // Cargar valores del enum
        categoriaChoiceBox.setValue(Categoria.LIMPIEZA); // Valor por defecto
        descripcionArea = new TextArea();
        prioridadField = new TextField();
        trabajadorChoiceBox = new ChoiceBox<>();
        // Crear una lista de trabajadores (de ejemplo)
        ObservableList<Worker> workers = FXCollections.observableArrayList(
                new Worker("W001", "12345678A", "Juan", "González", "Carpinteria", "password", "juan@example.com"),
                new Worker("W002", "87654321B", "María", "López", "Limpieza", "password", "maria@example.com"),
                new Worker("W003", "98765432C", "Pedro", "Martínez", "Electricidad", "password", "pedro@example.com")
        );
        trabajadorChoiceBox.setItems(workers);

        // Agregar los campos al diseño
        gridPane.add(new Label("Código:"), 0, 0);
        gridPane.add(codField, 1, 0);
        gridPane.add(new Label("Categoría:"), 0, 1);
        gridPane.add(categoriaChoiceBox, 1, 1);
        gridPane.add(new Label("Descripción:"), 0, 2);
        gridPane.add(descripcionArea, 1, 2);
        gridPane.add(new Label("Prioridad:"), 0, 3);
        gridPane.add(prioridadField, 1, 3);
        gridPane.add(new Label("Trabajador:"), 0, 4);
        gridPane.add(trabajadorChoiceBox, 1, 4);

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
        codField.textProperty().addListener((observable, oldValue, newValue) -> validateFields(createButton));
        descripcionArea.textProperty().addListener((observable, oldValue, newValue) -> validateFields(createButton));
        prioridadField.textProperty().addListener((observable, oldValue, newValue) -> validateFields(createButton));
        trabajadorChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> validateFields(createButton));

        // Mostrar el diálogo y esperar a que el usuario interactúe
        dialog.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == ButtonType.OK.getButtonData()) {
                // Obtener los valores del formulario y crear la tarea
                String cod = codField.getText();
                Categoria categoria = categoriaChoiceBox.getValue();
                String descripcion = descripcionArea.getText();
                LocalDate fecIniLocalDate = LocalDate.now();
                int prioridad = Integer.parseInt(prioridadField.getText());
                Worker trabajador = trabajadorChoiceBox.getValue();

                // Convertir LocalDate a Date
                Date fecIni = Date.from(fecIniLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

                Task task;
                if (trabajador != null)
                    task = new Task(cod, categoria.getDisplayName(), descripcion, fecIni, prioridad, trabajador.getIdTrabajador());
                else {
                    task = new Task(cod, categoria.getDisplayName(), descripcion, fecIni, prioridad);
                }
                //postTaskService = new PostTaskService(task);
                MessageUtils.showMessage("Tarea creada", "Tarea creada con éxito");
                System.out.println("Tarea creada");
            }
        });
    }

    private void validateFields(Node createButton) {
        boolean isPrioridadNumeric = isPrioridadValid(prioridadField.getText());
        boolean categoriaEspecialidad = isCategoriaValid(categoriaChoiceBox.getValue().getDisplayName());

        boolean allFieldsFilled = !codField.getText().isEmpty() && codField.getText().length() == 5 &&
                !descripcionArea.getText().isEmpty() && descripcionArea.getText().length() < 500 &&
                isPrioridadNumeric && categoriaEspecialidad;

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

    private boolean isCategoriaValid (String str) {
        if(trabajadorChoiceBox.getValue() == null)
            return true;

        if (trabajadorChoiceBox.getValue() != null && trabajadorChoiceBox.getValue().getEspecialidad().equals(str))
            return true;
        else
            return false;
    }

    private void updateTaskList() {
        if (rbAll.isSelected()) {
            getTasksService = new GetTasksService();
        } else if (rbAssigned.isSelected()) {
            // Mostrar tareas asignadas (con worker no nulo)
            getTasksService = new GetTasksService("/asignadas");
        } else if (rbUnassigned.isSelected()) {
            // Mostrar tareas no asignadas (con worker nulo)
            getTasksService = new GetTasksService("/sin-asignar");
        }

        // Configurar manejadores de éxito y fallo
        getTasksService.setOnSucceeded(e -> {
            if (getTasksService.getValue().getStatus() >= 200 && getTasksService.getValue().getStatus() < 300) {
                lvTaskMg.setItems(FXCollections.observableArrayList(getTasksService.getValue().getResult()));
            } else {
                MessageUtils.showError("Error getting tasks", getTasksService.getValue().getMessage());
            }
        });
        getTasksService.setOnFailed(e -> {
            MessageUtils.showError("Error", "Error connecting to server");
        });

        // Iniciar el servicio
        getTasksService.start();
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

enum Categoria {
    LIMPIEZA("Limpieza"),
    CARPINTERIA("Carpinteria"),
    FONTANERIA("Fontaneria"),
    ELECTRICIDAD("Electricidad");

    private final String displayName;

    Categoria(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}