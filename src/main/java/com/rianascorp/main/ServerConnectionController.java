package com.rianascorp.main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

public class ServerConnectionController {

    @FXML
    private RadioButton serverLocationRadioButton1;
    @FXML private RadioButton serverLocationRadioButton2;
    @FXML private TextField ipAdressTextField;
    @FXML private FontIcon saveFontIcon;
    @FXML private ListView ipAdressListView;
    @FXML private FontIcon minusFontIcon;
    @FXML private Button CloseButton;
    @FXML private Button SaveButton;

    private ServerIpAddress ipAddress;                     // shared model
    private final ObservableList<String> ipList = FXCollections.observableArrayList();

    public void initializeo(ServerIpAddress ipAddress){
        this.ipAddress =ipAddress;
        ToggleGroup group = new ToggleGroup();
        serverLocationRadioButton1.setToggleGroup(group);
        serverLocationRadioButton2.setToggleGroup(group);
        serverLocationRadioButton2.setSelected(true);

        // Disable IP controls when "Local" is selected
        ipAdressTextField.disableProperty().bind(
                serverLocationRadioButton1.selectedProperty()
        );
        ipAdressListView.disableProperty().bind(
                serverLocationRadioButton1.selectedProperty()
        );

        // Clear text field when switching to Local
        serverLocationRadioButton1.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                ipAdressTextField.clear();
            }
        });
        // ---------- ListView ----------
        ipAdressListView.setItems(ipList);
        ipAdressListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Click on an item → put it in the text field + make it default
        ipAdressListView.getSelectionModel().selectedItemProperty().addListener((obs, old, ip) -> {
            if (ip != null) {
                ipAdressTextField.setText(ip.toString());
                ipAddress.setIp(ip.toString());               // remember as default
            }
        });

        // ---------- Load saved IPs ----------
        loadIpHistory();

        // If we have a previous default, select it
        if (ipAddress.getIp() != null && !ipAddress.getIp().isBlank()) {
            ipAdressListView.getSelectionModel().select(ipAddress.getIp());
        } else if (!ipList.isEmpty()) {
            ipAdressListView.getSelectionModel().selectFirst();
        }
    }

    private void loadIpHistory() {
        List<String> saved = IpFile.load();
        ipList.setAll(saved);
        ipAddress.getHistory().clear();
        ipAddress.getHistory().addAll(saved);
    }

    private void saveIpHistory(){
        IpFile.save(ipList);
        ipAddress.getHistory().clear();
        ipAddress.getHistory().addAll(ipList);
    }

    public void saveButtonAction() {
        String candidate = ipAdressTextField.getText().strip();
        if (candidate.isBlank()) {
            showAlert("IP vide", "Veuillez saisir une adresse IP valide.");
            return;
        }

        // Very light validation – you can replace with a regex if you want
        if (!candidate.matches("^\\d{1,3}(\\.\\d{1,3}){3}$")) {
            showAlert("Format invalide", "L'adresse IP doit être au format xxx.xxx.xxx.xxx");
            return;
        }

        if (!ipList.contains(candidate)) {
            ipList.add(candidate);
            ipAdressListView.getSelectionModel().select(candidate);
            saveIpHistory();
        }

        ipAddress.setIp(candidate);
    }

    public void removeButtonAction() {
        String selected = ipAdressListView.getSelectionModel().getSelectedItem().toString();
        if (selected == null) {
            showAlert("Aucun élément", "Sélectionnez une adresse à supprimer.");
            return;
        }

        ipList.remove(selected);
        saveIpHistory();

        // If the removed address was the default, pick another one
        if (selected.equals(ipAddress.getIp())) {
            ipAddress.setIp(ipList.isEmpty() ? null : ipList.get(0));
            ipAdressTextField.setText(ipAddress.getIp() != null ? ipAddress.getIp() : "");
        }
    }

    public void SaveButtonAction() {
        String chosen = ipAdressTextField.getText().strip();
        if (serverLocationRadioButton2.isSelected() && (chosen.isBlank() || !chosen.matches("^\\d{1,3}(\\.\\d{1,3}){3}$"))) {
            showAlert("IP manquante", "Veuillez saisir ou sélectionner une adresse IP valide.");
            return;
        }

        ipAddress.setIp(serverLocationRadioButton2.isSelected() ? chosen : null);
        CloseButtonAction();
    }

    public void CloseButtonAction() {
        Stage stage=(Stage)CloseButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String header, String content) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();
    }
}
