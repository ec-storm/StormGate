package com.minhdtb.storm.base;

import javafx.scene.control.MenuItem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class MenuItemBuilder {

    private String text;
    private EventHandler<ActionEvent> event;

    public MenuItemBuilder() {
    }

    public static MenuItemBuilder create() {
        return new MenuItemBuilder();
    }

    public MenuItemBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public MenuItemBuilder setAction(EventHandler<ActionEvent> event) {
        this.event = event;
        return this;
    }

    public MenuItem build() {
        MenuItem menuItem = new MenuItem(this.text);
        menuItem.setOnAction(this.event);

        return menuItem;
    }
}
