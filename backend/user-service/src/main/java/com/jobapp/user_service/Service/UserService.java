package com.jobapp.user_service.Service;


import com.jobapp.user_service.DTO.*;
import com.jobapp.user_service.Model.User;
import com.jobapp.user_service.Repository.UserRepository;
import com.jobapp.user_service.securityConfigs.PasswordEncoder;
import com.jobapp.user_service.securityConfigs.Tokens;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService  {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Tokens tokens;
    private final UserMapper userMapper;

    public ResponseEntity<Object> createUser(UserCreate userCreate) {
        Optional<User> optionalUserEmail = userRepository.findUserByEmail(userCreate.getEmail());
        Optional<User> optionalUserMsidn = userRepository.findUserByMsidn(userCreate.getMsidn());
        Optional<User> optionalUserIdn = userRepository.findUserByIdn(userCreate.getIdn());
        String emailtaken = "email taken";
        String idntaken = "idn taken";
        String msidntaken = "msidn taken";
        if (optionalUserEmail.isPresent())
            return ResponseEntity.badRequest().body(emailtaken);
        if (optionalUserMsidn.isPresent())
            return ResponseEntity.badRequest().body(msidntaken);
        if (optionalUserIdn.isPresent())
            return ResponseEntity.badRequest().body(idntaken);
        User user = User.builder()
                .name(userCreate.getName())
                .surname(userCreate.getSurname())
                .email(userCreate.getEmail())
                .msidn(userCreate.getMsidn())
                .idn(userCreate.getIdn())
                .password(userCreate.getPassword())
                .username(userCreate.getUsername())
                .build();
        userRepository.save(user);

        String message = "Customer with name %s successfully created.".formatted(user.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    public ResponseEntity<UserLoginResponse> loginService(String username, String password) {
        PasswordEncoder passwordEncoder = new PasswordEncoder();
//        UserDetails user = loadUserByUsername(username);
        String user = username;

        if (passwordEncoder.bCryptPasswordEncoder().matches(password, user)) {
            // User authentication successful
            // Set the user's authentication in the session
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user, null);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            Instant instant = Instant.now();
            Instant expiryIso = instant.plus(1, ChronoUnit.HOURS);
            int expiryTime = 3600000; //1 Hour (Should be placed in config)
            Date expiry = new Date(System.currentTimeMillis() + expiryTime);
            Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            String token = tokens.getJWTToken(username, expiry,
                    user.toLowerCase());
            // Generate a JWT token for the user
            // ...

            UserLoginResponse response = new UserLoginResponse();
            response.setUsername(user.toLowerCase());
            response.setRole(user.toLowerCase());
            response.setToken(token);
            response.setExpiry(DateTimeFormatter.ISO_INSTANT.format(expiryIso));
            response.setIssuedAt(DateTimeFormatter.ISO_INSTANT.format(issuedAt));

            // Save the user's authentication in the session
//            session.setAttribute("user", user);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        }

        UserLoginResponse response = new UserLoginResponse();
        String message = "Username or password is incorrect";
        response.setMessage(message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    public ResponseEntity<UserDTO> getUser(Long id) {
        // Retrieve the user from the UserRepository
        Optional<UserDTO> userOptional = userRepository.findUserById(id)
                .stream()
                .map(userMapper)
                .findFirst();

        if (userOptional.isPresent()) {
            UserDTO userDTO = userOptional.get();
            return ResponseEntity.status(HttpStatus.FOUND).body(userDTO);
        } else {
            String message = "User with ID " + id + " not found";
            UserDTO errorUserDTO = new UserDTO(null, null, null, message, null );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorUserDTO);
        }
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findUserById(id);
    }

    public ResponseEntity<Object> updateUser(Long id, UserUpdate userUpdate) {
        // Retrieve the existing user from the UserRepository
        Optional<User> existingUser = userRepository.findUserById(id);
        User user = null;
        if (existingUser.isPresent()) {
            user = existingUser.get();

            // Check if the email is being updated
            if (userUpdate.getEmail() != null && !userUpdate.getEmail().equals(user.getEmail())) {
                // Check if the updated email already exists in the database
                Optional<User> existingEmailUser = userRepository.findUserByEmail(userUpdate.getEmail());
                if (existingEmailUser.isPresent()) {
                    String errorMessage = "Email taken";
                    return ResponseEntity.badRequest().body(errorMessage);
                }
                user.setEmail(userUpdate.getEmail());
            }

            // Check if msidn is being updated
            if (userUpdate.getMsidn() != null && !userUpdate.getMsidn().equals(user.getMsidn())) {
                // Check if the updated phone number already exists in the database
                Optional<com.jobapp.user_service.Model.User> existingMsidnUser = userRepository.findUserByMsidn(userUpdate.getMsidn());
                if (existingMsidnUser.isPresent()) {
                    String errorMessage = "msidn taken";
                    return ResponseEntity.badRequest().body(errorMessage);
                }
                user.setMsidn(userUpdate.getMsidn());
            }

            userRepository.save(user);
            String message = "UserDTO with id %s successfully updated.".formatted(id);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(message);
        }

        String message = "Customer with id %s not found.".formatted(id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }
    public ResponseEntity<Object> changePassword(Long id, UserPasswordUpdate userPasswordUpdate) {
        PasswordEncoder passwordEncoder = new PasswordEncoder();

        Optional<User> existingUser = userRepository.findUserById(id);
        User user = null;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            if (!passwordEncoder.bCryptPasswordEncoder().matches(userPasswordUpdate.getOldPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body("Old password is incorrect");
            }

            if (!userPasswordUpdate.getNewPassword().equals(userPasswordUpdate.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("New password and confirm password do not match");
            }
            if (userPasswordUpdate.getOldPassword().equals(userPasswordUpdate.getNewPassword())){
                return ResponseEntity.badRequest().body("new and old password are the same");
            }

            user.setPassword(passwordEncoder.bCryptPasswordEncoder().encode(userPasswordUpdate.getNewPassword()));
            userRepository.save(user);
            // Return a success response
            return ResponseEntity.ok("Password changed successfully");
        }
        String message = "Customer with id %s not found.".formatted(id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    public ResponseEntity<Object> forgetPassword(PasswordReset passwordReset) {
        PasswordEncoder passwordEncoder = new PasswordEncoder();
        Optional<User> validityEmail = userRepository.findUserByEmail(passwordReset.getEmail());
        User user = null;
        if (validityEmail.isPresent()) {
            user = validityEmail.get();
            if (!passwordEncoder.bCryptPasswordEncoder().matches(passwordReset.getActor(), user.getName())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incorrect");
            }
            user.setPassword(passwordEncoder.bCryptPasswordEncoder().encode(passwordReset.getPassword()));
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.OK).body("Password reset successful, try login now");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email does not exist");
    }

    @Transactional
    public ResponseEntity<Object> deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        String message = "User not found with ID %s: ".formatted(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }

        User user = userOptional.get();
        userRepository.delete(user);

        String successMessage = "User with ID %s successfully deleted.".formatted(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(successMessage);
    }
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper)
                .collect(Collectors.toList());
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) {
//        User user = userRepository.findUserByUsername(username);
//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                user.getPassword(),
//                user.getAuthorities()
//        );
    }
