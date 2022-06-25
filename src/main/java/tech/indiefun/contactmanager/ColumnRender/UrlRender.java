package tech.indiefun.contactmanager.ColumnRender;

import ezvcard.VCard;
import ezvcard.property.Url;
import ezvcard.property.VCardProperty;
import javafx.scene.control.TableColumn;
import javafx.util.converter.DefaultStringConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
                Url::new,
                new DefaultStringConverter()
        );
    }

    @Override
    public int order() {
        return ORDER;
    }
}
