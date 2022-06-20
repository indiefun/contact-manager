package tech.indiefun.contactmanager.ColumnRender;

import ezvcard.VCard;
import ezvcard.property.StructuredName;
import ezvcard.property.VCardProperty;
import javafx.scene.control.TableColumn;
import javafx.util.converter.DefaultStringConverter;

public class StructuredNameRender extends AbstractColumnRender {
    @Override
    public Class<? extends VCardProperty> support() {
        return StructuredName.class;
    }

    @Override
    protected String title() {
        return "StructuredName";
    }

    @Override
    protected TableColumn<VCard, String> column(String title, int index) {
        TableColumn<VCard, String> groupColumn = new TableColumn<>(title);

        TableColumn<VCard, String> familyColumn = RenderUtils.column(
                "Family",
                index,
                VCard::getStructuredNames,
                StructuredName::getFamily,
                StructuredName::setFamily,
                value -> {
                    StructuredName structuredName = new StructuredName();
                    structuredName.setFamily(value);
                    return structuredName;
                },
                new DefaultStringConverter()
        );

        groupColumn.getColumns().add(familyColumn);

        TableColumn<VCard, String> givenColumn = RenderUtils.column(
                "Given",
                index,
                VCard::getStructuredNames,
                StructuredName::getGiven,
                StructuredName::setGiven,
                value -> {
                    StructuredName structuredName = new StructuredName();
                    structuredName.setGiven(value);
                    return structuredName;
                },
                new DefaultStringConverter()
        );

        groupColumn.getColumns().add(givenColumn);

        return groupColumn;
    }
}
