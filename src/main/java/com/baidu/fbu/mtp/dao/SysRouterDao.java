package com.baidu.fbu.mtp.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.baidu.fbu.mtp.model.SystemRouter;

@Repository
public interface SysRouterDao {

    SystemRouter selectOneRoute(@Param("dstVersion") String dstVersion, @Param("mtpVersion") String mtpVersion);

    List<SystemRouter> selectAllRoute();
}
