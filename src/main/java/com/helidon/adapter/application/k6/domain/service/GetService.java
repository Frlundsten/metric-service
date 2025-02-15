package com.helidon.adapter.application.k6.domain.service;

import com.helidon.application.domain.service.Service;

public class GetService implements Service {

    @Override
    public void sayHello() {
        System.out.println("ENTER GET GET GET GET");
    }
}
