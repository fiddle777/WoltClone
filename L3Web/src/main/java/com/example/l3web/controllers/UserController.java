package com.example.l3web.controllers;

import com.example.l3web.model.Restaurant;
import com.example.l3web.model.User;
import com.example.l3web.repos.BasicUserRepo;
import com.example.l3web.repos.RestaurantRepo;
import com.example.l3web.repos.UserRepo;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Properties;

@RestController
//Tas pats kas
//@Controller
//@Mapping(path="users")
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BasicUserRepo basicUserRepo;
    @Autowired
    private RestaurantRepo restaurantRepo;


    @GetMapping(value = "/allUsers")
    public @ResponseBody Iterable<User> getAll() {
        return userRepo.findAll();
    }

    @GetMapping(value = "/allRestaurants")
    public @ResponseBody Iterable<Restaurant> getAllRestaurants() {
        return restaurantRepo.findAll();
    }

    //Nedaryti sito produkcineje
//    @GetMapping(value = "validateUser") // http://localhost:8080/validateUser
//    public @ResponseBody User getUserByCredentials(@RequestParam String login, @RequestParam String password){
//        return userRepository.getUserByLoginAndPassword(login, password);
//    }

    @PostMapping(value = "validateUser") //http://localhost:8080/validateUser
    public @ResponseBody User getUserByCredentials(@RequestBody String info) {
        System.out.println(info);
        //?Kaip parsint
        Gson gson = new Gson();
        Properties properties = gson.fromJson(info, Properties.class);
        var login = properties.getProperty("login");
        var psw = properties.getProperty("password");
        return userRepo.getUserByLoginAndPassword(login, psw);
    }

    @PutMapping(value = "updateUser")
    public @ResponseBody User updateUser(@RequestBody User user) {
        userRepo.save(user);
        return userRepo.getReferenceById(user.getId());
    }

    @PutMapping(value = "updateUserById/{id}")
    public @ResponseBody User updateUserById(@RequestBody String info, @PathVariable int id) {

        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException()); //cia noriu savo custom error

        Gson gson = new Gson();
        Properties properties = gson.fromJson(info, Properties.class);
        var name = properties.getProperty("name");
        user.setName(name);

        userRepo.save(user);
        return userRepo.getReferenceById(user.getId());
    }

    @PostMapping(value = "insertUser")
    public @ResponseBody User createUser(@RequestBody User user) {
        userRepo.save(user);
        return userRepo.getUserByLoginAndPassword(user.getLogin(), user.getPassword());
    }

    @DeleteMapping(value = "deleteUser/{id}")
    public @ResponseBody String deleteUser(@PathVariable int id) {
        userRepo.deleteById(id);
        User user = userRepo.findById(id).orElse(null);
        if (user != null) {
            return "fail on delete";
        } else {
            return "yay";
        }

    }

}
