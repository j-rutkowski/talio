/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import client.records.ClientData;
import commons.Card;
import commons.CardList;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Pair;


public class MainCtrl {
    private HelperCtrl helperCtrl;
    private Scene helper;

    private Stage primaryStage;

    private HomeBoardCtrl homeBoardCtrl;
    private Scene homeBoard;

    private AddCardCtrl addCardCtrl;
    private Scene addCard;

    private AddListCtrl addListCtrl;
    private Scene addList;

    private ChooseServerCtrl chooseServerCtrl;
    private Scene chooseServer;

    private MultiBoardCtrl multiBoardCtrl;
    private Scene multiBoard;

    private AddBoardCtrl addBoardCtrl;
    private Scene addBoard;

    private TagOverviewCtrl tagOverviewCtrl;
    private Scene tagOverview;

    private AddTagCtrl addTagCtrl;
    private Scene addTag;

    private CardOverviewCtrl cardOverviewCtrl;
    private Scene cardOverview;
    private boolean hasAdminRights;

    /**
     * Initializes a new main controller
     *  @param primaryStage primary stage pair
     * @param homeBoard home board pair
     * @param addCard add card pair
     * @param addList add list pair
     * @param chooseServer choose server pair
     * @param multiBoard multiboard pair
     * @param addBoard add board pair
     * @param tagOverview tag overview pair
     * @param addTag add tag pair
     * @param helper
     * @param cardOverview card overview pair
     */
    public void initialize(Stage primaryStage,
                           Pair<HomeBoardCtrl, Parent> homeBoard,
                           Pair<AddCardCtrl, Parent> addCard,
                           Pair<AddListCtrl, Parent> addList,
                           Pair<ChooseServerCtrl, Parent> chooseServer,
                           Pair<MultiBoardCtrl, Parent> multiBoard,
                           Pair<AddBoardCtrl, Parent> addBoard,
                           Pair<TagOverviewCtrl, Parent> tagOverview,
                           Pair<AddTagCtrl, Parent> addTag, Pair<HelperCtrl, Parent> helper,
                           Pair<CardOverviewCtrl, Parent> cardOverview)

    {
        this.primaryStage = primaryStage;

        this.addCardCtrl = addCard.getKey();
        this.addCard = new Scene(addCard.getValue());

        this.addListCtrl = addList.getKey();
        this.addList = new Scene(addList.getValue());

        this.homeBoardCtrl = homeBoard.getKey();
        this.homeBoard = new Scene(homeBoard.getValue());

        this.chooseServerCtrl = chooseServer.getKey();
        this.chooseServer = new Scene(chooseServer.getValue());

        this.helperCtrl = helper.getKey();
        this.helper = new Scene(helper.getValue());

        this.multiBoardCtrl = multiBoard.getKey();
        this.multiBoard = new Scene(multiBoard.getValue());

        this.addBoardCtrl = addBoard.getKey();
        this.addBoard = new Scene(addBoard.getValue());

        this.tagOverviewCtrl = tagOverview.getKey();
        this.tagOverview = new Scene(tagOverview.getValue());

        this.addTagCtrl = addTag.getKey();
        this.addTag = new Scene(addTag.getValue());

        this.cardOverviewCtrl = cardOverview.getKey();
        this.cardOverview = new Scene(cardOverview.getValue());

        showChooseServer();
        primaryStage.show();
        setShortCutBoard();
        setShortCutCards();
        this.helper.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().toString().equals("ESCAPE")) {
                    showBoard();
                }
            }
        });

        this.hasAdminRights = false;
    }

    /**
     * Initialises websockets for the controllers that need it.
     * Gets called after the server address is set.
     */
    public void initWebsockets(){
        multiBoardCtrl.registerForWebsockets();
        homeBoardCtrl.registerWebsocket();
        cardOverviewCtrl.registerWebsockets();
        addCardCtrl.registerWebsockets();
    }

    /**
     * Shortcuts for editing a Board
     */
    public void setShortCutBoard() {
        this.multiBoard.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().toString().equals("A")) {
                    multiBoardCtrl.add();
                }
            }
        });

        this.addBoard.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().toString().equals("ENTER")) {
                    addBoardCtrl.add();
                }
                if (event.getCode().toString().equals("ESCAPE"))
                    addBoardCtrl.cancel();
            }
        });

        this.homeBoard.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().toString().equals("A"))
                    homeBoardCtrl.addList();
                if (event.getCode().toString().equals("T"))
                    homeBoardCtrl.showTags();
                if (event.getCode().toString().equals("ESCAPE"))
                    homeBoardCtrl.exit();
                if (event.getCode().toString().equals("F1"))
                    showHelper();
            }
        });

        this.addList.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().toString().equals("ENTER"))
                    addListCtrl.add();
                if (event.getCode().toString().equals("ESCAPE"))
                    addListCtrl.cancel();
            }
        });
    }

    /**
     * These are the shortcuts for edting a card
     */
    public void setShortCutCards(){
        this.addTag.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().toString().equals("ENTER"))
                    addTagCtrl.addTag();
                if(event.getCode().toString().equals("ESCAPE"))
                    addTagCtrl.cancel();
            }
        });

        this.tagOverview.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().toString().equals("A"))
                    tagOverviewCtrl.addTag();
                if(event.getCode().toString().equals("ESCAPE"))
                    tagOverviewCtrl.exit();
            }
        });

        this.addCard.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().toString().equals("ENTER"))
                    addCardCtrl.add();
                if(event.getCode().toString().equals("ESCAPE"))
                    addCardCtrl.cancel();
                if(event.getCode().toString().equals("F6"))
                    addCardCtrl.addTags();
                if(event.getCode().toString().equals("F5"))
                    addCardCtrl.addTask();
            }
        });
    }

    /**
     * It shows the helper page
     */
    public void showHelper() {
        primaryStage.setTitle("Helper");
        primaryStage.setScene(helper);
    }
    /**
     * This function gets called when the client starts,
     * and it shows the user the chooseServer screen
     */
    public void showChooseServer() {
        primaryStage.setTitle("Cards: Choose Server");
        primaryStage.setScene(chooseServer);
    }

    /**
     * This function gets called when the client starts.
     * It loads the overview of boards
     */
    public void showOverview() {
        primaryStage.setTitle("Board: Overview");
        primaryStage.setScene(multiBoard);
        multiBoardCtrl.updateServerLabel();
        multiBoardCtrl.refresh();
    }

    /**
     * This function gets called when the user wants to add a card.
     * It loads the scene where users can add a card
     */
    public void showAddCard() {
        primaryStage.setTitle("Cards: Add");
        primaryStage.setScene(addCard);
    }

    /**
     * This function gets called whenever the
     * tagoverview needs to get updated
     */
    public void refreshTagOverview() {
        this.tagOverviewCtrl.refresh();
    }

    /**
     * Shows the add card scene, when a user clicks the "edit" button of a card.
     * This method also calls the editInit method in the add card controller, unlike the
     * showAddCard method
     * @param card card to be edited
     */
    public void showEditCard(Card card){
        primaryStage.setTitle("Card: " + card.getTitle());
        addCardCtrl.editInit(card);
        primaryStage.setScene(addCard);
    }

    /**

     * This function gets called when the users clickes the add list button,
     * and it shows the user the addList screen
     */
    public void showAddList() {
        primaryStage.setTitle("Lists: Add");
        primaryStage.setScene(addList);
    }

    /**
     * Shows the scene where a user can add a board.
     * Gets called when the "Add board" button is clicked.
     */
    public void showAddBoard(){
        primaryStage.setTitle("Board: Add");
        primaryStage.setScene(addBoard);
    }

    /**
     * Shows the scene where a user can add a tag.
     * Gets called when the "Add tag" button is clicked.
     */
    public void showTagOverview(){
        primaryStage.setTitle("Tag: Overview");
        primaryStage.setScene(tagOverview);
        this.tagOverviewCtrl.refresh();
    }

    /**
     * Shows the scene where a user can add a tag.
     * Gets called when the "Add tag" button is clicked.
     */
    public void showAddTag(){
        primaryStage.setTitle("Tag: Add");
        primaryStage.setScene(addTag);
    }

    /**
     * Sets the scene to the board view
     */
    public void showBoard(){
        primaryStage.setTitle("Board: " + homeBoardCtrl.getBoardTitle());
        homeBoardCtrl.setBoardTitle(homeBoardCtrl.getBoardTitle());
        primaryStage.setScene(homeBoard);

        homeBoardCtrl.refresh();
    }

    /**
     * Shows the card overview
     * @param id id of the card to show
     */
    public void showCardOverview(long id){
        cardOverviewCtrl.setCardId(id);
        cardOverviewCtrl.refresh();
        primaryStage.setTitle("Card: " + cardOverviewCtrl.getTitle());
        primaryStage.setScene(cardOverview);
    }

    /**
     * Changes the board id in the HomeBoardCtrl class
     * @param id id of the new board that needs to be viewed
     */
    public void setBoardId(long id){
        homeBoardCtrl.setBoardId(id);
        if (homeBoardCtrl.isLocked()) {
            homeBoardCtrl.setIsLocked(!ClientData.getInstance().hasUnlockedBoard(id));
        }
    }

    /**
     * Gets the id of the board that is currently loaded in the board view
     * @return id of the board
     */
    public long getBoardId(){
        return homeBoardCtrl.getBoardId();
    }

    /**
     * Sets the to be edited card list, in the add card controller
     * @param cardList the cardlist that will be edited
     */
    public void setCardList(CardList cardList){
        addCardCtrl.setEditedCardList(cardList);
    }

     /**
     * Sets needsWebSocket field from HomeBoardCtrl to false
     */
    public void needsWebSocketFalse(){
        homeBoardCtrl.needsWebSocketFalse();
    }

    /**
     * Sets the isLocked field from HomeBoardCtrl to specified value
     *
     * @param lock boolean to lock or unlock the board
     */
    public void setBoardIsLocked(boolean lock) {
        homeBoardCtrl.setIsLocked(lock);
    }

    /**
     * @return true if the user has admin rights, false if not
     */
    public boolean hasAdminRights() {
        return hasAdminRights;
    }

    /**
     * @param hasAdminRights the admin rights to set
     */
    public void setAdminRights(boolean hasAdminRights) {
        this.hasAdminRights = hasAdminRights;
    }

}