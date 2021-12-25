package reactspring.task_manager.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import reactspring.task_manager.entities.Users;

public interface UserService extends UserDetailsService {
    Users createUser(Users user);
    Users updateUser(Users user);
    Users findByEmail(String email);
}
