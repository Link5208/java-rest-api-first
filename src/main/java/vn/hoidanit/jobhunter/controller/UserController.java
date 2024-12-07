package vn.hoidanit.jobhunter.controller;

import java.util.List;

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

  @PostMapping("/user")
  public User createNewUser(@RequestBody User postManUser) {

    User newUser = this.userService.handleCreateUser(postManUser);
    return newUser;
  }

  @DeleteMapping("/user/{id}")
  public String deleteUser(@PathVariable("id") long id) {
    this.userService.handleDeleteUserByID(id);
    return "Delete user";
  }

  @GetMapping("/user/{id}")
  public User getUserByID(@PathVariable("id") long id) {
    User user = this.userService.fetchUserByID(id);
    return user;
  }

  @GetMapping("/user")
  public List<User> getAllUsers() {
    List<User> users = this.userService.fetchAllUsers();
    return users;
  }

  @PutMapping("/user")
  public User updateUser(@RequestBody User postManUser) {

    return this.userService.handleUpdateUser(postManUser);
  }

}
