package reactspring.task_manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactspring.task_manager.entities.Roles;

@Repository
@Transactional
public interface RoleRepository extends JpaRepository<Roles, Long> {
    Roles findByRole(String role);
}
