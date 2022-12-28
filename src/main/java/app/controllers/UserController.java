package app.controllers;

import app.dao.UserDao;
import app.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Response;

import java.util.Optional;

public class UserController extends Controller {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private UserDao userDao;

    public UserController(UserDao userDao) {
       setUserDao(userDao);
    }

    public Response createUser(String rawUser) {
        User user;

        try {
            user = getObjectMapper().readValue(rawUser, User.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"error\": \"Could not parse\", \"data\": " + rawUser + " }"
            );
        }

       if (user.getUsername() == null || user.getPassword() == null || user.getUsername().isBlank() || user.getPassword().isBlank() || user.getUsername().contains(" ") || user.getPassword().contains(" ")) {
           return new Response(
                   HttpStatus.BAD_REQUEST,
                   ContentType.JSON,
                   "{ \"error\": \" Username and/or Password are required and can not be blank or contain any spaces \", \"data\": " + rawUser + "}"
           );
       }

       return createUser(new User(user.getUsername(), user.getPassword()));
    }

    // POST /users
    private Response createUser(User user) {
        Optional<User> optionalUser = userDao.get(user.getUsername());
        if(optionalUser.isPresent()) {
            return new Response(
                    HttpStatus.CONFLICT,
                    ContentType.JSON,
                    "{ \"error\": \"User with same username already registered\"}"
            );
        }

        userDao.save(user);
        return new Response(
                HttpStatus.CREATED,
                ContentType.JSON,
                "{ \"message\": \"User created successfully\" }"
        );
    }

    // GET /users/:username
    public Response getUser(String username) {
        Optional<User> optionalUser = userDao.get(username);

        // TODO: CHECK IF ADMING
        if(optionalUser.isEmpty()) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \" User not found \"}"
            );
        }

        try {
            String userDataJson = getObjectMapper().writeValueAsString(optionalUser.get());
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \" Data successfully retrieved \", \"data\": " + userDataJson + "}"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
    }

    public Response updateUser(String username, String rawUser) {
        User updatedUser;

        try {
            updatedUser = getObjectMapper().readValue(rawUser, User.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"error\": \"Could not parse\", \"data\": " + rawUser + " }"
            );
        }

        return updateUser(username, updatedUser);
    }

    // PUT /users/:username
    private Response updateUser(String username, User updatedUser) {
        Optional<User> optionalUser = userDao.get(username);

        // TODO: CHECK IF ADMING
        if(optionalUser.isEmpty()) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \" User not found \"}"
            );
        }
        User user = optionalUser.get();
        user.setName(updatedUser.getName());
        user.getProfile().setBio(updatedUser.getProfile().getBio());
        user.getProfile().setImage(updatedUser.getProfile().getImage());
        userDao.update(new User(username, ""), user);
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"message\": \"User successfully updated\" }"
        );
    }

    public Response loginUser(String rawUser) {
        User user;

        try {
            user = getObjectMapper().readValue(rawUser, User.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"error\": \"Could not parse\", \"data\": " + rawUser + " }"
            );
        }

        if (user.getUsername() == null || user.getPassword() == null || user.getUsername().isBlank() || user.getPassword().isBlank() || user.getUsername().contains(" ") || user.getPassword().contains(" ")) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"error\": \" Username and/or Password are required and can not be blank or contain any spaces \", \"data\": " + rawUser + "}"
            );
        }

        return loginUser(new User(user.getUsername(), user.getPassword()));
    }

    // POST /users
    private Response loginUser(User user) {
        Optional<User> optionalUser = userDao.get(user.getUsername());
        if(optionalUser.isEmpty() || !optionalUser.get().getPassword().equals(user.getPassword())) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"error\": \" Invalid username/password provided \"}"
            );
        }

        // TODO: LOGIN HANDLING HERE
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"message\": \"User login successful\" }"
        );
    }

}
