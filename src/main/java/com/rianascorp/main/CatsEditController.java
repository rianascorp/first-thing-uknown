package com.rianascorp.main;

import com.rianascorp.objects.Cats;
import com.rianascorp.objects.Furr;
import com.rianascorp.objects.Gender;
import com.rianascorp.objects.Race;
import com.rianascorp.utils.ImageUtils;
import com.rianascorp.utils.Shower;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;
import rianaLibraries.controller.Controllers;
import rianaLibraries.controller.RianaValidation;
import rianaLibraries.controls.Controls;
import rianaLibraries.controls.RianaLongGetCode;
import rianaLibraries.model.Models;
import rianaLibraries.validation.Language;
import rianaLibraries.validation.RianaValidationRegex;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.lang.Boolean.FALSE;

public class CatsEditController implements RianaValidation {
    @FXML
    private TextField nameTextField;
    @FXML
    private ComboBox<Race> raceComboBox;
    @FXML
    private ComboBox<Furr> furrCombo;
    @FXML
    private ComboBox<Gender> genderCombo;
    @FXML
    private DatePicker dobDatePicker;
    @FXML
    private Hyperlink choosePhotoLink;
    @FXML
    private Label nameErrorLabel;
    @FXML
    private Label raceErrorLabel;
    @FXML
    private Label genderErrorLabel;
    @FXML
    private Label dobErrorLabel;
    @FXML
    private Label furrErrorLabel;
    @FXML
    private ImageView cat_image;
    @FXML
    private Button SaveButton;
    @FXML
    private Button CloseButton;


    public Models<Cats> catsModels;
    public Models<Race> raceModels;
    public Models<Furr> furrModels;
    public Models<Gender> genderModels;
    public List<Race> raceList;
    public List<Furr> furrList;
    public List<Gender> genderList;

    Cats currentCat;
    Controllers controllers;
    EntityManagerFactory emf;
    Shower shower;
    Controls controls;
    RianaValidationRegex rianaValidationRegex;
    Validator mainValidator;
    File selectedPhotoFile;
    byte[] selectedPhotoBytes;
    byte[] selectedThumbnailBytes;
    private boolean photoChanged = false;

    public void initialize(Models<Cats> catsModels) throws NoSuchMethodException {
        this.catsModels = catsModels;
        currentCat = this.catsModels.getCurrent();
        emf = LoginController.ENTITYMANAGERFACTORY;
        rianaValidationRegex = new RianaValidationRegex();
        controls = new Controls();
        shower = new Shower();
        mainValidator = new Validator();
        raceModels = raceList != null ? new Models<>(FXCollections.observableArrayList(raceList)) : new Models<>(FXCollections.observableArrayList());
        furrModels = furrList != null ? new Models<>(FXCollections.observableArrayList(furrList)) : new Models<>(FXCollections.observableArrayList());
        genderModels = genderList != null ? new Models<>(FXCollections.observableArrayList(genderList)) : new Models<>(FXCollections.observableArrayList());
        fetchData();
        initializeComboBoxes();
        if (currentCat != null) {
            setCurrentCatForm();
        }
    }

    public void fetchData() {
        shower.withEntityManager(em -> {
            raceList = ((EntityManager) em).createQuery("select r from Race r ", Race.class).getResultList();
            furrList = ((EntityManager) em).createQuery("select f from Furr f ", Furr.class).getResultList();
            genderList = ((EntityManager) em).createQuery("select g from Gender g ", Gender.class).getResultList();
            return null;
        });
        if (raceModels != null) {
            raceModels.justLoadAll(raceList);
        }
        if (furrModels != null) {
            furrModels.justLoadAll(furrList);
        }
        if (genderModels != null) {
            genderModels.justLoadAll(genderList);
        }

    }

    private void initializeComboBoxes() {
        controls.initializeCombo(raceComboBox, raceModels, race -> race.getName());
        controls.initializeCombo(furrCombo, furrModels, furr -> furr.getName());
        controls.initializeCombo(genderCombo, genderModels, gender -> gender.getName());
    }

    private void setCurrentCatForm() throws NoSuchMethodException {
        nameTextField.setText(currentCat.getName());
        controls.initializeCombo(furrCombo, furrModels, furr -> furr.getName(), furr -> furr.getId(), currentCat.getFurr().getId());
        controls.initializeCombo(raceComboBox, raceModels, race -> race.getName(), race -> race.getId(), currentCat.getRace().getId());
        controls.initializeCombo(genderCombo, genderModels, gender -> gender.getName(), gender -> gender.getId(), currentCat.getGender().getId());
        dobDatePicker.setValue(currentCat.getDob());
        if (currentCat.getPhoto() != null) {
            Image image = ImageUtils.bytesToImage(currentCat.getPhoto());
            cat_image.setImage(image);
        }
    }

    public void choosePhotoAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(choosePhotoLink.getScene().getWindow());
        if (selectedFile != null) try{
            //Resize image before displaying and saving
            selectedPhotoBytes=ImageUtils.fileToBytes(selectedFile,true);
            //Display resized image
            Image image = ImageUtils.bytesToImage(selectedPhotoBytes);
            cat_image.setImage(image);
            //Create thumbnail
            selectedThumbnailBytes = ImageUtils.createThumbnail(selectedPhotoBytes);
            //Store resized bytes for saving
            this.selectedPhotoFile = selectedFile;
            this.photoChanged=true;
        } catch (IOException e) {
            e.printStackTrace();
            shower.errorShower(e);
        }
    }

    public void saveButtonAction() throws IOException {
        EntityManager em = emf.createEntityManager();
        mainValidator = new Validator();
        validation(mainValidator);
        if (mainValidator.validate()) {
            try {
                em.getTransaction().begin();
                if (currentCat == null) {
                    Cats newCat = getCatFields(new Cats());
                    em.persist(newCat);
                    catsModels.getMList().add(newCat);
                } else {
                    Cats managedCat = em.find(Cats.class, currentCat.getId());
                    getCatFields(managedCat);
                    Cats modifiedCat = getCatFields(catsModels.getMList().stream().filter(x -> x.getId().equals(currentCat.getId())).findFirst().orElse(null));
                    if (modifiedCat != null) {
                        int index = catsModels.getMList().indexOf(modifiedCat);
                        getCatFields(modifiedCat);
                        catsModels.getMList().set(index, modifiedCat);
                    }
                }
                selectedPhotoFile = null;
                em.getTransaction().commit();
                em.close();
                CloseButtonAction();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur lors de l'enregistrement");
                alert.setContentText(ex.getMessage());
                alert.show();
                ex.printStackTrace();
            }
        }}

    public Cats getCatFields(Cats cat) throws IOException {
        cat.setName(nameTextField.getText());
        cat.setDob(dobDatePicker.getValue());
        cat.setFurr(furrCombo.getValue());
        cat.setGender(genderCombo.getValue());
        cat.setRace(raceComboBox.getValue());

        //Only if user selected a new photo
        if (photoChanged && selectedPhotoBytes != null) {
            cat.setPhoto(selectedPhotoBytes);
            cat.setThumbnail(selectedThumbnailBytes);
            photoChanged = false;
        }

        return cat;
    }

        public void validation (net.synedra.validatorfx.Validator validator){
            SaveButton.disableProperty().bind(validator.containsErrorsProperty());
            rianaValidationRegex.createCheckBlank("name", nameTextField, nameErrorLabel, Language.ENGLISH, validator);
        }

        public void CloseButtonAction () {
            Stage stage = (Stage) CloseButton.getScene().getWindow();
            stage.close();
        }

    }
