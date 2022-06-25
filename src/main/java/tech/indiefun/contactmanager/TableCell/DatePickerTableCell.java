package tech.indiefun.contactmanager.TableCell;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;

import java.time.LocalDate;

public class DatePickerTableCell<S> extends TableCell<S, LocalDate> {
    private final ObjectProperty<StringConverter<LocalDate>> converter = new SimpleObjectProperty<>(this, "converter");
    private DatePicker datePicker;

    public static <S> Callback<TableColumn<S, LocalDate>, TableCell<S, LocalDate>> forTableColumn() {
        return forTableColumn(new LocalDateStringConverter());
    }

    public static <S> Callback<TableColumn<S, LocalDate>, TableCell<S, LocalDate>> forTableColumn(StringConverter<LocalDate> converter) {
        return column -> new DatePickerTableCell<>(converter);
    }

    public DatePickerTableCell(StringConverter<LocalDate> converter) {
        setConverter(converter);
        this.getStyleClass().add("date-picker-table-cell");
    }

    @Override
    public void startEdit() {
        if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()) {
            return;
        }

        super.startEdit();

        if (isEditing()) {
            if (datePicker == null) {
                datePicker = new DatePicker(getItem());
                datePicker.converterProperty().bind(converterProperty());
                datePicker.setOnAction(event -> {
                    commitEdit(datePicker.getValue());
                    event.consume();
                });
                datePicker.setOnKeyReleased(t -> {
                    if (t.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                        t.consume();
                    }
                });
            }
            setText(null);
            setGraphic(datePicker);
            datePicker.requestFocus();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getConverter().toString(getItem()));
        setGraphic(null);
    }

    @Override
    protected void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);

        if (isEmpty()) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                datePicker.setValue(getItem());
                setText(null);
                setGraphic(datePicker);
            } else {
                setText(getConverter().toString(getItem()));
                setGraphic(null);
            }
        }
    }

    public final StringConverter<LocalDate> getConverter() {
        return converter.get();
    }

    public final ObjectProperty<StringConverter<LocalDate>> converterProperty() {
        return converter;
    }

    public final void setConverter(StringConverter<LocalDate> converter) {
        this.converter.set(converter);
    }
}
