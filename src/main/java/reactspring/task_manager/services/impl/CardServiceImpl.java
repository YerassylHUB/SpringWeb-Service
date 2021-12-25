package reactspring.task_manager.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactspring.task_manager.entities.CardTasks;
import reactspring.task_manager.entities.Cards;
import reactspring.task_manager.repositories.CardRepository;
import reactspring.task_manager.repositories.CardTaskRepository;
import reactspring.task_manager.services.CardService;

import java.util.List;

@Service
public class CardServiceImpl implements CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardTaskRepository cardTaskRepository;

    @Override
    public List<Cards> getAllCards() {
        return cardRepository.findAll();
    }

    @Override
    public Cards addCard(Cards card) {
        return cardRepository.save(card);
    }

    @Override
    public Cards getCardById(Long id) {
        return cardRepository.findCardsById(id);
    }

    @Override
    public List<Cards> findAllByNameLike(String name) {
        return cardRepository.findAllByNameLike("%"+name+"%");
    }

    @Override
    public List<CardTasks> getCardTasksByCard(Cards card) {
        return cardTaskRepository.findAllByCard(card);
    }

    @Override
    public CardTasks addCardTask(CardTasks cardTask) {
        return cardTaskRepository.save((cardTask));
    }

    @Override
    public void deleteCard(Cards card) {
        cardRepository.delete(card);
    }

    @Override
    public void deleteCardTask(CardTasks task) {
        cardTaskRepository.delete(task);
    }

    @Override
    public CardTasks getTask(Long id) {
        return cardTaskRepository.getOne(id);
    }
}
