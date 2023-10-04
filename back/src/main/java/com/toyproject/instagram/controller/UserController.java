package com.toyproject.instagram.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUser(@PathVariable int userId) {    //해당 조건에대한 값을 가져오는 어노테이션

        return ResponseEntity.ok().body(null);
    }
}
