package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import java.time.LocalDate;

public class UserPostDTO {

  private String name;

  private String username;

  private LocalDate entrydate;

  private LocalDate birthday;

  private UserStatus status;

  public void setEntrydate(LocalDate entrydate){ this.entrydate = entrydate; }

  public LocalDate getEntrydate(){return entrydate;}

  public void setBirthday(LocalDate birthday){this.birthday = birthday;}

  public LocalDate getBirthday(){return birthday;}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
  public UserStatus getStatus() {
        return status;
    }
  public void setStatus(UserStatus status) {
        this.status = status;
    }

}
