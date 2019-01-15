package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/brand")
public class BrandController {

	@Reference
	private BrandService brandService;
	
	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
		return brandService.findAll();		
	}
	@RequestMapping("/findPage")
	public PageResult findPage(Integer page,Integer size) {
		return brandService.findPage(page, size);
	}
	@RequestMapping("/add")
	public Result add(@RequestBody TbBrand tbBrand) {
		try {
			brandService.add(tbBrand);
			return new Result(true, "添加成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "添加失败");
		}
	}
	@RequestMapping("/findOne")
	public TbBrand findOne(long id) {
		return brandService.findOne(id);
	}
	@RequestMapping("/update")
	public Result update(@RequestBody TbBrand tbBrand) {
		try {
			brandService.update(tbBrand);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
		
	}
	@RequestMapping("/delete")
	public Result delete(long[] ids) {
		
		try {
			brandService.delete(ids);
			return new Result(true,"删除成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbBrand tbBrand,Integer page,Integer size) {
	return	brandService.search(tbBrand, page, size);
	}
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
	
}
