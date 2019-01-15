package com.pinyougou.service;

import java.util.List;
import java.util.Map;

/**
 * @Author kecuiZhang
 * @Company http://www.springs.com
 */
public interface ItemSearchService {
    public Map searchMap(Map map);
    public void importList(List list);
    public void deleteByGoodsIds(List goodsIds);
}
