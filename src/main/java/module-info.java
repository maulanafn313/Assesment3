module id.ac.telkomuniversity.projek.assesment3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens id.ac.telkomuniversity.projek.assesment3 to javafx.fxml;
    exports id.ac.telkomuniversity.projek.assesment3;
}