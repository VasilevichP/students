package com.example.demo.Services;

import com.example.demo.Entities.Secretary;
import com.example.demo.Repositories.SecretaryRepositary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SecretaryService {
    private final SecretaryRepositary secretaryRepositary;

    @Autowired
    public SecretaryService(SecretaryRepositary secretaryRepositary) {
        this.secretaryRepositary = secretaryRepositary;
    }
    public Iterable<Secretary> getAllSecretaries() {
        return secretaryRepositary.findAll();
    }
    public boolean doesExist(){
        if (secretaryRepositary.count()==0) return false;
        return true;
    }
    public int howManyAdmins(){
        return secretaryRepositary.countAllByStatus(1);
    }
    public Iterable<Secretary> getAllByStatus(int status){
        return secretaryRepositary.getAllByStatus(status);
    }
    public Secretary getByLogin(String login){
        Optional<Secretary> secretary = secretaryRepositary.findById(login);
        return secretary.get();
    }
    public boolean approve(String login){
        try{
            Secretary secretary = secretaryRepositary.findById(login).get();
            secretary.setStatus(1);
            secretaryRepositary.save(secretary);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public boolean delete(String login){
        try{
            secretaryRepositary.deleteById(login);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
