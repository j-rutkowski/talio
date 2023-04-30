package client.scenes;

import client.utils.ImageUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Card;
import commons.Tag;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;
import java.util.Optional;

public class TagOverviewCtrl {

    private final ServerUtils server;
    private final ImageUtils img;
    private final MainCtrl mainCtrl;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox tagList;

    /**
     * AddTagCtrl constructor
     *
     * @param server   server utilities
     * @param mainCtrl main controller
     * @param img      image utilities
     */
    @Inject
    public TagOverviewCtrl(ServerUtils server, MainCtrl mainCtrl, ImageUtils img) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.img = img;
    }

    /**
     * Function for adding a tag to the overview
     */
    public void addTag() {
        this.mainCtrl.showAddTag();
    }

    /**
     * Function for exiting the tag overview scene
     */
    public void exit() {
        this.mainCtrl.showBoard();
    }

    /**
     * Function for refreshing the tag overview scene
     */
    public void refresh() {
        System.out.println("REFRESHED CALLED");
        tagList.getChildren().clear();
        List<Tag> tags = server.getTags(this.mainCtrl.getBoardId());
        for (Tag tag : tags) {
            HBox hBox = new HBox();
            Label title = new Label(tag.getName());
            title.setFont(new Font(15));
            Label color = new Label("               ");
            Color colorOfTag = Color.valueOf(tag.getColor());
            color.setBackground(new Background(new BackgroundFill(colorOfTag, null, null)));
            Button editButton = new Button("Title", img.generateIcon("pen-solid.png", 14));
            editButton.setOnAction(event -> {
                TextInputDialog dialog = new TextInputDialog(tag.getName());
                dialog.setTitle("Edit title of tag");
                dialog.setHeaderText("Enter your new tag title below");
                dialog.setGraphic(null);
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(newTitle -> {
                    server.editTag(tag.getId(), newTitle);
                    title.setText(newTitle);
                });
                server.send("/topic/cards", new Card("", ""));
            });
            Button editColorButton = new Button("Color", img.generateIcon("pen-solid.png", 14));
            editColorButton.setOnAction(event -> {
                Dialog dialog = new Dialog<>();
                dialog.setTitle("Edit color of tag");
                ColorPicker cp = new ColorPicker();
                cp.setValue(Color.valueOf(tag.getColor()));
                dialog.getDialogPane().setContent(cp);
                ButtonType addTagType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(addTagType);
                dialog.setResultConverter(dialogButton -> {
                    server.editTagColor(tag.getId(), cp.getValue().toString());
                    return null;
                });
                dialog.showAndWait();
                server.send("/topic/cards", new Card("", ""));
            });
            Button removeButton = new Button("", img.generateIcon("trash-solid.png", 14));
            removeButton.getStyleClass().add("icon-button");
            removeButton.setOnAction(event -> {
                server.removeTag(tag.getId(), this.mainCtrl.getBoardId());
                server.send("/topic/cards", new Card("", ""));
            });
            hBox.getChildren().addAll(color, title, editButton, editColorButton, removeButton);
            hBox.setSpacing(15);
            tagList.getChildren().add(hBox);
        }
    }

}
