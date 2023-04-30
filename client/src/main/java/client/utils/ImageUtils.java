package client.utils;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageUtils {

    /**
     * Generates an icon
     *
     * @param iconName name of the icon
     * @param size size of the icon
     * @return an imageview of the icon
     */
    public ImageView generateIcon(String iconName, int size) {
        Image image = new Image(
                this.getClass()
                        .getClassLoader()
                        .getResource("client/icons/" + iconName)
                        .toExternalForm()
        );
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(size);
        imageView.setFitWidth(size);
        return imageView;
    }

    /**
     * Generates an icon button
     *
     * @param iconName name of the icon
     * @param size size of the icon
     * @param tooltipText text of the tooltip
     * @return a button with the icon as graphic
     */
    public Button generateIconButton(String iconName, int size, String tooltipText) {
        Button button = new Button();
        button.setGraphic(generateIcon(iconName, size));
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setTooltip(new Tooltip(tooltipText));
        button.getStyleClass().add("icon-button");
        return button;
    }

}
