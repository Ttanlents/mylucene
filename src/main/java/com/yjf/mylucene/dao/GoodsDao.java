package com.yjf.mylucene.dao;

import com.yjf.mylucene.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 余俊锋
 * @date 2020/11/13 11:05
 * @Description
 */
public interface GoodsDao extends JpaRepository<Goods,Integer> {

}
