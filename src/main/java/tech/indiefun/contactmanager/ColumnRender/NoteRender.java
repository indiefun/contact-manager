package tech.indiefun.contactmanager.ColumnRender;

import ezvcard.VCard;
import ezvcard.property.Note;
import ezvcard.property.ProductId;
import ezvcard.property.VCardProperty;
import javafx.scene.control.TableColumn;

import java.util.List;

public class NoteRender extends AbstractColumnRender {
    public static final int ORDER = 300;

    @Override
    public Class<? extends VCardProperty> support() {
        return Note.class;
    }

    @Override
    protected String title() {
        return "Note";
    }

    @Override
    protected TableColumn<VCard, String> column(String title, int index) {
        return RenderUtils.column(
                title,
                index,
                VCard::getNotes,
                Note::getValue,
                Note::setValue,
                Note::new
        );
    }

    @Override
    public int order() {
        return ORDER;
    }
}
