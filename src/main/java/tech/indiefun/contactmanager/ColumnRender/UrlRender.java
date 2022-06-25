package tech.indiefun.contactmanager.ColumnRender;

import ezvcard.VCard;
import ezvcard.property.Url;
import ezvcard.property.VCardProperty;
import javafx.scene.control.TableColumn;

public class UrlRender extends AbstractColumnRender {
    public static final int ORDER = 60;

    @Override
    public Class<? extends VCardProperty> support() {
        return Url.class;
    }

    @Override
    protected String title() {
        return "Url";
    }

    @Override
    protected TableColumn<VCard, String> column(String title, int index) {
        return RenderUtils.column(
                title,
                index,
                VCard::getUrls,
                Url::getValue,
                Url::setValue,
                Url::new
        );
    }

    @Override
    public int order() {
        return ORDER;
    }
}
