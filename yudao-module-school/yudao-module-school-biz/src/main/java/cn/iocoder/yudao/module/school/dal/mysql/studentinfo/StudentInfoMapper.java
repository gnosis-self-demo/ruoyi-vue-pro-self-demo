package cn.iocoder.yudao.module.school.dal.mysql.studentinfo;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.school.dal.dataobject.studentinfo.StudentInfoDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.school.controller.admin.studentinfo.vo.*;

/**
 * 学生信息 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface StudentInfoMapper extends BaseMapperX<StudentInfoDO> {

    default PageResult<StudentInfoDO> selectPage(StudentInfoPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StudentInfoDO>()
                .betweenIfPresent(StudentInfoDO::getCreateTime, reqVO.getCreateTime())
                .betweenIfPresent(StudentInfoDO::getUpdateTime, reqVO.getUpdateTime())
                .likeIfPresent(StudentInfoDO::getStuName, reqVO.getStuName())
                .eqIfPresent(StudentInfoDO::getStuAge, reqVO.getStuAge())
                .orderByDesc(StudentInfoDO::getId));
    }

    default List<StudentInfoDO> selectList(StudentInfoExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<StudentInfoDO>()
                .betweenIfPresent(StudentInfoDO::getCreateTime, reqVO.getCreateTime())
                .betweenIfPresent(StudentInfoDO::getUpdateTime, reqVO.getUpdateTime())
                .likeIfPresent(StudentInfoDO::getStuName, reqVO.getStuName())
                .eqIfPresent(StudentInfoDO::getStuAge, reqVO.getStuAge())
                .orderByDesc(StudentInfoDO::getId));
    }

}
