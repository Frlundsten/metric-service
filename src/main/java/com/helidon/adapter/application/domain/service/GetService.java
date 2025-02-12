package com.helidon.adapter.application.domain.service;

import com.helidon.application.domain.service.Service;

public class GetService implements Service {

    @Override
    public void sayHello() {
        System.out.println("ENTER GET GET GET GET");
    }
}
