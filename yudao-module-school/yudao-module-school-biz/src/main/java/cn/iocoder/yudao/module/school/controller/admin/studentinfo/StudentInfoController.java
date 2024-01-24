package cn.iocoder.yudao.module.school.controller.admin.studentinfo;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.school.controller.admin.studentinfo.vo.*;
import cn.iocoder.yudao.module.school.dal.dataobject.studentinfo.StudentInfoDO;
import cn.iocoder.yudao.module.school.convert.studentinfo.StudentInfoConvert;
import cn.iocoder.yudao.module.school.service.studentinfo.StudentInfoService;

@Tag(name = "管理后台 - 学生信息")
@RestController
@RequestMapping("/school/student-info")
@Validated
public class StudentInfoController {

    @Resource
    private StudentInfoService studentInfoService;

    @PostMapping("/create")
    @Operation(summary = "创建学生信息")
    @PreAuthorize("@ss.hasPermission('school:student-info:create')")
    public CommonResult<Long> createStudentInfo(@Valid @RequestBody StudentInfoCreateReqVO createReqVO) {
        return success(studentInfoService.createStudentInfo(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新学生信息")
    @PreAuthorize("@ss.hasPermission('school:student-info:update')")
    public CommonResult<Boolean> updateStudentInfo(@Valid @RequestBody StudentInfoUpdateReqVO updateReqVO) {
        studentInfoService.updateStudentInfo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除学生信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('school:student-info:delete')")
    public CommonResult<Boolean> deleteStudentInfo(@RequestParam("id") Long id) {
        studentInfoService.deleteStudentInfo(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得学生信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('school:student-info:query')")
    public CommonResult<StudentInfoRespVO> getStudentInfo(@RequestParam("id") Long id) {
        StudentInfoDO studentInfo = studentInfoService.getStudentInfo(id);
        return success(StudentInfoConvert.INSTANCE.convert(studentInfo));
    }

    @GetMapping("/list")
    @Operation(summary = "获得学生信息列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('school:student-info:query')")
    public CommonResult<List<StudentInfoRespVO>> getStudentInfoList(@RequestParam("ids") Collection<Long> ids) {
        List<StudentInfoDO> list = studentInfoService.getStudentInfoList(ids);
        return success(StudentInfoConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得学生信息分页")
    @PreAuthorize("@ss.hasPermission('school:student-info:query')")
    public CommonResult<PageResult<StudentInfoRespVO>> getStudentInfoPage(@Valid StudentInfoPageReqVO pageVO) {
        PageResult<StudentInfoDO> pageResult = studentInfoService.getStudentInfoPage(pageVO);
        return success(StudentInfoConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出学生信息 Excel")
    @PreAuthorize("@ss.hasPermission('school:student-info:export')")
    @OperateLog(type = EXPORT)
    public void exportStudentInfoExcel(@Valid StudentInfoExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<StudentInfoDO> list = studentInfoService.getStudentInfoList(exportReqVO);
        // 导出 Excel
        List<StudentInfoExcelVO> datas = StudentInfoConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "学生信息.xls", "数据", StudentInfoExcelVO.class, datas);
    }

}
