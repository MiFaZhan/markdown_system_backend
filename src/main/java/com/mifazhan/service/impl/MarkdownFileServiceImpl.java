package com.mifazhan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mifazhan.domain.convert.MarkdownFileConvert;
import com.mifazhan.domain.dto.MarkdownFileDTO;
import com.mifazhan.domain.entity.MarkdownFile;
import com.mifazhan.domain.exception.BusinessException;
import com.mifazhan.domain.vo.MarkdownFileVO;
import com.mifazhan.service.MarkdownFileService;
import com.mifazhan.mapper.MarkdownFileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author MIFAZHAN
* @description 针对表【markdown_file(Markdown 文件表)】的数据库操作Service实现
* @createDate 2025-12-16 15:01:03
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class MarkdownFileServiceImpl extends ServiceImpl<MarkdownFileMapper, MarkdownFile>
    implements MarkdownFileService{

    private final MarkdownFileMapper markdownFileMapper;
    private final MarkdownFileConvert markdownFileConvert;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarkdownFileVO insertMarkdownFile(MarkdownFileDTO markdownFileDTO) {
        log.info("开始插入Markdown文件: {}", markdownFileDTO);
        
        if (markdownFileDTO == null){
            log.error("新增markdownDTO为空");
            throw new BusinessException(400, "新增markdownDTO不能为空");
        }

        // 验证userId是否为空
        if (markdownFileDTO.getUserId() == null) {
            log.error("用户ID不能为空，DTO: {}", markdownFileDTO);
            throw new BusinessException(400, "用户ID不能为空");
        }

        // 验证markdownName是否为空
        if (markdownFileDTO.getMarkdownName() == null || markdownFileDTO.getMarkdownName().trim().isEmpty()) {
            log.error("文件名不能为空，DTO: {}", markdownFileDTO);
            throw new BusinessException(400, "文件名不能为空");
        }

        MarkdownFile markdownFile = markdownFileConvert.toEntity(markdownFileDTO);
        log.debug("转换后的MarkdownFile对象: {}", markdownFile);

        if(this.save(markdownFile)){
            log.info("成功保存Markdown文件，ID: {}", markdownFile.getMarkdownId());
            MarkdownFileVO result = markdownFileConvert.toVO(this.getById(markdownFile.getMarkdownId()));
            log.info("返回的VO对象: {}", result);
            return result;
        }else {
            log.error("新增markdownFile失败，DTO: {}", markdownFileDTO);
            throw new BusinessException(500, "新增markdownFile失败");
        }
    }

    @Override
    public MarkdownFileVO getMarkdownFileById(Long id) {
        log.info("开始查询Markdown文件，ID: {}", id);
        
        if (id == null) {
            log.error("查询的ID为空");
            throw new BusinessException(400, "查询的ID不能为空");
        }
        
        MarkdownFile markdownFile = this.getById(id);
        if (markdownFile != null) {
            log.debug("查询到的MarkdownFile对象: {}", markdownFile);
            MarkdownFileVO result = markdownFileConvert.toVO(markdownFile);
            log.info("成功查询Markdown文件，ID: {}", id);
            return result;
        } else {
            log.warn("未找到ID为 {} 的Markdown文件", id);
            throw new BusinessException(404, "未找到指定的Markdown文件");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarkdownFileVO updateMarkdownFile(Long id, MarkdownFileDTO markdownFileDTO) {
        log.info("开始更新Markdown文件，ID: {}，更新内容: {}", id, markdownFileDTO);
        
        if (id == null) {
            log.error("更新的ID为空");
            throw new BusinessException(400, "更新的ID不能为空");
        }
        
        if (markdownFileDTO == null) {
            log.error("更新的markdownDTO为空");
            throw new BusinessException(400, "更新的markdownDTO不能为空");
        }
        
        MarkdownFile existingFile = this.getById(id);
        if (existingFile == null) {
            log.error("未找到ID为 {} 的Markdown文件进行更新", id);
            throw new BusinessException(404, "无法更新：未找到指定的Markdown文件");
        }
        
        MarkdownFile markdownFile = markdownFileConvert.toEntity(markdownFileDTO);
        markdownFile.setMarkdownId(id); // 确保使用原始ID
        
        if (this.updateById(markdownFile)) {
            log.info("成功更新Markdown文件，ID: {}", id);
            MarkdownFileVO result = markdownFileConvert.toVO(markdownFile);
            log.debug("返回的更新后VO对象: {}", result);
            return result;
        } else {
            log.error("更新Markdown文件失败，ID: {}", id);
            throw new BusinessException(500, "更新markdownFile失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMarkdownFile(Long id) {
        log.info("开始删除Markdown文件，ID: {}", id);
        
        if (id == null) {
            log.error("删除的ID为空");
            throw new BusinessException(400, "删除的ID不能为空");
        }
        
        MarkdownFile existingFile = this.getById(id);
        if (existingFile == null) {
            log.warn("尝试删除不存在的Markdown文件，ID: {}", id);
            return false;
        }
        
        boolean result = this.removeById(id);
        if (result) {
            log.info("成功删除Markdown文件，ID: {}", id);
        } else {
            log.error("删除Markdown文件失败，ID: {}", id);
        }
        return result;
    }
}




