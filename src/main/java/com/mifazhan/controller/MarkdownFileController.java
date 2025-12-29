package com.mifazhan.controller;

import com.mifazhan.domain.convert.MarkdownFileConvert;
import com.mifazhan.domain.dto.MarkdownFileDTO;
import com.mifazhan.domain.entity.MarkdownFile;
import com.mifazhan.domain.vo.MarkdownFileVO;
import com.mifazhan.domain.vo.Result;
import com.mifazhan.service.MarkdownFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * @author MIFAZHAN
 */
@RestController
@RequestMapping("/api/markdownFile")
@Slf4j
public class MarkdownFileController {
    @Autowired
    private MarkdownFileService markdownFileService;

    @PostMapping
    public Result<MarkdownFileVO> createMarkdownFile(@Valid @RequestBody MarkdownFileDTO markdownFileDTO) {
        log.info("接收到创建Markdown文件请求: {}", markdownFileDTO);
        try {
            MarkdownFileVO result = markdownFileService.insertMarkdownFile(markdownFileDTO);
            log.info("成功创建Markdown文件，返回结果: {}", result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("处理创建Markdown文件请求时发生异常: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public Result<MarkdownFileVO> getMarkdownFile(@PathVariable Long id) {
        log.info("接收到查询Markdown文件请求，ID: {}", id);
        try {
            MarkdownFileVO result = markdownFileService.getMarkdownFileById(id);
            log.info("成功处理查询Markdown文件请求，ID: {}, 返回结果: {}", id, result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("处理查询Markdown文件请求时发生异常，ID: {}, 异常: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public Result<MarkdownFileVO> updateMarkdownFile(@PathVariable Long id, @Valid @RequestBody MarkdownFileDTO markdownFileDTO) {
        log.info("接收到更新Markdown文件请求，ID: {}, 更新内容: {}", id, markdownFileDTO);
        try {
            MarkdownFileVO result = markdownFileService.updateMarkdownFile(id, markdownFileDTO);
            log.info("成功处理更新Markdown文件请求，ID: {}, 返回结果: {}", id, result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("处理更新Markdown文件请求时发生异常，ID: {}, 异常: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteMarkdownFile(@PathVariable Long id) {
        log.info("接收到删除Markdown文件请求，ID: {}", id);
        try {
            boolean result = markdownFileService.deleteMarkdownFile(id);
            log.info("成功处理删除Markdown文件请求，ID: {}, 结果: {}", id, result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("处理删除Markdown文件请求时发生异常，ID: {}, 异常: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}