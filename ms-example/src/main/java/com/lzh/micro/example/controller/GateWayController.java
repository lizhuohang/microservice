package com.lzh.micro.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * @Author: lizhuohang
 * @Date: Created in 14:59 17/12/7
 * 服务发现测试controller
 */
@Controller
public class GateWayController {
    @RequestMapping(method = RequestMethod.GET, path = "/gateway")
    public String index(Map<String, Object> model) {
        return "index";
    }
}
