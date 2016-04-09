package com.minhdtb.storm.gui.openprofile;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.common.MenuItemBuilder;
import com.minhdtb.storm.common.Publisher;
import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.services.ProfileService;
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
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class DialogOpenProfileController extends AbstractController {

    @Autowired
    private Publisher<Profile> publisher;

    @Autowired
    ProfileService service;

    @FXML
    TableView<Profile> tableProfile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<Profile, String> columnName = new TableColumn<>("Name");
        columnName.setPrefWidth(310);
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Profile, Integer> columnChannels = new TableColumn<>("Channels");
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
                .setText("Delete Profile").setAction(event -> {
                    Profile profile = tableProfile.getSelectionModel().getSelectedItem();

                    Utils.showConfirm(this.getView(),
                            String.format("Do you really want to delete \"%s\"?", profile.getName()),
                            e -> {
                                tableProfile.getItems().remove(1);
                                publisher.publish("application:deleteProfile", profile);
                                onShow(null);
                            });
                }).build());

        tableProfile.setContextMenu(contextMenu);
    }

    public void actionOK() {
        Profile profile = tableProfile.getSelectionModel().getSelectedItem();
        if (profile != null) {
            close();
            publisher.publish("application:openProfile", profile);
        }
    }

    @Override
    protected void onShow(WindowEvent event) {
        tableProfile.setItems(FXCollections.observableArrayList((List<Profile>) service.findAllProfile()));
    }

    public void actionCancel() {
        this.close();
    }
}
