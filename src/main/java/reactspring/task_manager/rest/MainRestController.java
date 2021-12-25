package reactspring.task_manager.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactspring.task_manager.entities.CardTasks;
import reactspring.task_manager.entities.Cards;
import reactspring.task_manager.entities.Users;
import reactspring.task_manager.models.RegistrationDTO;
import reactspring.task_manager.models.UserDTO;
import reactspring.task_manager.services.CardService;
import reactspring.task_manager.services.UserService;

import javax.annotation.security.RolesAllowed;
import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class MainRestController {

    @Autowired
    private CardService cardService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/allcards/{keyword}")
    public ResponseEntity<?> getAllCards(@PathVariable(name = "keyword") String keyword){

        System.out.println(keyword);
        List<Cards> cards = cardService.getAllCards();
        if(!keyword.equals("all")){
            cards = cardService.findAllByNameLike(keyword);
        }
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @PostMapping(value = "/addcard")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addCard(@RequestBody Cards card){
        card.setAddedDate(new Timestamp(System.currentTimeMillis()));
        cardService.addCard(card);
        return ResponseEntity.ok(card);
    }

    @GetMapping(value = "/card/{id}")
    public ResponseEntity<?> getCard(@PathVariable(name = "id") Long id){
        if(id != null){

            Cards card = cardService.getCardById(id);
            if(card != null){
                List<CardTasks> cardTasks = cardService.getCardTasksByCard(card);
                return new ResponseEntity<>(card, HttpStatus.OK);
            }

        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/card_tasks/{id}")
    public ResponseEntity<?> getTasks(@PathVariable(name = "id") Long id){
        if(id != null){

            Cards card = cardService.getCardById(id);
            if(card != null){
                List<CardTasks> cardTasks = cardService.getCardTasksByCard(card);
                return new ResponseEntity<>(cardTasks, HttpStatus.OK);
            }

        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/addtask/{id}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addTask(@PathVariable(name = "id") Long cardId,
                                     @RequestBody CardTasks task){
        System.out.println(task.getTaskText());
        if(cardId!=null){
            Cards card = cardService.getCardById(cardId);

            if(card != null){
                if(task.getId()==null){

                    task.setAddedDate(new Timestamp(System.currentTimeMillis()));
                    task.setCard(card);
                    cardService.addCardTask(task);
                    return ResponseEntity.ok(task);
                }
                else {
                    CardTasks existedTask = cardService.getTask(task.getId());
                    existedTask.setDone(task.isDone());
                    cardService.addCardTask(existedTask);
                    return ResponseEntity.ok(existedTask);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/deletecard/{id}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteCard(@PathVariable(name = "id") Long cardId){

        if(cardId!=null){
            Cards card = cardService.getCardById(cardId);

            if(card!=null){
                List<CardTasks> tasks = cardService.getCardTasksByCard(card);
                if(tasks!=null){
                    for(CardTasks t : tasks){
                        cardService.deleteCardTask(t);
                    }
                }
                cardService.deleteCard(card);

                return ResponseEntity.ok(card);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping(value = "/profile")
    public ResponseEntity<?> profilePage(){
        Users user = getUser();
        return new ResponseEntity<>(new UserDTO(user.getId(), user.getEmail(), user.getFullName(), user.getRoles()), HttpStatus.OK);
    }

    @PostMapping(value = "/update-profile")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateProfile(@RequestBody RegistrationDTO updated){
        Users user = userService.findByEmail(updated.getEmail());
        if(user!=null){
            user.setFullName(updated.getFullName());
            updated.setSuccess(true);
            updated.setMessage("Updated");
            userService.updateUser(user);
        }
        return ResponseEntity.ok(updated);
    }

    @PostMapping(value = "/update-password")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updatePassword(@RequestBody RegistrationDTO updated){
        Users user = userService.findByEmail(updated.getEmail());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String error = "Incorrect old password";

        if(user!=null){
            if(encoder.matches(updated.getOldPassword(),user.getPassword())){
                error = "Password confirmation doesn't match with new password";

                if(updated.getPassword().equals(updated.getRePassword())){
                    error="Successfully updated";
                    user.setPassword(encoder.encode(updated.getPassword()));
                    updated.setSuccess(true);
                    userService.updateUser(user);
                }
            }
        }
        updated.setMessage(error);
        return ResponseEntity.ok(updated);
    }

    private Users getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            Users user = (Users) authentication.getPrincipal();
            System.out.println("I WAS HERE1");
            return user;
        }
        System.out.println("I WAS HERE RETURN NULL");
        return null;
    }


}
