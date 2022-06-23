module tech.indiefun.contactmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires ez.vcard;
    requires commons.lang;
    requires libphonenumber;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.reflections;
    requires org.slf4j;
    requires lombok;

    opens tech.indiefun.contactmanager to javafx.fxml;
    exports tech.indiefun.contactmanager;
}
