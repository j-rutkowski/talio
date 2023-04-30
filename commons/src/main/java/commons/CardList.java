package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CardList {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Card> cards;


    @SuppressWarnings("unused")
    protected CardList() {
        // for object mapper
    }

    /**
     * The construcer for the list
     * @param title the title of the list
     * @param cards the cards in the list
     */
    public CardList(String title, List<Card> cards) {
        this.title = title;
        if(cards == null || cards.size() < 1){
            this.cards = new ArrayList<>();
        }
        else{
            this.cards = cards;
        }

    }

    /**
     * Sets a new title
     *
     * @param title new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This method adds a card to the card list
     * @param c the card to be added
     */
    public void addCard(Card c){
        cards.add(c);
    }

    public void insertCard(Card c, int index){cards.add(index,c);}

    /**
     * A getter for the title
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * A getter for the cards
     * @return the cards
     */
    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> list) { cards = list;}

    /**
     * a getter for the id
     * @return the id
     */
    public long getId(){
        return id;
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
     * @return human-readable string containing lists properties
     */
    @Override
    public String toString() {
        return "CardList{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", cards=" + cards +
                '}';
    }
}
