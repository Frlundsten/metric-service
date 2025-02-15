package com.helidon.adapter.application.k6.domain.service;

import com.helidon.application.domain.service.Service;

public class PostService implements Service {

    @Override
    public void sayHello() {
        System.out.println("POST ME POST ME HEHE");
    }
}
