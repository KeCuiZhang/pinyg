package com.pinyougou.por.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author kecuiZhang
 * @Company http://www.springs.com
 */
@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;
    @RequestMapping("/findByCategoryId")
    public List<TbContent> findCategory(Long categoryId){
       return    contentService.findCategoryId(categoryId);
    }
}
