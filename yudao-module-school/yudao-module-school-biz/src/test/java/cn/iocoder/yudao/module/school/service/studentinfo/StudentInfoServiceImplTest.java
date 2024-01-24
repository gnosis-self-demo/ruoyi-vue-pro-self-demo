package cn.iocoder.yudao.module.school.service.studentinfo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.annotation.Resource;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;

import cn.iocoder.yudao.module.school.controller.admin.studentinfo.vo.*;
import cn.iocoder.yudao.module.school.dal.dataobject.studentinfo.StudentInfoDO;
import cn.iocoder.yudao.module.school.dal.mysql.studentinfo.StudentInfoMapper;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import javax.annotation.Resource;
import org.springframework.context.annotation.Import;
import java.util.*;
import java.time.LocalDateTime;

import static cn.hutool.core.util.RandomUtil.*;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.*;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.*;
import static cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils.*;
import static cn.iocoder.yudao.framework.common.util.object.ObjectUtils.*;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link StudentInfoServiceImpl} 的单元测试类
 *
 * @author 芋道源码
 */
@Import(StudentInfoServiceImpl.class)
public class StudentInfoServiceImplTest extends BaseDbUnitTest {

    @Resource
    private StudentInfoServiceImpl studentInfoService;

    @Resource
    private StudentInfoMapper studentInfoMapper;

    @Test
    public void testCreateStudentInfo_success() {
        // 准备参数
        StudentInfoCreateReqVO reqVO = randomPojo(StudentInfoCreateReqVO.class);

        // 调用
        Long studentInfoId = studentInfoService.createStudentInfo(reqVO);
        // 断言
        assertNotNull(studentInfoId);
        // 校验记录的属性是否正确
        StudentInfoDO studentInfo = studentInfoMapper.selectById(studentInfoId);
        assertPojoEquals(reqVO, studentInfo);
    }

    @Test
    public void testUpdateStudentInfo_success() {
        // mock 数据
        StudentInfoDO dbStudentInfo = randomPojo(StudentInfoDO.class);
        studentInfoMapper.insert(dbStudentInfo);// @Sql: 先插入出一条存在的数据
        // 准备参数
        StudentInfoUpdateReqVO reqVO = randomPojo(StudentInfoUpdateReqVO.class, o -> {
            o.setId(dbStudentInfo.getId()); // 设置更新的 ID
        });

        // 调用
        studentInfoService.updateStudentInfo(reqVO);
        // 校验是否更新正确
        StudentInfoDO studentInfo = studentInfoMapper.selectById(reqVO.getId()); // 获取最新的
        assertPojoEquals(reqVO, studentInfo);
    }

    @Test
    public void testUpdateStudentInfo_notExists() {
        // 准备参数
        StudentInfoUpdateReqVO reqVO = randomPojo(StudentInfoUpdateReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> studentInfoService.updateStudentInfo(reqVO), STUDENT_INFO_NOT_EXISTS);
    }

    @Test
    public void testDeleteStudentInfo_success() {
        // mock 数据
        StudentInfoDO dbStudentInfo = randomPojo(StudentInfoDO.class);
        studentInfoMapper.insert(dbStudentInfo);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbStudentInfo.getId();

        // 调用
        studentInfoService.deleteStudentInfo(id);
       // 校验数据不存在了
       assertNull(studentInfoMapper.selectById(id));
    }

    @Test
    public void testDeleteStudentInfo_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> studentInfoService.deleteStudentInfo(id), STUDENT_INFO_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetStudentInfoPage() {
       // mock 数据
       StudentInfoDO dbStudentInfo = randomPojo(StudentInfoDO.class, o -> { // 等会查询到
           o.setCreateTime(null);
           o.setUpdateTime(null);
           o.setStuName(null);
           o.setStuAge(null);
       });
       studentInfoMapper.insert(dbStudentInfo);
       // 测试 createTime 不匹配
       studentInfoMapper.insert(cloneIgnoreId(dbStudentInfo, o -> o.setCreateTime(null)));
       // 测试 updateTime 不匹配
       studentInfoMapper.insert(cloneIgnoreId(dbStudentInfo, o -> o.setUpdateTime(null)));
       // 测试 stuName 不匹配
       studentInfoMapper.insert(cloneIgnoreId(dbStudentInfo, o -> o.setStuName(null)));
       // 测试 stuAge 不匹配
       studentInfoMapper.insert(cloneIgnoreId(dbStudentInfo, o -> o.setStuAge(null)));
       // 准备参数
       StudentInfoPageReqVO reqVO = new StudentInfoPageReqVO();
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));
       reqVO.setUpdateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));
       reqVO.setStuName(null);
       reqVO.setStuAge(null);

       // 调用
       PageResult<StudentInfoDO> pageResult = studentInfoService.getStudentInfoPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbStudentInfo, pageResult.getList().get(0));
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetStudentInfoList() {
       // mock 数据
       StudentInfoDO dbStudentInfo = randomPojo(StudentInfoDO.class, o -> { // 等会查询到
           o.setCreateTime(null);
           o.setUpdateTime(null);
           o.setStuName(null);
           o.setStuAge(null);
       });
       studentInfoMapper.insert(dbStudentInfo);
       // 测试 createTime 不匹配
       studentInfoMapper.insert(cloneIgnoreId(dbStudentInfo, o -> o.setCreateTime(null)));
       // 测试 updateTime 不匹配
       studentInfoMapper.insert(cloneIgnoreId(dbStudentInfo, o -> o.setUpdateTime(null)));
       // 测试 stuName 不匹配
       studentInfoMapper.insert(cloneIgnoreId(dbStudentInfo, o -> o.setStuName(null)));
       // 测试 stuAge 不匹配
       studentInfoMapper.insert(cloneIgnoreId(dbStudentInfo, o -> o.setStuAge(null)));
       // 准备参数
       StudentInfoExportReqVO reqVO = new StudentInfoExportReqVO();
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));
       reqVO.setUpdateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));
       reqVO.setStuName(null);
       reqVO.setStuAge(null);

       // 调用
       List<StudentInfoDO> list = studentInfoService.getStudentInfoList(reqVO);
       // 断言
       assertEquals(1, list.size());
       assertPojoEquals(dbStudentInfo, list.get(0));
    }

}
