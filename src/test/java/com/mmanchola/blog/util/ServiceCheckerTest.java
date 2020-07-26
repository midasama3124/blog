package com.mmanchola.blog.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceCheckerTest {

    @Autowired
    ServiceChecker checker;

    @Test
    public void checkValidUrl() {
        String url = "";
        String urlChecked = checker.checkUrl(url).get();
        assert url.equals(urlChecked);
    }

}