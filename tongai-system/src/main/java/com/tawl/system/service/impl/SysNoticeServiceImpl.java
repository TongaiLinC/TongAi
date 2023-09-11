package com.tawl.system.service.impl;

import com.tawl.common.enums.NoticeStatus;
import com.tawl.common.utils.SecurityUtils;
import com.tawl.system.domain.SysNotice;
import com.tawl.system.domain.SysNoticeRead;
import com.tawl.system.mapper.SysNoticeMapper;
import com.tawl.system.service.ISysNoticeReadService;
import com.tawl.system.service.ISysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 公告 服务层实现
 *
 * @author tongai
 */
@Transactional(rollbackFor = Exception.class, readOnly = true)
@Service
public class SysNoticeServiceImpl implements ISysNoticeService {
    @Autowired
    private SysNoticeMapper noticeMapper;
    @Autowired
    private ISysNoticeReadService noticeReadService;

    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNotice selectNoticeById(Long noticeId) {
        return noticeMapper.selectNoticeById(noticeId);
    }

    /**
     * 查询公告列表(发布者列表)
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice) {
        notice.setCreateBy(SecurityUtils.getUsername());
        return noticeMapper.selectNoticeList(notice);
    }

    /**
     * 查询公告列表(用户列表)
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectReadNoticeList(SysNotice notice) {
        notice.setReceiverId(SecurityUtils.getUserId());
        return noticeMapper.selectReadNoticeList(notice);
    }

    /**
     * 查询公告列表(未读列表)
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectUnReadList(SysNotice notice) {
        notice.setReceiverId(SecurityUtils.getUserId());
        return noticeMapper.selectUnReadList(notice);
    }

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertNotice(SysNotice notice) {
        notice.setCreateBy(SecurityUtils.getUsername());
        int insertNotice = noticeMapper.insertNotice(notice);
        if (NoticeStatus.NO_PUBLISH.toString().equals(notice.getStatus())) {
            return insertNotice;
        }
        if (insertNotice != 0 && !SysNotice.DEFAULT_USER_IDS.equals(notice.getUserIds())) {
            for (String userId : notice.getUserIds().split(",")) {
                SysNoticeRead sysNoticeRead = new SysNoticeRead();
                sysNoticeRead.setNoticeId(notice.getNoticeId());
                sysNoticeRead.setIsRead("0");
                sysNoticeRead.setReceiverId(Long.valueOf(userId));
                noticeReadService.insertSysNoticeRead(sysNoticeRead);
            }
        }
        return insertNotice;
    }

    /**
     * 修改公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateNotice(SysNotice notice) {
        int updateNotice = noticeMapper.updateNotice(notice);
        if (NoticeStatus.NO_PUBLISH.toString().equals(notice.getStatus())) {
            return updateNotice;
        }
        if (updateNotice != 0 && !SysNotice.DEFAULT_USER_IDS.equals(notice.getUserIds())) {
            SysNoticeRead sysNoticeRead = new SysNoticeRead();
            sysNoticeRead.setIsRead("0");
            sysNoticeRead.setNoticeId(notice.getNoticeId());
            for (String userId : notice.getUserIds().split(",")) {
                sysNoticeRead.setReceiverId(Long.valueOf(userId));
                noticeReadService.insertSysNoticeRead(sysNoticeRead);
            }
        }
        return updateNotice;
    }

    /**
     * 删除公告对象
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteNoticeById(Long noticeId) {
        return noticeMapper.deleteNoticeById(noticeId);
    }

    /**
     * 批量删除公告信息
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteNoticeByIds(Long[] noticeIds) {
        return noticeMapper.deleteNoticeByIds(noticeIds);
    }

    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void changeReadStatus(Long noticeId) {
        SysNotice sysNotice = noticeMapper.selectNoticeById(noticeId);
        if (sysNotice != null) {
            SysNoticeRead sysNoticeRead = new SysNoticeRead();
            sysNoticeRead.setNoticeId(noticeId);
            sysNoticeRead.setReceiverId(SecurityUtils.getUserId());
            List<SysNoticeRead> sysNoticeReads = noticeReadService.selectSysNoticeReadList(sysNoticeRead);
            sysNoticeRead.setIsRead("1");
            if (sysNoticeReads.isEmpty()) {
                noticeReadService.insertSysNoticeRead(sysNoticeRead);
            } else {
                sysNoticeRead.setId(sysNoticeReads.get(0).getId());
                noticeReadService.updateSysNoticeRead(sysNoticeRead);
            }
        }
    }

    /**
     * 设置为已读
     *
     * @param noticeIds 需要设置的公告ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void setAllNoticeRead(Long[] noticeIds) {
        for (Long noticeId : noticeIds) {
            changeReadStatus(noticeId);
        }
    }

}
