package reactspring.task_manager.services;

import reactspring.task_manager.entities.CardTasks;
import reactspring.task_manager.entities.Cards;

import java.util.List;

public interface CardService {

    List<Cards> getAllCards();
    Cards addCard(Cards card);
    Cards getCardById(Long id);
    void deleteCard(Cards card);
    List<Cards> findAllByNameLike(String name);

    List<CardTasks> getCardTasksByCard(Cards card);
    CardTasks addCardTask(CardTasks cardTask);
    void deleteCardTask(CardTasks task);
    CardTasks getTask(Long id);
}
