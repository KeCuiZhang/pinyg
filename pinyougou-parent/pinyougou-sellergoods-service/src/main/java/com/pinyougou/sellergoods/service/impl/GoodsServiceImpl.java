package com.pinyougou.sellergoods.service.impl;

import java.util.*;

import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.pojoGroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;



import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
        List<TbGoods> tbGoods = goodsMapper.selectByExample(null);
        return tbGoods;
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}
     @Autowired
     private TbItemMapper tbItemMapper;
     @Autowired
     private TbBrandMapper tbBrandMapper;
     @Autowired
     private TbItemCatMapper itemCatMapper;
     @Autowired
     private TbSellerMapper sellerMapper;
  
	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
        TbGoods tbGoods = goods.getGoods();
        tbGoods.setAuditStatus("0");//状态未审核
        goodsMapper.insert(tbGoods);
        TbGoodsDesc tbGoodsDesc = goods.getGoodsDesc();
        //将商品基本表的id数据给扩展表
        tbGoodsDesc.setGoodsId(tbGoods.getId());
        goodsDescMapper.insert(tbGoodsDesc);
        setItemList( goods);
       
        
    
    }
	private void setItemValus(Goods goods,TbItem tbItem) {
		tbItem.setGoodsId(goods.getGoods().getId());//商品id
		tbItem.setSellerId(goods.getGoods().getSellerId());//商家id
		tbItem.setCategoryid(goods.getGoods().getCategory3Id());
		tbItem.setCreateTime(new Date());//创建日期
		tbItem.setUpdateTime(new Date());//更新时间
		//品牌名称
		TbBrand brand = tbBrandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		tbItem.setBrand(brand.getName());
		//分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		tbItem.setCategory(itemCat.getName());
		//商家名称
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		tbItem.setSeller(seller.getNickName());
		//图片地址（取 spu 的第一个图片）
		TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goods.getGoods().getId());
		List<Map> list = JSON.parseArray(goodsDesc.getItemImages(),Map.class);
		if(list.size()>0) {
			tbItem.setImage((String)list.get(0).get("url"));
		}
	
	}

	private void setItemList(Goods goods){
        if("1".equals(goods.getGoods().getIsEnableSpec())) {
            //标题
            List<TbItem> itemList = goods.getItemList();
            for (TbItem tbItem : itemList) {
                String title = goods.getGoods().getGoodsName();
                Map<String,Object> map = JSON.parseObject(tbItem.getSpec(),Map.class);
                for(String key:map.keySet()) {
                    title+=" "+map.get(key);
                }
                tbItem.setTitle(title);//标题
                setItemValus(goods,tbItem);
                tbItemMapper.insert(tbItem);
            }


        }else {
            TbItem item = new TbItem();
            item.setTitle(goods.getGoods().getGoodsName());//商品 KPU+规格描述串作为SKU 名称

            item.setPrice( goods.getGoods().getPrice() );//价格
            item.setStatus("1");//状态
            item.setIsDefault("1");//是否默认
            item.setNum(99999);//库存数量
            item.setSpec("{}");
            setItemValus(goods,item);
            tbItemMapper.insert(item);

        }
    }
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
	    goods.getGoods().setAuditStatus("0");
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		//删除sku
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getGoods().getId());
        tbItemMapper.deleteByExample(example);
        //插入sku数据
        setItemList(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
        Goods goods = new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
       criteria.andGoodsIdEqualTo(tbGoods.getId());
        System.out.println(tbGoods.getId());
        List<TbItem> itemList = tbItemMapper.selectByExample(example);
        goods.setItemList(itemList);
        return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			goodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
							criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		      criteria.andIsDeleteIsNull();
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public void updateStatus(Long[] ids,String status) {
        for (Long id : ids) {
            //更新商品表状态
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
            //更新item表状态
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(id);
            List<TbItem> tbItems = tbItemMapper.selectByExample(example);
            for (TbItem tbItem : tbItems) {
                tbItem.setStatus(status);
                tbItemMapper.updateByPrimaryKey(tbItem);
            }

        }
    }

    @Override
    public void updateDelete(Long[] ids, String num) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsDelete(num);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

    @Override
    public List<TbItem> findItems(Long[] ids, String status) {
        System.out.println(status);
        System.out.println(Arrays.asList(ids));
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();

     criteria.andGoodsIdIn(Arrays.asList(ids));
        criteria.andStatusEqualTo(status);
        List<TbItem> itemLis = tbItemMapper.selectByExample(example);
        System.out.println(itemLis.size());
        return itemLis;
    }


}
