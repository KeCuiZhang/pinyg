package com.pinyougou.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author kecuiZhang
 * @Company http://www.springs.com
 */
@RestController
@RequestMapping("/longins")
public class LonginsController {
    @RequestMapping("/name")
public Map getName(){
    String name = SecurityContextHolder.getContext().getAuthentication().getName();
    Map map = new HashMap();
    map.put("longinName",name);
    return map;
}
}
