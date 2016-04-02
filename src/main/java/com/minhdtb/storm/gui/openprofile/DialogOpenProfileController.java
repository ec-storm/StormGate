package com.minhdtb.storm.gui.openprofile;

import com.minhdtb.storm.base.AbstractController;
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
    ProfileService service;

    @FXML
    TableView<Profile> tableProfile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<Profile, String> name = new TableColumn<>("Name");
        name.setPrefWidth(310);
        name.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Profile, Integer> channels = new TableColumn<>("Channels");
        channels.setPrefWidth(120);
        channels.setCellValueFactory(tableCell -> new ReadOnlyObjectWrapper<>(tableCell.getValue().getChannels().size()));

        tableProfile.getColumns().add(name);
        tableProfile.getColumns().add(channels);

        tableProfile.setItems(FXCollections.observableArrayList(service.findAllProfile()));
    }

    public void actionOK() {

    }

    public void actionCancel() {
        this.close();
    }
}
