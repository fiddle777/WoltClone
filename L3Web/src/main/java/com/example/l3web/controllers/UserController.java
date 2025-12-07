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
import com.google.gson.JsonParser;
import jakarta.persistence.Basic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.l3web.model.VehicleType;

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
        Gson gson = new Gson();
        Properties properties = gson.fromJson(info, Properties.class);
        String login = properties.getProperty("login");
        String psw = properties.getProperty("password");

        JsonObject jsonObject = new JsonObject();

        // 1) Check if user with this login exists
        User userByLogin = userRepo.getUserByLogin(login);
        if (userByLogin == null) {
            jsonObject.addProperty("status", "NO_USER");
            return gson.toJson(jsonObject);
        }

        // 2) Check password
        if (!userByLogin.getPassword().equals(psw)) {
            jsonObject.addProperty("status", "WRONG_PASSWORD");
            return gson.toJson(jsonObject);
        }

        // 3) GREAT SUCCESS yekshemahs
        jsonObject.addProperty("status", "OK");
        jsonObject.addProperty("userType", userByLogin.getClass().getName());
        jsonObject.addProperty("login", userByLogin.getLogin());
        jsonObject.addProperty("password", userByLogin.getPassword());
        jsonObject.addProperty("name", userByLogin.getName());
        jsonObject.addProperty("surname", userByLogin.getSurname());
        jsonObject.addProperty("id", userByLogin.getId());

        return gson.toJson(jsonObject);
    }


    @PutMapping(value = "updateUser")
    public @ResponseBody User updateUser(@RequestBody User user) {
        userRepo.save(user);
        return userRepo.getReferenceById(user.getId());
    }

    @PutMapping(value = "updateUserById/{id}")
    public @ResponseBody User updateUserById(@RequestBody String info, @PathVariable int id) {

        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException());

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
    @PostMapping("updateUserInfo")
    public @ResponseBody User updateUserInfo(@RequestBody String body) {
        // Parse incoming JSON
        JsonObject json = new JsonParser().parse(body).getAsJsonObject();

        int id = json.get("id").getAsInt();
        User user = userRepo.findById(id).orElse(null);
        if (user == null) {
            return null;
        }

        if (json.has("name")) {
            user.setName(json.get("name").getAsString());
        }
        if (json.has("surname")) {
            user.setSurname(json.get("surname").getAsString());
        }
        if (json.has("phoneNumber")) {
            user.setPhoneNumber(json.get("phoneNumber").getAsString());
        }

        // BasicUser fields
        if (user instanceof BasicUser basicUser) {
            if (json.has("address")) {
                basicUser.setAddress(json.get("address").getAsString());
            }
        }

        // Driver-specific fields
        if (user instanceof Driver driver) {
            if (json.has("licence")) {
                driver.setLicence(json.get("licence").getAsString());
            }
            if (json.has("bDate") && !json.get("bDate").isJsonNull()) {
                String dateStr = json.get("bDate").getAsString();
                if (!dateStr.isEmpty()) {
                    driver.setBDate(LocalDate.parse(dateStr));   // yyyy-MM-dd
                }
            }
            if (json.has("vehicleType")) {
                String vt = json.get("vehicleType").getAsString();
                if (vt != null && !vt.isEmpty()) {
                    driver.setVehicleType(VehicleType.valueOf(vt));
                }
            }
        }

        try {
            user.setDateUpdated(LocalDateTime.now());
        } catch (Exception ignored) {
        }

        return userRepo.save(user);
    }

}