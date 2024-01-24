package cn.iocoder.yudao.module.school.convert.studentinfo;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import cn.iocoder.yudao.module.school.controller.admin.studentinfo.vo.*;
import cn.iocoder.yudao.module.school.dal.dataobject.studentinfo.StudentInfoDO;

/**
 * 学生信息 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface StudentInfoConvert {

    StudentInfoConvert INSTANCE = Mappers.getMapper(StudentInfoConvert.class);

    StudentInfoDO convert(StudentInfoCreateReqVO bean);

    StudentInfoDO convert(StudentInfoUpdateReqVO bean);

    StudentInfoRespVO convert(StudentInfoDO bean);

    List<StudentInfoRespVO> convertList(List<StudentInfoDO> list);

    PageResult<StudentInfoRespVO> convertPage(PageResult<StudentInfoDO> page);

    List<StudentInfoExcelVO> convertList02(List<StudentInfoDO> list);

}
