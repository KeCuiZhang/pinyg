package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;

import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {
/**
 * 分页
 */

	@Override
	public PageResult findPage(Integer pageNum, Integer pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum,pageSize);
		Page<TbBrand> page =( Page<TbBrand>)brandMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private TbBrandMapper brandMapper;
	/**
	 * 查询所有
	 */
	@Override
	public List<TbBrand> findAll() {

		return brandMapper.selectByExample(null);
	}
    
  /**
   * 新增
   */
	@Override
	public void add(TbBrand tbBrand) {
		// TODO Auto-generated method stub
		brandMapper.insert(tbBrand);
	}

@Override
public TbBrand findOne(long id) {
	// TODO Auto-generated method stub
	return brandMapper.selectByPrimaryKey(id);
}

@Override
public void update(TbBrand tbBrand) {
	// TODO Auto-generated method stub
	brandMapper.updateByPrimaryKey(tbBrand);
}

@Override
public void delete(long[] ids) {
	// TODO Auto-generated method stub
	for (long l : ids) {
		System.out.println(l+"service");
		brandMapper.deleteByPrimaryKey(l);
	}
}
@Override
	public PageResult search(TbBrand tbBrand, Integer pageNum, Integer pageSize) {
		// TODO Auto-generated method stub
	PageHelper.startPage(pageNum, pageSize);
	TbBrandExample example=new TbBrandExample();
	Criteria criteria = example.createCriteria();
	if(tbBrand!=null) {
		if(tbBrand.getName()!=null&&tbBrand.getName().length()>0) {
			criteria.andNameLike("%"+tbBrand.getName()+"%");
		}
		if(tbBrand.getFirstChar()!=null&&tbBrand.getFirstChar().length()>0) {
			criteria.andFirstCharLike("%"+tbBrand.getFirstChar()+"%");
		}
	}
	Page<TbBrand> page = (Page<TbBrand>)brandMapper.selectByExample(example);
	
		return new PageResult(page.getTotal(),page.getResult());
	}

    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }


}
