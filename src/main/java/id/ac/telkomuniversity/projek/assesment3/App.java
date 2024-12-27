package id.ac.telkomuniversity.projek.assesment3;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class App extends Application {
    private TaskController taskController = new TaskController();
    private TableView<Task> table = new TableView<>();
    private ObservableList<Task> taskList = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Task Manager");


        Label titleLabel = new Label("Task Manager");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2196F3"));
        titleLabel.setPadding(new Insets(10, 0, 20, 0));

        // Styling table
        table.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5px;");

        TableColumn<Task, String> nameColumn = createColumn("Nama Tugas", 0.25);
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Task, String> descriptionColumn = createColumn("Deskripsi", 0.35);
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        TableColumn<Task, String> dueDateColumn = createColumn("Tanggal Jatuh Tempo", 0.2);
        dueDateColumn.setCellValueFactory(cellData -> cellData.getValue().dueDateProperty());

        TableColumn<Task, String> statusColumn = createColumn("Status", 0.2);
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        table.getColumns().addAll(nameColumn, descriptionColumn, dueDateColumn, statusColumn);
        table.setItems(taskList);


        table.setRowFactory(tv -> new TableRow<Task>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (task == null || empty) {
                    setStyle("");
                } else if ("done".equals(task.getStatus())) {
                    setStyle("-fx-background-color: #90EE90;"); // Light green color
                } else {
                    setStyle("");
                }
            }
        });


        Button addButton = createStyledButton("Tambah Tugas", "#4CAF50");
        addButton.setOnAction(e -> showTaskForm(null));

        Button updateButton = createStyledButton("Edit Tugas", "#2196F3");
        updateButton.setOnAction(e -> {
            Task selectedTask = table.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                showTaskForm(selectedTask);
            }
        });

        Button deleteButton = createStyledButton("Hapus Tugas", "#F44336");
        deleteButton.setOnAction(e -> handleDeleteAction());

        HBox buttonBox = new HBox(15, addButton, updateButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15));
        buttonBox.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #e0e0e0; -fx-border-radius: 5px;");

        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: white;");
        mainLayout.getChildren().addAll(titleLabel, table, buttonBox);

        Scene scene = new Scene(mainLayout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        taskList.setAll(taskController.getTasks());
    }

    private TableColumn<Task, String> createColumn(String title, double widthPercent) {
        TableColumn<Task, String> column = new TableColumn<>(title);
        column.prefWidthProperty().bind(table.widthProperty().multiply(widthPercent));
        column.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-size: 14px;");
        return column;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-padding: 8 15; " +
                        "-fx-border-radius: 5; -fx-background-radius: 5;", color));

        button.setOnMouseEntered(e -> button.setStyle(String.format(
                "-fx-background-color: derive(%s, 20%%); -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-padding: 8 15; " +
                        "-fx-border-radius: 5; -fx-background-radius: 5;", color)));

        button.setOnMouseExited(e -> button.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-padding: 8 15; " +
                        "-fx-border-radius: 5; -fx-background-radius: 5;", color)));

        return button;
    }

    private void showTaskForm(Task task) {
        Stage formStage = new Stage();
        formStage.setTitle(task == null ? "Tambah Tugas Baru" : "Edit Tugas");


        Label formTitle = new Label(task == null ? "Tambah Tugas Baru" : "Edit Tugas");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        formTitle.setTextFill(Color.web("#2196F3"));
        formTitle.setPadding(new Insets(0, 0, 20, 0));

        TextField nameField = createStyledTextField("Nama Tugas");
        TextField descriptionField = createStyledTextField("Deskripsi");
        DatePicker dueDateField = createStyledDatePicker();
        ComboBox<String> statusField = createStyledComboBox();

        if (task != null) {
            nameField.setText(task.getName());
            descriptionField.setText(task.getDescription());
            dueDateField.setValue(java.time.LocalDate.parse(task.getDueDate()));
            statusField.setValue(task.getStatus());
        }

        Button saveButton = createStyledButton("Simpan", "#4CAF50");
        saveButton.setPrefWidth(200);
        saveButton.setOnAction(e -> handleSaveAction(task, nameField, descriptionField, dueDateField, statusField, formStage));

        VBox formLayout = new VBox(15);
        formLayout.setPadding(new Insets(20));
        formLayout.setStyle("-fx-background-color: white;");
        formLayout.getChildren().addAll(
                formTitle,
                createFormField("Nama Tugas:", nameField),
                createFormField("Deskripsi:", descriptionField),
                createFormField("Tanggal Jatuh Tempo:", dueDateField),
                createFormField("Status:", statusField),
                new VBox(20, saveButton)
        );

        ((VBox)formLayout.getChildren().get(5)).setAlignment(Pos.CENTER);

        Scene formScene = new Scene(formLayout, 450, 500);
        formStage.setScene(formScene);
        formStage.show();
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(
                "-fx-font-size: 14px; -fx-padding: 8; " +
                        "-fx-border-color: #e0e0e0; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );
        return field;
    }

    private DatePicker createStyledDatePicker() {
        DatePicker datePicker = new DatePicker();
        datePicker.setStyle(
                "-fx-font-size: 14px; -fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 5; -fx-background-radius: 5;"
        );
        return datePicker;
    }

    private ComboBox<String> createStyledComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("done", "not done");
        comboBox.setStyle(
                "-fx-font-size: 14px; -fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 5; -fx-background-radius: 5;"
        );
        return comboBox;
    }

    private VBox createFormField(String labelText, Control field) {
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        label.setTextFill(Color.web("#424242"));

        VBox container = new VBox(5);
        container.getChildren().addAll(label, field);
        return container;
    }

    private void handleSaveAction(Task task, TextField nameField, TextField descriptionField,
                                  DatePicker dueDateField, ComboBox<String> statusField, Stage formStage) {
        if (nameField.getText().isEmpty() || descriptionField.getText().isEmpty() ||
                dueDateField.getValue() == null || statusField.getValue() == null) {
            showAlert("Peringatan", "Semua field harus diisi!");
            return;
        }

        if (task == null) {
            Task newTask = new Task(0, nameField.getText(), descriptionField.getText(),
                    dueDateField.getValue().toString(), statusField.getValue());
            taskController.addTask(newTask);
        } else {
            task.setName(nameField.getText());
            task.setDescription(descriptionField.getText());
            task.setDueDate(dueDateField.getValue().toString());
            task.setStatus(statusField.getValue());
            taskController.updateTask(task);
        }

        taskList.setAll(taskController.getTasks());
        formStage.close();
    }

    private void handleDeleteAction() {
        Task selectedTask = table.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Konfirmasi Penghapusan");
            alert.setHeaderText("Hapus Tugas");
            alert.setContentText("Apakah Anda yakin ingin menghapus tugas ini?");
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: white;");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    taskController.deleteTask(selectedTask.getId());
                    taskList.setAll(taskController.getTasks());
                }
            });
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;");
        alert.show();
    }
}