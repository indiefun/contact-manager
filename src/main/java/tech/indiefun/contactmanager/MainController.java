package tech.indiefun.contactmanager;

import com.google.i18n.phonenumbers.NumberParseException;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.property.*;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import tech.indiefun.contactmanager.ColumnRender.ColumnRender;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class MainController {

    private static final String HIGHLIGHT_PROPERTY = "X-HIGHLIGHT";

    @FXML
    private MenuItem menuDuplicate;

    @FXML
    private MenuItem menuDelete;

    private List<MenuItem> menusShouldBeDisabledWhenSelectNone;

    @FXML
    private TableView<VCard> tableView;

    private final Map<Class<? extends VCardProperty>, ColumnRender> supportRenders = new HashMap<>();
    private final Map<Class<? extends VCardProperty>, Integer> propertyOrders = new HashMap<>();

    @FXML
    protected void initialize() {
        Reflections reflections = new Reflections(ColumnRender.class.getPackage().getName(), Scanners.SubTypes);
        Set<Class<? extends ColumnRender>> renderClasses = reflections.getSubTypesOf(ColumnRender.class);
        try {
            for (Class<? extends ColumnRender> renderClass : renderClasses) {
                if (!Modifier.isAbstract(renderClass.getModifiers()) && !Modifier.isInterface(renderClass.getModifiers())) {
                    Constructor<? extends ColumnRender> constructor = renderClass.getConstructor();
                    ColumnRender columnRender = constructor.newInstance();
                    supportRenders.put(columnRender.support(), columnRender);
                    propertyOrders.put(columnRender.support(), columnRender.order());
                }
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        menusShouldBeDisabledWhenSelectNone = List.of(menuDuplicate, menuDelete);

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<VCard>) change -> updateMenuStatus(change.getList().size()));
        tableView.setRowFactory(new Callback<TableView<VCard>, TableRow<VCard>>() {
            @Override
            public TableRow<VCard> call(TableView<VCard> tableView) {
                return new TableRow<VCard>() {
                    @Override
                    protected void updateItem(VCard item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty && item != null
                                && item.getExtendedProperty(HIGHLIGHT_PROPERTY) != null
                                && !getTableView().getSelectionModel().getSelectedItems().contains(item)
                        ) {
                            String[] values = item.getExtendedProperty(HIGHLIGHT_PROPERTY).getValue().split(";");
                            if (values.length == 0) return;
                            this.setStyle("-fx-background-color: yellow; -fx-text-fill : #000000;");
                        } else {
                            this.setStyle(null);
                        }
                    }
                };
            }
        });
    }

    private void updateMenuStatus(int selectedCount) {
        boolean disable = (selectedCount == 0);
        menusShouldBeDisabledWhenSelectNone.forEach(menuItem -> menuItem.setDisable(disable));
    }

    private void initializeColumns() {
        tableView.getColumns().clear();
        Map<Class<? extends VCardProperty>, Integer> propertyCountMap = new HashMap<>();
        for (VCard card : tableView.getItems()) {
            Map<Class<? extends VCardProperty>, Integer> curCountMap = new HashMap<>();
            Collection<VCardProperty> properties = card.getProperties();
            for (VCardProperty property : properties) {
                Integer count = curCountMap.getOrDefault(property.getClass(), 0);
                curCountMap.put(property.getClass(), count + 1);
            }
            for (Class<? extends VCardProperty> clazz : curCountMap.keySet()) {
                Integer curCount = curCountMap.getOrDefault(clazz, 0);
                Integer maxCount = propertyCountMap.getOrDefault(clazz, 0);
                int count = Math.max(curCount, maxCount);
                if (count > 0) {
                    propertyCountMap.put(clazz, count);
                }
            }
        }

        List<Class<? extends VCardProperty>> propertyClassList = new ArrayList<>(propertyCountMap.keySet());
        propertyClassList.sort((l, r) -> {
            Integer lo = propertyOrders.getOrDefault(l, ColumnRender.ORDER);
            Integer ro = propertyOrders.getOrDefault(r, ColumnRender.ORDER);
            if (Objects.equals(lo, ro)) {
                return l.getSimpleName().compareTo(r.getSimpleName());
            } else {
                return lo.compareTo(ro);
            }
        });

        for (Class<? extends VCardProperty> propertyClass : propertyClassList) {
            Integer count = propertyCountMap.get(propertyClass);
            ColumnRender columnRender = supportRenders.getOrDefault(propertyClass, null);
            if (columnRender != null) {
                columnRender.render(tableView, count);
            } else {
                log.debug("Unsupported Property Class : {}", propertyClass);
            }
        }
    }

    private List<VCard> readFiles(List<File> files) throws IOException {
        List<VCard> cards = new ArrayList<>();
        for (File file : files) {
            cards.addAll(Ezvcard.parse(file).all());
        }
        return cards;
    }

    @FXML
    protected void onMenuOpenClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select vCard files");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All vCard files", "*.vcf", "*.vcard"));
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());
        if (files == null || files.isEmpty()) {
            return;
        }
        List<VCard> cards;
        try {
            cards = readFiles(files);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errors");
            alert.setContentText(e.getLocalizedMessage());
            alert.showAndWait();
            return;
        }
        if (tableView.getItems().size() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Tips");
            alert.setHeaderText(null);
            alert.setContentText("Added " + cards.size() + " contacts, now has total " + (tableView.getItems().size() + cards.size()) + " contacts.");
            alert.showAndWait();
        }
        tableView.getItems().addAll(cards);
        initializeColumns();
    }

    @FXML
    protected void onMenuSaveAsClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as vCard file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All vCard files", "*.vcf", "*.vcard"));
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            if (!file.getName().endsWith(".vcf") || !file.getName().endsWith(".vcard")) {
                file = new File(file.getAbsolutePath() + ".vcf");
            }
            try {
                Ezvcard.write(tableView.getItems()).go(file);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Save as " + file.getName() + " failed, cause of " + e.getLocalizedMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    protected void onMenuExitClicked(ActionEvent actionEvent) {
        Platform.exit();
    }

    @FXML
    protected void onMenuDuplicateClicked(ActionEvent actionEvent) {
        List<VCard> selectedCards = new ArrayList<>(tableView.getSelectionModel().getSelectedItems());
        if (!selectedCards.isEmpty()) {
            List<VCard> duplicatedCards = selectedCards.stream().map(VCard::new).collect(Collectors.toList());
            tableView.getSelectionModel().clearSelection();
            int duplicatedIndex = tableView.getItems().size();
            tableView.getItems().addAll(duplicatedCards);
            tableView.getSelectionModel().selectRange(duplicatedIndex, tableView.getItems().size());
            tableView.scrollTo(duplicatedIndex);
            updateMenuStatus(duplicatedCards.size());
        }
    }

    @FXML
    protected void onMenuDeleteClicked(ActionEvent actionEvent) {
        List<VCard> selectedCards = new ArrayList<>(tableView.getSelectionModel().getSelectedItems());
        if (!selectedCards.isEmpty()) {
            tableView.getSelectionModel().clearSelection();
            tableView.getItems().removeAll(selectedCards);
            updateMenuStatus(0);
        }
    }

    @FXML
    protected void onMenuRemoveWhitespacesForAllFieldsClicked(ActionEvent actionEvent) {
        List<VCard> selectedCards = new ArrayList<>(tableView.getSelectionModel().getSelectedItems());
        if (selectedCards.isEmpty()) {
            selectedCards = tableView.getItems();
        }
        for (VCard card : selectedCards) {
            removeWhitespacesForFormattedNames(card);
            removeWhitespacesForStructuredNames(card);
        }
        tableView.refresh();
    }

    private void removeWhitespacesForFormattedNames(VCard card) {
        for (FormattedName formattedName : card.getFormattedNames()) {
            formattedName.setValue(StringUtils.deleteWhitespace(formattedName.getValue()));
        }
    }

    @FXML
    protected void onMenuRemoveWhitespacesForFormattedNamesClicked(ActionEvent actionEvent) {
        List<VCard> selectedCards = new ArrayList<>(tableView.getSelectionModel().getSelectedItems());
        if (selectedCards.isEmpty()) {
            selectedCards = tableView.getItems();
        }
        for (VCard card : selectedCards) {
            removeWhitespacesForFormattedNames(card);
        }
        tableView.refresh();
    }

    private void removeWhitespacesForStructuredNames(VCard card) {
        for (StructuredName structuredName : card.getStructuredNames()) {
            structuredName.setFamily(StringUtils.deleteWhitespace(structuredName.getFamily()));
            structuredName.setGiven(StringUtils.deleteWhitespace(structuredName.getGiven()));
        }
    }

    @FXML
    protected void onMenuRemoveWhitespacesForStructuredNamesClicked(ActionEvent actionEvent) {
        List<VCard> selectedCards = new ArrayList<>(tableView.getSelectionModel().getSelectedItems());
        if (selectedCards.isEmpty()) {
            selectedCards = tableView.getItems();
        }
        for (VCard card : selectedCards) {
            removeWhitespacesForStructuredNames(card);
        }
        tableView.refresh();
    }

    private void formatTelephones(VCard card) throws NumberParseException {
        List<Telephone> telephones = card.getTelephoneNumbers();
        for (Telephone telephone : telephones) {
            telephone.setText(TelephoneStringFormatter.format(telephone.getText()));
        }
    }

    @FXML
    protected void onMenuFormatTelephonesClicked(ActionEvent actionEvent) {
        List<VCard> selectedCards = new ArrayList<>(tableView.getSelectionModel().getSelectedItems());
        if (selectedCards.isEmpty()) {
            selectedCards = tableView.getItems();
        }
        try {
            for (VCard card : selectedCards) {
                formatTelephones(card);
            }
        } catch (NumberParseException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errors");
            alert.setContentText(e.getLocalizedMessage());
            alert.showAndWait();
        }
        tableView.refresh();
    }

    private void removeDuplications(List<VCard> cards) {
        Map<String, List<VCard>> map = new HashMap<>();
        for (VCard card : cards) {
            String telephones = card.getTelephoneNumbers()
                    .stream()
                    .map(telephone -> {
                        try {
                            return TelephoneStringFormatter.format(telephone.getText());
                        } catch (NumberParseException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(StringUtils::isNotEmpty)
                    .sorted()
                    .reduce((left, right) -> left + right)
                    .orElse(null);
            if (StringUtils.isNotEmpty(telephones)) {
                List<VCard> list = map.getOrDefault(telephones, new ArrayList<>());
                list.add(card);
                map.put(telephones, list);
            }
        }
        List<VCard> shouldRemoved = new ArrayList<>();
        for (String telephones : map.keySet()) {
            List<VCard> list = map.get(telephones);
            List<VCard> duplications = list
                    .stream()
                    .sorted(
                            Comparator.comparing(
                                    card -> card.getRevision() == null ? new Date(0) : card.getRevision().getValue(),
                                    Comparator.reverseOrder()
                            )
                    )
                    .skip(1)
                    .collect(Collectors.toList());
            shouldRemoved.addAll(duplications);
        }
        if (!shouldRemoved.isEmpty()) {
            tableView.getSelectionModel().clearSelection();
            tableView.getItems().removeAll(shouldRemoved);
            cards.stream().filter(card -> !shouldRemoved.contains(card)).forEach(card -> tableView.getSelectionModel().select(card));
        }
    }

    @FXML
    protected void onMenuRemoveDuplicationsClicked(ActionEvent actionEvent) {
        List<VCard> selectedCards = new ArrayList<>(tableView.getSelectionModel().getSelectedItems());
        if (selectedCards.isEmpty()) {
            selectedCards = tableView.getItems();
        }
        removeDuplications(selectedCards);
    }

    @FXML
    protected void onMenuAboutClicked(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setContentText("Contact Manager is a tool for managing vCard files, copy right owned by kevin_loo@live.cn, you can use it freely!");
        alert.showAndWait();
    }

    @FXML
    protected void onMenuSettingsClicked(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("settings-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Settings");
        stage.setScene(scene);
        stage.show();
    }

    private void findByKeywords(String keywords) {
        for (VCard card : tableView.getItems()) {
            List<String> highlightProperties = new ArrayList<>();
            if (card.getFormattedNames().stream().anyMatch(formattedName -> formattedName.getValue().contains(keywords))) {
                highlightProperties.add("FormattedName");
            }
            if (card.getStructuredNames().stream().anyMatch(structuredName -> String.join(";", structuredName.getFamily(), structuredName.getGiven()).contains(keywords))) {
                highlightProperties.add("StructuredName");
            }
            if (card.getTelephoneNumbers().stream().anyMatch(telephone -> telephone.getText().contains(keywords))) {
                highlightProperties.add("Telephone");
            }
            if (card.getEmails().stream().anyMatch(email -> email.getValue().contains(keywords))) {
                highlightProperties.add("Email");
            }
            if (card.getOrganizations().stream().anyMatch(organization -> organization.getValues().stream().anyMatch(value -> value.contains(keywords)))) {
                highlightProperties.add("Organization");
            }
            if (card.getNotes().stream().anyMatch(note -> note.getValue().contains(keywords))) {
                highlightProperties.add("Note");
            }
            if (!highlightProperties.isEmpty()) {
                card.setExtendedProperty(HIGHLIGHT_PROPERTY, String.join(";", highlightProperties));
            } else {
                card.removeExtendedProperty(HIGHLIGHT_PROPERTY);
            }
        }
        tableView.refresh();
    }

    @FXML
    protected void onMenuFindClicked(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Find");
        dialog.setHeaderText(null);
        dialog.setContentText("Keywords: ");
        dialog.showAndWait().filter(StringUtils::isNotEmpty).ifPresent(this::findByKeywords);
    }
}
