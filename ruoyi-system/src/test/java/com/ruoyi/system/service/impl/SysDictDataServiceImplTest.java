package com.ruoyi.system.service.impl;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.system.mapper.SysDictDataMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * SysDictDataServiceImpl 缓存失效单测。
 * Mock Mapper，mockStatic 隔离 DictUtils，验证跨类型移动后新旧缓存都被刷新。
 */
@ExtendWith(MockitoExtension.class)
class SysDictDataServiceImplTest
{
    @Mock
    private SysDictDataMapper dictDataMapper;

    @InjectMocks
    private SysDictDataServiceImpl service;

    @Test
    void updateDictData_sameType_shouldRefreshOnlyCurrentCache()
    {
        SysDictData oldData = new SysDictData();
        oldData.setDictCode(1L);
        oldData.setDictType("A");
        SysDictData data = new SysDictData();
        data.setDictCode(1L);
        data.setDictType("A");

        when(dictDataMapper.selectDictDataById(1L)).thenReturn(oldData);
        when(dictDataMapper.updateDictData(data)).thenReturn(1);
        when(dictDataMapper.selectDictDataByType("A")).thenReturn(Collections.emptyList());

        try (MockedStatic<DictUtils> mocked = Mockito.mockStatic(DictUtils.class))
        {
            service.updateDictData(data);
            mocked.verify(() -> DictUtils.setDictCache(eq("A"), any()), times(1));
            mocked.verify(() -> DictUtils.setDictCache(eq("B"), any()), never());
        }
    }

    @Test
    void updateDictData_crossType_shouldRefreshBothCaches()
    {
        SysDictData oldData = new SysDictData();
        oldData.setDictCode(1L);
        oldData.setDictType("A");
        SysDictData data = new SysDictData();
        data.setDictCode(1L);
        data.setDictType("B");

        when(dictDataMapper.selectDictDataById(1L)).thenReturn(oldData);
        when(dictDataMapper.updateDictData(data)).thenReturn(1);
        when(dictDataMapper.selectDictDataByType("B")).thenReturn(Collections.emptyList());
        when(dictDataMapper.selectDictDataByType("A")).thenReturn(Collections.emptyList());

        try (MockedStatic<DictUtils> mocked = Mockito.mockStatic(DictUtils.class))
        {
            service.updateDictData(data);
            mocked.verify(() -> DictUtils.setDictCache(eq("B"), any()), times(1));
            mocked.verify(() -> DictUtils.setDictCache(eq("A"), any()), times(1));
        }
    }
}
