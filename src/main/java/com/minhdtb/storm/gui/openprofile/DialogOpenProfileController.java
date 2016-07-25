package com.minhdtb.storm.gui.openprofile;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.common.MenuItemBuilder;
import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.services.DataManager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class DialogOpenProfileController extends AbstractController {

    final
    DataManager dataManager;

    @FXML
    TableView<Profile> tableProfile;

    @Autowired
    public DialogOpenProfileController(DataManager dataManager) {
        Assert.notNull(dataManager, "DataManager must not be null");
        this.dataManager = dataManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<Profile, String> columnName = new TableColumn<>(resources.getString("TXT001"));
        columnName.setPrefWidth(310);
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Profile, Integer> columnChannels = new TableColumn<>(resources.getString("TXT002"));
        columnChannels.setPrefWidth(120);
        columnChannels.setStyle("-fx-alignment: CENTER;");
        columnChannels.setCellValueFactory(tableCell -> new ReadOnlyObjectWrapper<>(tableCell.getValue().getChannels().size()));

        tableProfile.setRowFactory(tv -> {
            TableRow<Profile> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    actionOK();
                }
            });

            return row;
        });

        tableProfile.getColumns().add(columnName);
        tableProfile.getColumns().add(columnChannels);

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(MenuItemBuilder.create()
                .setText(resources.getString("TXT003")).setAction(event -> {
                    Profile profile = tableProfile.getSelectionModel().getSelectedItem();

                    Utils.showConfirm(this.getView(),
                            String.format(resources.getString("MSG001"), profile.getName()),
                            e -> {
                                tableProfile.getItems().remove(profile);
                                getPublisher().publish("application:deleteProfile", profile);
                            });
                }).build());

        tableProfile.setContextMenu(contextMenu);
    }

    public void actionOK() {
        Profile profile = tableProfile.getSelectionModel().getSelectedItem();
        if (profile != null) {
            getPublisher().publish("application:openProfile", profile);
            close();
        }
    }

    @Override
    public void onShow(WindowEvent event) {
        tableProfile.setItems(FXCollections.observableArrayList(dataManager.getProfiles()));
    }

    public void actionCancel() {
        this.close();
    }
}
