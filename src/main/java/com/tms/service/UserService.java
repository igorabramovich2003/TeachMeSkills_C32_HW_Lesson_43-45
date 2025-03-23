package com.tms.service;

import com.tms.model.User;
import com.tms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserById(Long id){
        return userRepository.getUserById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAllUsers();
    }
    public Optional<User> updateUser(User user) {
        Boolean updated = userRepository.updateUser(user);
        if (updated) {
            return userRepository.getUserById(user.getId());
        }
        return Optional.empty();
    }

    public Optional<User> deleteUser(Long id) {
        Optional<User> userOptional = userRepository.getUserById(id);
        System.out.println("Starting the process of deleting a user with ID:"+ id);
        if (userOptional.isEmpty()) {
            System.out.println("User with ID not found: "+ id);
            return Optional.empty();
        }
        User user = userOptional.get();
        user.setDeleted(true);
        Boolean isDeleted = userRepository.deleteUser(id);
        if (!isDeleted) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    public Optional<User> createUser(User user){
        Optional<Long> userId = userRepository.createUser(user);
        if(userId.isPresent()){
            return getUserById(userId.get());
        }
        return Optional.empty();
    }
}
