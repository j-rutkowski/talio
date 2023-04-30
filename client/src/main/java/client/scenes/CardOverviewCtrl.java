package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Card;
import commons.Tag;
import commons.Task;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;

public class CardOverviewCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private long cardId = -1;

    @FXML
    private Text title;

    @FXML
    private Text description;

    @FXML
    private VBox taskList;

    @FXML
    private HBox tagsList;

    /**
     * Creates a new Card overview controller
     * @param server server
     * @param mainCtrl main controller
     */
    @Inject
    public CardOverviewCtrl(ServerUtils server, MainCtrl mainCtrl){
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Refreshes the card overview
     */
    public void refresh(){
        Card card = this.server.getCard(cardId);

        if(card == null) {
            deletedCard();
            return;
        }

        clear();

        this.title.setText(card.getTitle());
        this.description.setText(card.getDescription());
        loadTasks();
        loadTags();
    }

    private void deletedCard(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle( "Card: " + getTitle());
        alert.setHeaderText("This card does not exist");
        alert.setContentText("It appears someone has deleted this card\n");

        alert.showAndWait();

        back();
    }

    private void loadTags(){
        List<Tag> tags = server.getCard(cardId).getTags();

        for(Tag t : tags) {
            Label name = new Label(t.getName());
            Label color = new Label("                     ");
            Color colorOfTag = Color.valueOf(t.getColor());
            color.setBackground(new Background(new BackgroundFill(colorOfTag,null,null)));

            HBox hb = new HBox(8, name, color);

            tagsList.getChildren().add(hb);
        }
    }

    private void loadTasks(){
        List<Task> tasks = server.getCard(cardId).getTasks();

        for(Task task: tasks){
            HBox hBox = new HBox(5);

            CheckBox box = new CheckBox();
            box.setSelected(task.isStatus());

            box.selectedProperty().addListener(change -> {
                server.editTask(box.isSelected(), task.getId());
                server.send("/topic/tasks", null);
                server.send("/topic/cards", new Card(null, null));
            });

            Label taskDesc = new Label(task.getTitle());

            hBox.getChildren().addAll(box,taskDesc);

            taskList.getChildren().add(hBox);
        }

    }

    /**
     * Gets the card id
     * @return the card id
     */
    public long getCardId() {
        return cardId;
    }

    /**
     * Sets the card id
     * @param id id of the card
     */
    public void setCardId(long id){
        this.cardId = id;
    }

    /**
     * Gets card's title
     * @return card's title
     */
    public String getTitle(){
        return title.getText();
    }

    /**
     * Sends the application back to the board overview,
     * this is called when the "back" button is pressed
     */
    public void back(){
        mainCtrl.showBoard();
        this.cardId = -1;
    }

    private void clear(){
        taskList.getChildren().clear();
        tagsList.getChildren().clear();
    }

    private void deletedBoard(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle( "Card: " + getTitle());
        alert.setHeaderText("This card does not exist");
        alert.setContentText("It appears someone has deleted the board\n");

        alert.showAndWait();

        this.cardId = -1;
        mainCtrl.showOverview();
    }

    /**
     * Registers controller for websocket messages
     */
    public void registerWebsockets(){
        server.registerForMessages("/topic/tasks", Card.class, c -> {
            Platform.runLater(() -> {
                if(cardId != -1) {

                    if(c != null && c.getTitle().equals("Board")) deletedBoard();

                    else refresh();
                }
            });
        });
    }

}
