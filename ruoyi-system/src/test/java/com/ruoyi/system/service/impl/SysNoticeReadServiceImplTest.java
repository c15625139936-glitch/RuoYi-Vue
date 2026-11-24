package com.ruoyi.system.service.impl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.domain.SysNoticeRead;
import com.ruoyi.system.flag.FeatureFlagService;
import com.ruoyi.system.mapper.SysNoticeReadMapper;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * SysNoticeReadServiceImpl 单元测试。
 * Mapper 全部 Mock，不连数据库；验证落库、空参短路、透传、flag 包裹逻辑。
 */
@ExtendWith(MockitoExtension.class)
class SysNoticeReadServiceImplTest {

    /** 与 SysNoticeReadServiceImpl 中保持一致的 flag key */
    private static final String FLAG_NOTICE_NEW_READ_VIEW = "notice.new-read-view";

    @Mock
    private SysNoticeReadMapper noticeReadMapper;

    @Mock
    private FeatureFlagService featureFlagService;

    @InjectMocks
    private SysNoticeReadServiceImpl service;

    @Test
    void markRead_shouldInsertRecordWithIds() {
        service.markRead(10L, 100L);

        ArgumentCaptor<SysNoticeRead> captor = ArgumentCaptor.forClass(SysNoticeRead.class);
        verify(noticeReadMapper).insertNoticeRead(captor.capture());
        SysNoticeRead record = captor.getValue();
        assertEquals(10L, record.getNoticeId());
        assertEquals(100L, record.getUserId());
    }

    @Test
    void selectUnreadCount_shouldPassThroughMapper() {
        when(noticeReadMapper.selectUnreadCount(100L)).thenReturn(3);
        assertEquals(3, service.selectUnreadCount(100L));
    }

    @Test
    void markReadBatch_nullOrEmpty_shouldSkipMapper() {
        service.markReadBatch(100L, null);
        service.markReadBatch(100L, new Long[]{});
        verify(noticeReadMapper, never()).insertNoticeReadBatch(anyLong(), any());
    }

    @Test
    void markReadBatch_withIds_shouldCallMapper() {
        Long[] ids = {1L, 2L};
        service.markReadBatch(100L, ids);
        verify(noticeReadMapper).insertNoticeReadBatch(100L, ids);
    }

    @Test
    void selectNoticeListWithReadStatus_shouldPassThrough() {
        List<SysNotice> expected = Collections.emptyList();
        when(noticeReadMapper.selectNoticeListWithReadStatus(100L, 5)).thenReturn(expected);
        assertSame(expected, service.selectNoticeListWithReadStatus(100L, 5));
    }

    @Test
    void deleteByNoticeIds_shouldCallMapper() {
        Long[] ids = {1L, 2L};
        service.deleteByNoticeIds(ids);
        verify(noticeReadMapper).deleteByNoticeIds(ids);
    }

    @Test
    void getUnreadBadgeCount_flagOff_shouldReturnZeroAndNotHitMapper() {
        when(featureFlagService.isOn(FLAG_NOTICE_NEW_READ_VIEW, 100L)).thenReturn(false);

        assertEquals(0, service.getUnreadBadgeCount(100L));

        verify(noticeReadMapper, never()).selectUnreadCount(anyLong());
    }

    @Test
    void getUnreadBadgeCount_flagOn_shouldPassThroughUnreadCount() {
        when(featureFlagService.isOn(FLAG_NOTICE_NEW_READ_VIEW, 100L)).thenReturn(true);
        when(noticeReadMapper.selectUnreadCount(100L)).thenReturn(5);

        assertEquals(5, service.getUnreadBadgeCount(100L));
    }
}
