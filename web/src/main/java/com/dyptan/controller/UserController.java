package com.dyptan.controller;

import com.dyptan.exception.UserNotFoundException;
import com.dyptan.model.Filter;
import com.dyptan.model.User;
import com.dyptan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@CrossOrigin(origins = "*")
@RestController
public class UserController {
    Logger log = Logger.getLogger(this.getClass().getName());

    @Autowired
    UserRepository userRepository;

    @GetMapping("/users")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @GetMapping("/user/{name}")
    public User getUser(@PathVariable(name="name") String name){
            return userRepository.findByName(name).orElseThrow(()->new UserNotFoundException(name));
    }

    @GetMapping("/user/{name}/filters")
    public List<Filter> getAllUserFilters(@PathVariable(name="name") String name){
            return userRepository.findByName(name)
                    .map(user-> user.getFilters())
                    .orElseThrow(()->new UserNotFoundException(name));
    }


    @GetMapping("/user/{name}/filter/{id}")
    public Filter getUserFilterById(@PathVariable(name="name") String name,
                                          @PathVariable(name="id") int filterId){
        return userRepository.findByName(name)
                .map(user-> user.getFilters().get(filterId))
                .orElseThrow(()->new UserNotFoundException(name));
    }

    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public User createUser(@ModelAttribute User newUser) {
        log.info("user added : "+newUser.getName());
        return userRepository.save(newUser);
    }

    @PostMapping(value = "/user/{name}/filters",  consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public User createUserFilter(@PathVariable(name="name") String name, @RequestBody Filter newFilter) {
        User user = userRepository.findByName(name).orElseThrow(()->new UserNotFoundException(name));
        user.addFilter(newFilter);
        return userRepository.save(user);
    }


    @PutMapping("/user/{name}")
    public User updateUser(@RequestBody User newUser, @PathVariable String name){
        return userRepository.findByName(name)
                .map(
                        user -> {
                            user.setPassword(newUser.getPassword());
                            return userRepository.save(user);
                                }
                        )
                .orElseGet(
                        ()-> {return userRepository.save(newUser);}
                        );
    }

    @DeleteMapping("/user/{name}/filter/{id}")
    public void deleteUserFilter(@PathVariable String name, @PathVariable int id){
        User user = userRepository.findByName(name).orElseThrow(()->new UserNotFoundException(name));
        user.deleteFilter(id);
        userRepository.save(user);
    }

    @DeleteMapping("/user/{name}")
    public void deleteUser(@PathVariable String name){
        userRepository.delete(getUser(name));
    }
}
