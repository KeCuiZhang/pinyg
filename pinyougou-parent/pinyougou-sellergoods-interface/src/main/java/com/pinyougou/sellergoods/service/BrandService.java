package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

/**
 * 品牌接口
 * @author Administrator
 *
 */
public interface BrandService {

	public List<TbBrand> findAll();
	public PageResult findPage(Integer pageNum,Integer pageSize) ;
	public void add(TbBrand tbBrand);
	public TbBrand findOne(long id);
	public void update(TbBrand tbBrand);
	public void delete(long[] ids);
	public PageResult search(TbBrand tbBrand,Integer pageNum,Integer pageSize );
    List<Map> selectOptionList();
	
}
