package oriongram.model;

import oriongram.repos.FollowRepository;

import java.util.ArrayList;

public class FullImage {
    private Image image;
    private ArrayList<Comment> comments;
    private String[] thumbsUps;
    private String buttons;
    private FollowRepository followRepository;
    public FullImage(Image image, ArrayList<Comment> comments, ArrayList<ThumbsUp> thumbsUps, FollowRepository followRepository) {
        this.followRepository = followRepository;
        this.image = image;
        this.comments = comments;
        this.thumbsUps = new String[thumbsUps.size()];
        for (int i = 0; i < thumbsUps.size(); i++)
            this.thumbsUps[i] = thumbsUps.get(i).getUsername();
    }

    public void addButtons(String username) {
        buttons = "";

        boolean check = false;
        for (String user : thumbsUps)
            if (user.equals(username))
                check = true;
        if (!check)
            buttons += String.format("<a href=\"thumbsUp/%s\"><button class=\"btn btn-sm btn-success\" >thumbsUp (%s)</button></a> ", image.getId(), thumbsUps.length);
        else
            buttons += String.format("<a href=\"thumbsDown/%s\"><button class=\"btn btn-sm btn-danger\" >nah! (%s)</button></a> ", image.getId(), thumbsUps.length);

        check = false;
        for (Follow follow : followRepository.findAllByFollower(username))
            if (follow.getFollowed().equals(image.getUsername()))
                check = true;
        if (!check)
            buttons += String.format("<a href=\"follow/%s\"><button class=\"btn btn-sm btn-success\" >follow %s</button></a>", image.getUsername(), image.getUsername());
        else
            buttons += String.format("<a href=\"unfollow/%s\"><button class=\"btn btn-sm btn-danger\" >unfollow %s</button></a>", image.getUsername(), image.getUsername());


        //<a th:href="@{'thumbsUp/' + ${i.image.id}}"><button class="btn btn-sm btn-success" th:text="${ 'thumbsUp (' + {i.thumbsUps.length} + ')'}"></button></a>
        //<a th:href="@{'follow/' + ${i.image.username}}"><button class="btn btn-sm btn-primary" th:text="${ 'follow ' + {i.image.username} }"></button></a>
    }

    public String getButtons() {return buttons;}
    public void setButtons(String buttons) {this.buttons = buttons;}
    public Image getImage() {return image;}
    public ArrayList<Comment> getComments() {return comments;}
    public String[] getThumbsUps() {return thumbsUps;}

}
