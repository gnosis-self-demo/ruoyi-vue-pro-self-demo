package cn.iocoder.yudao.module.school.service.studentinfo;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.school.controller.admin.studentinfo.vo.*;
import cn.iocoder.yudao.module.school.dal.dataobject.studentinfo.StudentInfoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 学生信息 Service 接口
 *
 * @author 芋道源码
 */
public interface StudentInfoService {

    /**
     * 创建学生信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createStudentInfo(@Valid StudentInfoCreateReqVO createReqVO);

    /**
     * 更新学生信息
     *
     * @param updateReqVO 更新信息
     */
    void updateStudentInfo(@Valid StudentInfoUpdateReqVO updateReqVO);

    /**
     * 删除学生信息
     *
     * @param id 编号
     */
    void deleteStudentInfo(Long id);

    /**
     * 获得学生信息
     *
     * @param id 编号
     * @return 学生信息
     */
    StudentInfoDO getStudentInfo(Long id);

    /**
     * 获得学生信息列表
     *
     * @param ids 编号
     * @return 学生信息列表
     */
    List<StudentInfoDO> getStudentInfoList(Collection<Long> ids);

    /**
     * 获得学生信息分页
     *
     * @param pageReqVO 分页查询
     * @return 学生信息分页
     */
    PageResult<StudentInfoDO> getStudentInfoPage(StudentInfoPageReqVO pageReqVO);

    /**
     * 获得学生信息列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 学生信息列表
     */
    List<StudentInfoDO> getStudentInfoList(StudentInfoExportReqVO exportReqVO);

}
