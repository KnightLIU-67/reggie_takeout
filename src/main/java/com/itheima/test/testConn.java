package com.itheima.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
public class testConn {

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public void testGet(){
        System.out.println("enter get /test/test_get");
        log.info("enter get /test/test_get");
    }
}
