package com.mifazhan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mifazhan.domain.dto.ShareAccessDTO;
import com.mifazhan.domain.dto.ShareCreateDTO;
import com.mifazhan.domain.dto.ShareUpdateDTO;
import com.mifazhan.domain.entity.ShareLink;
import com.mifazhan.domain.vo.ShareContentVO;
import com.mifazhan.domain.vo.ShareLinkVO;

import java.util.List;

public interface ShareLinkService extends IService<ShareLink> {
    ShareLinkVO createShare(ShareCreateDTO dto);
    
    ShareLinkVO updateShare(ShareUpdateDTO dto);

    ShareLinkVO accessShare(String shareCode, ShareAccessDTO dto);

    ShareContentVO getShareContent(String shareCode);

    /**
     * 获取分享链接中的特定节点内容
     * @param shareCode 分享码
     * @param nodeId 节点ID
     * @return Markdown内容
     */
    String getShareNodeContent(String shareCode, Long nodeId);

    List<ShareLinkVO> getMyShareList(List<Integer> targetTypes, Long targetId);

    void deleteShare(Long shareId);

    void deleteShareByTarget(Integer targetType, Long targetId);

    void deleteNodeShares(List<Long> nodeIds);

    void restoreShareByTarget(Integer targetType, Long targetId);

    void restoreNodeShares(List<Long> nodeIds);

    void physicalDeleteShareByTarget(Integer targetType, Long targetId);

    void physicalDeleteNodeShares(List<Long> nodeIds);
}
