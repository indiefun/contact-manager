package tech.indiefun.contactmanager;

import com.google.i18n.phonenumbers.NumberParseException;
import javafx.scene.control.Alert;
import javafx.util.StringConverter;

public class TelephoneStringConverter extends StringConverter<String> {

    @Override
    public String toString(String s) {
        try {
            return TelephoneStringFormatter.format(s);
        } catch (NumberParseException e) {
            return null;
        }
    }

    @Override
    public String fromString(String s) {
        try {
            return TelephoneStringFormatter.format(s);
        } catch (NumberParseException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errors");
            alert.setContentText("Telephone " + s + " could not be formatted.");
            alert.showAndWait();
            return null;
        }
    }
}
