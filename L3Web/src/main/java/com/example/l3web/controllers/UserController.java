package com.example.l3web.controllers;

import com.example.l3web.model.BasicUser;
import com.example.l3web.model.Restaurant;
import com.example.l3web.model.User;
import com.example.l3web.model.Driver;
import com.example.l3web.repos.BasicUserRepo;
import com.example.l3web.repos.RestaurantRepo;
import com.example.l3web.repos.UserRepo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.persistence.Basic;
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
//        return userRepo.getUserByLoginAndPassword(login, password);
//    }

    @PostMapping(value = "validateUser") //http://localhost:8080/validateUser
    public @ResponseBody String getUserByCredentials(@RequestBody String info) {
        System.out.println(info);
        //?Kaip parsint
        Gson gson = new Gson();
        Properties properties = gson.fromJson(info, Properties.class);
        var login = properties.getProperty("login");
        var psw = properties.getProperty("password");
        User user = userRepo.getUserByLoginAndPassword(login, psw);
        if (user != null) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userType", user.getClass().getName());
            jsonObject.addProperty("login", user.getLogin());
            jsonObject.addProperty("password", user.getPassword());
            jsonObject.addProperty("name", user.getName());
            jsonObject.addProperty("surname", user.getSurname());
            jsonObject.addProperty("id", user.getId());

            String json = gson.toJson(jsonObject);

            return json;
        }
        return null;
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

    @PostMapping(value = "insertDriver")
    public @ResponseBody User createDriver(@RequestBody Driver driver) {
        basicUserRepo.save(driver);
        return userRepo.getUserByLoginAndPassword(driver.getLogin(), driver.getPassword());
    }

    @PostMapping(value = "insertBasic")
    public @ResponseBody User createUser(@RequestBody BasicUser user) {
        basicUserRepo.save(user);
        return userRepo.getUserByLoginAndPassword(user.getLogin(), user.getPassword());
    }

    @PostMapping(value = "insertBasicUser")
    public @ResponseBody User createBasicUser(@RequestBody BasicUser basicUser) {
        basicUserRepo.save(basicUser);
        return userRepo.getUserByLoginAndPassword(basicUser.getLogin(), basicUser.getPassword());
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