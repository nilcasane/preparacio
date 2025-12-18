package cat.tecnocampus.veterinarymanagement.domain;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "administrator")
@PrimaryKeyJoinColumn(name = "person_id")
public class Administrator extends Person {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            joinColumns = @JoinColumn(name = "administrator_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRole(Role role) {
        this.roles.add(role);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
