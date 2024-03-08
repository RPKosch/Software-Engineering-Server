package ch.uzh.ifi.hase.soprafs24.controller;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetLoginDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {
  private final UserService userService;
  private final Logger log = LoggerFactory.getLogger(UserService.class);

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    log.info("THIS IS USERS: {}", userGetDTOs);

    if(userGetDTOs.isEmpty()){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("This is not valid because there is no Content to see!"));
    }
    return userGetDTOs;
  }

  @GetMapping("/users/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUserById(@PathVariable Long id) {
      // fetch user by ID from the internal representation
      User user = userService.getUsersById(id)
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User was not found"));;

      // convert the user to the API representation
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }
  @PutMapping("/users/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public UserGetDTO updateUser(@PathVariable Long id, @RequestBody UserPutDTO userPutDTO){
      //This Authentifaction does also not work. i do not get the key in my backend
      //@RequestHeader(name = "authorization", defaultValue = "") String token){
      //log.info("THIS IS PUT TOKEN: {}", token);
      
      // Fetch the existing user or give back an error if user does not exist
      // Also check Authentification
      //User ExistingUser = userService.checkauthentification_and_if_user_exists(token, id);

      // Ensure the provided ID matches the path variable
      if (!userPutDTO.getId().equals(id)) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mismatched user ID in path and request body");
      }

      User ExistingUser = userService.getUsersById(id)
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User was not found"));

      // Update the user with the new data
      User UpdatedUser = userService.updateUser(ExistingUser, userPutDTO);

      // Return the updated user
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(UpdatedUser);
  }

  @PostMapping("/userslogout/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO logoutUser(@PathVariable Long id) {
      // Ensure the provided ID matches the path variable
      User user = userService.getUsersById(id)
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
      User OfflineUser = userService.logoutUser(user);// Return the updated user
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(OfflineUser);
    }


  @PostMapping("/users")
  //@ResponseStatus(HttpStatus.CREATED)
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetLoginDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
    // , HttpServletResponse response
    //response.addHeader("authorization", createdUser.getToken());
    UserGetLoginDTO finaluser = DTOMapper.INSTANCE.convertEntityToUserGetLoginDTO(createdUser);
    return finaluser;
  }

  // New Commit and push
  @PostMapping("/users/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetLoginDTO loginUser(@RequestBody UserPostDTO userPostDTO) {
      User userlogin = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
      // convert API user to internal representation
      User loginUser = userService.loginUser(userlogin);

      // 1. Solution: Header Token wont work - No idea why.
      // , HttpServletResponse response
      //response.addHeader("Authorization", loginUser.getToken());
      //return DTOMapper.INSTANCE.convertEntityToUserGetDTO(loginUser);


      // convert internal representation of user back to API

      return DTOMapper.INSTANCE.convertEntityToUserGetLoginDTO(loginUser);
  }
}
