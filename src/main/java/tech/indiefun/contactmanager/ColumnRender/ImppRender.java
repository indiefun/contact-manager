package tech.indiefun.contactmanager.ColumnRender;

import ezvcard.VCard;
import ezvcard.property.Impp;
import ezvcard.property.VCardProperty;
import javafx.scene.control.TableColumn;
import javafx.util.StringConverter;
import org.apache.commons.lang.StringUtils;

import java.net.URI;

public class ImppRender extends AbstractColumnRender {
    public static final int ORDER = 70;

    @Override
    public Class<? extends VCardProperty> support() {
        return Impp.class;
    }

    @Override
    protected String title() {
        return "Impp";
    }

    @Override
    protected TableColumn<VCard, URI> column(String title, int index) {
        return RenderUtils.column(
                title,
                index,
                VCard::getImpps,
                Impp::getUri,
                Impp::setUri,
                Impp::new,
                new UriStringConverter()
        );
    }

    @Override
    public int order() {
        return ORDER;
    }

    public static final class UriStringConverter extends StringConverter<URI> {
        @Override
        public String toString(URI uri) {
            return uri == null ? null : uri.toString();
        }

        @Override
        public URI fromString(String str) {
            return StringUtils.isEmpty(str) ? null : URI.create(str);
        }
    }
}
