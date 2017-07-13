package oriongram.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ThumbsUp {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private int imageId;
    private String username;

    public ThumbsUp(int imageId, String username) {
        this.imageId = imageId;
        this.username = username;
    }
    public ThumbsUp() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
