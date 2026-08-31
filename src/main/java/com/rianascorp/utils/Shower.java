package com.rianascorp.utils;


import com.rianascorp.main.LoginController;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import org.hibernate.engine.jdbc.BlobProxy;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.SQLException;

import java.io.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class Shower<C> {

    private TableView<C> table;
    private C c;
    String resource;
    StackPane root;

    EntityManagerFactory emf;



   public void AddButtonsLayout(Button EditBtn,Button DeleteBtn){
       EditBtn.getStyleClass().setAll("btn-sm","btn-warning");
       EditBtn.setCursor(Cursor.HAND);
       DeleteBtn.setCursor(Cursor.HAND);
       DeleteBtn.getStyleClass().setAll("btn-sm","btn-danger");
       FontIcon deleteIcon=new FontIcon(FontAwesomeSolid.TRASH);
       FontIcon editIcon=new FontIcon(FontAwesomeSolid.EDIT);
       DeleteBtn.setGraphic(deleteIcon);
       EditBtn.setGraphic(editIcon);
   }

    public FXMLLoader showEdit(String path) throws IOException {
        FXMLLoader editorLoader=new FXMLLoader(getClass().getResource(path));
        Stage stage=new Stage();
        Parent root=editorLoader.load();
        Scene scene=new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/main/rianastyle.css").toExternalForm());
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        stage.centerOnScreen();
        return editorLoader;
    }

    public FXMLLoader showEdit(String path,String stageTitle) throws IOException {
        FXMLLoader editorLoader=new FXMLLoader(getClass().getResource(path));
        Stage stage=new Stage();
        Parent root=editorLoader.load();
        Scene scene=new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/main/rianastyle.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle(stageTitle);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        stage.centerOnScreen();
        return editorLoader;
    }

    public FXMLLoader showEditNoStyle(String path,String stageTitle) throws IOException {
        FXMLLoader editorLoader=new FXMLLoader(getClass().getResource(path));
        Stage stage=new Stage();
        Parent root=editorLoader.load();
        Scene scene=new Scene(root);
        stage.setScene(scene);
        stage.setTitle(stageTitle);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        stage.centerOnScreen();
        return editorLoader;
    }

    public FXMLLoader showEdit(String path,String stageTitle,boolean maximize) throws IOException {
        FXMLLoader editorLoader=new FXMLLoader(getClass().getResource(path));
        Stage stage=new Stage();
        Parent root=editorLoader.load();
        Scene scene=new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/main/rianastyle.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle(stageTitle);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        stage.centerOnScreen();
        if (maximize){
            //stage.setFullScreen(true);
            stage.setMaximized(true);
        }
        return editorLoader;
    }

    public FXMLLoader showEditNone(String path,String stageTitle,boolean maximize) throws IOException {
        FXMLLoader editorLoader=new FXMLLoader(getClass().getResource(path));
        Stage stage=new Stage();
        Parent root=editorLoader.load();
        Scene scene=new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/main/rianastyle.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle(stageTitle);
        stage.show();
        stage.centerOnScreen();
        if (maximize){
            //stage.setFullScreen(true);
            stage.setMaximized(true);
        }
        return editorLoader;
    }


    public void selectError(String thing){
        Alert alert=new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hadisoana");
        alert.setHeaderText("Safidy");
        alert.setContentText("Azafady, misafidiana "+thing+ " iray");
        alert.show();
    }

    public void errorShower(Exception ex){
        Alert alert=new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hadisoana");
        alert.setHeaderText("Misy Diso ao ô!");
        alert.setContentText(ex.getMessage());
        alert.show();
    }

    public void errorShower(String message){
        Alert alert=new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hadisoana");
        alert.setHeaderText("Misy diso ao ô");
        alert.setContentText(message);
        alert.show();
    }


    public <T> T withEntityManager(Function<EntityManager, T> work) {
        EntityManager em = LoginController.ENTITYMANAGERFACTORY.createEntityManager();
        try {
            return work.apply(em);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public void withEntityManagerDo(Consumer<EntityManager> work) {
        withEntityManager(em -> {
            work.accept(em);
            return null;
        });
    }
}
