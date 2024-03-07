package ch.uzh.ifi.hase.soprafs24.service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public Optional<User> getUsersById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.ONLINE);
        checkIfUserExists(newUser);
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        LocalDate currentDate = LocalDate.now();
        newUser.setEntrydate(currentDate);
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User loginUser(User loginuser) {
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        checkIfUserInDatabase(loginuser);
        User official_user = userRepository.findByUsername(loginuser.getUsername());
        official_user.setToken(UUID.randomUUID().toString());
        official_user.setStatus(UserStatus.ONLINE);
        if (loginuser.getName().equals(official_user.getName())) {
            User loginusersaved = userRepository.save(official_user);
            userRepository.flush();
            return loginusersaved;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                String.format("Your Login is incorrect. Please try again!"));
    }

    public User logoutUser(User logoutUser){
        logoutUser.setStatus(UserStatus.OFFLINE);
        userRepository.save(logoutUser);
        return logoutUser;

        //userRepository.flush()
    }

    public User updateUser(User existingUser, UserPutDTO userPutDTO) {
        checkifDateInputisvalidandUpdate(existingUser,userPutDTO);
        checkifUserCanBeUpdated(existingUser, userPutDTO);
        existingUser.setUsername(userPutDTO.getUsername());
        userRepository.save(existingUser);
        return existingUser;
    }
    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username and the name
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
        //Optional<User> userById = userRepository.findById(userToBeCreated.getId());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        String UserorPasswordnullMessage = "This User is invalid due to Non Inputs for Username or Password.";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));

        }
        else if(userToBeCreated.getName().equals("") || userToBeCreated.getUsername().equals("")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format(UserorPasswordnullMessage));
        }
    }

    private void checkifUserCanBeUpdated(User existingUser, UserPutDTO userPutDTO){
        if(!existingUser.getUsername().equals(userPutDTO.getUsername())){
            User userByUsername = userRepository.findByUsername(userPutDTO.getUsername());
            String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be updated!";
            String UserorPasswordnullMessage = "This User is invalid due to Non Inputs for Username";
            if (userByUsername != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
            }
            else if(userPutDTO.getUsername().equals("")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format(UserorPasswordnullMessage));
            }
        }
    }

    private void checkifDateInputisvalidandUpdate(User existingUser,UserPutDTO userPutDTO){
        try {
            if(userPutDTO.getBirthday() == null){
                return;
            }
            String dateString = userPutDTO.getBirthday();
            // Define the expected date format
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            // Parse the input string
            LocalDate parsedDate = LocalDate.parse(dateString, formatter);
            existingUser.setBirthday(parsedDate);
            userRepository.save(existingUser);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    String.format("Your Birthday Format is incorrect. Format Spesification: yyyy-mm-dd"));
        }
    }

    private void checkIfUserInDatabase(User userInDatabase) {
        User userByUsername = userRepository.findByUsername(userInDatabase.getUsername());
        if (userByUsername == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    String.format("Your Login is incorrect. Please try again!"));
        }
    }



}