package com.zhangjun.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.gulimall.member.entity.IntegrationChangeHistoryEntity;

import java.util.Map;

/**
 * ???ֱ仯??ʷ??¼
 *
 * @author zhangjun
 * @email 2328432115@qq.com
 * @date 2022-04-12 11:12:18
 */
public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

