package client.scenes;


import client.utils.ServerUtils;
import commons.Card;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import commons.CardList;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AddListCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private Text text;

    @FXML
    private TextField input;

    /**
     * The constructer for the controller
     * @param server the server
     * @param mainCtrl the mainContoller
     */
    @Inject
    public AddListCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * This method gets called then the 'add' button is pressed,
     * it first checks if there is a title input,
     * after that it creates a new empty list and adds it to the server
     */

    public void add(){
        String title = input.getText();
        if(title.equals("")){
            setEmptyTitle();
        }
        else{
            CardList cardList = new CardList(title, new ArrayList<>());
            server.addListToBoard(mainCtrl.getBoardId(), cardList);
            input.clear();
            this.mainCtrl.showBoard();
            server.send("/topic/cards", new Card("",""));
        }
    }

    /**
     * This function gets called whenever a user tries to upload a card without a title.
     * It shows the user an error message which will disappear after 2 seconds
     */
    public void setEmptyTitle() {
        text.setText("No empty title allowed");
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                text.setText("");
            }
        };
        timer.schedule(task, 2000);
    }

    /**
     * This method is called when the cancel button is pressed
     * and is just goes back to the main screen
     */
    public void cancel() {
        this.mainCtrl.showBoard();
    }
}
