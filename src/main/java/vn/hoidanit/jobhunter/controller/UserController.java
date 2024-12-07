package vn.hoidanit.jobhunter.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/users")
  public ResponseEntity<User> createNewUser(@RequestBody User postManUser) {

    User newUser = this.userService.handleCreateUser(postManUser);
    return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
  }

  @DeleteMapping("/users/{id}")
  public ResponseEntity<String> deleteUser(@PathVariable("id") long id) {
    this.userService.handleDeleteUserByID(id);
    return ResponseEntity.ok("Delete user");
    // return ResponseEntity.status(HttpStatus.OK).body("Delete user");
  }

  @GetMapping("/users/{id}")
  public ResponseEntity<User> getUserByID(@PathVariable("id") long id) {
    User user = this.userService.fetchUserByID(id);
    return ResponseEntity.status(HttpStatus.OK).body(user);
  }

  @GetMapping("/users")
  public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = this.userService.fetchAllUsers();
    return ResponseEntity.status(HttpStatus.OK).body(users);
  }

  @PutMapping("/users")
  public ResponseEntity<User> updateUser(@RequestBody User postManUser) {
    User user = this.userService.handleUpdateUser(postManUser);
    return ResponseEntity.status(HttpStatus.OK).body(user);
  }

}
