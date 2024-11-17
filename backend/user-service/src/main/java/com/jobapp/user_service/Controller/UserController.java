package com.jobapp.user_service.Controller;

import com.jobapp.user_service.DTO.*;
import com.jobapp.user_service.Model.User;
import com.jobapp.user_service.Repository.UserRepository;
import com.jobapp.user_service.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Controller
@AllArgsConstructor
@RequestMapping
public class UserController {
    private final UserService userService;
    private UserRepository userRepository;
    public final BCryptPasswordEncoder bCryptPasswordEncoder;


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<Object> createUser(@RequestBody UserCreate userCreate) {
        String hashedPassword = bCryptPasswordEncoder.encode(userCreate.getPassword());
        userCreate.setPassword(hashedPassword);
        return userService.createUser(userCreate);
    }


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<UserLoginResponse> login( @RequestBody UserLoginRequests userLoginRequests) throws Exception {
        return  userService.loginService(userLoginRequests.getUsername(),userLoginRequests.getPassword());
    }


    @GetMapping("/viewUser")
    public Optional<UserDTO> getCurrentUser() {
        // Get the authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        // Fetch the user entity
        Optional<User> userOptional = Optional.ofNullable(userRepository.findUserByUsername(id));

        // Map User entity to UserDTO

        return userOptional.map(this::mapUserToDTO);
    }
    private UserDTO mapUserToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUsername(),
                null);
    }


    @RequestMapping(value = "/editUser", method = RequestMethod.POST)
    public ResponseEntity<Object> updateUser(@RequestBody UserUpdate userUpdate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userOptional = userRepository.findUserByUsername(username).getId();
        return userService.updateUser(userOptional, userUpdate);
    }

    @RequestMapping(value = "/editUserPassword", method = RequestMethod.POST)
    public ResponseEntity<Object> updateUserPassword(@RequestBody UserPasswordUpdate userPasswordUpdate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userOptional = userRepository.findUserByUsername(username).getId();
        return userService.changePassword(userOptional, userPasswordUpdate);
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public ResponseEntity<Object> resetUserPassword(@RequestBody PasswordReset passwordReset) {
        return userService.forgetPassword(passwordReset);
    }

    @RequestMapping(value = "/deleteUser/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }



    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        }

        return errors;
    }
}