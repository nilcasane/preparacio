package cat.tecnocampus.veterinarymanagement.security.authentication;

import cat.tecnocampus.veterinarymanagement.application.exceptions.PersonDoesNotExistException;
import cat.tecnocampus.veterinarymanagement.domain.*;
import cat.tecnocampus.veterinarymanagement.persistence.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class PersonDetailsService implements UserDetailsService {
    private final PersonRepository personRepository;

    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws PersonDoesNotExistException {
        Person person = personRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new PersonDoesNotExistException("Person not found with username: " + username));
        return new PersonDetails(person);
    }
}