package com.example.proyecto;

import com.example.proyecto.model.Task;
import com.example.proyecto.model.Worker;
import com.example.proyecto.response.BaseResponse;
import com.example.proyecto.response.GetWorkerResponse;
import com.example.proyecto.service.*;
import com.example.proyecto.utils.MessageUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.net.URL;
import java.text.Collator;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
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
    private ListView<Task> lvTasksA;

    @FXML
    private ListView<Worker> lvWorkersA;

    @FXML
    private ListView<Task> lvAssignments;

    @FXML
    private ChoiceBox<Categoria> cbJobs;

    @FXML
    private Button btnDeleteA;

    @FXML
    private Button btnConfirmA;

    @FXML
    private TabPane tabPane;

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
    private TextField tiempoField;

    @FXML
    private CheckBox finalizarCheckbox;

    @FXML
    private ChoiceBox<Worker> trabajadorChoiceBox;

    private String workerFilter;

    private GetWorkersService getWorkersService;
    private PostTaskService postTaskService;
    private GetTasksService getTasksService;
    private GetTaskService getTaskService;
    private DeleteTaskService deleteTaskService;
    private UpdateTaskService updateTaskService;

    private DeleteWorkerService deleteWorkerService;

    boolean errorAssignment;

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

        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                handleTabChange(newTab);
            }
        });

        // Mostrar lista de trabajadores
        updateTaskList();

        // Mostrar lista de trabajadores
        updateWorkerList();

        btnCreateT.setOnAction(event -> createTask());
        btnDeleteT.setOnAction(event -> deleteTask());
        btnUpdateT.setOnAction(event -> updateTask());

        btnCreateW.setOnAction(event -> createWorker());
        btnDeleteW.setOnAction(event -> deleteWorker());
        btnUpdateW.setOnAction(event -> updateWorker());

        btnDeleteA.setOnAction(event -> deleteAssignment());
        btnConfirmA.setOnAction(event -> confirmAssignments());

        dragAndDropAssignmentConf();
    }

    private void confirmAssignments() {
        errorAssignment = false;
        for (Task task : lvAssignments.getItems()) {
            String filter = "/asignar/" + task.getIdTrabajador();
            UpdateTaskService updateTaskService = new UpdateTaskService(task, filter);

            // Configurar manejadores de éxito y fallo
            updateTaskService.setOnSucceeded(e -> {
                if (!(updateTaskService.getValue().getStatus() >= 200 && updateTaskService.getValue().getStatus() < 300)) {
                    errorAssignment = true;
                }
            });

            updateTaskService.setOnFailed(e -> {
                errorAssignment = true;
            });

            // Iniciar el servicio
            updateTaskService.start();
        }

        if (errorAssignment) {
            MessageUtils.showError("Error Assigning", "An error occurred while assigning tasks");
        }

        // Limpiar la lista de asignaciones después de confirmar
        lvAssignments.getItems().clear();
    }

    private void dragAndDropAssignmentConf(){
        // Set up drag and drop for tasks
        lvTasksA.setCellFactory(lv -> {
            ListCell<Task> cell = new ListCell<>() {
                @Override
                protected void updateItem(Task item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item.toString());
                }
            };

            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(cell.getItem().getCodTrabajo());
                    db.setContent(content);
                    event.consume();
                }
            });

            return cell;
        });

        // Set up drag and drop for workers
        lvWorkersA.setCellFactory(lv -> {
            ListCell<Worker> cell = new ListCell<>() {
                @Override
                protected void updateItem(Worker item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item.toString());
                }
            };

            cell.setOnDragOver(event -> {
                if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            cell.setOnDragDropped(event -> {
                boolean success = false;
                Dragboard db = event.getDragboard();
                if (db.hasString()) {
                    String taskCod = db.getString();
                    Worker worker = cell.getItem();

                    Task taskToAssign = lvTasksA.getItems().stream()
                            .filter(task -> task.getCodTrabajo().equals(taskCod))
                            .findFirst()
                            .orElse(null);

                    if (taskToAssign != null &&
                            equalsIgnoreCaseAndAccents(taskToAssign.getCategoria(), cell.getItem().getEspecialidad())) {
                        taskToAssign.setIdTrabajador(worker.getIdTrabajador());
                        lvAssignments.getItems().add(taskToAssign);
                        lvTasksA.getItems().remove(taskToAssign);
                        success = true;
                    } else {
                        MessageUtils.showError("Error assigning task", "The task category must be related to worker specialization");
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });

            return cell;
        });
    }

    private void deleteAssignment() {
        Task selectedItem = lvAssignments.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            lvAssignments.getItems().remove(selectedItem);
            selectedItem.setIdTrabajador(null);
            lvTasksA.getItems().add(selectedItem);
        }
    }

    private void handleTabChange(Tab newTab) {
        String tabText = newTab.getText();

        switch (tabText) {
            case "Task management":
                // Lógica para cuando se selecciona la pestaña "Task management"
                updateTaskList();
                break;
            case "Workers management":
                // Lógica para cuando se selecciona la pestaña "Workers management"
                updateWorkerList();
                break;
            case "Task Assignment":
                // Lógica para cuando se selecciona la pestaña "Task Assignment"
                assignmentTaskList();
                assignmentWorkersList();
                lvAssignments.getItems().clear();
                break;
        }
    }

    private void assignmentTaskList() {
        getTasksService = new GetTasksService("/sin-asignar");

        // Configurar manejadores de éxito y fallo
        getTasksService.setOnSucceeded(e -> {
            if (getTasksService.getValue().getStatus() >= 200 && getTasksService.getValue().getStatus() < 300) {
                lvTasksA.setItems(FXCollections.observableArrayList(getTasksService.getValue().getResult()));
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

    private void assignmentWorkersList(){
        workerFilter = "";
        cbJobs.getItems().clear();
        cbJobs.getItems().addAll(Categoria.values()); // Cargar valores del enum
        cbJobs.setValue(null); // Valor por defecto

        cbJobs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            workerFilter = "/especialidad/" + cbJobs.getSelectionModel().getSelectedItem().getDisplayName();
            loadWorkersList();
        });

        loadWorkersList();
    }

    private void loadWorkersList(){
        getWorkersService = new GetWorkersService(workerFilter);
        // Configurar manejadores de éxito y fallo
        getWorkersService.setOnSucceeded(e -> {
            if (getWorkersService.getValue().getStatus() >= 200 && getWorkersService.getValue().getStatus() < 300) {
                lvWorkersA.setItems(FXCollections.observableArrayList(getWorkersService.getValue().getResult()));
            } else {
                MessageUtils.showError("Error getting workers", getWorkersService.getValue().getMessage());
            }
        });
        getWorkersService.setOnFailed(e -> {
            MessageUtils.showError("Error", "Error connecting to server");
        });

        // Iniciar el servicio
        getWorkersService.start();
    }

    private void deleteTask() {
        // Obtener el índice de la tarea seleccionada en lvTaskMg
        int selectedIndex = lvTaskMg.getSelectionModel().getSelectedIndex();

        // Verificar si hay alguna tarea seleccionada
        if (selectedIndex != -1) {
            // Obtener la tarea seleccionada
            Task selectedTask = lvTaskMg.getItems().get(selectedIndex);

            // Obtener el cod de la tarea seleccionada
            String taskId = selectedTask.getCodTrabajo();

            deleteTaskService = new DeleteTaskService(taskId);

            // Configurar manejadores de éxito y fallo
            deleteTaskService.setOnSucceeded(e -> {
                if (deleteTaskService.getValue().getStatus() >= 200 && deleteTaskService.getValue().getStatus() < 300) {
                    MessageUtils.showMessage("Task deleted", deleteTaskService.getMessage());
                    updateTaskList();
                } else {
                    MessageUtils.showError("Error deleting the task", deleteTaskService.getValue().getMessage());
                }
            });
            deleteTaskService.setOnFailed(e -> {
                MessageUtils.showError("Error", "Error connecting to server");
            });

            // Iniciar el servicio
            deleteTaskService.start();
        }
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

        getWorkersService = new GetWorkersService();
        // Configurar manejadores de éxito y fallo
        getWorkersService.setOnSucceeded(e -> {
            if (getWorkersService.getValue().getStatus() >= 200 && getWorkersService.getValue().getStatus() < 300) {
                trabajadorChoiceBox.setItems(FXCollections.observableArrayList(getWorkersService.getValue().getResult()));
            } else {
                MessageUtils.showError("Error getting workers", getWorkersService.getValue().getMessage());
            }
        });
        getWorkersService.setOnFailed(e -> {
            MessageUtils.showError("Error", "Error connecting to server");
        });

        // Iniciar el servicio
        getWorkersService.start();

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
        codField.textProperty().addListener((observable, oldValue, newValue) -> validateCreateFields(createButton));
        categoriaChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> validateCreateFields(createButton));
        descripcionArea.textProperty().addListener((observable, oldValue, newValue) -> validateCreateFields(createButton));
        prioridadField.textProperty().addListener((observable, oldValue, newValue) -> validateCreateFields(createButton));
        trabajadorChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> validateCreateFields(createButton));

        // Mostrar el diálogo y esperar a que el usuario interactúe
        dialog.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == ButtonType.OK.getButtonData()) {
                // Obtener los valores del formulario y crear la tarea
                String cod = codField.getText();
                Categoria categoria = categoriaChoiceBox.getValue();
                String descripcion = descripcionArea.getText();
                int prioridad = Integer.parseInt(prioridadField.getText());
                Worker trabajador = trabajadorChoiceBox.getValue();

                Task task;
                String filter = "";
                if (trabajador != null) {
                    task = new Task(cod, categoria.getDisplayName(), descripcion, prioridad, null);
                    filter = "/trabajador/" + trabajador.getIdTrabajador() + "/crear-trabajo";
                }
                else {
                    task = new Task(cod, categoria.getDisplayName(), descripcion, prioridad);
                }
                postTaskService = new PostTaskService(task, filter);

                // Configurar manejadores de éxito y fallo
                postTaskService.setOnSucceeded(e -> {
                    if (postTaskService.getValue().getStatus() >= 200 && postTaskService.getValue().getStatus() < 300) {
                        MessageUtils.showMessage("Task Created", "Task created successfully");
                        updateTaskList();
                    } else {
                        MessageUtils.showError("Error creating task", postTaskService.getValue().getMessage());
                    }
                });
                postTaskService.setOnFailed(e -> {
                    MessageUtils.showError("Error", "Error connecting to server");
                });

                // Iniciar el servicio
                postTaskService.start();
            }
        });
    }

    private void updateTask() {
        // Obtener el índice de la tarea seleccionada en lvTaskMg
        int selectedIndex = lvTaskMg.getSelectionModel().getSelectedIndex();

        // Verificar si hay alguna tarea seleccionada
        if (selectedIndex != -1) {
            // Obtener la tarea seleccionada
            Task selectedTask = lvTaskMg.getItems().get(selectedIndex);

            // Crear el diseño del formulario
            GridPane gridPane = new GridPane();
            gridPane.setPadding(new Insets(20));
            gridPane.setHgap(10);
            gridPane.setVgap(10);

            // Inicializar los campos del formulario
            descripcionArea = new TextArea(selectedTask.getDescripcion());
            prioridadField = new TextField(String.valueOf(selectedTask.getPrioridad()));
            finalizarCheckbox = new CheckBox("Finalizar tarea");
            tiempoField = new TextField();
            tiempoField.setPromptText("Tiempo empleado");

            // Agregar los campos al diseño
            gridPane.add(new Label("Descripción:"), 0, 0);
            gridPane.add(descripcionArea, 1, 0);
            gridPane.add(new Label("Prioridad:"), 0, 1);
            gridPane.add(prioridadField, 1, 1);
            gridPane.add(finalizarCheckbox, 0, 2);
            gridPane.add(tiempoField, 1, 2);
            tiempoField.setVisible(false); // Ocultar el campo de tiempo al inicio

            // Añadir listener al checkbox para mostrar/ocultar el campo de tiempo
            finalizarCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                tiempoField.setVisible(newValue);
            });

            // Crear el diálogo
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Actualizar tarea");
            dialog.getDialogPane().setContent(gridPane);

            // Agregar botones al diálogo
            ButtonType updateButtonType = new ButtonType("Actualizar", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            // Habilitar el botón de actualizar cuando el formulario es válido
            Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
            updateButton.setDisable(true);

            // Validar el formulario
            descripcionArea.textProperty().addListener((observable, oldValue, newValue) -> validateUpdateFields(updateButton));
            prioridadField.textProperty().addListener((observable, oldValue, newValue) -> validateUpdateFields(updateButton));
            tiempoField.textProperty().addListener((observable, oldValue, newValue) -> validateUpdateFields(updateButton));

            // Mostrar el diálogo y esperar a que el usuario interactúe
            dialog.showAndWait().ifPresent(response -> {
                if (response.getButtonData() == ButtonType.OK.getButtonData()) {
                    selectedTask.setDescripcion(descripcionArea.getText());
                    selectedTask.setPrioridad(Integer.parseInt(prioridadField.getText()));

                    if (finalizarCheckbox.isSelected()) {
                        BigDecimal tiempo = new BigDecimal(tiempoField.getText());
                        selectedTask.setTiempo(tiempo);
                    }

                    selectedTask.setFecIni(null);
                    selectedTask.setFecFin(null);
                    updateTaskService = new UpdateTaskService(selectedTask);

                    // Configurar manejadores de éxito y fallo
                    updateTaskService.setOnSucceeded(e -> {
                        if (updateTaskService.getValue().getStatus() >= 200 && updateTaskService.getValue().getStatus() < 300) {
                            MessageUtils.showMessage("Tarea Actualizada", "Tarea actualizada exitosamente");
                            if (finalizarCheckbox.isSelected()) {
                                FinishTaskService finishTaskService = new FinishTaskService(updateTaskService.getValue().getResult());
                                finishTaskService.start();
                            }
                            updateTaskList();
                        } else {
                            MessageUtils.showError("Error actualizando la tarea", updateTaskService.getValue().getMessage());
                        }
                    });
                    updateTaskService.setOnFailed(e -> {
                        MessageUtils.showError("Error", "Error conectando con el servidor");
                    });

                    // Iniciar el servicio
                    updateTaskService.start();
                }
            });
        }
    }


    private void validateCreateFields(Node createButton) {
        boolean isPrioridadNumeric = isPrioridadValid(prioridadField.getText());
        boolean categoriaEspecialidad = isCategoriaValid(categoriaChoiceBox.getValue().getDisplayName());

        boolean allFieldsFilled = !codField.getText().isEmpty() && codField.getText().length() == 5 &&
                !descripcionArea.getText().isEmpty() && descripcionArea.getText().length() < 500 &&
                isPrioridadNumeric && categoriaEspecialidad;

        // Habilitar el botón si todos los campos están rellenos
        createButton.setDisable(!allFieldsFilled);
    }

    private void validateUpdateFields(Node createButton) {
        boolean isPrioridadNumeric = isPrioridadValid(prioridadField.getText());
        boolean isTiempoValid = true;

        if (finalizarCheckbox.isSelected())
            isTiempoValid = isTiempoValid(tiempoField.getText());

        boolean allFieldsFilled = !descripcionArea.getText().isEmpty() && descripcionArea.getText().length() < 500 &&
                isPrioridadNumeric && isTiempoValid ;

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
        if(Integer.parseInt(str) < 1 || Integer.parseInt(str) > 4){
            return false;
        }

        return true;
    }

    private boolean isTiempoValid(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            BigDecimal tiempo = new BigDecimal(str);
            if (tiempo.compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }
            // Aquí puedes agregar cualquier otra validación adicional que necesites
            // Por ejemplo, si necesitas que el valor esté entre 0 y 3, puedes descomentar la siguiente línea:
            // return tiempo.compareTo(BigDecimal.ZERO) >= 0 && tiempo.compareTo(new BigDecimal("3")) <= 0;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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


    private void updateWorkerList() {
        getWorkersService = new GetWorkersService();
        // Configurar manejadores de éxito y fallo
        getWorkersService.setOnSucceeded(e -> {
            if (getWorkersService.getValue().getStatus() >= 200 && getWorkersService.getValue().getStatus() < 300) {
                lvWorkersMg.setItems(FXCollections.observableArrayList(getWorkersService.getValue().getResult()));
            } else {
                MessageUtils.showError("Error getting workers", getWorkersService.getValue().getMessage());
            }
        });
        getWorkersService.setOnFailed(e -> {
            MessageUtils.showError("Error", "Error connecting to server");
        });

        // Iniciar el servicio
        getWorkersService.start();
    }


    private void createWorker() {
        // Crear el diseño del formulario
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Inicializar los campos del formulario
        TextField idTrabajadorField = new TextField();
        TextField dniField = new TextField();
        TextField nombreField = new TextField();
        TextField apellidosField = new TextField();
        ChoiceBox<String> especialidadChoiceBox = new ChoiceBox<>();
        especialidadChoiceBox.getItems().addAll("Limpieza", "Carpinteria", "Fontaneria", "Electricidad");
        especialidadChoiceBox.setValue("Limpieza");
        PasswordField contrasenyaField = new PasswordField();
        TextField emailField = new TextField();

        // Agregar los campos al diseño
        gridPane.add(new Label("ID:"), 0, 0);
        gridPane.add(idTrabajadorField, 1, 0);
        gridPane.add(new Label("DNI:"), 0, 1);
        gridPane.add(dniField, 1, 1);
        gridPane.add(new Label("Nombre:"), 0, 2);
        gridPane.add(nombreField, 1, 2);
        gridPane.add(new Label("Apellidos:"), 0, 3);
        gridPane.add(apellidosField, 1, 3);
        gridPane.add(new Label("Especialidad:"), 0, 4);
        gridPane.add(especialidadChoiceBox, 1, 4);
        gridPane.add(new Label("Contraseña:"), 0, 5);
        gridPane.add(contrasenyaField, 1, 5);
        gridPane.add(new Label("Email:"), 0, 6);
        gridPane.add(emailField, 1, 6);

        // Crear el diálogo
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Crear Trabajador");
        dialog.getDialogPane().setContent(gridPane);

        // Agregar botones al diálogo
        ButtonType createButtonType = new ButtonType("Crear", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Habilitar el botón de crear cuando el formulario es válido
        Node createButton = dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);

        // Validar el formulario
        dniField.textProperty().addListener((observable, oldValue, newValue) -> validateCreateFieldsWorker(createButton, idTrabajadorField, dniField, nombreField, apellidosField, especialidadChoiceBox, contrasenyaField, emailField));
        nombreField.textProperty().addListener((observable, oldValue, newValue) -> validateCreateFieldsWorker(createButton, idTrabajadorField, dniField, nombreField, apellidosField, especialidadChoiceBox, contrasenyaField, emailField));
        apellidosField.textProperty().addListener((observable, oldValue, newValue) -> validateCreateFieldsWorker(createButton, idTrabajadorField, dniField, nombreField, apellidosField, especialidadChoiceBox, contrasenyaField, emailField));
        especialidadChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> validateCreateFieldsWorker(createButton, idTrabajadorField, dniField, nombreField, apellidosField, especialidadChoiceBox, contrasenyaField, emailField));
        contrasenyaField.textProperty().addListener((observable, oldValue, newValue) -> validateCreateFieldsWorker(createButton, idTrabajadorField, dniField, nombreField, apellidosField, especialidadChoiceBox, contrasenyaField, emailField));
        emailField.textProperty().addListener((observable, oldValue, newValue) -> validateCreateFieldsWorker(createButton, idTrabajadorField, dniField, nombreField, apellidosField, especialidadChoiceBox, contrasenyaField, emailField));

        // Mostrar el diálogo y esperar a que el usuario interactúe
        dialog.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == ButtonType.OK.getButtonData()) {
                // Obtener los valores del formulario y crear el trabajador
                String idTrabajador = idTrabajadorField.getText();
                String dni = dniField.getText();
                String nombre = nombreField.getText();
                String apellidos = apellidosField.getText();
                String especialidad = especialidadChoiceBox.getValue();
                String contraseña = contrasenyaField.getText();
                String email = emailField.getText();

                Worker nuevoTrabajador = new Worker(idTrabajador, dni, nombre, apellidos, especialidad, contraseña, email);

                // Llamar al servicio para crear el trabajador con los datos ingresados
                PostWorkerService postWorkerService = new PostWorkerService(nuevoTrabajador);
                postWorkerService.setOnSucceeded(e -> {
                    GetWorkerResponse postResponse = postWorkerService.getValue();
                    if (postResponse.getStatus() >= 200 && postResponse.getStatus() < 300) {
                        MessageUtils.showMessage("Trabajador Creado", "Trabajador creado exitosamente");
                        // Aquí puedes actualizar la lista de trabajadores si es necesario
                        updateWorkerList();
                    } else {
                        MessageUtils.showError("Error creando trabajador", postResponse.getMessage());
                    }
                });
                postWorkerService.setOnFailed(e -> {
                    MessageUtils.showError("Error", "Error al conectar con el servidor");
                });

                postWorkerService.start();

                // Limpiar los campos
                idTrabajadorField.clear();
                dniField.clear();
                nombreField.clear();
                apellidosField.clear();
                especialidadChoiceBox.setValue("LIMPIEZA");
                contrasenyaField.clear();
                emailField.clear();
            }
        });
    }

    // Función para validar los campos del formulario
    private void validateCreateFieldsWorker(Node createButton, TextField idTrabajadorField, TextField dniField, TextField nombreField, TextField apellidosField, ChoiceBox<String> especialidadChoiceBox, PasswordField contrasenyaField, TextField emailField) {
        boolean isValid = !idTrabajadorField.getText().isEmpty() &&
                !dniField.getText().isEmpty() &&
                !nombreField.getText().isEmpty() &&
                !apellidosField.getText().isEmpty() &&
                especialidadChoiceBox.getValue() != null &&
                !contrasenyaField.getText().isEmpty() &&
                !emailField.getText().isEmpty();
        createButton.setDisable(!isValid);
    }

    private void updateWorker() {
        // Obtener el índice del trabajador seleccionado en lvTaskMg
        int selectedIndex = lvWorkersMg.getSelectionModel().getSelectedIndex();

        // Verificar si hay algún trabajador seleccionado
        if (selectedIndex != -1) {
            // Obtener el trabajador seleccionado
            Worker selectedWorker = lvWorkersMg.getItems().get(selectedIndex);

            // Crear el diseño del formulario
            GridPane gridPane = new GridPane();
            gridPane.setPadding(new Insets(20));
            gridPane.setHgap(10);
            gridPane.setVgap(10);

            // Inicializar los campos del formulario con los datos del trabajador a actualizar
            Label idTrabajadorLabel = new Label(selectedWorker.getIdTrabajador());
            TextField dniField = new TextField(selectedWorker.getDni());
            TextField nombreField = new TextField(selectedWorker.getNombre());
            TextField apellidosField = new TextField(selectedWorker.getApellidos());
            ChoiceBox<String> especialidadChoiceBox = new ChoiceBox<>();
            especialidadChoiceBox.getItems().addAll("Limpieza", "Carpinteria", "Fontaneria", "Electricidad");
            especialidadChoiceBox.setValue(selectedWorker.getEspecialidad());
            PasswordField contrasenyaField = new PasswordField();
            TextField emailField = new TextField(selectedWorker.getEmail());

            // Agregar los campos al diseño
            gridPane.add(new Label("ID:"), 0, 0);
            gridPane.add(idTrabajadorLabel, 1, 0);
            gridPane.add(new Label("DNI:"), 0, 1);
            gridPane.add(dniField, 1, 1);
            gridPane.add(new Label("Nombre:"), 0, 2);
            gridPane.add(nombreField, 1, 2);
            gridPane.add(new Label("Apellidos:"), 0, 3);
            gridPane.add(apellidosField, 1, 3);
            gridPane.add(new Label("Especialidad:"), 0, 4);
            gridPane.add(especialidadChoiceBox, 1, 4);
            gridPane.add(new Label("Contraseña:"), 0, 5);
            gridPane.add(contrasenyaField, 1, 5);
            gridPane.add(new Label("Email:"), 0, 6);
            gridPane.add(emailField, 1, 6);

            // Crear el diálogo
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Actualizar Trabajador");
            dialog.getDialogPane().setContent(gridPane);

            // Agregar botones al diálogo
            ButtonType updateButtonType = new ButtonType("Actualizar", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            // Habilitar el botón de actualizar cuando el formulario es válido
            Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
            updateButton.setDisable(true);

            // Validar el formulario
            dniField.textProperty().addListener((observable, oldValue, newValue) -> validateUpdateFieldsWorker(updateButton, idTrabajadorLabel, dniField, nombreField, apellidosField, especialidadChoiceBox, contrasenyaField, emailField));
            nombreField.textProperty().addListener((observable, oldValue, newValue) -> validateUpdateFieldsWorker(updateButton, idTrabajadorLabel, dniField, nombreField, apellidosField, especialidadChoiceBox, contrasenyaField, emailField));
            apellidosField.textProperty().addListener((observable, oldValue, newValue) -> validateUpdateFieldsWorker(updateButton, idTrabajadorLabel, dniField, nombreField, apellidosField, especialidadChoiceBox, contrasenyaField, emailField));
            especialidadChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> validateUpdateFieldsWorker(updateButton, idTrabajadorLabel, dniField, nombreField, apellidosField, especialidadChoiceBox, contrasenyaField, emailField));
            contrasenyaField.textProperty().addListener((observable, oldValue, newValue) -> validateUpdateFieldsWorker(updateButton, idTrabajadorLabel, dniField, nombreField, apellidosField, especialidadChoiceBox, contrasenyaField, emailField));
            emailField.textProperty().addListener((observable, oldValue, newValue) -> validateUpdateFieldsWorker(updateButton, idTrabajadorLabel, dniField, nombreField, apellidosField, especialidadChoiceBox, contrasenyaField, emailField));

            // Mostrar el diálogo y esperar a que el usuario interactúe
            dialog.showAndWait().ifPresent(response -> {
                if (response.getButtonData() == ButtonType.OK.getButtonData()) {
                    // Obtener los valores del formulario y actualizar el trabajador
                    selectedWorker.setDni(dniField.getText());
                    selectedWorker.setNombre(nombreField.getText());
                    selectedWorker.setApellidos(apellidosField.getText());
                    selectedWorker.setEspecialidad(especialidadChoiceBox.getValue());
                    selectedWorker.setContraseña(contrasenyaField.getText());
                    selectedWorker.setEmail(emailField.getText());

                    // Llamar al servicio para actualizar el trabajador con los datos ingresados
                    UpdateWorkerService updateWorkerService = new UpdateWorkerService(selectedWorker);
                    updateWorkerService.setOnSucceeded(e -> {
                        GetWorkerResponse updateResponse = updateWorkerService.getValue();
                        if (updateResponse.getStatus() >= 200 && updateResponse.getStatus() < 300) {
                            MessageUtils.showMessage("Trabajador Actualizado", "Trabajador actualizado exitosamente");
                            // Aquí puedes actualizar la lista de trabajadores si es necesario
                            updateWorkerList();
                        } else {
                            MessageUtils.showError("Error actualizando trabajador", updateResponse.getMessage());
                        }
                    });
                    updateWorkerService.setOnFailed(e -> {
                        MessageUtils.showError("Error", "Error al conectar con el servidor");
                    });

                    updateWorkerService.start();
                }
            });
        }
    }

    private void validateUpdateFieldsWorker(Node updateButton, Label idTrabajadorLabel, TextField dniField, TextField nombreField, TextField apellidosField, ChoiceBox<String> especialidadChoiceBox, PasswordField contrasenyaField, TextField emailField) {
        boolean isDniValid = !dniField.getText().isEmpty();
        boolean isNombreValid = !nombreField.getText().isEmpty();
        boolean isApellidosValid = !apellidosField.getText().isEmpty();
        boolean isEspecialidadValid = especialidadChoiceBox.getValue() != null;
        boolean isContrasenyaValid = !contrasenyaField.getText().isEmpty();
        boolean isEmailValid = !emailField.getText().isEmpty();

        boolean isFormValid = isDniValid && isNombreValid && isApellidosValid && isEspecialidadValid && isContrasenyaValid && isEmailValid;

        // Habilitar el botón de actualizar si el formulario es válido y el ID del trabajador no está vacío
        updateButton.setDisable(!isFormValid || idTrabajadorLabel.getText().isEmpty());
    }



    private void deleteWorker() {
        int selectedIndex = lvWorkersMg.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Worker selectedWorker = lvWorkersMg.getItems().get(selectedIndex);
            String workerId = selectedWorker.getIdTrabajador();

            deleteWorkerService = new DeleteWorkerService(workerId);

            deleteWorkerService.setOnSucceeded(e -> {
                BaseResponse response = deleteWorkerService.getValue();
                if (response != null) {
                    if (response.getStatus() >= 200 && response.getStatus() < 300) {
                        MessageUtils.showMessage("Worker deleted", response.getMessage());
                        updateWorkerList();
                    } else {
                        MessageUtils.showError("Error deleting the worker", response.getMessage());
                    }
                } else {
                    MessageUtils.showError("Error", "No se puede eliminar un trabajador con trabajo asignado");
                }
            });

            deleteWorkerService.setOnFailed(e -> {
                MessageUtils.showError("Error", "Error connecting to server");
            });

            deleteWorkerService.start();
        }
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

    public static boolean equalsIgnoreCaseAndAccents(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return str1 == str2;
        }

        String normalizedStr1 = normalizeString(str1);
        String normalizedStr2 = normalizeString(str2);

        Collator collator = Collator.getInstance(Locale.getDefault());
        collator.setStrength(Collator.PRIMARY);

        return collator.equals(normalizedStr1, normalizedStr2);
    }

    private static String normalizeString(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
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