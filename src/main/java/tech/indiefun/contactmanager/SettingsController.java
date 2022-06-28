package tech.indiefun.contactmanager;

import com.google.i18n.phonenumbers.ShortNumbersRegionCodeSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.time.ZoneId;

public class SettingsController {

    @FXML
    private ChoiceBox<String> regionCodeChoiceBox;

    @FXML
    private ChoiceBox<String> timeZoneIDChoiceBox;

    private GlobalSettings.Configurations configurations;

    @FXML
    protected void initialize() {
        configurations = new GlobalSettings.Configurations(GlobalSettings.getInstance().getConfigurations());
        for (String region : ShortNumbersRegionCodeSet.getRegionCodeSet()) {
            regionCodeChoiceBox.getItems().add(region);
        }
        regionCodeChoiceBox.getSelectionModel().select(configurations.getDefaultRegionCode());
        regionCodeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtils.isNotEmpty(newValue)) {
                configurations.setDefaultRegionCode(newValue);
            }
        });
        for (String zoneId : ZoneId.getAvailableZoneIds()) {
            timeZoneIDChoiceBox.getItems().add(zoneId);
        }
        timeZoneIDChoiceBox.getSelectionModel().select(configurations.getDefaultTimeZoneID());
        regionCodeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtils.isNotEmpty(newValue)) {
                configurations.setDefaultTimeZoneID(newValue);
            }
        });
    }

    @FXML
    protected void onButtonSaveClicked(ActionEvent actionEvent) {
        try {
            GlobalSettings.getInstance().setConfigurations(configurations);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errors");
            alert.setContentText("Could not save settings");
            alert.showAndWait();
        }
        final Node source = (Node) actionEvent.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void onButtonCancelClicked(ActionEvent actionEvent) {
        final Node source = (Node) actionEvent.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
