//module darbuAprite {
//    requires javafx.fxml;
//    requires javafx.controls;
//    requires java.sql;
//
//    opens paka;
//}

module darbuAprite {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.base;
    requires javafx.web;
    requires javafx.swing;

    requires java.sql;

    opens paka;
}