package client.scenes;

import client.records.ClientData;
import client.utils.ImageUtils;
import client.utils.PasswordDialog;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HomeBoardCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final ImageUtils img;

    private long boardId;

    private Board myBoard;

    @FXML
    private Text boardTitle;
    @FXML
    private AnchorPane anchor;

    @FXML
    private HBox cardHolder;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private AnchorPane firstList;

    @FXML
    private Label taskProgression;

    private List<VBox> lists;

    private VBox child;

    private boolean needsWebsocket;


    @FXML
    private MenuButton lockMenu;

    private boolean isListColored = false;

    private boolean isLocked;

    private final DataFormat cardFormat = new DataFormat("commons.src.main.java.commons.Card");


    private final ClientData clientData;

    /**
     * Home board controller constructor
     *
     * @param server server utilities
     * @param mainCtrl main controller
     * @param imageUtils image utilities
     */
    @Inject
    public HomeBoardCtrl(ServerUtils server, MainCtrl mainCtrl, ImageUtils imageUtils) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.img = imageUtils;
        this.clientData = ClientData.getInstance();
        lists = new ArrayList<VBox>();
    }

    /**
     * This method registers the websocket for the cards
     */
    public void registerWebsocket() {
        if (server.isServerSet()) {
            this.needsWebsocket = true;
            server.registerForMessages("/topic/cards", Card.class, c -> {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("WEBSOCKET MESSAGE RECEIVED");
                        refresh();
                    }
                });
            });

            server.registerForMessages("/topic/lock", Boolean.TYPE, c -> {
                Platform.runLater(() -> {
                    System.out.println("WEBSOCKET MESSAGE RECEIVED");
                    isLocked = c;
                    updateLockMenu(myBoard, true);
                });
            });

        }
    }

    /**
     * This method gets called whenever the overview (or homeboard) gets loaded.
     * It will find all the lists on the server, and then for each list,
     * find all the connected cards and display them
     * it will display them on the overview scene
     */
    public void refresh() {
        this.mainCtrl.refreshTagOverview();
        cardHolder.getChildren().clear();
        myBoard = server.getBoardById(boardId);
        updateLockMenu(myBoard, false);
        List<CardList> cardLists = myBoard.getCardLists();
        colorPicker.setValue(refreshColor(getBoardColor()));
        for (CardList cardList : cardLists) {
            VBox pane = new VBox(15);
            pane.setPadding(new Insets(10, 10, 10, 10));
            pane.setAlignment(Pos.TOP_CENTER);
            pane.setPrefWidth(250);
            setupDragTarget(pane, cardList.getId());
            this.lists.add(pane);

            GridPane titleGrid = createTitleHBox(cardList.getTitle(), cardList.getId(), boardId);
            pane.getChildren().addAll(titleGrid, new Separator(Orientation.HORIZONTAL));

            VBox vBox = new VBox(15);
            List<Card> cards = cardList.getCards();

            for (Card c : cards) {
                GridPane grid = createCardGrid(c.getTitle(), c.getId(), c.getTags());
                setupDragTarget(grid,cardList.getId(),c.getId());
                grid.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        if(event.getCode().toString().equals("E"))
                            System.out.println("E");
                        Button b = (Button)grid.getChildren().get(1);
                        b.fire();
                        if(event.getCode().toString().equals("BACK-SPACE"))
                            System.out.println(event);
                        Button c = (Button)grid.getChildren().get(2);
                        c.fire();
                    }});
                vBox.getChildren().add(grid);
            }

            Button add = addCardButton(cardList);
            pane.getChildren().addAll(vBox, add);
            cardHolder.getChildren().addAll(new Separator(Orientation.VERTICAL), pane);
        }
        cardHolder.getChildren().remove(child);
        updateTasksProgress();
    }

    /**
     * A method that updates the progress of all the tasks of the cards on the board
     */
    public void updateTasksProgress() {
        Board board = server.getBoardById(boardId);
        int total = 0;
        int completed = 0;

        for(CardList cardList: board.getCardLists()){
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

        taskProgression.setText("Task progression: " + completed + "/" + total);
    }

    private void updateLockMenu(Board board, boolean websocketTriggered) {
        lockMenu.setGraphic(
                img.generateIcon(isLocked ? "lock-solid.png" : "lock-open-solid.png", 16)
        );
        lockMenu.getItems().clear();
        if (board.hasPassword()) {
            lockMenu.getItems().addAll(
                    new MenuItem(isLocked ? "Unlock the board" : "Lock the board"),
                    new MenuItem("Edit password"),
                    new MenuItem("Remove password")
            );

            lockMenu.getItems().get(0).setOnAction(e -> {
                if (mainCtrl.hasAdminRights() || !isLocked || verifyPassword()) {
                    isLocked = !isLocked;
                    updateLockMenu(board, false);
                }
            });

            lockMenu.getItems().get(1).setOnAction(e -> {
                if (mainCtrl.hasAdminRights() || verifyPassword()) {
                    editBoardPassword(true);
                }
            });

            lockMenu.getItems().get(2).setOnAction(e -> {
                if (mainCtrl.hasAdminRights() || verifyPassword()) {
                    removePassword();
                }
            });
        } else {
            lockMenu.getItems().add(new MenuItem("Add password"));

            lockMenu.getItems().get(0).setOnAction(e -> {
                editBoardPassword(false);
            });
        }

        if (!websocketTriggered) server.send("/topic/lock", isLocked);
        clientData.addVisitedBoard(boardId, isLocked);
    }

    /**
     * This method changes the color of the rootPane, but it can be extended to other panes
     * @param event This is the color which will be picked from the ColorPicker
     */
    @FXML
    public void changeColor(ActionEvent event){
        Color color = colorPicker.getValue();
        server.updateColor(boardId, color.toString());
        if(isListColored == true)
                cardHolder.setBackground(new Background
                (new BackgroundFill(colorScheme(getBoardColor()),null,null)));
        anchor.setBackground(new Background(new BackgroundFill(color,null,null)));
        server.send("/topic/cards", new Card("",""));

    }

    /**
     * activates color Scheme
     * @param event
     */
    @FXML
    public void activateColorScheme(ActionEvent event){
        if(!isListColored) {
            isListColored = true;
            cardHolder.setBackground(new Background(
                new BackgroundFill(colorScheme(getBoardColor()),null,null)));
            server.send("/topic/cards", new Card("",""));
        }
        else isListColored = false;
        cardHolder.setBackground(Background.EMPTY);
        server.send("/topic/cards", new Card("",""));
    }

    /**
     * This method loads the color of the board
     * @param color it's a customized color stored in the Board Object
     * @return return the customized color
     */
    public Color refreshColor(String color){
        Color setColor = Color.rgb(Integer.parseInt(color.substring(2, 4), 16),
                Integer.parseInt(color.substring(4,6),16),
                Integer.parseInt(color.substring(6,8),16));
        anchor.setBackground(new Background(new BackgroundFill(setColor,null,null)));
        if(isListColored)
                cardHolder.setBackground(new Background(new BackgroundFill(
                colorScheme(getBoardColor()),null,null)));
        return setColor;
    }

    /**
     * Function for creating a card
     *
     * @param title     The name of the card
     * @param id        The id of the card
     * @param tags      The tags of the card
     * @return          A card with the title
     */
    public GridPane createCardGrid(String title, long id, List<Tag> tags) {
        Label cardTitle = new Label(title);
        Card c = server.getCard(id);
        Button editButton = new Button("Edit", img.generateIcon("pen-solid.png", 14));
        editButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        editButton.getStyleClass().add("icon-button");
        editButton.setOnAction(event -> {
            if (isLocked && !mainCtrl.hasAdminRights()) {
                passwordProtectedAlert();
                return;
            }
            mainCtrl.showEditCard(server.getCard(id));
            server.send("/topic/cards", new Card("",""));
        });

        Button removeButton = new Button("Remove", img.generateIcon("trash-solid.png", 14));
        removeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        removeButton.getStyleClass().add("icon-button");
        removeButton.setOnAction(event -> {
            event.consume();
            if (isLocked && !mainCtrl.hasAdminRights()) {
                passwordProtectedAlert();
                return;
            }
            cardRemovalWarning(removeButton,id);
        });

        GridPane grid = new GridPane();
        grid.getStyleClass().add("card-box");
        ColumnConstraints titleColumn = new ColumnConstraints();
        titleColumn.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().add(titleColumn);
        grid.add(cardTitle, 0, 0);
        grid.add(editButton, 3, 0);
        grid.add(removeButton, 4, 0);

        if(c != null){
            if(!(c.getDescription().equals("") || c.getDescription() == null)){
                Button desc = new Button("", img.generateIcon("description.png", 14));
                desc.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                desc.getStyleClass().add("icon-button");
                grid.add(desc, 2, 0);
            }
            Label taskAmount = cardTasks(id);
            grid.add(taskAmount, 1, 0);
        }

        if(tags != null && tags.size() > 0) {
            Label colorArea = new Label("                            ");
            Color colorOfTag = Color.valueOf(tags.get(0).getColor());

            colorArea.setBackground(new Background(new BackgroundFill(colorOfTag,null,null)));
            colorArea.setMaxHeight(3);

            grid.add(colorArea, 0, 1, 3, 1);
        }
        setUpDragSource(grid, id);
        grid.setOnMouseClicked(event -> enterCard(id, event));
        return grid;
    }

    /**
     * A method that creates a label indicating the status of the tasks of the card
     * @param cardID the id of the card
     * @return the newly made label
     */
    public Label cardTasks(long cardID) {
        Card card = server.getCard(cardID);
        int total = 0;
        int completed = 0;

        List<Task> tasks = card.getTasks();
        for(Task task: tasks){
            if(task.isStatus()){
                completed++;
            }
            total++;
        }
        return new Label(completed + "/" + total);


    }

    private void enterCard(long id, MouseEvent event){
        if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
            mainCtrl.showCardOverview(id);
        }
    }

    /**
     * Function for creating a title
     *
     * @param title     The name of the title
     * @param listId        The id of the title
     * @param boardId       The id of the board
     * @return          A title with appropriate buttons
     */
    public GridPane createTitleHBox(String title, long listId, long boardId) {
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font(16));

        Button editButton = new Button("Edit", img.generateIcon("pen-solid.png", 14));
        editButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        editButton.getStyleClass().add("icon-button");
        editButton.setOnAction(event -> {
            if (isLocked && !mainCtrl.hasAdminRights()) {
                passwordProtectedAlert();
                return;
            }
            TextInputDialog dialog = new TextInputDialog(title);
            dialog.setTitle("Edit title of cardList");
            dialog.setHeaderText("Enter your new card title below");
            dialog.setGraphic(null);
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newTitle -> {
                CardList editedCardList = server.editListTitle(listId, newTitle);
                titleLabel.setText(newTitle);
            });
            server.send("/topic/cards", new Card("",""));
        });

        Button removeButton = new Button("Remove", img.generateIcon("trash-solid.png", 14));
        removeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        removeButton.getStyleClass().add("icon-button");
        removeButton.setOnAction(event -> {
            event.consume();
            if (isLocked && !mainCtrl.hasAdminRights()) {
                passwordProtectedAlert();
                return;
            }
            listRemovalWarning(removeButton,listId,boardId);
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setPadding(new Insets(10, 10, 0, 10));

        ColumnConstraints titleColumn = new ColumnConstraints();
        titleColumn.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().add(titleColumn);
        grid.add(titleLabel, 0, 0);
        grid.add(editButton, 1, 0);
        grid.add(removeButton, 2, 0);

        return grid;
    }

    /**
     *
     * @param label this is the minimum unit which can be dragged
     * This method will set up each label to be draggable, a new label will be created
     * and the old label will be removed, list information will be updated in the database
     */
    void setUpDragSource(final GridPane label, long id){
        label.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (isLocked && !mainCtrl.hasAdminRights()) {
                    passwordProtectedAlert();
                    return;
                }

                Dragboard db = label.startDragAndDrop(TransferMode.ANY);
                db.setDragView(label.snapshot(null, null), event.getX(), event.getY());
                ClipboardContent content = new ClipboardContent();

                Label l = (Label) label.getChildren().get(0);
                content.putString(l.getText());
                content.put(cardFormat, server.getCard(id));

                server.removeCard(id);

                db.setContent(content);
                event.consume();
            }
        });

        /**
         * Method called when the drag is done
         */
        label.setOnDragDone(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                VBox cardContainer = (VBox) label.getParent();
                cardContainer.getChildren().remove(label);
                event.consume();
            }
        });


    }

    /**
     *
     * @param color the wanted color by user
     * @return Color to be implemented with the lists
     */
    public Color colorScheme(String color)
    {
        int r = Integer.parseInt(color.substring(2, 4), 16);
        int g = Integer.parseInt(color.substring(4,6),16);
        int b = Integer.parseInt(color.substring(6,8),16);

        if(r>=g&&r>=b)
        {
            if(r<=120)
            {
                r = r + (255 - r) / 7;
                g = g + (255 - g) / 10 * 4;
            }
            else {
                r = r + (255 - r) / 7;
                g = g + (255 - g) / 5 * 4;
                b = b + (255 - b) / 5 * 4;
            }
        }
        else if (g>=r&&g>=b)
        {

            if(g<=120)
            {
                g = g + (255 - g) / 7;
                b = b + (255 - b) / 10 * 4;
            }
            else {
                g = g + (255 - g) / 7;
                r = r + (255 - r) / 5 * 4;
                b = b + (255 - b) / 5 * 4;
            }
        }
        else
        {
            if(b<=120)
            {
                b = b + (255 - b) / 7;
                r = r + (255 - r) / 10 * 4;
            }
            else {
                b = b + (255 - b) / 7;
                r = r + (255 - r) / 5 * 4;
                g = g + (255 - g) / 5 * 4;
            }
        }

        System.out.println(r+""+g+""+b);
        return Color.rgb(r,g,b);
    }

    /**
     * This method creates an 'add card' button
     * @param cardList the cardList the cards should be added
     * @return the button
     */
    public Button addCardButton(CardList cardList){
        Button add = new Button("Add card");
        add.getStyleClass().add("action-button");

        add.setOnAction(event -> {
            if (isLocked && !mainCtrl.hasAdminRights()) {
                passwordProtectedAlert();
                return;
            }
            addCard(cardList);
        });

        return add;
    }

    /**
     * @param pane It's a list containing cards, it can be anything not just pane
     * This function gets called whenever a user presses the 'add card' button.
     * It will send the user to the scene where they can add a card
     */
    void setupDragTarget(final VBox pane, Long listId) {
        pane.setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                event.consume();
            }
        });

        /**
         * * This method is called when the item is dropped into the target
         */
        pane.setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                Card card = (Card) db.getContent(cardFormat);

                GridPane grid = createCardGrid(card.getTitle(), card.getId(), card.getTags());

                server.updateList(listId, card);
                VBox children = (VBox) pane.getChildren().get(2);
                children.getChildren().add(grid);

                server.send("/topic/cards", new Card("",""));

                event.setDropCompleted(true);
                event.consume();
            }

        });
    }
    void setupDragTarget(final GridPane pane, Long listId, Long targetId) {
        pane.setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                event.consume();
            }
        });

        /**
         * * This method is called when the item is dropped into the target
         */
        pane.setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                Card card = (Card) db.getContent(cardFormat);
                GridPane grid = createCardGrid(card.getTitle(), card.getId(), card.getTags());
                int index = server.getList(listId).getCards().indexOf(server.getCard(targetId));
                if (index == -1) index = 0;
                System.out.println(index);
                server.reorderList(listId,index,card);
                refresh();

                event.setDropCompleted(true);
                event.consume();
                server.send("/topic/cards", new Card("",""));

            }

        });
    }

    /**
     * Sets boardId field.
     * Used when changing from boards in the boards overview
     * @param id id of the new board
     */
    public void setBoardId(long id) {
        this.boardId = id;
    }

    /**
     * Set the current board color
     * @param color customized color
     */
    public void setBoardColor(String color){
        server.getBoardById(boardId).setColor(color);
    }

    /**
     * This method gets the color of the board from the database
     * @return a string witch is the color of the board saved in the database
     */
    public String getBoardColor()
    {
        return server.getBoardById(boardId).getColor();
    }

    /**
     * Gets the id of the current board that is being viewed
     * @return board id
     */
    public long getBoardId(){
        return boardId;
    }

    /**
     * Gets the title of the board that is to be viewed
     * @return title of the board
     */
    public String getBoardTitle(){
        return server.getBoardById(boardId).getTitle();
    }

    /**
     * This function gets called whenever a user presses the 'add card' button.
     * It will send the user to the scene where they can add a card
     * @param cardList the card list that the card will be added to
     */
    public void addCard(CardList cardList) {
        this.mainCtrl.setCardList(cardList);
        this.mainCtrl.showAddCard();
    }

    /**
     * This function gets called whenever a user presses the 'add tag' button.
     */
    public void showTags() {
        if (isLocked && !mainCtrl.hasAdminRights()) {
            passwordProtectedAlert();
            return;
        }
        this.mainCtrl.showTagOverview();
    }


    /**
     * This function gets called when the 'add list' button is pressed,
     * and redirect the user to the needed screen.
     */
    public void addList() {
        if (isLocked && !mainCtrl.hasAdminRights()) {
            passwordProtectedAlert();
            return;
        }
        this.mainCtrl.showAddList();
    }

    /**
     * Changes scene to the boards overview
     */
    public void exit(){
        cardHolder.getChildren().clear();
        clientData.saveToFile();
        this.mainCtrl.showOverview();
    }

    /**
     * Sets the title of the board
     * @param title title of the board
     */
    public void setBoardTitle(String title){
        this.boardTitle.setText(title);
    }
    /**
     *  Sets needsWebSocket to false
     */
    public void needsWebSocketFalse(){
        needsWebsocket = false;
    }

    /**
     * This method prevents the user from removing the list accidentally and generates a warning
     * @param button this list removal button
     * @param listId   the id of the current list
     * @param boardId   the id of the current board
     */
    public void listRemovalWarning(Button button, long listId, long boardId ){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove List");
        alert.setHeaderText("Remove "+server.getList(listId).getTitle());
        alert.setContentText("Are you sure you want to remove this List?\n" +
                "It will remove all the remaining cards.");


        if(alert.showAndWait().get() == ButtonType.OK)
        {
            server.removeList(listId, boardId);
            server.send("/topic/cards", new Card("",""));
            server.send("/topic/tasks", null);
            System.out.println("removed");
        }
    }

    /**
     * This method prevents the user from removing the card
     * accidentally and generates a confirmation warning
     * @param button this card removal button
     * @param id the id of the card
     */
    public void cardRemovalWarning(Button button, long id ){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Card");
        alert.setHeaderText("Remove "+ server.getCard(id).getTitle());
        alert.setContentText("Are you sure you want to remove this Card?\n");


        if(alert.showAndWait().get() == ButtonType.OK)
        {
            server.removeCard(id);
            server.send("/topic/cards", new Card("",""));
            server.send("/topic/tasks", null);
            System.out.println("removed");
        }
    }

    /**
     * This method gets triggered when the user presses the edit board password button,
     * and it will open a dialog where the user can insert a new password
     *
     * @param hasPassword indicates whether a board already has a password
     */
    public void editBoardPassword(boolean hasPassword) {
        Optional<String> result = new PasswordDialog(
                hasPassword ? "Edit Password" : "Add Password",
                "Insert a new password for this board",
                "Password:",
                "Password"
        ).showAndWait();

        result.ifPresent(password -> {
            myBoard.setPassword(password);
            server.editBoardPassword(myBoard.getId(), password);
            isLocked = !password.isEmpty();
            clientData.addVisitedBoard(boardId, isLocked);
            refresh();
        });
    }

    /**
     * Removes the password from the board
     */
    private void removePassword() {
        myBoard.setPassword("");
        server.editBoardPassword(myBoard.getId(), "");
        isLocked = false;
        refresh();
    }

    /**
     * Checks if the password is correct
     *
     * @return true if the password is correct, false otherwise
     */
    private boolean verifyPassword() {
        Optional<String> result = new PasswordDialog("Enter Password",
                                                       "This board is password protected",
                                                       "Please enter the password:",
                                                       "Password")
                .showAndWait();
        return result.map(s -> myBoard.validatePassword(s)).orElse(false);
    }

    /**
     * Sets the isLocked boolean
     *
     * @param isLocked boolean to set isLocked to
     */
    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    /**
     * @return true if the board is locked, false otherwise
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Shows an alert if the board is password protected
     */
    public void passwordProtectedAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("This board is password protected");
        alert.setContentText("Please enter the correct password to access this board");
        alert.showAndWait();
    }

}