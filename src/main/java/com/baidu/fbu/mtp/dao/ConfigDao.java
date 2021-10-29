package com.baidu.fbu.mtp.dao;

import com.baidu.fbu.mtp.common.type.ConfigType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created on 13:01 11/04/2015.
 *
 * @author skywalker
 */
@Repository
public interface ConfigDao {

    List<String> selectValue(@Param("key") String key, @Param("type") ConfigType type);

}
