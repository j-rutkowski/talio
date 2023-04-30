package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import javax.swing.*;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Card implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;

    private String description;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    @ManyToMany
    private List<Tag> tags;

    @SuppressWarnings("unused")
    protected Card() {
        // for object mapper
    }

    /**
     * A getter for the id
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Constructs a new card with the given title and description
     *
     * @param title card's title
     * @param description card's description
     */
    public Card(String title, String description) {
        this.title = title;
        this.description = description;
        this.tasks = new ArrayList<>();
        this.tags = new ArrayList<>();
    }
    /**
     * Constructs a new card with the given title and description
     * @param title card's title
     * @param description card's description
     * @param tasks the list of tasks
     */
    public Card(String title, String description, List<Task> tasks) {
        this.title = title;
        this.description = description;
        if(tasks == null || tasks.size() < 1)
            this.tasks = new ArrayList<>();
        else
            this.tasks = tasks;
    }

    /**
     * Constructor for cards
     * @param title         Title of card
     * @param description   Description of card
     * @param cardList      List of card
     * @param tasks         list of tasks
     * @param tagSet        set of tags
     */
    public Card(String title, String description,
                CardList cardList,
                List<Task> tasks,
                List<Tag> tagSet) {
        this.title = title;
        this.description = description;
        this.tags = tagSet;
        if(tasks == null || tasks.size() < 1)
            this.tasks = new ArrayList<>();
        else
            this.tasks = tasks;
    }

    /**
     * @return the card's title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * A setter for tasks
     * @param tasks the new list
     */
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Sets a new title
     *
     * @param title new title
     */
    public void setTitle(String title) {
        this.title = title;
        this.description = description;
    }

    /**
     * Gets the card's description
     * @return the description
     */
    public String getDescription(){
        return this.description;
    }

    /**
     * Sets a new description
     * @param description the new description
     */
    public void setDescription(String description){this.description = description;}

    /**
     * A getter for tasks
     * @return the list
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * A method to add tasks the tasklist
     * @param t the task to add
     */
    public void addTask(Task t){
        tasks.add(t);
    }

    /**
     * A method to add tasks the tasklist
     * @param t the task to add
     */
    public void addTag(Tag t){
        tags.add(t);
    }

    /**
     * Getting the tags of a card
     * @return      the tags of a card
     */
    public List<Tag> getTags() {
        return this.tags;
    }

    /**
     * Setting the tags of a card
     * @param tags  the tags for the card
     */
    public void setTags(List<Tag> tags) {
        this.tags = new ArrayList<>();

        if (tags == null || tags.size() < 1) {
            return;
        }

        this.tags.addAll(tags);
    }

    /**
     * A method that updates a task in the list
     * @param t the new task
     * @param index the index of said task
     */
    public void updateTask(Task t, int index){
        tasks.set(index, t);
    }

    /**
     * A method that updates a task in the list
     * @param t the new task
     * @param index the index of said task
     */
    public void updateTag(Tag t, int index){
        tags.set(index, t);
    }

    /**
     * @param obj object to compare to
     * @return if the object obj is equal
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * @return card's hash code
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * @return human-readable string containing card's properties
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

}
