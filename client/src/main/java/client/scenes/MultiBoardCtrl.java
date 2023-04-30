package client.scenes;

import client.utils.ImageUtils;
import client.utils.PasswordDialog;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import commons.Card;
import commons.CardList;
import commons.Task;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.util.Pair;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class MultiBoardCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final ImageUtils img;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox boardList;

    @FXML
    private Label serverLabel;

    @FXML
    private Label adminLabel;

    /**
     * Constructs a MultiBoardCtrl object.
     * @param server server utilities
     * @param mainCtrl the main controller
     * @param imageUtils image utilities
     */
    @Inject
    public MultiBoardCtrl(ServerUtils server, MainCtrl mainCtrl, ImageUtils imageUtils) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.img = imageUtils;
    }

    /**
     * Initializes the overview and
     * binds the width of the board list to the width of the scroll pane.
     */
    public void initialize() {
        boardList.prefWidthProperty().bind(scrollPane.widthProperty());
    }

    /**
     * Refreshes the overview.
     */
    public void refresh(){
        boardList.getChildren().clear();
        List<Board> boards = server.getBoards();

        for(Board b: boards) addToBoardList(b);
    }

    private void addToBoardList(Board board){
        Label boardTitle = new Label(board.getTitle());

        Button editButton = img.generateIconButton("pen-solid.png", 14, "Edit board's name");
        editButton.setOnAction(event -> renameBoard(board.getId()));

        Button removeButton = img.generateIconButton("trash-solid.png", 14, "Remove board");
        removeButton.setOnAction(event -> removeBoard(board.getId()));

        Pair<Integer, Integer> completedAndTotal = calculateCompletedAndTotal(board);

        Label amountOfTasks = new Label(
                completedAndTotal.getKey() + "/" + completedAndTotal.getValue()
        );

        Button enterBoard = img.generateIconButton("right-to-bracket.png", 16, "Enter board");
        enterBoard.setOnAction(event -> enterBoard(board));

        Button copyKey = img.generateIconButton("key.png", 16, "Copy board's key");
        copyKey.setText("Copied!");
        copyKey.setTextFill(javafx.scene.paint.Color.GREEN);
        copyKey.setOnAction(event -> {
            copyKey(board);
            copyKey.setContentDisplay(ContentDisplay.TEXT_ONLY);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> copyKey.setContentDisplay(ContentDisplay.GRAPHIC_ONLY));
                }
            }, 1000);
        });

        GridPane grid = new GridPane();
        grid.getStyleClass().add("card-box");
        ColumnConstraints titleColumn = new ColumnConstraints();
        titleColumn.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(new ColumnConstraints(), titleColumn);
        grid.add(boardTitle, 0, 0);
        grid.add(amountOfTasks, 1, 0);
        grid.add(enterBoard, 2, 0);
        grid.add(editButton, 3, 0);
        grid.add(copyKey, 4, 0);
        grid.add(removeButton, 5, 0);
        boardList.getChildren().add(grid);
    }

    private Pair<Integer, Integer> calculateCompletedAndTotal(Board board){
        List<CardList> lists = board.getCardLists();
        int total = 0;
        int completed = 0;

        for(CardList cardList: lists){
            List<Card> cards = cardList.getCards();
            for(Card card: cards){
                List<Task> tasks = card.getTasks();
                for(Task task: tasks){
                    if(task.isStatus()){
                        completed++;
                    }
                    total++;
                }
            }
        }

        return new Pair<>(completed, total);
    }

    private void renameBoard(long id){
        TextInputDialog input = new TextInputDialog(server.getBoardById(id).getTitle());
        input.setHeaderText("Enter the new board name:");
        input.setTitle("Rename board");
        input.setContentText("Board name:");


        Optional<String> res = input.showAndWait();
        if(res.isEmpty()) return;

        String newTitle = res.get();
        server.renameBoard(newTitle, id);
        server.send("/topic/boards", null);
        refresh();
    }

    /**
     * Prompts the user with a confirmation alert before removing a board
     * @param id id of the board
     */
    private void removeBoard(long id){
        Board board = server.getBoardById(id);
        if (board.hasPassword() && !mainCtrl.hasAdminRights()) {
            PasswordDialog passwordDialog = new PasswordDialog("Enter Password",
                    "This board is password protected",
                    "Please enter the password:",
                    "Password");

            final Button okButton = passwordDialog.getOkButton();

            okButton.addEventFilter(ActionEvent.ACTION, ae -> {
                if (!board.validatePassword(passwordDialog.getPassword())) {
                    ae.consume(); // not valid
                    passwordDialog.invalidate("Invalid password");
                } else {
                    passwordDialog.getDialog().close();
                    removeBoardAlert(board);
                }
            });

            passwordDialog.getDialog().show();
        } else {
            removeBoardAlert(board);
        }
    }

    private void removeBoardAlert(Board board) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Board");
        alert.setHeaderText("Remove "+ board.getTitle());
        alert.setContentText("Are you sure you want to remove this Board?\n");

        Optional<ButtonType> res = alert.showAndWait();

        if(res.isPresent() && res.get() == ButtonType.OK)
        {
            server.removeBoard(board.getId());
            server.send("/topic/boards", null);
            server.send("/topic/tasks", new Card("Board", null));
            System.out.println("removed");
        }
    }

    /**
     * Handles the user entering a board.
     *
     * @param board the board to enter
     */
    private void enterBoard(Board board) {
        mainCtrl.setBoardIsLocked(board.hasPassword());

        mainCtrl.setBoardId(board.getId());
        mainCtrl.showBoard();
    }

    /**
     * Shows the add board scene.
     * This method is called when the "Add board" button is pressed.
     */
    public void add(){
        this.mainCtrl.showAddBoard();
    }

    /**
     * Disconnects the user from the server
     */
    public void disconnect() {
        mainCtrl.showChooseServer();
    }

    /**
     * Registers controller for websockets
     */
    public void registerForWebsockets(){
        server.registerForMessages("/topic/boards", Board.class, c ->{
            Platform.runLater(this::refresh);
        });
    }

    /**
     * Updates the server label
     */
    public void updateServerLabel() {
        serverLabel.setText("Currently connected to: " + server.getServerAddress());
    }

    private void copyKey(Board board){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(board.getKey());
        clipboard.setContent(content);
    }

    /**
     * Prompts the user to enter a board key and joins the board if it exists
     */
    public void joinBoard(){
        TextInputDialog input = new TextInputDialog();
        input.setHeaderText("Enter the board's invite key");
        input.setTitle("Join board");
        input.setContentText("Invite key:");


        Optional<String> res = input.showAndWait();
        if (res.isEmpty()) return;

        String key = res.get();
        Board board = server.getBoardByKey(key);
        if(board == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Board not found");
            alert.setContentText("The board with the key " + key + " could not be found");
            alert.showAndWait();
        } else {
            mainCtrl.setBoardId(board.getId());
            mainCtrl.showBoard();
        }
    }

    /**
     * Checks if the password is correct
     */
    public void verifyPassword() {
        PasswordDialog passwordDialog = new PasswordDialog("Enter Password",
                "Admin rights require a server password.",
                "Please enter the password:",
                "Password");

        final Button okButton = passwordDialog.getOkButton();

        okButton.addEventFilter(ActionEvent.ACTION, ae -> {
            if (!server.validateAdminPassword(passwordDialog.getPassword())) {
                ae.consume(); // not valid
                passwordDialog.invalidate("Invalid password");
            } else {
                mainCtrl.setAdminRights(true);
                passwordDialog.getDialog().close();
                adminLabel.setText("Admin rights granted");
            }
        });

        passwordDialog.getDialog().show();
    }

}
