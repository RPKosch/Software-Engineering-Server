package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import java.time.LocalDate;

public class UserGetLoginDTO {

    private Long id;
    private String username;
    private UserStatus status;
    private LocalDate entrydate;
    private LocalDate birthday;
    private String token;

    public void setEntrydate(LocalDate entrydate){ this.entrydate = entrydate; }

    public LocalDate getEntrydate(){return entrydate;}

    public void setBirthday(LocalDate birthday){this.birthday = birthday;}

    public LocalDate getBirthday(){return birthday;}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
