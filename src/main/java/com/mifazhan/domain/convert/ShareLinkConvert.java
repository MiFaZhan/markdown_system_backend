package com.mifazhan.domain.convert;

import com.mifazhan.domain.entity.ShareLink;
import com.mifazhan.domain.vo.ShareLinkVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ShareLinkConvert {
    @Mapping(target = "targetName", ignore = true)
    @Mapping(target = "hasPassword", expression = "java(shareLink.getPassword() != null && !shareLink.getPassword().isEmpty())")
    ShareLinkVO toVO(ShareLink shareLink);

    List<ShareLinkVO> toVOList(List<ShareLink> shareLinks);
}
