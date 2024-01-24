package cn.iocoder.yudao.module.school.service.studentinfo;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.school.controller.admin.studentinfo.vo.*;
import cn.iocoder.yudao.module.school.dal.dataobject.studentinfo.StudentInfoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import cn.iocoder.yudao.module.school.convert.studentinfo.StudentInfoConvert;
import cn.iocoder.yudao.module.school.dal.mysql.studentinfo.StudentInfoMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.*;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;

/**
 * 学生信息 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class StudentInfoServiceImpl implements StudentInfoService {

    @Resource
    private StudentInfoMapper studentInfoMapper;

    @Override
    public Long createStudentInfo(StudentInfoCreateReqVO createReqVO) {
        // 插入
        StudentInfoDO studentInfo = StudentInfoConvert.INSTANCE.convert(createReqVO);
        studentInfoMapper.insert(studentInfo);
        // 返回
        return studentInfo.getId();
    }

    @Override
    public void updateStudentInfo(StudentInfoUpdateReqVO updateReqVO) {
        // 校验存在
        validateStudentInfoExists(updateReqVO.getId());
        // 更新
        StudentInfoDO updateObj = StudentInfoConvert.INSTANCE.convert(updateReqVO);
        studentInfoMapper.updateById(updateObj);
    }

    @Override
    public void deleteStudentInfo(Long id) {
        // 校验存在
        validateStudentInfoExists(id);
        // 删除
        studentInfoMapper.deleteById(id);
    }

    private void validateStudentInfoExists(Long id) {
        if (studentInfoMapper.selectById(id) == null) {
            throw exception(STUDENT_INFO_NOT_EXISTS);
        }
    }

    @Override
    public StudentInfoDO getStudentInfo(Long id) {
        return studentInfoMapper.selectById(id);
    }

    @Override
    public List<StudentInfoDO> getStudentInfoList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return ListUtil.empty();
        }
        return studentInfoMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<StudentInfoDO> getStudentInfoPage(StudentInfoPageReqVO pageReqVO) {
        return studentInfoMapper.selectPage(pageReqVO);
    }

    @Override
    public List<StudentInfoDO> getStudentInfoList(StudentInfoExportReqVO exportReqVO) {
        return studentInfoMapper.selectList(exportReqVO);
    }

}
