package com.minhdtb.storm.gui.openprofile;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.base.Publisher;
import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.services.ProfileService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
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

        tableProfile.getColumns().add(columnName);
        tableProfile.getColumns().add(columnChannels);

        tableProfile.setItems(FXCollections.observableArrayList(service.findAllProfile()));
    }

    public void actionOK() {
        Profile profile = tableProfile.getSelectionModel().getSelectedItem();
        if (profile != null) {
            this.close();
            publisher.publish("application:openProfile", profile);
        }
    }

    public void actionCancel() {
        this.close();
    }
}
