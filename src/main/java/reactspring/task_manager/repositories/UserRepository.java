package reactspring.task_manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactspring.task_manager.entities.Users;

@Repository
@Transactional
public interface UserRepository  extends JpaRepository<Users, Long> {
    Users findByEmail(String email);
}
