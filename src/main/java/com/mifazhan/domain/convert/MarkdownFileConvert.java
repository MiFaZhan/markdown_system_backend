package com.mifazhan.domain.convert;

import com.mifazhan.domain.dto.MarkdownFileDTO;
import com.mifazhan.domain.entity.MarkdownFile;
import com.mifazhan.domain.vo.MarkdownFileVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author MIFAZHAN
 */
@Mapper(componentModel = "spring")
public interface MarkdownFileConvert {


    MarkdownFile toEntity(MarkdownFileDTO markdownFileDTO);

    MarkdownFileVO toVO(MarkdownFile markdownFile);

    List<MarkdownFile> toEntityList(List<MarkdownFileDTO> markdownFileDTOList);

    List<MarkdownFileVO> toVOList(List<MarkdownFile> markdownFileList);
}