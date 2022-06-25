package tech.indiefun.contactmanager.ColumnRender;

import ezvcard.VCard;
import ezvcard.property.Organization;
import ezvcard.property.VCardProperty;
import javafx.scene.control.TableColumn;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrganizationRender extends AbstractColumnRender {
    public static final int ORDER = 60;
    public static final String SEPARATOR = ";";

    @Override
    public Class<? extends VCardProperty> support() {
        return Organization.class;
    }

    @Override
    protected String title() {
        return "Organization";
    }

    private String getOrganization(Organization organization) {
        return organization.getValues().stream().filter(StringUtils::isNotEmpty).collect(Collectors.joining(SEPARATOR));
    }

    private void addOrganization(Organization organization, String value) {
        List<String> list = Arrays.stream(value.split(SEPARATOR)).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        organization.getValues().addAll(list);
    }

    private void setOrganization(Organization organization, String value) {
        organization.getValues().clear();
        addOrganization(organization, value);
    }

    private Organization newOrganization(String value) {
        Organization organization = new Organization();
        addOrganization(organization, value);
        return organization;
    }

    @Override
    protected TableColumn<VCard, String> column(String title, int index) {
        return RenderUtils.column(
                title,
                index,
                VCard::getOrganizations,
                this::getOrganization,
                this::setOrganization,
                this::newOrganization
        );
    }

    @Override
    public int order() {
        return ORDER;
    }
}
