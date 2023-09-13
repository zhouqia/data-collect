package com.ipharmacare.collect.data.mapper;

import com.ipharmacare.collect.data.entity.OdsData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: zhouqiang
 * @Date: 2023/5/31 16:49
 */
public interface OdsDataMapper {

    Integer batchSaveOdsData(@Param("odsDataList") List<OdsData> odsDataList);
}
