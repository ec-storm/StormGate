package com.minhdtb.storm.common;

import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.GlyphsDude;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;

public class MenuItemBuilder {

    private String text;
    private EventHandler<ActionEvent> event;
    private GlyphIcons glyphIcons;
    private String iconSize;
    private KeyCombination keyCombination;

    private MenuItemBuilder() {
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

    public MenuItemBuilder setIcon(GlyphIcons glyphIcons, String iconSize) {
        this.glyphIcons = glyphIcons;
        this.iconSize = iconSize;
        return this;
    }

    public MenuItemBuilder setAccelerator(KeyCombination keyCombination) {
        this.keyCombination = keyCombination;
        return this;
    }

    public MenuItem build() {
        MenuItem menuItem = new MenuItem(this.text);
        menuItem.setOnAction(this.event);

        if (this.glyphIcons != null && this.iconSize != null) {
            GlyphsDude.setIcon(menuItem, this.glyphIcons, this.iconSize);
        }

        if (this.keyCombination != null) {
            menuItem.setAccelerator(this.keyCombination);
        }

        return menuItem;
    }
}
