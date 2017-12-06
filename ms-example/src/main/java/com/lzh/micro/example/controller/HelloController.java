package com.lzh.micro.example.controller;

import com.lzh.micro.framework.annotation.ServiceName;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: lizhuohang
 *
 * @Date: Created in 16:04 17/12/6
 */
@RestController
public class HelloController {
    @RequestMapping(method = RequestMethod.GET, path = "/hello")
    @ServiceName("helloService")
    public String hello() {
        return "Hello";
    }


    @RequestMapping(method = RequestMethod.GET, path = "/hello1")
    @ServiceName("hello1Service")
    public String hello1() {
        return "Hello1";
    }
}
