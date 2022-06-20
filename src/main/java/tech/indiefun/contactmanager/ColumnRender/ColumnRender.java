package tech.indiefun.contactmanager.ColumnRender;

import ezvcard.VCard;
import ezvcard.property.VCardProperty;
import javafx.scene.control.TableView;

public interface ColumnRender {
    Class<? extends VCardProperty> support();
    void render(TableView<VCard> tableView, int count);
}
