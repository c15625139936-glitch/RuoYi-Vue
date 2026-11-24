package com.ruoyi.system.service.impl;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.system.mapper.SysDictDataMapper;
import com.ruoyi.system.mapper.SysDictTypeMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * SysDictTypeServiceImpl 缓存失效单测。
 * Mock Mapper，mockStatic 隔离 DictUtils，验证改名后旧类型缓存被清除。
 */
@ExtendWith(MockitoExtension.class)
class SysDictTypeServiceImplTest
{
    @Mock
    private SysDictTypeMapper dictTypeMapper;

    @Mock
    private SysDictDataMapper dictDataMapper;

    @InjectMocks
    private SysDictTypeServiceImpl service;

    @Test
    void updateDictType_renamed_shouldRemoveOldCache()
    {
        SysDictType oldDict = new SysDictType();
        oldDict.setDictId(1L);
        oldDict.setDictType("sys_old");
        SysDictType dict = new SysDictType();
        dict.setDictId(1L);
        dict.setDictType("sys_new");

        when(dictTypeMapper.selectDictTypeById(1L)).thenReturn(oldDict);
        when(dictTypeMapper.updateDictType(dict)).thenReturn(1);
        when(dictDataMapper.selectDictDataByType("sys_new")).thenReturn(Collections.emptyList());

        try (MockedStatic<DictUtils> mocked = Mockito.mockStatic(DictUtils.class))
        {
            service.updateDictType(dict);
            mocked.verify(() -> DictUtils.removeDictCache("sys_old"));
            mocked.verify(() -> DictUtils.setDictCache(eq("sys_new"), any()));
            mocked.verify(() -> DictUtils.removeDictCache("sys_new"), never());
        }
    }

    @Test
    void updateDictType_sameType_shouldNotRemoveAnyCache()
    {
        SysDictType oldDict = new SysDictType();
        oldDict.setDictId(1L);
        oldDict.setDictType("sys_same");
        SysDictType dict = new SysDictType();
        dict.setDictId(1L);
        dict.setDictType("sys_same");

        when(dictTypeMapper.selectDictTypeById(1L)).thenReturn(oldDict);
        when(dictTypeMapper.updateDictType(dict)).thenReturn(1);
        when(dictDataMapper.selectDictDataByType("sys_same")).thenReturn(Collections.emptyList());

        try (MockedStatic<DictUtils> mocked = Mockito.mockStatic(DictUtils.class))
        {
            service.updateDictType(dict);
            mocked.verify(() -> DictUtils.removeDictCache(any()), never());
            mocked.verify(() -> DictUtils.setDictCache(eq("sys_same"), any()), times(1));
        }
    }
}
