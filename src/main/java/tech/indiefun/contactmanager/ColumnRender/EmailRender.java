package tech.indiefun.contactmanager.ColumnRender;

import ezvcard.VCard;
import ezvcard.property.Email;
import ezvcard.property.VCardProperty;
import javafx.scene.control.TableColumn;

public class EmailRender extends AbstractColumnRender {
    public static final int ORDER = 40;

    @Override
    public Class<? extends VCardProperty> support() {
        return Email.class;
    }

    @Override
    protected String title() {
        return "Email";
    }

    @Override
    protected TableColumn<VCard, String> column(String title, int index) {
        return RenderUtils.column(
                title,
                index,
                VCard::getEmails,
                Email::getValue,
                Email::setValue,
                Email::new
        );
    }

    @Override
    public int order() {
        return ORDER;
    }
}
