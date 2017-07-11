package oriongram.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import oriongram.model.Role;
import oriongram.model.User;
import oriongram.repos.UserRepository;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Transactional
public class SSUserDetailsService implements UserDetailsService {
    UserRepository userRepository;

    public SSUserDetailsService (UserRepository userRepository) {
        this.userRepository  = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        try {
            User user = userRepository.findByUsername(s);
            if (user == null)
                return null;
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthorities(user));
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found");
        }
    }

    private Set<GrantedAuthority> getAuthorities(User user) {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        for(Role role : user.getRoles())
            authorities.add(new SimpleGrantedAuthority(role.getRole()));
        return authorities;
    }

}
