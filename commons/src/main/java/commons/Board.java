package commons;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardList> cardLists = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Tag> tags = new ArrayList<>();

    private String title;

    private String color;

    private String password;

    private String key;

    protected Board(){
        //for object mapper
    }

    /**
     * Constructor for Board object
     * @param title title of the board
     * @param cardLists card lists within the board
     */
    public Board(String title, List<CardList> cardLists){
        this.title = title;
        this.color= "0xcacacaff";
        this.password = "";
        if(cardLists == null || cardLists.size() < 1)
            this.cardLists = new ArrayList<>();
        else
            this.cardLists = cardLists;
    }

    /**
     * gets the id of the board
     * @return id of the board
     */
    public long getId(){
        return this.id;
    }

    /**
     * Funtcion for adding a tag to a board
     * @param tag   the tag to be added
     * @return      The board that the tag is added to
     */
    public Board addTag(Tag tag) {
        this.tags.add(tag);
        return this;
    }

    /**
     * Getter for tags
     * @return  the tags of a board
     */
    public List<Tag> getTags() {
        return this.tags;
    }

    /**
     * returns title
     * @return title of this
     */
    public String getTitle(){
        return this.title;
    }

    /**
     * Sets Color
     * @param color new title
     */
    public void setColor(String color){
        this.color = color;
    }

    /**
     * Find the color of the board
     * @return color
     */
    public String getColor(){
        return this.color;
    }

    /**
     * @param title gets the cardlists of this
     */
    public void setTitle(String title){
        this.title = title;
    }

    /**
     * gets the cardlists of this
     * @return cardlists of this
     */
    public List<CardList> getCardLists(){
        return cardLists;
    }

    /**
     * Adds a cardlist to the board
     * @param cardList cardlist to be added
     */
    public void addCardList(CardList cardList){
        cardLists.add(cardList);
    }

    /**
     * Sets the password of the board
     *
     * @param password password to be set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return password of the board
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @return true if the board has a password, false otherwise
     */
    public boolean hasPassword() {
        return !this.password.equals("");
    }

    /**
     * Validates the password of the board
     *
     * @param password password to be validated
     * @return true if the password is correct, false otherwise
     */
    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }

    /**
     * @return key of this board
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key new key of this board
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Hashes this
     * @return hash code of this
     */
    public int hashCode(){
        return HashCodeBuilder.reflectionHashCode(this);
    }


    /**
     * Checks for equality between this and other
     * @param other other object to compare with this
     * @return true if other equals this, false otherwise
     */
    @Override
    public boolean equals(Object other){
        return EqualsBuilder.reflectionEquals(this, other);
    }

    /**
     * Makes this into a human-readable format
     * @return human-readable format of this
     */
    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", cardLists=" + cardLists +
                ", tags=" + tags +
                ", title='" + title + '\'' +
                ", color='" + color + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
