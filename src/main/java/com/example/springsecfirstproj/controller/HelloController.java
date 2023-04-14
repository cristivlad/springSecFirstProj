package com.example.springsecfirstproj.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public ResponseEntity<String> getHello() {
        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }

    @GetMapping("/aloha")
    public ResponseEntity<String> getAloha() {
        SecurityContext context = SecurityContextHolder.getContext();
        var auth = context.getAuthentication().getName();
        return new ResponseEntity<>("Aloha + " + auth, HttpStatus.OK);
    }

    @GetMapping("/ciao")
    public ResponseEntity<String> getCiao() throws Exception {
        Callable<String> task = () -> {
            SecurityContext context = SecurityContextHolder.getContext();
            return context.getAuthentication().getName();
        };
        ExecutorService e = Executors.newCachedThreadPool();
        try {
            var contextTask = new DelegatingSecurityContextCallable<>(task);
            return new ResponseEntity<>("Ciao, " + e.submit(contextTask).get() + "!", HttpStatus.OK);
        } finally {
            e.shutdown();
        }
    }

    @GetMapping("/hola")
    public String hola() throws Exception {
        Callable<String> task = () -> {
            SecurityContext context = SecurityContextHolder.getContext();
            return context.getAuthentication().getName();
        };
        ExecutorService e = Executors.newCachedThreadPool();
        e = new DelegatingSecurityContextExecutorService(e);
        try {
            return "Hola, " + e.submit(task).get() + "!";
        } finally {
            e.shutdown();
        }
    }
}
