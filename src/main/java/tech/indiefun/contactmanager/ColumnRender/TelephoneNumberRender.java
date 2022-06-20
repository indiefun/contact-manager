package tech.indiefun.contactmanager.ColumnRender;

import ezvcard.VCard;
import ezvcard.property.Telephone;
import ezvcard.property.VCardProperty;
import javafx.scene.control.TableColumn;
import tech.indiefun.contactmanager.TelephoneStringConverter;

public class TelephoneNumberRender extends AbstractColumnRender {
    @Override
    public Class<? extends VCardProperty> support() {
        return Telephone.class;
    }

    @Override
    protected String title() {
        return "Telephone";
    }

    @Override
    protected TableColumn<VCard, String> column(String title, int index) {
        return RenderUtils.column(
                title,
                index,
                VCard::getTelephoneNumbers,
                Telephone::getText,
                Telephone::setText,
                Telephone::new,
                new TelephoneStringConverter()
        );
    }
}
