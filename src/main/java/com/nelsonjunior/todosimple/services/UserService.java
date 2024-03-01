package com.nelsonjunior.todosimple.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nelsonjunior.todosimple.models.User;
import com.nelsonjunior.todosimple.repositories.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    // Inicio do CRUD

    // Encontrar usuário pelo ID
    public User findById(Long id) {
        @SuppressWarnings("null")
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(()-> new RuntimeException("Usuário não encontrado! " + id + ", Tipo: " + User.class.getName())); // nao para o programa caso tenha uma excecao
    }

    // Criar um novo usuário
    @Transactional // utilizado para caso for salvar algo no banco, por exemplo, create e update
    public User create(User obj){
        obj.setId(null); // garantir que ta criando um novo
        obj = this.userRepository.save(obj);
        return obj;
    }

    // Atualizar um usuário ja existente
    @Transactional
    private User update(User obj){
        User newObj = findById(obj.getId()); // pra ve se existe esse usuario 
        newObj.setPassword(obj.getPassword());
        return this.userRepository.save(newObj);
    }

    // Deletar um usuário que não tenhas tarefas
    @SuppressWarnings("null")
    public void delete(Long id){
        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não é possível excluir! Pois há tasks relacionadas!");
        }
    }


}
