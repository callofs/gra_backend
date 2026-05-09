package com.graProject.graBackend.controller;

import com.graProject.graBackend.common.annotation.HasPermission;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.result.HttpResult;
import com.graProject.graBackend.dto.DictCreateRequestDTO;
import com.graProject.graBackend.dto.DictDTO;
import com.graProject.graBackend.dto.DictUpdateRequestDTO;
import com.graProject.graBackend.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统模块（字典）接口。
 */
@RestController
@RequestMapping("/api/dict")
@Tag(name = "系统模块接口")
public class DictController {

    private final DictService dictService;

    public DictController(DictService dictService) {
        this.dictService = dictService;
    }

    /**
     * 获取系统模块列表。
     *
     * <p>
     * 用于前端加载模块/板块等下拉选项。
     * </p>
     *
     * @param dictType 字典类型
     * @return 模块列表
     */
    @Operation(summary = "获取系统模块列表")
    @GetMapping("/getDictList")
    public HttpResult<List<DictDTO>> listDict(@RequestParam("dictType") String dictType) {
        return HttpResult.success(dictService.listByType(dictType));
    }

    /**
     * 新增系统模块（管理员）。
     *
     * @param createRequestDTO 新增参数
     * @return 新增后的模块
     */
    @HasPermission(roles = { 3 })
    @Operation(summary = "新增系统模块（管理员）")
    @PostMapping("/addDict")
    public HttpResult<DictDTO> createDict(@RequestBody DictCreateRequestDTO createRequestDTO) {
        return HttpResult.success(dictService.createDict(createRequestDTO));
    }

    /**
     * 修改系统模块（管理员）。
     *
     * @param id               字典项 ID
     * @param updateRequestDTO 修改参数
     * @return 修改后的字典项
     */
    @HasPermission(roles = { 3 })
    @Operation(summary = "修改系统模块（管理员）")
    @PutMapping("/updateDict/{id}")
    public HttpResult<DictDTO> updateDict(@PathVariable("id") Long id,
            @RequestBody DictUpdateRequestDTO updateRequestDTO) {
        DictDTO dto = dictService.updateDict(id, updateRequestDTO);
        if (dto == null) {
            return HttpResult.of(HttpCode.NOT_FOUND, "模块不存在", null);
        }
        return HttpResult.success(dto);
    }
}
