package cat.tecnocampus.veterinarymanagement.security.authentication;

import cat.tecnocampus.veterinarymanagement.domain.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;

public class PersonDetails implements UserDetails {
    private final Person person;
    private final Collection<? extends GrantedAuthority> authorities;

    public PersonDetails(Person person) {
        this.person = person;
        this.authorities = determineAuthorities(person);
    }

    private Collection<? extends GrantedAuthority> determineAuthorities(Person person) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (person instanceof Administrator administrator) {
            // Administrators get their role-based authorities
            authorities.addAll(administrator.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                    .collect(Collectors.toList()));
        } else if (person instanceof Veterinarian) {
            // Veterinarians get VETERINARIAN scope
            authorities.add(new SimpleGrantedAuthority("VETERINARIAN"));
        } else if (person instanceof PetOwner) {
            // PetOwners get PET_OWNER scope
            authorities.add(new SimpleGrantedAuthority("PET_OWNER"));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return person.getPassword();
    }

    @Override
    public String getUsername() {
        return String.valueOf(person.getId());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    @Override
    public String toString() {
        return "PersonDetails{" +
                "username='" + getUsername() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", authorities=" + authorities.toString() +
                '}';
    }

    public Person getPerson() {
        return person;
    }
}