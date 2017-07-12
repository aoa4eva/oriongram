package oriongram.model;

import java.util.ArrayList;

/**
 * Created by student on 7/12/17.
 */
public class FullImage {

    private Image image;
    private ArrayList<Comment> comments;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }
}
