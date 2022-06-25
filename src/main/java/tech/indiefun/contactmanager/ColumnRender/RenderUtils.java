package tech.indiefun.contactmanager.ColumnRender;

import ezvcard.VCard;
import ezvcard.property.VCardProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.List;

public class RenderUtils {

    public interface PropertiesExtractor<T extends VCardProperty> {
        List<T> extract(VCard card);
    }

    public interface PropertyValueGetter<T extends VCardProperty, P> {
        P get(T property);
    }

    public interface PropertyValueSetter<T extends VCardProperty, P> {
        void set(T property, P value);
    }

    public interface PropertyCreator<T extends VCardProperty, P> {
        T create(P value);
    }

    public static <T extends VCardProperty, P> TableColumn<VCard, P> column(
            String title,
            int index,
            PropertiesExtractor<T> propertiesExtractor,
            PropertyValueGetter<T, P> propertyValueGetter,
            PropertyValueSetter<T, P> propertyValueSetter,
            PropertyCreator<T, P> propertyCreator,
            Callback<TableColumn<VCard, P>, TableCell<VCard, P>> tableCellFactory
    ) {
        TableColumn<VCard, P> column = new TableColumn<>(title);
        column.setCellValueFactory(features -> {
            List<T> properties = propertiesExtractor.extract(features.getValue());
            SimpleObjectProperty<P> objectProperty = new SimpleObjectProperty<>();
            if (index < properties.size()) {
                P value = propertyValueGetter.get(properties.get(index));
                objectProperty.setValue(value);
            }
            return objectProperty;
        });
        column.setCellFactory(tableCellFactory);
        column.setOnEditCommit(event -> {
            P value = event.getNewValue();
            List<T> properties = propertiesExtractor.extract(event.getRowValue());
            if (index < properties.size()) {
                propertyValueSetter.set(properties.get(index), value);
            } else {
                properties.add(propertyCreator.create(value));
            }
        });
        return column;
    }

    public static <T extends VCardProperty, P> TableColumn<VCard, P> column(
            String title,
            int index,
            PropertiesExtractor<T> propertiesExtractor,
            PropertyValueGetter<T, P> propertyValueGetter,
            PropertyValueSetter<T, P> propertyValueSetter,
            PropertyCreator<T, P> propertyCreator,
            StringConverter<P> stringConverter
    ) {
        return column(
                title,
                index,
                propertiesExtractor,
                propertyValueGetter,
                propertyValueSetter,
                propertyCreator,
                TextFieldTableCell.forTableColumn(stringConverter)
        );
    }

    public static <T extends VCardProperty> TableColumn<VCard, String> column(
            String title,
            int index,
            PropertiesExtractor<T> propertiesExtractor,
            PropertyValueGetter<T, String> propertyValueGetter,
            PropertyValueSetter<T, String> propertyValueSetter,
            PropertyCreator<T, String> propertyCreator
    ) {
        return column(
                title,
                index,
                propertiesExtractor,
                propertyValueGetter,
                propertyValueSetter,
                propertyCreator,
                TextFieldTableCell.forTableColumn()
        );
    }
}
