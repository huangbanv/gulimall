package com.zhangjun.gulimall.search.controller;

import com.zhangjun.gulimall.search.service.MallSearchService;
import com.zhangjun.gulimall.search.vo.SearchParam;
import com.zhangjun.gulimall.search.vo.SearchResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-20 18:45
 */
@Controller
public class SearchController {

    @Autowired
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model, HttpServletRequest request){
        searchParam.setQueryString(request.getQueryString());
        SearchResult result = mallSearchService.search(searchParam);
        model.addAttribute("result",result);
        return "list";
    }
}
