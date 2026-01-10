package com.mifazhan.domain.convert;

import com.mifazhan.domain.dto.NodeDTO;
import com.mifazhan.domain.dto.NodeUpdateDTO;
import com.mifazhan.domain.entity.Node;
import com.mifazhan.domain.vo.NodeVO;
import com.mifazhan.domain.vo.NodeTreeVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author MIFAZHAN
 */
@Mapper(componentModel = "spring")
public interface NodeConvert {
    Node toEntity(NodeDTO nodeDTO);

    NodeVO toVO(Node node);

    List<Node> toEntityList(List<NodeDTO> nodeDTOList);

    List<NodeVO> toVOList(List<Node> nodeList);

    Node toEntity(NodeUpdateDTO nodeUpdateDTO);

    /**
     * Node转换为NodeItemVO
     */
    NodeTreeVO.NodeItemVO toNodeItemVO(Node node);

    /**
     * NodeVO转换为NodeItemVO
     */
    NodeTreeVO.NodeItemVO toNodeItemVO(NodeVO nodeVO);
}