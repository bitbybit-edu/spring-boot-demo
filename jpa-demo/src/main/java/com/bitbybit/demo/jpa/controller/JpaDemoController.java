package com.bitbybit.demo.jpa.controller;

import com.bitbybit.demo.jpa.entity.JpaUser;
import com.bitbybit.demo.jpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * @author liulin
 */
@RestController
@RequestMapping("jpa/demo")
public class JpaDemoController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("user/add")
    @ResponseBody
    public JpaUser addNewUser(@RequestParam String name, @RequestParam String email) {
        JpaUser n = new JpaUser();
        n.setName(name);
        n.setEmail(email);
        JpaUser save = userRepository.save(n);
        return save;
    }

    @PostMapping("user/findByName")
    public List<JpaUser> findByName(@RequestParam String name) {
        List<JpaUser> a = userRepository.findByName(name);
        Optional<JpaUser> byId = userRepository.findById(1L);
        return a;
    }
}
