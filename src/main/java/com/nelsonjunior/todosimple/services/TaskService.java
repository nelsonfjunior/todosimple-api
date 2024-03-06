package com.nelsonjunior.todosimple.services;

import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nelsonjunior.todosimple.models.Task;
import com.nelsonjunior.todosimple.models.User;
import com.nelsonjunior.todosimple.repositories.TaskRepository;
import com.nelsonjunior.todosimple.services.exceptions.DataBindingViolationException;
import com.nelsonjunior.todosimple.services.exceptions.ObjectNotFoundException;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    // Inicio do CRUD

    // Encontrar task pelo ID
    public Task findById(Long id) {
        @SuppressWarnings("null")
        Optional<Task> task = taskRepository.findById(id);
        return task.orElseThrow(()-> new ObjectNotFoundException("Task não encontrada! " + id + ", Tipo: " + Task.class.getName()));
    }

    public List<Task> findAllByUserId(Long userId) {
        List<Task> tasks = this.taskRepository.findByUser_Id(userId);
        return tasks;
    }

    // Criar uma nova task
    @Transactional
    public Task create(Task obj){
        User user = this.userService.findById(obj.getUser().getId());
        obj.setId(null);
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;
    }

    // Atualizar uma task ja existente
    @Transactional
    public Task update(Task obj){
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return this.taskRepository.save(newObj);
    }

    // Deletar uma task
    @SuppressWarnings("null")
    public void delete(Long id){
        findById(id);
        try {
            this.taskRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possível excluir! Pois há usuários relacionados!");
        }
    }
    

}
