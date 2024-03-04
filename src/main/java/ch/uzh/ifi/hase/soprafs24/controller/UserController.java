package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    return userGetDTOs;
  }

  @GetMapping("/users/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUserById(@PathVariable Long id) {
      // fetch user by ID from the internal representation
      User user = userService.getUsersById(id)
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));;

      // convert the user to the API representation
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }
  @PutMapping("/users/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO updateUser(@PathVariable Long id, @RequestBody UserPutDTO userPutDTO) {
      // Ensure the provided ID matches the path variable
      if (!userPutDTO.getId().equals(id)) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mismatched user ID in path and request body");
      }

      // Fetch the existing user
      User existingUser = userService.getUsersById(id)
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

      // Update the user with the new data
      userService.updateUser(existingUser, userPutDTO);

      // Return the updated user
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(existingUser);
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
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

  // New Commit and push
  @PostMapping("/users/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO loginUser(@RequestBody UserPostDTO userPostDTO) {
      User userlogin = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
      // convert API user to internal representation
      User loginUser = userService.loginUser(userlogin);
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(loginUser);
      // create user

      // convert internal representation of user back to API

      //return DTOMapper.INSTANCE.convertEntityToUserGetDTO(loginUser);
  }
}
