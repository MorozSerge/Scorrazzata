package com.example.proj.service;

import com.example.proj.domain.Role;
import com.example.proj.domain.User;
import com.example.proj.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.Math;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.*;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailSender mailSender;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);

        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }
        return user;

    }
     public boolean addUser(User user){
         User userFromDb = userRepo.findByUsername(user.getUsername());

         if(userFromDb != null){
             return false;
         }

         user.setActive(false);
         user.setRoles(Collections.singleton(Role.USER));
         user.setActivationCode(UUID.randomUUID().toString());
         user.setPassword(passwordEncoder.encode(user.getPassword()));
         user.setLat(59.93);
         user.setLon(30.32);
         userRepo.save(user);

         sendMessage(user);

         return true;

     }

    private void sendMessage(User user) {
        if (!StringUtils.isEmpty(user.getEmail())){
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to proj. Please visit next link: http://localhost:8080/activate/%s",
                    user.getUsername(),
                    user.getActivationCode()
            );
            mailSender.send(user.getEmail(), "Activation code", message );
        }
    }

    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);
        if (user == null){
            return false;
        }
        user.setActivationCode(null);
        user.setActive(true);
        userRepo.save(user);
        return true;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public List<User> findInRange(int range, double lon, double lat){
        List <User> userList = findAll();
        for(User user : findAll()){
            if(range<(2*6371008)*Math.asin(Math.sqrt(sin((lat-user.getLat())*acos(0)/180)*sin((lat-user.getLat())*acos(0)/180) + cos(lat*acos(0)/180)*cos(user.getLat()*acos(0)/180)*sin((lon-user.getLon())*acos(0)/180)*sin((lon-user.getLon())*acos(0)/180)))){
                userList.add(user);
            }
        }
        return userList;
    }

    public void saveUser(User user, String username, Map<String, String> form, boolean active) {
        user.setUsername(username);
        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());
        user.getRoles().clear();
        user.setActive(active);
        for (String key : form.keySet()) {
            if (roles.contains(key)){
                user.getRoles().add(Role.valueOf(key));
            }
        }
        userRepo.save(user);
    }

    public void updateProfile(User user, String password, String email) {
        String userEmail = user.getEmail();
        boolean isEmailChanged = (email != null && !email.equals(userEmail)) ||
                (userEmail != null && !userEmail.equals(email));
        if (isEmailChanged) {
            user.setEmail(email);

            if (!StringUtils.isEmpty(email)) {
                user.setActivationCode(UUID.randomUUID().toString());
            }
        }
        if (!StringUtils.isEmpty(password))
            user.setPassword(passwordEncoder.encode(password));
    userRepo.save(user);
    if(isEmailChanged)
        sendMessage(user);
    }

    public void setCoordinates(User user, String lon, String lat) {
        user.setLat(Double.parseDouble(lat));
        user.setLon(Double.parseDouble(lon));
        userRepo.save(user);
    }
}
