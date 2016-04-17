package com.minhdtb.storm.common;


import de.jensd.fx.glyphs.GlyphIcon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class GraphicItemBuilder {

    private String text;
    private GlyphIcon icon;

    public static GraphicItemBuilder create() {
        return new GraphicItemBuilder();
    }

    public GraphicItemBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public GraphicItemBuilder setIcon(GlyphIcon icon) {
        this.icon = icon;
        return this;
    }

    public Node build() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(this.text);

        if (this.icon != null) {
            label.setPadding(new Insets(0, 10, 0, 10));
            hBox.getChildren().addAll(this.icon, label);
        } else {
            hBox.getChildren().addAll(label);
        }

        return hBox;
    }
}
