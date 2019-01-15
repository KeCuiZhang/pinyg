package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Author kecuiZhang
 * @Company http://www.springs.com
 */
@Component
public class SolrUtil {
    @Autowired
    SolrTemplate solrTemplate;
    @Autowired
    TbItemMapper itemMapper;
    public void addToSolr(){
        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);
        for (TbItem tbItem : tbItems) {
            Map<String,String> specMap = JSON.parseObject(tbItem.getSpec(), Map.class);
            tbItem.setSpecMap(specMap);
            System.out.println("标题"+tbItem.getTitle());
        }
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        ApplicationContext context=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
       SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
       solrUtil.addToSolr();//将数据库TbItem表中的数据导入solr中

    }
}
