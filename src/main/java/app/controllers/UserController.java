package app.controllers;

import app.dto.UserStatsProfileDTO;
import app.exceptions.DBErrorException;
import app.models.Profile;
import app.models.Stats;
import app.models.User;
import app.service.TokenServiceImpl;
import app.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import helper.CommonErrors;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Response;

import java.util.Objects;
import java.util.Optional;

public class UserController extends Controller {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private UserService userService;
    private final TokenServiceImpl tokenService = new TokenServiceImpl();

    public UserController(UserService userService) {
        setUserService(userService);
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
        Optional<User> optionalUser = userService.getUserByUsername(user.getUsername());
        if(optionalUser.isPresent()) {
            return new Response(
                    HttpStatus.CONFLICT,
                    ContentType.JSON,
                    "{ \"error\": \"User with same username already registered\"}"
            );
        }

        try {
            userService.saveUser(user);
        } catch (DBErrorException e) {
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Could not create User:"+ e.getMessage() +"\""
            );
        }
        return new Response(
                HttpStatus.CREATED,
                ContentType.JSON,
                "{ \"message\": \"User created successfully\" }"
        );
    }

    // GET /users/:username
    public Response getUser(User user, String username) {
        if (user == null || (!Objects.equals(user.getUsername(), username) && !user.isAdmin())) {
            return CommonErrors.TOKEN_ERROR;
        }

        UserStatsProfileDTO userDTO = new UserStatsProfileDTO();

        Optional<User> optionalUser = userService.getUserByUsername(username);
        if(optionalUser.isEmpty()) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \" User not found \"}"
            );
        }
        userDTO.setUser(optionalUser.get());
        Optional<Stats> optionalStats = userService.findStatsByUserId(user.getId());
        Optional<Profile> optionalProfile = userService.findProfileByUserId(user.getId());

        optionalStats.ifPresent(userDTO::setStats);
        optionalProfile.ifPresent(userDTO::setProfile);

        try {
            String userDataJson = getObjectMapper().writeValueAsString(userDTO);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \" Data successfully retrieved \", \"data\": " + userDataJson + "}"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return CommonErrors.INTERNAL_SERVER_ERROR;
        }
    }

    public Response updateUser(User user, String username, String rawUser) {
        if (user == null || (!Objects.equals(user.getUsername(), username) && !user.isAdmin())) {
            return CommonErrors.TOKEN_ERROR;
        }

        UserStatsProfileDTO updatedUser;

        try {
            updatedUser = getObjectMapper().readValue(rawUser, UserStatsProfileDTO.class);
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
    private Response updateUser(String username, UserStatsProfileDTO updatedUser) {
        Optional<User> optionalUser = userService.getUserByUsername(username);

        if(optionalUser.isEmpty()) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \" User not found \"}"
            );
        }
        User user = optionalUser.get();
        user.setName(updatedUser.getUser().getName());
        Optional<Profile> optionalProfile = userService.findProfileByUserId(user.getId());
        Profile profile;
        if(optionalProfile.isEmpty()) {
            profile = new Profile(user.getId());
            try {
                int affectedRows = userService.createProfile(profile);
                if (affectedRows == 0) {
                    return new Response(
                            HttpStatus.BAD_REQUEST,
                            ContentType.JSON,
                            "{ \"error\": \"Could not created Profile\" }"
                    );
                }
            } catch (DBErrorException e) {
                return new Response(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ContentType.JSON,
                        "{ \"error\": \"Something went wrong: "+ e.getMessage() +"\"}"
                );
            }
        } else profile = optionalProfile.get();
        profile.setBio(updatedUser.getProfile().getBio());
        profile.setImage(updatedUser.getProfile().getImage());

        userService.updateUser(username, user);
        userService.updateProfile(profile.getId(), profile);
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

    // POST /sessions
    private Response loginUser(User user) {
        Optional<User> optionalUser = userService.getUserByUsername(user.getUsername());
        if(optionalUser.isEmpty() || !optionalUser.get().getPassword().equals(user.getPassword())) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"error\": \"Invalid username/password provided \"}"
            );
        }

        String token = tokenService.generateAccessToken(user.getUsername());
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"message\": \"User login successful\", \"token\": \""+ token +"\"}"
        );
    }

}
