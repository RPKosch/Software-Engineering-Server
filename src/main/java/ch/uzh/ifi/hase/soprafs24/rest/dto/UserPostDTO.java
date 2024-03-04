package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

public class UserPostDTO {

  private String name;

  private String username;

  private String entrydate;

  private String birthday;

  private UserStatus status;

  public void setEntrydate(String entrydate){ this.entrydate = entrydate; }

  public String getEntrydate(){return entrydate;}

  public void setBirthday(String birthday){this.birthday = birthday;}

  public String getBirthday(){return birthday;}

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
