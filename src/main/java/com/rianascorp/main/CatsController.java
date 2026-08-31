package com.rianascorp.main;

import com.rianascorp.objects.Cats;
import com.rianascorp.utils.ImageUtils;
import com.rianascorp.utils.Shower;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import rianaLibraries.controller.Controllers;
import rianaLibraries.controller.RianaDelete;
import rianaLibraries.controller.RianaEdit;
import rianaLibraries.controller.RianaRefresh;
import rianaLibraries.controls.Controls;
import rianaLibraries.model.Models;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CatsController implements RianaEdit,RianaDelete,RianaRefresh {

    @FXML private TableView<Cats> Table;
    @FXML private TableColumn<Cats,String> nameTableColumn;
    @FXML private TableColumn<Cats,String> raceTableColumn;
    @FXML private TableColumn<Cats,String> genderTableColumn;
    @FXML private TableColumn<Cats,String> ageTableColumn;
    @FXML private TableColumn<Cats,String> furrTableColumn;
    @FXML private TableColumn<Cats,Void> photoTableColumn;
    @FXML private Button CloseButton;
    @FXML private Button AddButton;
    @FXML private FontIcon catFontIcon;

    Models<Cats> catsModels;
    List<Cats> catsList;

    Cats currentCat;
    EntityManagerFactory emf;
    Shower shower;
    Controllers controllers;
    Controls controls;

    public void initializeo() throws IOException {

        emf = LoginController.ENTITYMANAGERFACTORY;
        shower=new Shower();
        controllers=new Controllers();
        catFontIcon.setIconCode(FontAwesomeSolid.CAT);

        fetchData();

        catsModels = catsList != null ? new Models<>(FXCollections.observableArrayList(catsList)) : new Models<>(FXCollections.observableArrayList());
        nameTableColumn.setCellValueFactory(param->param.getValue().getName()!=null?new SimpleStringProperty(param.getValue().getName()):new SimpleStringProperty(""));
        raceTableColumn.setCellValueFactory(param -> param.getValue().getRace()!=null?new SimpleStringProperty(param.getValue().getRace().getName()):new SimpleStringProperty(""));
        genderTableColumn.setCellValueFactory(param -> param.getValue().getGender()!=null?new SimpleStringProperty(param.getValue().getGender().getName()):new SimpleStringProperty(""));
        furrTableColumn.setCellValueFactory(param -> param.getValue().getFurr()!=null?new SimpleStringProperty(param.getValue().getFurr().getName()):new SimpleStringProperty(""));
        ageTableColumn.setCellValueFactory(param -> {
            Cats cat = param.getValue();
            if (cat != null && cat.getDob() != null) {
                int age = java.time.Period.between(cat.getDob(), java.time.LocalDate.now()).getYears();
                return new javafx.beans.property.SimpleStringProperty(String.valueOf(age));
            } else {
                return new javafx.beans.property.SimpleStringProperty("");
            }
        });


                setupPhotoColumn();

        controllers.initModel(catsModels,Table,this::Edit,this::Delete,this::Refresh);

    }

    private void setupPhotoColumn() {
        // Create a new column for the photo
        photoTableColumn.setPrefWidth(70);
        photoTableColumn.setSortable(false);

        photoTableColumn.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Cats cat = getTableView().getItems().get(getIndex());
                if (cat != null) {
                    // ✅ Use thumbnail if available, fallback to full photo
                    byte[] imageBytes = cat.getThumbnail() != null ?
                            cat.getThumbnail() : cat.getPhoto();

                    if (imageBytes != null && imageBytes.length > 0) {
                        Image image = ImageUtils.bytesToImage(imageBytes);
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } else {
                        // No photo - show placeholder or empty
                        imageView.setImage(null);
                        setGraphic(null);
                    }
                } else {
                    setGraphic(null);
                }
            }
        });
    }


    private void fetchData() {
            Table.setDisable(true);  // Disable while loading
            try {
                EntityManager em = emf.createEntityManager();
                catsList = em.createQuery("select c from Cats c", Cats.class).getResultList();
                if (catsModels != null) {
                    catsModels.justLoadAll(catsList);
                }
            } finally {
                Table.setDisable(false);  // Re-enable
            }
        }



    public void AddButtonAction() throws IOException, NoSuchMethodException {
        catsModels.setCurrent(null);
        FXMLLoader catsLoader=shower.showEdit("/main/new_cat.fxml");
        CatsEditController catsEditController=catsLoader.getController();
        catsEditController.initialize(catsModels);
    }

    public void CloseButtonAction() {
        Stage stage=(Stage)CloseButton.getScene().getWindow();
        stage.close();
    }


    @Override
    public <M> void Delete(M m) throws IOException {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete this cat?");
        confirm.setContentText("This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK && m instanceof Cats) {
                Cats cat = (Cats) m;
                try (EntityManager em = emf.createEntityManager()) {
                    em.getTransaction().begin();
                    Cats managed = em.find(Cats.class, cat.getId());
                    if (managed != null) {
                        em.remove(managed);
                        em.getTransaction().commit();
                        catsModels.getMList().removeIf(cats -> cat.getId().equals(managed.getId()));
                    }
                }
            }
        });
    }

    @Override
    public void Edit() throws IOException, NoSuchMethodException {
        FXMLLoader catsLoader=shower.showEdit("/main/new_cat.fxml");
        CatsEditController catsEditController=catsLoader.getController();
        catsEditController.initialize(catsModels);
    }

    @Override
    public void Refresh() throws IOException {

    }

}
