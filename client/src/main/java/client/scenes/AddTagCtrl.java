package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Card;
import commons.Tag;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class AddTagCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField titleTextField;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Text emptyTitle;

    @FXML
    private Button addButton;

    /**
     * AddTagCtrl constructor
     *
     * @param server server utilities
     * @param mainCtrl main controller
     */
    @Inject
    public AddTagCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * This function gets called whenever a user wants to add a tag to the tag overview
     */
    public void addTag() {
        if(this.titleTextField.getText().equals(""))
            this.emptyTitle.setText("Set title");
        else {
            Tag tag = new Tag(this.titleTextField.getText(),
                    this.colorPicker.getValue().toString());
            server.addTag(tag, this.mainCtrl.getBoardId());
            this.titleTextField.clear();
            this.colorPicker.setValue(new Color(0,0,0,1));
            this.mainCtrl.showTagOverview();
            server.send("/topic/cards", new Card("",""));
        }
    }

    /**
     * This function gets called whenever a user cancel an add tag scene
     */
    public void cancel() {
        this.mainCtrl.showTagOverview();
    }




}
