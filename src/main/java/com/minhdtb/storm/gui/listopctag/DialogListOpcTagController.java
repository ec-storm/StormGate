package com.minhdtb.storm.gui.listopctag;

import com.google.common.base.Strings;
import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.core.lib.opcda.OPCDaClient;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.WindowEvent;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DialogListOpcTagController extends AbstractController {

    @FXML
    public TreeView<DataItem> treeTags;
    @FXML
    public TableView<DataItem> tableTags;

    private OPCDaClient opcDaClient = new OPCDaClient();

    @Override
    public void onShow(WindowEvent event) {
        TableColumn<DataItem, String> columnName = new TableColumn<>(getResourceString("TXT001"));
        columnName.setPrefWidth(120);
        columnName.setCellValueFactory(tableCell -> new ReadOnlyObjectWrapper<>(tableCell.getValue().getName()));

        TableColumn<DataItem, String> columnChannels = new TableColumn<>(getResourceString("TXT002"));
        columnChannels.setPrefWidth(300);
        columnChannels.setCellValueFactory(tableCell -> new ReadOnlyObjectWrapper<>(tableCell.getValue().getPath()));

        tableTags.setRowFactory(tv -> {
            TableRow<DataItem> row = new TableRow<>();
            row.setOnMouseClicked(eventMouse -> {
                if (eventMouse.getClickCount() == 2 && (!row.isEmpty())) {
                    actionOK();
                }
            });

            return row;
        });

        tableTags.getColumns().add(columnName);
        tableTags.getColumns().add(columnChannels);

        treeTags.setOnMouseClicked(eventMouse -> {
            if (treeTags.getSelectionModel().getSelectedItem() != null) {
                DataItem selected = treeTags.getSelectionModel().getSelectedItem().getValue();
                tableTags.getItems().clear();

                tableTags.getItems().addAll(selected.getChildLeafs().stream().sorted(Comparator.
                        comparing(DataItem::getName)).collect(Collectors.toList()));
            }
        });

        DialogListOpcTagView view = (DialogListOpcTagView) getView();
        opcDaClient.setHost(view.getHost());
        opcDaClient.disconnect();
        opcDaClient.connect(view.getProgId());

        tableTags.getItems().clear();

        DataItem rootItem = new DataItem("");
        TreeItem<DataItem> mRoot = new OpcTagTreeItem(true, rootItem);
        treeTags.setRoot(mRoot);

        treeTags.setCellFactory(treeView -> new TreeCell<DataItem>() {

            @Override
            public void updateItem(DataItem dataItem, boolean empty) {
                super.updateItem(dataItem, empty);

                if (empty) {
                    setText(null);
                } else {
                    if (Strings.isNullOrEmpty(dataItem.getPath())) {
                        setText("root");
                    } else {
                        setText(dataItem.getName());
                    }
                }
            }
        });
    }

    public void actionOK() {
        DataItem item = tableTags.getSelectionModel().getSelectedItem();
        if (item != null) {
            getPublisher().publish("opc:select", item.getPath());
            close();
        }
    }

    public void actionCancel() {
        close();
    }

    private class DataItem {
        private String path;
        private String name;
        private List<DataItem> childs;
        private List<DataItem> childLeafs;

        public List<DataItem> getChilds() {
            childs.clear();

            List<String> items = opcDaClient.getTagBranches(getPath());
            items.forEach(item -> {
                DataItem dataItem = new DataItem(item);
                childs.add(dataItem);
            });

            return childs;
        }

        public List<DataItem> getChildLeafs() {
            childLeafs.clear();

            List<String> items = opcDaClient.getTagLeafs(getPath());
            items.forEach(item -> {
                DataItem dataItem = new DataItem(item);
                childLeafs.add(dataItem);
            });

            return childLeafs;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getName() {
            if (Strings.isNullOrEmpty(name)) {
                String[] items = getPath().split("/");
                if (items.length == 1) {
                    items = getPath().split("\\.");
                }

                name = items[items.length - 1];
            }

            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        DataItem() {
            this.childs = new ArrayList<>();
            this.childLeafs = new ArrayList<>();
        }

        DataItem(String path) {
            this();
            setPath(path);
        }
    }

    private class OpcTagTreeItem extends TreeItem<DataItem> {

        private boolean isFirstTimeChildren = true;
        private boolean isFirstTimeLeaf = true;
        private boolean isLeaf = false;
        private boolean isRoot = false;

        OpcTagTreeItem(Boolean flag, DataItem dataItem) {
            if (flag) {
                isRoot = true;
                setExpanded(true);
            }

            setValue(dataItem);
        }

        @Override
        public ObservableList<TreeItem<DataItem>> getChildren() {
            if (isFirstTimeChildren) {
                isFirstTimeChildren = false;
                super.getChildren().setAll(buildChildren());
            }

            return super.getChildren();
        }

        @Override
        public boolean isLeaf() {
            if (isFirstTimeLeaf) {
                isFirstTimeLeaf = false;
                isLeaf = !isRoot && getValue().getChilds().size() == 0;
            }

            return isLeaf;
        }

        private ObservableList<TreeItem<DataItem>> buildChildren() {
            if (getValue() != null) {
                return (new ArrayList<>(getValue().getChilds())).stream()
                        .sorted(Comparator.comparing(item -> {
                            if (item.getChilds().size() > 0) {
                                return "0-" + item.getPath();
                            } else {
                                return "1-" + item.getPath();
                            }
                        }))
                        .map(dataItem -> new OpcTagTreeItem(false, dataItem))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
            }

            return FXCollections.emptyObservableList();
        }
    }
}

