package oriongram.controller;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import oriongram.config.CloudinaryConfig;
import oriongram.model.Image;
import oriongram.model.User;
import oriongram.repos.ImageRepository;
import oriongram.repos.UserRepository;
import oriongram.services.UserService;
import oriongram.services.UserValidator;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {


    private UserValidator userValidator;
    private ImageRepository imageRepository;
    private UserService userService;
    private CloudinaryConfig cloudc;
    private UserRepository userRepository;

    @Autowired
    public HomeController (UserValidator userValidator, ImageRepository imageRepository
            , UserService userService, CloudinaryConfig cloudc, UserRepository userRepository) {
        this.userValidator = userValidator;
        this.imageRepository = imageRepository;
        this.userService = userService;
        this.cloudc = cloudc;
        this.userRepository = userRepository;
    }


    @RequestMapping("/")
    public  String index(Model model) {
        newImg(model);
        return "index";
    }

    @RequestMapping("/login")
    public String login(Model model) {
        newImg(model);
        return "login";
    }



    @RequestMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, Image image, Model model){
        newImg(model);
        try {
            Map uploadResult =  cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            //String filename = uploadResult.get("public_id").toString() +
              //      "." + uploadResult.get("format").toString();



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
    public String save(Image image, Model model, Authentication authentication) {
        newImg(model);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        image.setUsername(user.getUsername());

        imageRepository.save(image);

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
        return "index";
    }




    public void newImg(Model model) { model.addAttribute("image", new Image()); }
    public UserValidator getUserValidator() {
        return userValidator;
    }
    public void setUserValidator(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

}
