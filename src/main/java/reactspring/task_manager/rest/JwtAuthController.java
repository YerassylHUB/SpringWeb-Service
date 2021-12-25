package reactspring.task_manager.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactspring.task_manager.entities.Users;
import reactspring.task_manager.models.JwtRequest;
import reactspring.task_manager.models.JwtResponse;
import reactspring.task_manager.jwt.JwtTokenGenerator;
import reactspring.task_manager.models.Message;
import reactspring.task_manager.models.RegistrationDTO;
import reactspring.task_manager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;

@RestController
@CrossOrigin
public class JwtAuthController {

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @RequestMapping(value = "/auth")
    public ResponseEntity<?> auth(@RequestBody JwtRequest request) throws Exception{

        authenticate(request.getEmail(), request.getPassword());
        final UserDetails userDetails =
                userService.loadUserByUsername(request.getEmail());

        final String token = jwtTokenGenerator.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));

    }

    @PostMapping(value = "/signup")
    public ResponseEntity<?> signUp(@RequestBody RegistrationDTO registrationDTO){

        registrationDTO.setMessage("User with such email is exists");
        registrationDTO.setSuccess(false);
        if(userService.findByEmail(registrationDTO.getEmail()) == null){
            registrationDTO.setMessage("Password confirmation isn't match with password");
            if(registrationDTO.getPassword().equals(registrationDTO.getRePassword())){
                registrationDTO.setMessage("You have successfully signed up!");
                Users newUser = new Users();
                newUser.setEmail(registrationDTO.getEmail());
                newUser.setPassword(registrationDTO.getPassword());
                newUser.setFullName(registrationDTO.getFullName());
                registrationDTO.setSuccess(true);
                userService.createUser(newUser);
            }
        }

        return new ResponseEntity<>(registrationDTO, HttpStatus.OK);
    }

    public void authenticate(String email, String password) throws Exception{

        try{

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        }catch (DisabledException e){
            throw new Exception("USER_DISABLED", e);
        }catch (BadCredentialsException e){
            throw new Exception("INVALID_CREDENTIALS", e);
        }

    }

}