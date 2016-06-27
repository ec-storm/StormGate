package com.minhdtb.storm.gui.listopctag;

import com.minhdtb.storm.base.AbstractController;
import com.minhdtb.storm.core.lib.opcda.OPCDaClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.WindowEvent;
import lombok.Data;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
public class DialogListOpcTagController extends AbstractController {

    @FXML
    public TreeView<DataItem> treeTags;

    private OPCDaClient opcDaClient = new OPCDaClient();

    private HashMap<String, DataItem> root = new HashMap<>();

    @Override
    public void onShow(WindowEvent event) {
        DialogListOpcTagView view = (DialogListOpcTagView) getView();
        opcDaClient.setHost(view.getHost());
        opcDaClient.connect(view.getProgId());

        opcDaClient.getTags().stream().forEach(this::parseText);

        DataItem rootItem = new DataItem();
        rootItem.setPath("root");

        TreeItem<DataItem> mRoot = new OpcTagTreeItem(true, rootItem);
        treeTags.setRoot(mRoot);

        treeTags.setCellFactory(treeView -> new TreeCell<DataItem>() {

            @Override
            public void updateItem(DataItem dataItem, boolean empty) {
                super.updateItem(dataItem, empty);

                if (empty) {
                    setText(null);
                } else {
                    setText(dataItem.getPath());
                }
            }
        });
    }

    public void actionOK() {
        close();
    }

    public void actionCancel() {
        close();
    }

    private void parseText(String line) {
        String[] tempArray = line.split("/");
        String sRoot = tempArray[0];
        DataItem item;

        if (root.get(sRoot) != null) {
            item = root.get(sRoot);

            for (int i = 1; i < tempArray.length; i++) {
                item = parse(item, tempArray[i]);
            }
        } else {
            item = new DataItem();
            item.setPath(sRoot);
            root.put(sRoot, item);
            item = root.get(sRoot);

            for (int i = 1; i < tempArray.length; i++) {
                item = parse(item, tempArray[i]);
            }
        }
    }

    private DataItem parse(DataItem item, String key) {
        if (item.childs.get(key) == null) {
            DataItem newItem = new DataItem();
            newItem.setPath(key);
            item.childs.put(key, newItem);
        }

        return item.childs.get(key);
    }

    @Data
    private class DataItem {
        private String path;
        private HashMap<String, DataItem> childs;

        public DataItem() {
            this.childs = new HashMap<>();
        }
    }

    private class OpcTagTreeItem extends TreeItem<DataItem> {

        private boolean isFirstTimeChildren = true;
        private boolean isFirstTimeLeaf = true;
        private boolean isLeaf = false;
        private boolean isRoot = false;

        public OpcTagTreeItem(Boolean flag, DataItem dataItem) {
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
                isLeaf = !isRoot && getValue().childs.values().size() == 0;
            }

            return isLeaf;
        }

        private ObservableList<TreeItem<DataItem>> buildChildren() {
            if (isRoot) {
                return (new ArrayList<>(root.values())).stream()
                        .sorted(Comparator.comparing(DataItem::getPath))
                        .map(dataItem -> new OpcTagTreeItem(false, dataItem))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

            } else {
                if (getValue() != null) {
                    return (new ArrayList<>(getValue().childs.values())).stream()
                            .sorted(Comparator.comparing(DataItem::getPath))
                            .map(dataItem -> new OpcTagTreeItem(false, dataItem))
                            .collect(Collectors.toCollection(FXCollections::observableArrayList));
                }
            }

            return FXCollections.emptyObservableList();
        }
    }
}

