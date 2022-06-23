package tech.indiefun.contactmanager.ColumnRender;

import ezvcard.VCard;
import ezvcard.property.VCardProperty;
import javafx.scene.control.TableView;

public interface ColumnRender {
    int ORDER = 1000;

    Class<? extends VCardProperty> support();

    void render(TableView<VCard> tableView, int count);

    default int order() {
        return ORDER;
    }
}
