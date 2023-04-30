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
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import commons.Board;
import commons.Card;
import commons.CardList;
import commons.Tag;
import commons.Task;
import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class ServerUtils {

    private static String server;

    private StompSession session;

    /**
     * This function gets called whenever the overview gets loaded and it needs
     * to be filled with all the cards currently in the database
     *
     * @return a list of all the cards that are currently stored in the database
     */
    public List<Card> getCards() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("cards") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Card>>() {
                });
    }

    /**
     * This function returns all the tags
     *
     * @param boardId id of the board to get the tags from
     * @return a list of all the tags that are currently stored in the database
     */
    public List<Tag> getTags(Long boardId) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("tags") //
                .queryParam("boardId", boardId)
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Tag>>() {
                });
    }

    /**
     * This function returns all the tags
     *
     * @param cardId id of the card to get the tags from
     * @return a list of all the tags that are currently stored in the database
     */
    public List<Tag> getTagsOfCard(Long cardId) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("tags/card") //
                .queryParam("cardId", cardId)
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Tag>>() {
                });
    }

    /**
     * This function gets called whenever a user wants to add a tag to the database
     *
     * @param tag     The tag to be added to the database
     * @param boardId id of the board to add the tag to
     * @return The tag that is added to the database
     */
    public Tag addTag(Tag tag, Long boardId) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("tags") //
                .queryParam("boardId", boardId)
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(tag, APPLICATION_JSON), Tag.class);
    }

    /**
     * This function is called to get one specific card
     *
     * @param id id of card
     * @return a list of all the cards that are currently stored in the database
     */
    public Card getCard(long id) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("cards/getCard")
                .queryParam("id", id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.json(null), Card.class);
    }

    /**
     * This method updates the color of the object in the database
     *
     * @param id    id of the Board
     * @param color customized color
     */
    public void updateColor(long id, String color) {
        ClientBuilder.newClient(new ClientConfig())
                .target(server).path("boards/update-color")
                .queryParam("id", id)
                .queryParam("color", color)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.json(null), Board.class);
    }

    /**
     * This function is called to get one specific list
     *
     * @param id id of list
     * @return a list of all the cards that are currently stored in the database
     */
    public CardList getList(long id) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("lists/getList")
                .queryParam("id", id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.json(null), CardList.class);
    }

    /**
     * This function gets called whenever a user wants to add a card to the database
     *
     * @param card The card to be added to the database
     * @return The card that is added to the database
     */
    public Card addCard(Card card) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("cards") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(card, APPLICATION_JSON), Card.class);
    }

    /**
     * A method that gets all the lists on the server
     *
     * @return a list of lists from the server
     */
    public List<CardList> getLists() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("lists") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<CardList>>() {
                });
    }

    /**
     * A method that adds a cardList to the server
     *
     * @param cardList the cardList to be added
     * @return the added cardList to the sever
     */
    public CardList addList(CardList cardList) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("lists") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(cardList, APPLICATION_JSON), CardList.class);
    }

    /**
     * A method that removes a card from the server
     *
     * @param cardId the card to be removed
     * @return the removed card
     */
    public Card removeCard(Long cardId) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("lists/card") //
                .queryParam("cardId", cardId)
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .delete(Card.class);
    }

    /**
     * A method that removes a tag from the server
     * @param boardId id of the board of the tag
     * @param tagId tag to be removed from database
     * @return the removed card
     */
    public Tag removeTag(Long tagId, Long boardId) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("tags/delete") //
                .queryParam("tagId", tagId)
                .queryParam("boardId", boardId)
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .delete(Tag.class);
    }

    /**
     * A method that edits the name of a tag from the server
     * @param newName new name of the tag
     * @param tagId tag to be edited from database
=     */
    public void editTag(Long tagId, String newName) {
        ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("tags/edit") //
                .queryParam("tagId", tagId)
                .queryParam("newName", newName)
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get();
    }

    /**
     * A method that edits the color of a tag from the server
     * @param newColor new color of the tag
     * @param tagId tag to be edited from database
    =     */
    public void editTagColor(Long tagId, String newColor) {
        ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("tags/editColor") //
                .queryParam("tagId", tagId)
                .queryParam("newColor", newColor)
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get();
    }

    /**
     * A method that removes a list from the server
     *
     * @param listId  the list to be removed
     * @param boardId id of the board
     */
    public void removeList(Long listId, long boardId) {
        ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("lists").path("list") //
                .queryParam("listId", listId)
                .queryParam("boardId", boardId)
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .delete();
    }

    /**
     * A method that is called when a card is added to a list, and updates the list
     *
     * @param id   the id of the list
     * @param card the card to be added
     */
    public void updateList(long id, Card card) {
        ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("lists").path("edit") //
                .queryParam("listId", id)
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(card, APPLICATION_JSON), CardList.class);
    }

    /**
     * Edits the title of a card
     *
     * @param id    id of the card to be edited
     * @param title new card's title
     * @return the edited card
     */
    public Card editCardTitle(long id, String title) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("cards/edit")
                .queryParam("id", id)
                .queryParam("title", title)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.json(null), Card.class);
    }
    /**
     * A method that is called when a card reordered
     *
     * @param id   the id of the list
     * @param index in index to be inserted
     * @param card the card to be added
     */
    public void reorderList(long id, int index, Card card) {
        ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("lists/reorderList") //
                .queryParam("id", id)
                .queryParam("index", index)
                .queryParam("card", card)
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(card, APPLICATION_JSON), CardList.class);
    }

    /**
     * Edits the description of a card
     *
     * @param id   id of the card
     * @param desc new description of the card
     */
    public void editCardDescription(long id, String desc) {
        ClientBuilder.newClient(new ClientConfig())
                .target(server).path("cards/edit-desc")
                .queryParam("id", id)
                .queryParam("description", desc)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.json(null), Card.class);
    }

    /**
     * edits the tasklist of a card
     *
     * @param id    the id of the card
     * @param tasks the new tasklist
     */
    public void editCardTasks(long id, List<Task> tasks) {
        ClientBuilder.newClient(new ClientConfig())
                .target(server).path("cards/edit-task")
                .queryParam("id", id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(tasks, APPLICATION_JSON), Card.class);
    }

    /**
     * edits the tagslist of a card
     *
     * @param id   the id of the card
     * @param tags the new tagslist
     */
    public void editTagsCard(long id, List<Tag> tags) {
        ClientBuilder.newClient(new ClientConfig())
                .target(server).path("cards/edit-tags")
                .queryParam("id", id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(tags, APPLICATION_JSON), Card.class);
    }

    /**
     * Edits the title of a list
     *
     * @param id    id of the list to be edited
     * @param title new list's title
     * @return the edited list
     */
    public CardList editListTitle(long id, String title) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("lists/editName")
                .queryParam("id", id)
                .queryParam("title", title)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.json(null), CardList.class);
    }

    /**
     * Gets all boards stored in the database
     *
     * @return boards in the database
     */
    public List<Board> getBoards() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("boards") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Board>>() {
                });
    }

    /**
     * Gets a specific board
     *
     * @param id id of the board
     * @return board
     */
    public Board getBoardById(long id) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("boards/get-board")
                .queryParam("id", id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Board.class);
    }

    /**
     * Gets a specific board
     *
     * @param key key of the board
     * @return board
     */
    public Board getBoardByKey(String key) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("boards/get-by-key")
                .queryParam("key", key)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Board.class);
    }

    /**
     * Updates the board in the database when a cardlist is added to it
     *
     * @param id       id of the board
     * @param cardList cardlist that is added to the board
     */
    public void addListToBoard(long id, CardList cardList) {
        ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("boards").path("add-list") //
                .queryParam("id", id)
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(cardList, APPLICATION_JSON), Board.class);
    }

    /**
     * Adds a board to the database
     *
     * @param board board to be added
     * @return board that has been added
     */
    public Board addBoard(Board board) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("boards") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(board, APPLICATION_JSON), Board.class);
    }

    /**
     * Removes board from the database
     *
     * @param id id from board to remove
     */
    public void removeBoard(long id) {
        ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("boards/delete") //
                .queryParam("id", id)
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .delete();

    }

    /**
     * Renames a board
     *
     * @param title new title for the board
     * @param id    id of the board
     */
    public void renameBoard(String title, long id) {
        ClientBuilder.newClient(new ClientConfig())
                .target(server).path("boards/rename")
                .queryParam("title", title)
                .queryParam("id", id)
                .request(APPLICATION_JSON)
                .post(Entity.json(null), Board.class);

    }

    /**
     * Adds a task to the database
     *
     * @param task task to add to the database
     */
    public void addTask(Task task) {
        ClientBuilder.newClient(new ClientConfig())
                .target(server).path("tasks/add")
                .request(APPLICATION_JSON)
                .post(Entity.json(task), Task.class);
    }

    /**
     * Edits the status of a task
     *
     * @param b  status of the task
     * @param id id of the task
     */
    public void editTask(boolean b, long id) {
        ClientBuilder.newClient(new ClientConfig())
                .target(server).path("tasks/edit-status")
                .queryParam("status", b)
                .queryParam("id", id)
                .request(APPLICATION_JSON)
                .post(Entity.json(null), Task.class);
    }

    private StompSession connect(String url) {
        var client = new StandardWebSocketClient();

        var stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());

        try {
            return stomp.connect(url, new StompSessionHandlerAdapter() {
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException();
    }

    /**
     * Handles messages for the subscribers at destination dest
     *
     * @param dest     endpoint to subscribe to
     * @param type     Class of the object that is sent via the websockets
     * @param consumer function to be executed once accept() is called one the consumer
     * @param <T>      Generic type
     */
    public <T> void registerForMessages(String dest, Class<T> type, Consumer<T> consumer) {
        session.subscribe(dest, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                consumer.accept((T) payload);
            }
        });
    }

    /**
     * Sends object o to destination dest
     *
     * @param dest destination to send to
     * @param o    Object to send
     */
    public void send(String dest, Object o) {
        this.session.send(dest, o);
    }

    /**
     * Sets the server address
     *
     * @param address the address of the server
     */
    public void setServerAddress(String address) {
        server = "http://" + address + "/";
        this.session = connect("ws://" + address + "/websocket");
    }

    /**
     * Returns the server address
     *
     * @return the server address
     */
    public String getServerAddress() {
        return server;
    }

    /**
     * @return true if the server address is set
     */
    public boolean isServerSet() {
        return session != null;
    }

    /**
     * Edits the password of a board
     *
     * @param id id of the board
     * @param password password of the board
     */
    public void editBoardPassword(long id, String password){
        ClientBuilder.newClient(new ClientConfig())
                .target(server).path("boards/edit-password")
                .queryParam("id", id)
                .queryParam("password", password)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.json(null), Board.class);
    }

    /**
     * @param password password to validate
     * @return true if the password is correct
     */
    public boolean validateAdminPassword(String password) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("admin")
                .queryParam("password", password)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.json(null), Boolean.class);
    }
}