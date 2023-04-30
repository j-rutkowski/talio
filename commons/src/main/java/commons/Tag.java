package commons;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Tag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private String color;

    @SuppressWarnings("unused")
    protected Tag() {
        // for object mapper
    }

    /**
     * Constructor for tag
     * @param name      the name of the tag
     * @param color     the color of the tag
     */
    public Tag(String name, String color) {
        this.name = name;
        this.color = color;
    }

    /**
     * Getter for the id
     * @return  the id of the tag
     */
    public long getId() {
        return this.id;
    }

    /**
     * Getter for name of the tag
     * @return  name of the tag
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the color of a tag
     * @return  the color of a tag
     */
    public String getColor() {
        return color;
    }

    /**
     * Setter for the name of a tag
     * @param name The new name of the set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter for the color of a tag
     * @param color The new color of the set
     */
    public void setColor(String color) {
        this.color = color;
    }
}
