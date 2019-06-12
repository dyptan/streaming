package com.dyptan.controller;

import com.dyptan.model.Filter;
import com.dyptan.model.User;
import com.dyptan.repository.UserRepository;
import com.dyptan.service.AuthService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class UserController {
    Logger log = Logger.getLogger(this.getClass().getName());

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthService authService;

    @GetMapping("/users")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @GetMapping("/user/{name}")
    public User getUser(@PathVariable(name="name") String name){
        return userRepository.findByUsername(name).orElseThrow(() -> new UsernameNotFoundException(name));
    }

    @GetMapping("/user/{name}/filters")
    public List<Filter> getAllUserFilters(@PathVariable(name="name") String name){
        return userRepository.findByUsername(name)
                    .map(user-> user.getFilters())
                .orElseThrow(() -> new UsernameNotFoundException(name));
    }


    @GetMapping("/user/{name}/filter/{id}")
    public Filter getUserFilterById(@PathVariable(name="name") String name,
                                          @PathVariable(name="id") int filterId){
        return userRepository.findByUsername(name)
                .map(user-> user.getFilters().get(filterId))
                .orElseThrow(() -> new UsernameNotFoundException(name));
    }

    //TODO registration is now on login controller
    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public User createUser(@ModelAttribute User newUser) {
        log.info("user added : " + newUser.getUsername());
        return userRepository.save(newUser);
    }

    @PostMapping(value = "/user/{name}/filters",  consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public User createUserFilter(@PathVariable(name="name") String name, @RequestBody Filter newFilter) {
        User user = userRepository.findByUsername(name).orElseThrow(() -> new UsernameNotFoundException(name));
        user.addFilter(newFilter);
        return userRepository.save(user);
    }


    @PutMapping("/user/{name}")
    public User updateUser(@RequestBody User newUser, @PathVariable String name){
        return userRepository.findByUsername(name)
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
        User user = userRepository.findByUsername(name).orElseThrow(() -> new UsernameNotFoundException(name));
        user.deleteFilter(id);
        userRepository.save(user);
    }

    @DeleteMapping("/user/{name}")
    public void deleteUser(@PathVariable String name){
        userRepository.delete(getUser(name));
    }
}
