package tech.indiefun.contactmanager.ColumnRender;

import ezvcard.VCard;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public abstract class AbstractColumnRender implements ColumnRender {

    protected abstract String title();

    protected abstract TableColumn<VCard, String> column(String title, int index);

    @Override
    public void render(TableView<VCard> tableView, int count) {
        if (count == 1) {
            tableView.getColumns().add(column(title(), 0));
        } else {
            TableColumn<VCard, String> groupColumn = new TableColumn<>(title());
            for (int i = 0; i < count; ++i) {
                groupColumn.getColumns().add(column("#" + String.valueOf(i + 1), i));
            }
            tableView.getColumns().add(groupColumn);
        }
    }
}
