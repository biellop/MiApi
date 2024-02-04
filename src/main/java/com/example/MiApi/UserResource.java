package com.example.MiApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(UserResource.USERS)
public class UserResource {
    public static final String USERS= "/users";

    @Autowired
    UserController userController;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<UserDto>> users(){

        return new ResponseEntity<>( userController.readAll(), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> user(@PathVariable Integer id){
        return new ResponseEntity<>( userController.getUserById(id), HttpStatus.OK);
    }
    @GetMapping("/{id}/email")
    public ResponseEntity<Map<String,String>> email(@PathVariable Integer id){
        return new ResponseEntity<>(Collections.singletonMap(
                                                "email",
                                                userController.getUserById(id).getEmail()),
                                    HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> addUser(@RequestBody User user) {
        return ResponseEntity.ok(userController.addUser(user));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id){
        userController.removeUSer(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Integer id, @RequestBody User user) {
        UserDto existingUser = userController.getUserById(id);
        if (existingUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        user.setId(id);
        UserDto updatedUser = userController.addUser(user);

        UserDto updatedUserDto = new UserDto(updatedUser);

        return ResponseEntity.ok(updatedUserDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> patchUser(@PathVariable Integer id, @RequestBody JsonPatch patch) {
        UserDto existingUser = userController.getUserById(id);
        if (existingUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User patchedUser = applyPatch(patch, existingUser);

        userController.addUser(patchedUser);

        return ResponseEntity.ok(new UserDto(patchedUser));
    }

    private User applyPatch(JsonPatch patch, User targetUser) {
        try {
            JsonNode patched = patch.apply(objectMapper.convertValue(targetUser, JsonNode.class));
            return objectMapper.treeToValue(patched, User.class);
        } catch (JsonPatchException | JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
    private User applyPatch(JsonPatch patch, UserDto targetUser) {
        try {
            ObjectMapper objectMapper = null;
            JsonNode patched = patch.apply(objectMapper.convertValue(targetUser, JsonNode.class));
            return objectMapper.treeToValue(patched, User.class);
        } catch (JsonPatchException | JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*{
        "id": 2,
            "email": "pol.email.com",
            "fullName": "Pol",
            "password": "12345"
    }
    Fer un post*/

    /*http://localhost:8080/users/2*/

}
