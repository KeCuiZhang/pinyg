package com.pinyougou.solrutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

/**
 * @Author kecuiZhang
 * @Company http://www.springs.com
 */
@Component
public class DeleteSolr {
    @Autowired
    private SolrTemplate solrTemplate;
    public static void main(String[] args) {
        ApplicationContext context=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        DeleteSolr deleteSolr = (DeleteSolr) context.getBean("deleteSolr");
        deleteSolr.testDeleteAll();//将数据库TbItem表中的数据导入solr中

    }

    /**
     * 删除所有存储在solrhome中的数据数据
     */
    public void testDeleteAll(){
        Query query=new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
