module com.simplerun {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires validatorfx;
    requires org.rianalibraries;
    requires java.naming;
    requires java.desktop;
    requires org.kordamp.bootstrapfx.core;

    opens com.rianascorp.main to javafx.fxml;
    opens com.rianascorp.utils to javafx.fxml;
    opens com.rianascorp.objects to  org.hibernate.orm.core;
    exports com.rianascorp.main;
    exports com.rianascorp.utils;


}
