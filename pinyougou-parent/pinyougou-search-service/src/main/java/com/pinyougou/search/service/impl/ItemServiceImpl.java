package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author kecuiZhang
 * @Company http://www.springs.com
 */
@Service
public class ItemServiceImpl implements ItemSearchService {
    @Autowired
    SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map searchMap(Map searchMap) {
           Map map=new HashMap();
           //关键字查询
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ",""));
        map.putAll(searchList(searchMap));
       String category = (String) searchMap.get("category");
        //根据关键字查询商品分类
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);
        //品牌、规格和规格选项查询
        if(!"".equals(category)){//如果有分类名称
            map.putAll(searchBrandListAndSpecList(category));
        }else {//如果没有分类名称，按第一个分类查询
            map.putAll(searchBrandListAndSpecList(categoryList.get(0)));
        }

             return map;

    }

    /**
     * 将新审核的商品导入到solr
     * @param list
     */
    @Override
    public void importList(List list) {
           solrTemplate.saveBeans(list);
           solrTemplate.commit();
    }

    /**
     * 批量删除索引
     * @param goodsIds
     */
    @Override
    public void deleteByGoodsIds(List goodsIds) {
        Query query =new SimpleQuery() ;
        Criteria criteria = new Criteria("item_goodsid").in(goodsIds);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 根据查询列表并高亮显示搜索框中的keywords
     * @param searchMap
     * @return
     */
    private Map searchList(Map searchMap){
        Map<String,Object> map=new HashMap();
         /*Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_keywords").is(map.get("keywords"));
        query.addCriteria(criteria);
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);*/
        HighlightQuery query=new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//设置高亮域、
        highlightOptions.setSimplePrefix("<em style='color:red'>");//设置高亮前缀
        highlightOptions.setSimplePostfix("</em>");//设置高亮后缀
        query.setHighlightOptions(highlightOptions);//设置高亮选项
        //1.根据关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //2.根据商品分类查询
        if(!"".equals(searchMap.get("category"))){
            Criteria filterCriteria=new Criteria("item_category").is(  searchMap.get("category"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //3.根据品牌查询
        if(!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria=new Criteria("item_brand").is(  searchMap.get("brand"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //4.根据规格和规格选项查询
        if(searchMap.get("spec")!=null){
            Map<String,String> specs = (Map<String,String>) searchMap.get("spec");
            for (String key : specs.keySet()) {
                Criteria filterCriteria=new Criteria("item_spec_"+key).is(  specs.get(key));
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //5.根据价格查询
        if(!"".equals(searchMap.get("price"))){
            String[] prices = searchMap.get("price").toString().split("-");
            if(!"0".equals(prices[0])) {
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if(!"*".equals(prices[1])){
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //6.分页
        Integer pageNo = (Integer) searchMap.get("pageNo");//当前页
        Integer pageSize = (Integer) searchMap.get("pageSize");//页条数
        if("".equals(pageNo)){
            //如果当前页为空
              pageNo=1;
        }
        if("".equals(pageSize)){
            pageSize=20;
        }
        query.setOffset((pageNo-1)*pageSize);//设置页的开始索引
        query.setRows(pageSize);//设置每页显示条数
        //7.根据字段排序排序
       String sort = (String) searchMap.get("sort");//排序方式ASC DESC
        String  sortField = (String) searchMap.get("sortField");//排序域
        if(sort!=null&&!"".equals(sort)){
            if("ASC".equals(sort)) {
                Sort orders = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(orders);
            }
            if("DESC".equals(sort)){
                Sort orders = new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(orders);
            }
        }


        //高亮集合入口
//获取所有查询的数据
        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //获取所有高亮域（高亮域可能不止“item_title”）
        List<HighlightEntry<TbItem>> highlighted = tbItems.getHighlighted();
        //循环单个高亮域
        for (HighlightEntry<TbItem> highlightEntry : highlighted) {
            TbItem tbItem = highlightEntry.getEntity();//获取原实体TbItem
            tbItem.setTitle(highlightEntry.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果

        }
        map.put("rows",tbItems.getContent());//页面内容
        map.put("totalPages",tbItems.getTotalPages());//总页数
        map.put("total",tbItems.getTotalElements());//总记录数
        return map;
    }

    /**
     * 根据搜索框中的keywords查询商品分类列表，原理：
     * 类似mysql中的分组查询
     * select category from tb_item where x=(keywords) group by category
     * @param map
     * @return
     */
    private List<String> searchCategoryList(Map map){
       List<String> list = new ArrayList<>();
        Query query = new SimpleQuery();
        //相当于sql中的where
        Criteria criteria = new Criteria("item_keywords").is(map.get("keywords"));
        query.addCriteria(criteria);
        //设置分组域相当于sql中的group by
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        GroupPage<TbItem> tbItems = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> tbItemsGroupResult = tbItems.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = tbItemsGroupResult.getGroupEntries();
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content) {
            list.add(entry.getGroupValue());
        }
        return list;
    }

    /**
     * 从Redis中查询模板中的规格和规格选项
     * @param category
     * @return
     */
    private Map searchBrandListAndSpecList(String category){
        Map map=new HashMap<>();
       Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (typeId!=null){
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("brandList",brandList);
            map.put("specList",specList);
        }

        return map;
    }
}
