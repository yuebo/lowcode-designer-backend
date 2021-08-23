package com.eappcat.flow.flowweb.binding;

import com.eappcat.flow.flowweb.BindingObject;
import org.springframework.stereotype.Component;

@Component
public class Random implements BindingObject {
    java.util.Random random = new java.util.Random();
    @Override
    public String getName() {
        return "random";
    }

    public int nextInt(int upper){
        return random.nextInt(upper);
    }
    public int nextInt(){
        return random.nextInt(100);
    }
}
