package com.example.MiApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserController {

   @Autowired
   UserService userService;


    public List<UserDto> readAll() {

        return userService.readAllUsers().stream().map(UserDto::new).toList();
    }

    public UserDto getUserById(Integer id) {
       return new UserDto( userService.getUserById(id));

    }

    public UserDto addUser(User user) {
        User u = userService.addUser(user);
        return new UserDto(u);
    }

    public void removeUSer(Integer id) {
        userService.removeUser(id);
    }
}
