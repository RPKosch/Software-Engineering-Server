package ch.uzh.ifi.hase.soprafs24.controller;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetLoginDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setName("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
  }

  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setUsername("testUsername");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setName("Test User");
    userPostDTO.setUsername("testUsername");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }


  //Fails Currently
  @Test
  public void get_single_valid_user_by_id() throws Exception {
      // given
      User user = new User();
      user.setId(1L);
      user.setName("Test User");
      user.setUsername("testUsername");
      user.setToken("1");
      user.setStatus(UserStatus.ONLINE);
      UserGetDTO userGetDTO = new UserGetDTO();

      given(userService.getUsersById(Mockito.any())).willReturn(Optional.of(user));

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder getRequest = get("/users/1")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userGetDTO));

        // then
      mockMvc.perform(getRequest)
              .andExpect(status().isNoContent())
              .andExpect(jsonPath("$.id", is(user.getId().intValue())))
              .andExpect(jsonPath("$.username", is(user.getUsername())))
              .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }


  @Test
  public void get_single_not_valid_user_by_id() throws Exception {
      // given
      User user = new User();
      user.setId(1L);
      user.setName("Test User");
      user.setUsername("testUsername");
      user.setToken("1");
      user.setStatus(UserStatus.ONLINE);
      UserGetDTO UserGetDTO = new UserGetDTO();

      given(userService.createUser(Mockito.any())).willReturn(user);

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder getRequest = get("/users/100")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(UserGetDTO));

      // then
      mockMvc.perform(getRequest)
              .andExpect(status().isNotFound());
  }


  @Test
  public void put_change_valid_user_by_id() throws Exception {
      User user_new = new User();
      user_new.setId(2L);
      user_new.setName("New Test User");
      user_new.setUsername("NewtestUsername");
      user_new.setBirthday(LocalDate.parse("2000-06-11"));
      user_new.setToken("1");
      user_new.setStatus(UserStatus.ONLINE);

      UserPutDTO userPutDTO = new UserPutDTO();
      userPutDTO.setBirthday("1990-06-25");
      userPutDTO.setUsername("OldUsername");
      userPutDTO.setId(2L);

      User iduser = new User();
      iduser.setId(2L);
      iduser.setUsername("NewUsername");
      iduser.setBirthday(LocalDate.parse("1000-04-06"));

      given(userService.getUsersById(Mockito.any())).willReturn(Optional.of(iduser));


      given(userService.updateUser(Mockito.any(), Mockito.any())).willReturn(user_new);

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder putRequest = put("/users/2")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userPutDTO));

      // then
      mockMvc.perform(putRequest)
              .andExpect(status().isNoContent())
              .andExpect(jsonPath("$.id", is(user_new.getId().intValue())))
              .andExpect(jsonPath("$.username", is(user_new.getUsername())))
              .andExpect(jsonPath("$.birthday", is(user_new.getBirthday().toString())));
  }


  @Test
  public void put_change_not_same_id() throws Exception {
      User user_new = new User();
      user_new.setId(2L);
      user_new.setName("Old Test User");
      user_new.setUsername("NewtestUsername");
      user_new.setBirthday(LocalDate.parse("2000-06-11"));
      user_new.setToken("1");
      user_new.setStatus(UserStatus.ONLINE);

      UserPutDTO userPutDTO = new UserPutDTO();
      userPutDTO.setBirthday("1990-06-25");
      userPutDTO.setUsername("OldUsername");
      userPutDTO.setId(2L);

      User iduser = new User();
      iduser.setId(2L);
      iduser.setUsername("NewUsername");
      iduser.setBirthday(LocalDate.parse("1000-04-06"));

      given(userService.getUsersById(Mockito.any())).willReturn(Optional.of(iduser));


      given(userService.updateUser(Mockito.any(), Mockito.any())).willReturn(user_new);

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder putRequest = put("/users/3")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userPutDTO));

      // then
      mockMvc.perform(putRequest)
              .andExpect(status().isBadRequest());
  }



    /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}