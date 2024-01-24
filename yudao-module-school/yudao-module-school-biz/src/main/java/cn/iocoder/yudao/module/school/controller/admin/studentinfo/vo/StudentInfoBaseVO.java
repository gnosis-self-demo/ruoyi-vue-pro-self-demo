package cn.iocoder.yudao.module.school.controller.admin.studentinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import javax.validation.constraints.*;

/**
 * 学生信息 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class StudentInfoBaseVO {

    @Schema(description = "学生姓名", example = "张三")
    private String stuName;

    @Schema(description = "学生年龄")
    private String stuAge;

}
