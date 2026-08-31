package com.rianascorp.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    public void start(Stage stage) throws Exception {
        FXMLLoader guideLoader=new FXMLLoader(getClass().getResource("/main/cat_login.fxml"));
        Parent root=guideLoader.load();
        LoginController loginController=guideLoader.getController();
        loginController.initializeo();
        Scene scene = new Scene(root);
        //scene.getStylesheets().add(getClass().getResource("/main/modena.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Cats");

        stage.getIcons().addAll(
                new Image(getClass().getResourceAsStream("/icons/icon_16.png")),
                new Image(getClass().getResourceAsStream("/icons/icon_32.png")),
                new Image(getClass().getResourceAsStream("/icons/icon_64.png")),
                new Image(getClass().getResourceAsStream("/icons/icon_128.png")),
                new Image(getClass().getResourceAsStream("/icons/icon_256.png")),
                new Image(getClass().getResourceAsStream("/icons/icon_512.png"))
        );

        stage.show();
        stage.centerOnScreen();
    }
}
