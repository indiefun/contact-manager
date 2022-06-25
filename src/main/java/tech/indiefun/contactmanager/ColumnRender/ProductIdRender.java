package tech.indiefun.contactmanager.ColumnRender;

import ezvcard.VCard;
import ezvcard.property.ProductId;
import ezvcard.property.VCardProperty;
import javafx.scene.control.TableColumn;

import java.util.List;

public class ProductIdRender extends AbstractColumnRender {
    public static final int ORDER = 200;

    @Override
    public Class<? extends VCardProperty> support() {
        return ProductId.class;
    }

    @Override
    protected String title() {
        return "Product ID";
    }

    List<ProductId> getProductIds(VCard card) {
        ProductId productId = card.getProductId();
        return productId == null ? List.of() : List.of(productId);
    }

    @Override
    protected TableColumn<VCard, String> column(String title, int index) {
        return RenderUtils.column(
                title,
                index,
                this::getProductIds,
                ProductId::getValue,
                ProductId::setValue,
                ProductId::new
        );
    }

    @Override
    public int order() {
        return ORDER;
    }
}
