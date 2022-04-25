package com.zhangjun.gulimall.search.service;

import com.zhangjun.gulimall.search.vo.SearchParam;
import com.zhangjun.gulimall.search.vo.SearchResult;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-20 19:21
 */
public interface MallSearchService {
    SearchResult search(SearchParam searchParam);
}
