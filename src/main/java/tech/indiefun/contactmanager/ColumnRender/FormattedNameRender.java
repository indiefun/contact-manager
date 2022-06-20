package tech.indiefun.contactmanager.ColumnRender;

import ezvcard.VCard;
import ezvcard.property.FormattedName;
import ezvcard.property.SimpleProperty;
import ezvcard.property.VCardProperty;
import javafx.scene.control.TableColumn;
import javafx.util.converter.DefaultStringConverter;

public class FormattedNameRender extends AbstractColumnRender {
    @Override
    public Class<? extends VCardProperty> support() {
        return FormattedName.class;
    }

    @Override
    protected String title() {
        return "FormattedName";
    }

    @Override
    protected TableColumn<VCard, String> column(String title, int index) {
        return RenderUtils.column(
                title,
                index,
                VCard::getFormattedNames,
                SimpleProperty::getValue,
                SimpleProperty::setValue,
                FormattedName::new,
                new DefaultStringConverter()
        );
    }
}
