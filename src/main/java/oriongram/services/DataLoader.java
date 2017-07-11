package oriongram.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import oriongram.model.Role;
import oriongram.model.User;
import oriongram.repos.RoleRepository;
import oriongram.repos.UserRepository;

import java.util.Arrays;
@Component
public class DataLoader implements CommandLineRunner{
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Override
    public void run(String... strings) throws Exception {
        if (!roleRepository.existsByRole("USER")) {
            roleRepository.save(new Role("USER"));
            roleRepository.save(new Role("ADMIN"));
            Role adminRole = roleRepository.findByRole("ADMIN");
            Role userRole = roleRepository.findByRole("USER");
            User user = new User("user@user.com", "password", "user", "username");
            user.setRoles(Arrays.asList(userRole));
            userRepository.save(user);
            user = new User("admin@admin.com", "password", "admin", "adminname");
            user.setRoles(Arrays.asList(adminRole));
            userRepository.save(user);
        }
    }
}