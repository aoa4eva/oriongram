package oriongram.controller;

import com.cloudinary.utils.ObjectUtils;
import com.google.common.collect.Lists;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import oriongram.config.CloudinaryConfig;
import oriongram.model.*;
import oriongram.repos.*;
import oriongram.services.UserService;
import oriongram.services.UserValidator;

import javax.mail.internet.InternetAddress;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@Controller
public class HomeController {

    private EmailService emailService;
    private UserValidator userValidator;
    private ImageRepository imageRepository;
    private UserService userService;
    private CloudinaryConfig cloudc;
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private FollowRepository followRepository;
    private ThumbsUpRepository thumbsUpRepository;

    @Autowired
    public HomeController (UserValidator userValidator, ImageRepository imageRepository, CommentRepository commentRepository
            , ThumbsUpRepository thumbsUpRepository, EmailService emailService
            , FollowRepository followRepository, UserService userService, CloudinaryConfig cloudc, UserRepository userRepository) {
        this.userValidator = userValidator;
        this.imageRepository = imageRepository;
        this.userService = userService;
        this.cloudc = cloudc;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.followRepository = followRepository;
        this.thumbsUpRepository = thumbsUpRepository;
        this.emailService = emailService;
    }


    @RequestMapping("/")
    public  String home() {
        return "redirect:/index";
    }

    @RequestMapping("/login")
    public String login(Model model) {
        newImg(model);
        return "login";
    }

    @RequestMapping("/all")
    public String all(Model model,Authentication authentication) {
        newImg(model);
        String username = getUser(authentication).getUsername();
        ArrayList<FullImage> fullImages = new ArrayList<>();
        Iterable<Image> images = imageRepository.findAll();
        for (Image i : images)
            fullImages.add(new FullImage(i,commentRepository.findByImageId(i.getId()), thumbsUpRepository.findAllByImageId(i.getId())));


        model.addAttribute("images", fullImages);
        model.addAttribute("username", username);

        return "images";
    }

    @RequestMapping("/following")
    public String following(Model model, Authentication authentication) {
        User user = getUser(authentication);

        newImg(model);
        ArrayList<FullImage> fullImages = new ArrayList<>();
        ArrayList<Follow> following = followRepository.findAllByFollower(user.getUsername());

        for (Follow f : following)
            for (Image i : imageRepository.findAllByUsername(f.getFollowed()))
                fullImages.add(new FullImage(i,commentRepository.findByImageId(i.getId()), thumbsUpRepository.findAllByImageId(i.getId())));

        model.addAttribute("images", fullImages);
        return "images";
    }

    @RequestMapping("/comment")
    public String comment(Image image, Model model, Authentication authentication) {
        User user = getUser(authentication);
        int imageId = image.getId();
        String body = image.getCaption();

        Comment comment = new Comment();

        comment.setBody(body);
        comment.setDate(new Date().toString());
        comment.setImageId(imageId);
        comment.setUsername(user.getUsername());

        commentRepository.save(comment);

        return "redirect:/index";
    }

    @RequestMapping("/thumbsUp/{id}")
    public String thumbsUp(@PathVariable("id") int id, Authentication authentication) {
        User user = getUser(authentication);
        String userName = user.getUsername();
        int imageId = imageRepository.findOne(id).getId();
        if (!hasThumbsUp(imageId,userName))
            thumbsUpRepository.save(new ThumbsUp(imageId,userName));
        return "redirect:/index";
    }

    @RequestMapping("/follow/{username}")
    public String follow(@PathVariable("username") String username, Authentication authentication) {
        User user = getUser(authentication);

        Follow follow = new Follow();

        follow.setFollower(user.getUsername());
        follow.setFollowed(username);

        followRepository.save(follow);

        return "redirect:/index";
    }


    @RequestMapping("/email/{id}")
    public void sendEmail(@PathVariable("id") int id,Authentication authentication) {
        User user = getUser(authentication);
        String src = imageRepository.findOne(id).getSrc();
        src = src.substring(10, src.length() - 17);
        try {
            final Email email = DefaultEmail.builder()
                    .from(new InternetAddress("OrionGram@email.bot", "Orion Gram email bot"))
                    .to(Lists.newArrayList(new InternetAddress(user.getEmail(), user.getUsername())))
                    .subject("your picture link")
                    .body(String.format("Hello %s! Your image can be found at: %s please come back and use our services again!",user.getUsername(), src))
                    .encoding("UTF-8").build();
            emailService.send(email);
        } catch(Exception e) {
            //do nothing
        }

    }


    @RequestMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, Image image, Model model){
        newImg(model);
        try {
            Map uploadResult =  cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));

            String[] urlParts = uploadResult.get("url").toString().split("upload/");

            String filename = urlParts[1];
            String url = urlParts[0] + "upload/";

            model.addAttribute("message", "You successfully uploaded '" + file.getOriginalFilename() + "'");
            model.addAttribute("filename", filename);
            model.addAttribute("url", url);
            model.addAttribute("image_edit", image);

            System.out.println("this is the caption: " + image.getCaption());
            System.out.println("this is the filename: " + filename);
            System.out.println("this is the url: " + url);

        } catch (IOException e){
            e.printStackTrace();
            model.addAttribute("filename", "");
            model.addAttribute("url", "");
            model.addAttribute("message", "Sorry I can't upload that!");
        }
        return "preview";
    }

    @RequestMapping("/save")
    public String save(Image image, Authentication authentication) {
        User user = getUser(authentication);
        image.setUsername(user.getUsername());
        image.setDate(new Date().toString());
        imageRepository.save(image);
        return "redirect:/index";
    }

    @RequestMapping("/index")
    public String index(Model model, Authentication authentication) {
        User user = getUser(authentication);

        ArrayList<FullImage> fullImages = new ArrayList<>();
        ArrayList<Image> images = imageRepository.findAllByUsername(user.getUsername());
        for (Image i : images)
            fullImages.add(new FullImage(
                    i,
                    commentRepository.findByImageId(i.getId()),
                    thumbsUpRepository.findAllByImageId(i.getId()))
            );

        newImg(model);
        model.addAttribute("images", fullImages);
        return "images";
    }




    @RequestMapping(value="/register", method = RequestMethod.GET)
    public String showRegistrationPage(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }

    @RequestMapping(value="/register", method = RequestMethod.POST)
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model){
        model.addAttribute("user", user);
        userValidator.validate(user, result);
        if (result.hasErrors()) {
            return "registration";
        } else {
            //todo add default image here: user.setProfileImage('default')
            userService.saveUser(user);
            model.addAttribute("message", "User Account Successfully Created");
        }
        return "login";
    }

    private User getUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername());
    }

    private boolean hasThumbsUp(int imageId, String username) {
        for (ThumbsUp thumbsUp : thumbsUpRepository.findAllByUsername(username))
            if (thumbsUp.getImageId() == imageId)
                return true;
        return false;
    }
    private void newImg(Model model) { model.addAttribute("image", new Image()); }
    public UserValidator getUserValidator() {
        return userValidator;
    }
    public void setUserValidator(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

}
