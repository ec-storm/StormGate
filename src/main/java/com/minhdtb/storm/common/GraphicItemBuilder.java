package com.minhdtb.storm.common;


import de.jensd.fx.glyphs.GlyphIcon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class GraphicItemBuilder {

    private String text;
    private GlyphIcon icon;
    private Insets insets;
    private Font font;

    public static GraphicItemBuilder create() {
        return new GraphicItemBuilder();
    }

    private GraphicItemBuilder() {
        insets = new Insets(0, 10, 0, 10);
    }

    public GraphicItemBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public GraphicItemBuilder setIcon(GlyphIcon icon) {
        this.icon = icon;
        return this;
    }

    public GraphicItemBuilder setLabelPadding(Insets insets) {
        this.insets = insets;
        return this;
    }

    public GraphicItemBuilder setFont(Font font) {
        this.font = font;
        return this;
    }

    public Node build() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(this.text);
        if (font != null) {
            label.setFont(font);
        }
        
        if (this.icon != null) {
            label.setPadding(insets);
            hBox.getChildren().addAll(this.icon, label);
        } else {
            hBox.getChildren().addAll(label);
        }

        return hBox;
    }
}
