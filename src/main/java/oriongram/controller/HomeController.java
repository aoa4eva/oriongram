package oriongram.controller;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import oriongram.config.CloudinaryConfig;
import oriongram.model.Follow;
import oriongram.model.FullImage;
import oriongram.model.Image;
import oriongram.model.User;
import oriongram.repos.CommentRepository;
import oriongram.repos.FollowRepository;
import oriongram.repos.ImageRepository;
import oriongram.repos.UserRepository;
import oriongram.services.UserService;
import oriongram.services.UserValidator;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@Controller
public class HomeController {


    private UserValidator userValidator;
    private ImageRepository imageRepository;
    private UserService userService;
    private CloudinaryConfig cloudc;
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private FollowRepository followRepository;

    @Autowired
    public HomeController (UserValidator userValidator, ImageRepository imageRepository, CommentRepository commentRepository
            , FollowRepository followRepository, UserService userService, CloudinaryConfig cloudc, UserRepository userRepository) {
        this.userValidator = userValidator;
        this.imageRepository = imageRepository;
        this.userService = userService;
        this.cloudc = cloudc;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.followRepository = followRepository;
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
    public String all(Model model) {
        newImg(model);
        ArrayList<FullImage> fullImages = new ArrayList<>();
        Iterable<Image> images = imageRepository.findAll();
        for (Image i : images) {
            FullImage thisImage = new FullImage();
            thisImage.setImage(i);
            thisImage.setComments(commentRepository.findByImageId(i.getId()));
            fullImages.add(thisImage);
        }
        model.addAttribute("images", fullImages);
        return "global";
    }

    @RequestMapping("/following")
    public String following(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());

        newImg(model);
        ArrayList<FullImage> fullImages = new ArrayList<>();
        ArrayList<Follow> following = followRepository.findAllByFollower(user.getUsername());

        for (Follow f : following)
            for (Image i : imageRepository.findAllByUsername(f.getFollowed())) {
                FullImage thisImage = new FullImage();
                thisImage.setImage(i);
                thisImage.setComments(commentRepository.findByImageId(i.getId()));
                fullImages.add(thisImage);
            }

        model.addAttribute("images", fullImages);
        return "following";
    }

    @RequestMapping("/like/{id}")
    public String like(@PathVariable("id") int id) {

        Image image = imageRepository.findOne(id);
        image.setLikes(image.getLikes() + 1);

        imageRepository.save(image);

        return "redirect:/index";
    }

    @RequestMapping("/follow/{username}")
    public String follow(@PathVariable("username") String username, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());

        Follow follow = new Follow();

        follow.setFollower(user.getUsername());
        follow.setFollowed(username);

        followRepository.save(follow);

        return "redirect:/index";
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
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        image.setUsername(user.getUsername());
        image.setLikes(0);
        image.setDate(new Date().toString());
        imageRepository.save(image);
        return "redirect:/index";
    }

    @RequestMapping("/index")
    public String index(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        newImg(model);
        model.addAttribute("images", imageRepository.findAllByUsername(user.getUsername()));
        model.addAttribute("user", user);
        return "index";
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
            userService.saveUser(user);
            model.addAttribute("message", "User Account Successfully Created");
        }
        return "login";
    }

    public void newImg(Model model) { model.addAttribute("image", new Image()); }
    public UserValidator getUserValidator() {
        return userValidator;
    }
    public void setUserValidator(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

}
