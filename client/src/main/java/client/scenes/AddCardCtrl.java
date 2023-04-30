package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.util.*;

public class AddCardCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private CardList editedCardList;

    @FXML
    private TextField titleTextField;

    @FXML
    private Text emptyTitle;

    @FXML
    private TextArea descriptionText;

    @FXML
    private Button addButton;

    @FXML
    private HBox tasks;

    @FXML
    private HBox tagsBox;

    private long cardId = -1;

    private List<Task> taskList;
    private List<VBox> vboxes;

    private List<Tag> tagSet = new ArrayList<>();

    /**
     * Add card controller constructor
     *
     * @param server server utilities
     * @param mainCtrl main controller
     */
    @Inject
    public AddCardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * This method is only called when a card is edited, it sets up the text fields with
     * the corresponding title and description of the card. It also changes the action performed
     * by the "add" button
     * @param card card to be edited
     */
    public void editInit(Card card){
        if(addButton != null){
            addButton.setOnAction(event -> {
                editCard(card);
            });
            addButton.setText("Edit");
        }

        this.cardId = card.getId();

        if(titleTextField == null) titleTextField = new TextField();
        titleTextField.setText(card.getTitle());
        if(descriptionText == null) descriptionText = new TextArea();
        descriptionText.setText(card.getDescription());
        taskList = card.getTasks();
        this.tagSet = card.getTags();
        //System.out.println("THIS CARDS HAS SET SIZE " + card.getTags().size());
        refreshTasks();
        refreshTags();
    }

    /**
     * This function gets called whenever a user presses the 'add a card' button
     * It checks whether the title given is empty. If it is empty it will show an error,
     * otherwise it will upload the card to the server and show the overview again
     */
    public void add() {
        String title = this.titleTextField.getText();
        String description = this.descriptionText.getText();
        if(title.equals(""))
            setEmptyTitle();
        else {
            CardList cl = server.getList(2);
            Card card = new Card(title, description, cl, taskList, tagSet);
            System.out.println("CARDLISTID = " + editedCardList.getId());
            System.out.println("CARD = " + card.getTitle() + " "  + card.getId());

            server.updateList(editedCardList.getId(), card);
            server.send("/topic/cards", card);
            titleTextField.setText("");
            descriptionText.clear();
            tasks.getChildren().clear();
            tagsBox.getChildren().clear();
            mainCtrl.needsWebSocketFalse();
            if(taskList!= null){
                taskList.clear();
                server.send("/topic/boards", new Board("", null));
            }
            cardId = -1;
            this.mainCtrl.showBoard();
        }

    }

    /**
     * This will be called when the user clicks the "edit" button.
     * It updates the card in the database, and restores the scene and button to their
     * original state.
     * @param card card to be edited
     */
    private void editCard(Card card){
        String title = this.titleTextField.getText();
        String description = this.descriptionText.getText();
        for (int i = 0; i < vboxes.size(); i++) {
            VBox v = vboxes.get(i);
            for (int j = 0; j < v.getChildren().size(); j++) {
                HBox hBox = (HBox) v.getChildren().get(j);
                CheckBox box = (CheckBox) hBox.getChildren().get(0);
                boolean checked = box.isSelected();
                taskList.get(i * 3 + j).setStatus(checked);
            }
        }

        if(title.equals(""))
            setEmptyTitle();
        else {
            server.editCardTitle(card.getId(), title);
            server.editCardDescription(card.getId(), description);
            server.editCardTasks(card.getId(), taskList);
            server.editTagsCard(card.getId(), tagSet);
            taskList = new ArrayList<>();
            tagSet = new ArrayList<>();
            titleTextField.clear();
            descriptionText.clear();
            refreshTasks();
            refreshTags();
        }

        addButton.setOnAction(event -> add());
        addButton.setText("Add");
        server.send("/topic/tasks", null);
        cardId = -1;
        this.mainCtrl.showBoard();
        server.send("/topic/cards", new Card("",""));
        server.send("/topic/boards", new Board("", null));
    }

    /**
     * This function adds the tag functionality to the add card screen
     */
    public void addTags() {

        System.out.println();
        VBox checkBoxesHBOX = new VBox();
        List<Tag> boardTags = server.getTags(this.mainCtrl.getBoardId());
        List<Tag> currentTags = this.tagSet;
        if(this.cardId != -1)
            currentTags = server.getTagsOfCard(this.cardId);
        Pair<CheckBox, Tag>[] pairs = new Pair[boardTags.size()];

        addCheckboxesToTag(boardTags, currentTags, pairs, checkBoxesHBOX);

        this.tagSet = new ArrayList<>();
        Dialog dialog = new Dialog<>();

        Button tagOverviewButton = new Button("Go to tag overview");
        tagOverviewButton.setOnAction(event -> {
            this.mainCtrl.showTagOverview();
            dialog.close();
        });
        HBox tagOverviewHbox = new HBox(tagOverviewButton);
        checkBoxesHBOX.getChildren().add(tagOverviewHbox);
        dialog.getDialogPane().setContent(checkBoxesHBOX);
        ButtonType addTagType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addTagType);
        dialog.setResultConverter(dialogButton -> {
            if(dialogButton == addTagType) {
                for(int i = 0; i < pairs.length; i++) {
                    if(pairs[i].getKey().isSelected()) {
                        this.tagSet.add(pairs[i].getValue());
                    }
                }
            }
            this.cardId = -1;
            refreshTags();
            return null;
        });
        dialog.showAndWait();
    }

    /**
     * This function adds the checkboxes to a tag overview
     * @param boardTags list of tags
     * @param currentTags the current tags of the board
     * @param checkBoxesHBOX the hbox with checkboxes
     * @param pairs the pair of checkboxes and their tag
     */
    public void addCheckboxesToTag(List<Tag> boardTags,
                                   List<Tag> currentTags,
                                   Pair<CheckBox, Tag>[] pairs,
                                   VBox checkBoxesHBOX) {
        for(int i = 0; i < boardTags.size(); i++) {
            HBox hBox = new HBox();
            CheckBox checkBox = new CheckBox(boardTags.get(i).getName());
            boolean isSelected = false;
            for(Tag t : currentTags) {
                if (t.getName().equals(boardTags.get(i).getName())) {
                    isSelected = true;
                }
            }
            checkBox.setSelected(isSelected);
            pairs[i] = new Pair(checkBox, boardTags.get(i));
            Label colorArea = new Label("               ");
            Color colorOfTag = Color.valueOf(boardTags.get(i).getColor());
            colorArea.setBackground(new Background(new BackgroundFill(colorOfTag,null,null)));
            hBox.setSpacing(15);
            hBox.setMinHeight(30);
            hBox.getChildren().addAll(colorArea, checkBox);
            checkBoxesHBOX.getChildren().add(hBox);
        }
    }

    /**
     * This method gets called whenever the tags of a card needs to be refreshed
     */
    public void refreshTags() {
        this.tagsBox.getChildren().clear();

        for(Tag t : this.tagSet) {
            Label name = new Label(t.getName());
            Label color = new Label("     ");
            Color colorOfTag = Color.valueOf(t.getColor());
            color.setBackground(new Background(new BackgroundFill(colorOfTag,null,null)));

            HBox hb = new HBox(8, name, color);

            this.tagsBox.getChildren().add(hb);
        }

    }

    /**
     * This function gets called whenever a user tries to upload a card without a title.
     * It shows the user an error message which will disappear after 2 seconds
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
     * This method is called when pressing the 'add task' button
     * and prompts the user to put in their task
     */
    public void addTask(){
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Edit title of task");
        dialog.setHeaderText("Enter your new task below");
        dialog.setGraphic(null);
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(title -> {
            Task task = new Task(title);
            try{
                taskList.add(task);
                server.addTask(task);
            }
            catch (NullPointerException e){
                taskList = new ArrayList<>();
                taskList.add(task);
                server.addTask(task);
            }
            refreshTasks();
        });


    }

    /**
     * This method is called each time a task is added, removed or edited
     * as well as when the scene is loaded and shows all the tasks as checkboxes
     */
    public void refreshTasks(){
        tasks.getChildren().clear();
        vboxes = new ArrayList<>();

        for (int i = 0; i < taskList.size() / 3 + 1; i++) {
            VBox vBox = new VBox(1);
            vboxes.add(vBox);
        }
        for(int i = 0; i < taskList.size(); i++) {
            Task t = taskList.get(i);
            CheckBox checkBox = new CheckBox(t.getTitle());
            checkBox.setSelected(t.isStatus());
            HBox box = createCardHbox(checkBox, i);
            box.setLayoutX(50);
            vboxes.get(i / 3).getChildren().add(box);

        }
        for(VBox v: vboxes){
            this.tasks.getChildren().add(v);
            HBox.setHgrow(tasks, Priority.ALWAYS);
        }

    }

    /**
     * This method creates a hbox with a checkbox along with a button to edit and a button to remove
     * @param checkBox the checkbox
     * @param index the index of the task
     * @return the new hbox
     */
    public HBox createCardHbox(CheckBox checkBox, int index) {

        Button editButton = new Button("Edit");
        editButton.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog(checkBox.getText());
            dialog.setTitle("Edit title of the task");
            dialog.setHeaderText("Enter your new task title below");
            dialog.setGraphic(null);
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newTitle -> {
                taskList.get(index).setTitle(newTitle);
                refreshTasks();
                refreshTags();
            });
        });

        Button removeButton = new Button("Re");
        removeButton.setOnAction(event -> {
            taskList.remove(index);
            refreshTasks();
            refreshTags();
        });


        HBox hBox = new HBox(8, checkBox, editButton, removeButton);

        hBox.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                BorderWidths.DEFAULT)));
        return hBox;
    }

    /**
     * This function gets called whenever a user cancels the 'add card' functionality
     * The user will be sent back to the overview
     */
    public void cancel() {
        taskList = new ArrayList<>();
        tagSet = new ArrayList<>();
        titleTextField.clear();
        descriptionText.clear();
        refreshTasks();
        refreshTags();
        this.addButton.setText("Add");
        this.addButton.setOnAction(event -> add());
        cardId = -1;
        this.mainCtrl.showBoard();
    }

    /**
     * Sets editedCardList, this is used when adding a new card to the card list
     * @param toBeEditedCardList CardList that will be added to
     */
    public void setEditedCardList(CardList toBeEditedCardList) {
        this.editedCardList = toBeEditedCardList;
    }

    /**
     * A getter for the title text field, for testing purposes
     * @return the textfield
     */
    public TextField getTitleTextField() {
        return titleTextField;
    }

    /**
     * A getter for the description text field, for testing purposes
     * @return the textfield
     */
    public TextArea getDescriptionText() {
        return descriptionText;
    }

    /**
     * A getter for the tagset, for testing purposes
     * @return the list
     */
    public List<Tag> getTagSet() {
        return tagSet;
    }

    /**
     * Registers this controller to receives websocket messages
     */
    public void registerWebsockets() {
        server.registerForMessages("/topic/tasks", Card.class, c -> {
            Platform.runLater(() -> {
                if(cardId != -1){
                    if (c != null && c.getTitle().equals("Board")) deletedBoard();

                    else deletedCard();
                }
            });
        });
    }

    /**
     * A setter for testing purposes
     * @param titleTextField the textField
     */
    public void setTitleTextField(TextField titleTextField) {
        this.titleTextField = titleTextField;
    }

    /**
     * A setter for testing purposes
     * @param descriptionText the textArea
     */
    public void setDescriptionText(TextArea descriptionText) {
        this.descriptionText = descriptionText;
    }

    /**
     * A setter for testing purposes
     * @param emptyTitle the text
     */
    public void setEmptyTitle(Text emptyTitle) {
        this.emptyTitle = emptyTitle;
    }

    /**
     * A getter for testing purposes
     * @return the cardlist
     */
    public CardList getEditedCardList() {
        return editedCardList;
    }

    /**
     * A setter for testing purposes
     * @param tasks the hbox
     */
    public void setTasks(HBox tasks) {
        this.tasks = tasks;
    }

    /**
     * A getter for testing purposes
     * @return the hbox
     */
    public HBox getTasks() {
        return tasks;
    }
    /**
     * A getter for testing purposes
     * @return the hbox
     */
    public HBox getTagsBox() {
        return tagsBox;
    }

    /**
     * A setter for testing purposes
     * @param tagsBox the hbox
     */
    public void setTagsBox(HBox tagsBox) {
        this.tagsBox = tagsBox;
    }

    private void deletedCard(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle( "Card: " + titleTextField.getText());
        alert.setHeaderText("This card does not exist");
        alert.setContentText("It appears someone has deleted this card\n");

        alert.showAndWait();

        cancel();
    }

    private void deletedBoard(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle( "Card: " + titleTextField.getText());
        alert.setHeaderText("This card does not exist");
        alert.setContentText("It appears someone has deleted the board\n");

        alert.showAndWait();

        this.cardId = -1;
        mainCtrl.showOverview();
    }
}
