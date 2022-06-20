package tech.indiefun.contactmanager.ColumnRender;

import ezvcard.VCard;
import ezvcard.property.VCardProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class RenderUtils {

    public interface PropertiesExtractor<T extends VCardProperty> {
        List<T> extract(VCard card);
    }

    public interface PropertyValueGetter<T extends VCardProperty> {
        String get(T property);
    }

    public interface PropertyValueSetter<T extends VCardProperty> {
        void set(T property, String value);
    }

    public interface PropertyCreator<T extends VCardProperty> {
        T create(String value);
    }

    public static <T extends VCardProperty> TableColumn<VCard, String> column(
            String title,
            int index,
            PropertiesExtractor<T> propertiesExtractor,
            PropertyValueGetter<T> propertyValueGetter,
            PropertyValueSetter<T> propertyValueSetter,
            PropertyCreator<T> propertyCreator,
            StringConverter<String> stringConverter
    ) {
        TableColumn<VCard, String> column = new TableColumn<>(title);
        column.setCellValueFactory(features -> {
            List<T> properties = propertiesExtractor.extract(features.getValue());
            SimpleStringProperty stringProperty = new SimpleStringProperty();
            if (index < properties.size()) {
                String value = propertyValueGetter.get(properties.get(index));
                stringProperty.setValue(StringUtils.defaultIfEmpty(value, null));
            }
            return stringProperty;
        });
        column.setCellFactory(TextFieldTableCell.forTableColumn(stringConverter));
        column.setOnEditCommit(event -> {
            String value = event.getNewValue();
            List<T> properties = propertiesExtractor.extract(event.getRowValue());
            if (index < properties.size()) {
                propertyValueSetter.set(properties.get(index), value);
            } else {
                properties.add(propertyCreator.create(value));
            }
        });
        return column;
    }
}
