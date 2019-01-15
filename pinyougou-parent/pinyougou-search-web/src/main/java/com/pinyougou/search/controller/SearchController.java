package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.ItemSearchService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author kecuiZhang
 * @Company http://www.springs.com
 */
@RestController
@RequestMapping("/itemsearch")
public class SearchController {
    @Reference
    private ItemSearchService itemSearchService;
    @RequestMapping("/search")
    public Map search(@RequestBody Map searchMap){
        return itemSearchService.searchMap(searchMap);
    }
}
