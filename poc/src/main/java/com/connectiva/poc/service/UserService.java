package com.connectiva.poc.service;

import com.connectiva.poc.model.User;
import com.connectiva.poc.repository.UserRepository;


import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.rmi.server.UID;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    public List<User> getAllUser()
    {
        return this.userRepository.findAll();
    }
    public User createUser(User user){
        user.setId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return this.userRepository.save(user);}
}
