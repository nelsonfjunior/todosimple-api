package com.nelsonjunior.todosimple.services;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nelsonjunior.todosimple.models.User;
import com.nelsonjunior.todosimple.models.enums.ProfileEnum;
import com.nelsonjunior.todosimple.repositories.UserRepository;
import com.nelsonjunior.todosimple.services.exceptions.DataBindingViolationException;
import com.nelsonjunior.todosimple.services.exceptions.ObjectNotFoundException;

@Service
public class UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder; //criptar o password
    
    @Autowired
    private UserRepository userRepository;

    // Inicio do CRUD

    // Encontrar usuário pelo ID
    public User findById(Long id) {
        @SuppressWarnings("null")
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(()-> new ObjectNotFoundException("Usuário não encontrado! " + id + ", Tipo: " + User.class.getName())); // nao para o programa caso tenha uma excecao
    }

    // Criar um novo usuário
    @Transactional // utilizado para caso for salvar algo no banco, por exemplo, create e update
    public User create(User obj){
        obj.setId(null); // garantir que ta criando um novo
        obj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword())); // senha criptografada
        obj.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet())); // garantir que quando o usuario for criado, for criado com o perfil de numero 2
        obj = this.userRepository.save(obj);
        return obj;
    }

    // Atualizar um usuário ja existente
    @Transactional
    public User update(User obj){
        User newObj = findById(obj.getId()); // pra ve se existe esse usuario 
        newObj.setPassword(obj.getPassword());
        newObj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword())); // senha criptografada
        return this.userRepository.save(newObj);
    }

    // Deletar um usuário que não tenhas tarefas
    @SuppressWarnings("null")
    public void delete(Long id){
        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possível excluir! Pois há tasks relacionadas!");
        }
    }


}
