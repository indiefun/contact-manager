package tech.indiefun.contactmanager.ColumnRender;

import ezvcard.VCard;
import ezvcard.property.Revision;
import ezvcard.property.VCardProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import lombok.extern.slf4j.Slf4j;
import tech.indiefun.contactmanager.TableCell.DatePickerTableCell;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class RevisionRender extends AbstractColumnRender {
    public static final int ORDER = 100;

    @Override
    public Class<? extends VCardProperty> support() {
        return Revision.class;
    }

    @Override
    protected String title() {
        return "Revision";
    }

    LocalDate toLocalDate(Date date) {
        return LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Override
    protected TableColumn<VCard, ?> column(String title, int index) {
        TableColumn<VCard, LocalDate> column = new TableColumn<>();
        column.setCellValueFactory(features -> {
            VCard card = features.getValue();
            Revision revision = card.getRevision();
            return new SimpleObjectProperty<LocalDate>(revision == null ? null : toLocalDate(revision.getValue()));
        });
        column.setCellFactory(DatePickerTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            VCard card = event.getRowValue();
            Date value = toDate(event.getNewValue());
            Revision revision = card.getRevision();
            if (revision == null) card.setRevision(value);
            else revision.setValue(value);
        });
        return column;
    }

    @Override
    public int order() {
        return ORDER;
    }
}
