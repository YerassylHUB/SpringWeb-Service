package reactspring.task_manager.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "card_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardTasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    private Cards card;

    @Column(name = "task_text")
    private String taskText;

    @Column(name = "added_date")
    private Timestamp addedDate;

    @Column(name = "done")
    private boolean done;
}
