package oriongram.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import oriongram.model.User;
import oriongram.repos.UserRepository;

@Component
public class UserValidator implements Validator {
    @Autowired
    UserRepository userRepository;
    public boolean supports(Class<?> clazz){
        return User.class.isAssignableFrom(clazz);
    }
    public void validate(Object target, Errors errors){
        User user = (User) target;
        String email = user.getEmail();
        String username = user.getUsername();
        String password = user.getPassword();
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "user.firstName.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "user.lastName.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "user.username.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "user.email.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "user.password.empty");
        if(username.length() < 5){
            errors.rejectValue("username","user.username.tooShort");
        }
        if(password.length() < 5){
            errors.rejectValue("password","user.password.tooShort");
        }
        if(userRepository.existsByEmail(email)){
            errors.rejectValue("email", "user.email.duplicate");
        }
        if(userRepository.existsByUsername(username)){
            errors.rejectValue("username", "user.username.duplicate");
        }
    }
}