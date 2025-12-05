module com.example.courseprifs {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires org.hibernate.orm.core;
    requires java.sql;
    requires java.naming;
    requires mysql.connector.j;
    requires jakarta.persistence;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;

    opens com.example.woltbetblogiau to javafx.fxml, org.hibernate.orm.core, jakarta.persistence;
    exports com.example.woltbetblogiau;
    opens com.example.woltbetblogiau.fxControllers to javafx.fxml;
    exports com.example.woltbetblogiau.fxControllers;
    opens com.example.woltbetblogiau.model to org.hibernate.orm.core;
    exports com.example.woltbetblogiau.model;
}