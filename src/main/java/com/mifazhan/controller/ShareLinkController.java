package com.mifazhan.controller;

import com.mifazhan.domain.dto.ShareAccessDTO;
import com.mifazhan.domain.dto.ShareCreateDTO;
import com.mifazhan.domain.dto.ShareUpdateDTO;
import com.mifazhan.domain.vo.Result;
import com.mifazhan.domain.vo.ShareContentVO;
import com.mifazhan.domain.vo.ShareLinkVO;
import com.mifazhan.service.ShareLinkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = "分享链接管理")
public class ShareLinkController {
    private final ShareLinkService shareLinkService;

    @PostMapping("/api/share")
    @ApiOperation("创建分享链接")
    public Result<ShareLinkVO> createShare(@Valid @RequestBody ShareCreateDTO dto) {
        return Result.success(shareLinkService.createShare(dto));
    }

    @PutMapping("/api/share")
    @ApiOperation("修改分享链接设置")
    public Result<ShareLinkVO> updateShare(@Valid @RequestBody ShareUpdateDTO dto) {
        return Result.success(shareLinkService.updateShare(dto));
    }

    @GetMapping("/api/share/list")
    @ApiOperation("获取我的分享列表")
    public Result<List<ShareLinkVO>> getMyShareList(
            @RequestParam(required = false) List<Integer> targetTypes,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long nodeId) {
        return Result.success(shareLinkService.getMyShareList(targetTypes, projectId, nodeId));
    }

    @DeleteMapping("/api/share/{shareId}")
    @ApiOperation("删除分享链接")
    public Result<Void> deleteShare(@PathVariable Long shareId) {
        shareLinkService.deleteShare(shareId);
        return Result.success();
    }

    @PostMapping("/api/share/public/{shareCode}")
    @ApiOperation("访问分享链接")
    public Result<ShareLinkVO> accessShare(@PathVariable String shareCode, @RequestBody(required = false) ShareAccessDTO dto) {
        return Result.success(shareLinkService.accessShare(shareCode, dto));
    }

    @GetMapping("/api/share/public/{shareCode}/content")
    @ApiOperation("获取分享内容")
    public Result<ShareContentVO> getShareContent(@PathVariable String shareCode) {
        return Result.success(shareLinkService.getShareContent(shareCode));
    }

    @GetMapping("/api/share/public/{shareCode}/file/{nodeId}")
    @ApiOperation("获取分享中的文件内容")
    public Result<String> getShareNodeContent(@PathVariable String shareCode, @PathVariable Long nodeId) {
        return Result.success(shareLinkService.getShareNodeContent(shareCode, nodeId));
    }
}
