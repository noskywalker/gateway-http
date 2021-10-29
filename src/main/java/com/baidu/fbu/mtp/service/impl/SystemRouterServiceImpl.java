package com.baidu.fbu.mtp.service.impl;

import java.util.List;
import java.util.Map;

import com.baidu.fbu.mtp.common.exception.MTPException;
import com.baidu.fbu.mtp.common.type.ResultCode;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.fbu.mtp.dao.SysRouterDao;
import com.baidu.fbu.mtp.model.SystemRouter;
import com.baidu.fbu.mtp.service.SystemRouterService;

import javax.annotation.Resource;

public class SystemRouterServiceImpl implements SystemRouterService {

    /** 日志实例. */
    private static final Logger logger = LoggerFactory.getLogger(SystemRouterServiceImpl.class);

    /** 系统路由表DAO. */
    @Resource
    private SysRouterDao sysRouterDao;

    /** 系统路由表. */
    private Map<String, SystemRouter> routerTable = Maps.newConcurrentMap();

    /** 是否首次加载. */
    private volatile boolean isLoaded = false;

    /**
     * 首次加载系统路由表.
     */
    public void initRouterTable() {
        try {
            List<SystemRouter> dataList = sysRouterDao.selectAllRoute();
            if (CollectionUtils.isEmpty(dataList)) {
                throw new RuntimeException();
            }

            for (SystemRouter route : dataList) {
                routerTable.put(route.generateKey(), route);
            }
            isLoaded = true;
        } catch (Exception e) {
            logger.error("首次加载系统路由表为空或者失败!", e);
            throw new RuntimeException("首次加载系统路由表为空或者失败!", e);
        }
    }

    /**
     * 更新系统路由表.
     */
    public void syncRouterTable() {
        try {
            if (! isLoaded) {
                return;
            }
            List<SystemRouter> dataList = sysRouterDao.selectAllRoute();
            if (CollectionUtils.isEmpty(dataList)) {
                throw new RuntimeException();
            }
            Map<String, SystemRouter> sycRouterTable = Maps.newHashMap();
            for (SystemRouter route : dataList) {
                sycRouterTable.put(route.generateKey(), route);
            }
            routerTable = sycRouterTable;
        } catch (Exception e) {
            logger.error("error when loading route table.", e);
        }
    }

    /**
     * 得到系统路由.
     */
    @Override
    public String getSystemURI(String mtpVersion, String dstVersion) {
        SystemRouter systemRouter;
        try {
            String key = Joiner.on(SystemRouter.SEPARATOR).join(mtpVersion, dstVersion);
            systemRouter = routerTable.get(key);
            if (systemRouter != null) {
                return systemRouter.getRoute();
            }
        } catch (Exception e) {
            systemRouter = sysRouterDao.selectOneRoute(dstVersion, mtpVersion);
            if (systemRouter != null) {
                return systemRouter.getRoute();
            }
            throw new MTPException(ResultCode.NO_ROUTE);
        }
        throw new MTPException(ResultCode.NO_ROUTE);
    }
}
