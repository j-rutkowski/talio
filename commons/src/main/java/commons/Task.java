package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Task implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;

    private boolean status;


    @SuppressWarnings("unused")
    protected Task() {
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
     * Constructor for cards
     * @param title         Title of card
     */
    public Task(String title) {
        this.title = title;
        this.status = false;
    }

    /**
     * a getter for the title
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * A setter for the title
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * a getter for the status
     * @return the status
     */
    public boolean isStatus() {
        return status;
    }
    /**
     * A setter for the status
     * @param status the status
     */

    public void setStatus(boolean status) {
        this.status = status;
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
//    @Override
//    public String toString() {
//        return "Task{" +
//                "id=" + id +
//                ", title='" + title + '\'' +
//                ", status=" + status +
//                '}';
//    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
