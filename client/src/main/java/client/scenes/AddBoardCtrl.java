package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.util.Timer;
import java.util.TimerTask;

public class AddBoardCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private AnchorPane anchor;

    @FXML
    private TextField titleTextField;

    @FXML
    private Text emptyTitle;

    /**
     * Constructs an AddBoardCtrl object.
     * @param server server utilities
     * @param mainCtrl main controller
     */
    @Inject
    public AddBoardCtrl(ServerUtils server, MainCtrl mainCtrl){
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Adds the board to the database.
     * This method is called when the "Add" button is clicked.
     */
    public void add() {
        String title = this.titleTextField.getText();
        if (title.equals(""))
            setEmptyTitle();
        else {
            Board board = new Board(title, null);
            server.addBoard(board);
            server.send("/topic/boards", null);
            titleTextField.clear();
            this.mainCtrl.showOverview();
        }
    }

    /**
     * Shows an error message when a board with an empty title is tried to be added.
     */
    public void setEmptyTitle() {
        emptyTitle.setText("No empty title allowed");
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                emptyTitle.setText("");
            }
        };
        timer.schedule(task, 2000);
    }

    /**
     * Shows the overview with all the boards.
     * This method is called when the "Cancel" button is clicked.
     */
    public void cancel() {
        this.mainCtrl.showOverview();
    }

}
