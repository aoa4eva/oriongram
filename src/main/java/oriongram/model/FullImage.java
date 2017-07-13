package oriongram.model;

import java.util.ArrayList;

public class FullImage {

    private Image image;
    private ArrayList<Comment> comments;
    private String[] thumbsUps;

    public FullImage(Image image, ArrayList<Comment> comments, ArrayList<ThumbsUp> thumbsUps) {
        this.image = image;
        this.comments = comments;
        this.thumbsUps = new String[thumbsUps.size()];
        for (int i = 0; i < thumbsUps.size(); i++)
            this.thumbsUps[i] = thumbsUps.get(i).getUsername();
    }

    public Image getImage() {
        return image;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public String[] getThumbsUps() {return thumbsUps;}

    public int compare(FullImage o2) {
        if (o2.getThumbsUps().length > this.getThumbsUps().length)
            return 1;
        return 0;
    }
}
