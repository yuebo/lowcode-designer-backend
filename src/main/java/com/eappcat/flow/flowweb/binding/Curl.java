package com.eappcat.flow.flowweb.binding;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

@Component
public class Curl implements BindingObject {
    @Override
    public String getName() {
        return "curl";
    }
    public Connection connect(String url){
        return Jsoup.connect(url).ignoreContentType(true);
    }
}
