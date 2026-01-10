package com.mifazhan.domain.convert;

import com.mifazhan.domain.dto.MarkdownContentDTO;
import com.mifazhan.domain.entity.MarkdownContent;
import com.mifazhan.domain.vo.MarkdownContentVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MarkdownContentConvert {
    MarkdownContent toEntity(MarkdownContentDTO markdownContentDTO);

    MarkdownContentVO toVO(MarkdownContent markdownContent);
}
