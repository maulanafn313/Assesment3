package id.ac.telkomuniversity.projek.assesment3;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

        // Tabel tugas
        TableColumn<Task, String> nameColumn = new TableColumn<>("Nama Tugas");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Task, String> descriptionColumn = new TableColumn<>("Deskripsi");
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        TableColumn<Task, String> dueDateColumn = new TableColumn<>("Tanggal Jatuh Tempo");
        dueDateColumn.setCellValueFactory(cellData -> cellData.getValue().dueDateProperty());

        TableColumn<Task, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        table.getColumns().addAll(nameColumn, descriptionColumn, dueDateColumn, statusColumn);
        table.setItems(taskList);

        // Tombol aksi
        Button addButton = new Button("Tambah Tugas");
        addButton.setOnAction(e -> showTaskForm(null));

        Button updateButton = new Button("Edit Tugas");
        updateButton.setOnAction(e -> {
            Task selectedTask = table.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                showTaskForm(selectedTask);
            }
        });

        Button deleteButton = new Button("Hapus Tugas");
        deleteButton.setOnAction(e -> {
            Task selectedTask = table.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setTitle("Konfirmasi Penghapusan");
                confirmationAlert.setHeaderText("Hapus Tugas");
                confirmationAlert.setContentText("Apakah Anda yakin ingin menghapus tugas ini?");

                confirmationAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        taskController.deleteTask(selectedTask.getId());
                        taskList.setAll(taskController.getTasks());
                    }
                });
            }
        });

        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton);
        buttonBox.setPadding(new Insets(10));

        VBox vbox = new VBox(10, table, buttonBox);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        taskList.setAll(taskController.getTasks());
    }

    private void showTaskForm(Task task) {
        Stage formStage = new Stage();
        formStage.setTitle(task == null ? "Tambah Tugas" : "Edit Tugas");

        TextField nameField = new TextField();
        nameField.setPromptText("Nama Tugas");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Deskripsi");

        DatePicker dueDateField = new DatePicker();

        ComboBox<String> statusField = new ComboBox<>();
        statusField.getItems().addAll("done", "not done");

        if (task != null) {
            nameField.setText(task.getName());
            descriptionField.setText(task.getDescription());
            dueDateField.setValue(java.time.LocalDate.parse(task.getDueDate()));
            statusField.setValue(task.getStatus());
        }

        Button saveButton = new Button("Simpan");
        saveButton.setOnAction(e -> {
            if (nameField.getText().isEmpty() || descriptionField.getText().isEmpty() || dueDateField.getValue() == null || statusField.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Semua field harus diisi!");
                alert.show();
                return;
            }

            if (task == null) {
                Task newTask = new Task(0, nameField.getText(), descriptionField.getText(), dueDateField.getValue().toString(), statusField.getValue());
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
        });

        VBox formLayout = new VBox(10, nameField, descriptionField, dueDateField, statusField, saveButton);
        formLayout.setPadding(new Insets(10));

        Scene formScene = new Scene(formLayout, 400, 300);
        formStage.setScene(formScene);
        formStage.show();
    }
}
