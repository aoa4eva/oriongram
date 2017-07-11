package oriongram.model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Please provide a valid e-mail")
    @NotEmpty(message = "Please provide an e-mail")
    private String email;
    @Column(name = "password")
    @org.springframework.data.annotation.Transient
    private String password;
    @Column(name = "name")
    @NotEmpty(message = "Please provide your name")
    private String name;
    @Column(name = "username")
    @NotEmpty(message = "Please provide your username")
    private String username;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "user_id"),inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;

    public User(String email, String password, String name, String username) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.username = username;
    }
    public User() {}

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
    public String getFirstName() {return name;}
    public void setFirstName(String name) {this.name = name;}
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public Collection<Role> getRoles() {return roles;}
    public void setRoles(Collection<Role> roles) {this.roles = roles;}

}
