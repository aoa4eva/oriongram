package oriongram.controller;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import oriongram.config.CloudinaryConfig;
import oriongram.model.Image;
import oriongram.model.User;
import oriongram.repos.ImageRepository;
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

    @Autowired
    public HomeController (UserValidator userValidator, ImageRepository imageRepository
            , UserService userService,CloudinaryConfig cloudc) {
        this.userValidator = userValidator;
        this.imageRepository = imageRepository;
        this.userService = userService;
        this.cloudc = cloudc;
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
    public String upload(@RequestParam("file") MultipartFile file, Image image){
        System.out.println(image.getCaption());
        try {
            Map uploadResult =  cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            String filename = uploadResult.get("public_id").toString() +
                    "." + uploadResult.get("format").toString();
            String url = uploadResult.get("url").toString();

            System.out.println(url);

        } catch (IOException e){
            e.printStackTrace();
        }
        return "login";
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
