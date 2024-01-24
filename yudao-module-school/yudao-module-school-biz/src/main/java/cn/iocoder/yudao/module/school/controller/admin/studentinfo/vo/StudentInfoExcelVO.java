package cn.iocoder.yudao.module.school.controller.admin.studentinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * 学生信息 Excel VO
 *
 * @author 芋道源码
 */
@Data
public class StudentInfoExcelVO {

    @ExcelProperty("主键")
    private Long id;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("修改时间")
    private LocalDateTime updateTime;

    @ExcelProperty("学生姓名")
    private String stuName;

    @ExcelProperty("学生年龄")
    private String stuAge;

}
