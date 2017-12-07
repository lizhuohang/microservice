package com.lzh.micro.example.controller;

import com.lzh.micro.framework.annotation.ServiceName;
import com.lzh.micro.framework.annotation.UpperServiceName;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: lizhuohang
 *
 * @Date: Created in 16:04 17/12/6
 */
@RestController
@UpperServiceName({"test1","upper1","hello1"})
public class ServiceRegistryController {
    @RequestMapping(method = RequestMethod.GET, path = "/hello")
    @ServiceName("helloService")
    public String hello() {
        return "Hello";
    }


    @RequestMapping(method = RequestMethod.GET, path = "/hello1")
    @ServiceName("helloService")
    public String hello1() {
        return "Hello1";
    }
}
