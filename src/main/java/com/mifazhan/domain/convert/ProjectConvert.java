package com.mifazhan.domain.convert;

import com.mifazhan.domain.dto.ProjectDTO;
import com.mifazhan.domain.dto.ProjectUpdateDTO;
import com.mifazhan.domain.entity.Project;
import com.mifazhan.domain.vo.ProjectVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectConvert {
    Project toEntity(ProjectDTO projectDTO);

    ProjectVO toVO(Project project);

    List<ProjectVO> toVOList(List<Project> projectList);

    List<Project> toEntityList(List<ProjectDTO> projectDTOList);

    Project toUpdateEntity(ProjectUpdateDTO projectUpdateDTO);

}
