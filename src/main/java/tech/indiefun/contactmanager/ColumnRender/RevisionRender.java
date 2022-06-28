package tech.indiefun.contactmanager.ColumnRender;

import ezvcard.VCard;
import ezvcard.property.Revision;
import ezvcard.property.VCardProperty;
import javafx.scene.control.TableColumn;
import tech.indiefun.contactmanager.GlobalSettings;
import tech.indiefun.contactmanager.TableCell.DatePickerTableCell;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

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

    private ZoneId zoneId() {
        return ZoneId.of(GlobalSettings.getInstance().getConfigurations().getDefaultTimeZoneID());
    }

    LocalDate toLocalDate(Date date) {
        return LocalDate.ofInstant(date.toInstant(), zoneId());
    }

    Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(zoneId()).toInstant());
    }

    List<Revision> getRevisions(VCard card) {
        Revision revision = card.getRevision();
        return revision == null ? List.of() : List.of(revision);
    }

    @Override
    protected TableColumn<VCard, ?> column(String title, int index) {
        return RenderUtils.column(
                title,
                index,
                this::getRevisions,
                revision -> toLocalDate(revision.getValue()),
                (property, value) -> property.setValue(toDate(value)),
                value -> new Revision(toDate(value)),
                DatePickerTableCell.forTableColumn()
        );
    }

    @Override
    public int order() {
        return ORDER;
    }
}
