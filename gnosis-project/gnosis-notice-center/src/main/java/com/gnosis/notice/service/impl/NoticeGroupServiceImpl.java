package com.gnosis.notice.service.impl;

import com.gnosis.common.dto.PageResult;
import com.gnosis.notice.domain.NoticeGroup;
import com.gnosis.notice.dto.group.NoticeGroupCreateRequest;
import com.gnosis.notice.dto.group.NoticeGroupQueryRequest;
import com.gnosis.notice.dto.group.NoticeGroupUpdateRequest;
import com.gnosis.notice.dto.group.NoticeGroupVO;
import com.gnosis.notice.mapper.NoticeGroupMapper;
import com.gnosis.notice.service.NoticeGroupService;
import com.gnosis.notice.util.ExcelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 消息分组服务实现
 */
@Service
public class NoticeGroupServiceImpl implements NoticeGroupService {

    private static final Logger log = LoggerFactory.getLogger(NoticeGroupServiceImpl.class);

    @Autowired
    private NoticeGroupMapper groupMapper;

    @Override
    public PageResult<NoticeGroupVO> pageList(NoticeGroupQueryRequest request) {
        if (request == null) {
            request = new NoticeGroupQueryRequest();
        }
        if (request.getPageNum() == null) {
            request.setPageNum(0);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }

        Long total = groupMapper.countByCondition(request);
        if (total == 0) {
            PageResult<NoticeGroupVO> result = new PageResult<NoticeGroupVO>();
            result.setTotal(0L);
            result.setList(new ArrayList<NoticeGroupVO>());
            return result;
        }

        List<NoticeGroup> groups = groupMapper.selectByCondition(request);
        List<NoticeGroupVO> voList = new ArrayList<NoticeGroupVO>();
        for (NoticeGroup group : groups) {
            voList.add(convertToVO(group));
        }

        PageResult<NoticeGroupVO> result = new PageResult<NoticeGroupVO>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    @Override
    public NoticeGroupVO detail(String id) {
        NoticeGroup group = groupMapper.selectById(id);
        if (group == null) {
            return null;
        }
        return convertToVO(group);
    }

    @Override
    public NoticeGroupVO getByCode(String groupCode) {
        if (StringUtils.isEmpty(groupCode)) {
            return null;
        }
        NoticeGroup group = groupMapper.selectByCode(groupCode);
        if (group == null) {
            return null;
        }
        return convertToVO(group);
    }

    @Override
    public String create(NoticeGroupCreateRequest request, String userId) {
        NoticeGroup group = new NoticeGroup();
        group.setId(UUID.randomUUID().toString().replace("-", ""));
        group.setGroupCode(request.getGroupCode());
        group.setGroupName(request.getGroupName());
        group.setDescription(request.getDescription());
        group.setStatus(1);
        group.setCreateUserId(userId);
        group.setUpdateUserId(userId);
        group.setCreateTime(new Date());
        group.setUpdateTime(new Date());

        groupMapper.insert(group);
        return group.getId();
    }

    @Override
    public int update(NoticeGroupUpdateRequest request, String userId) {
        NoticeGroup group = groupMapper.selectById(request.getId());
        if (group == null) {
            return 0;
        }

        if (request.getGroupCode() != null) {
            group.setGroupCode(request.getGroupCode());
        }
        if (request.getGroupName() != null) {
            group.setGroupName(request.getGroupName());
        }
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            group.setStatus(request.getStatus());
        }
        group.setUpdateUserId(userId);
        group.setUpdateTime(new Date());

        return groupMapper.updateById(group);
    }

    @Override
    public int delete(String id) {
        return groupMapper.deleteById(id);
    }

    @Override
    public int batchDelete(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return groupMapper.deleteByIds(ids);
    }

    @Override
    public int batchEnable(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return groupMapper.batchUpdateStatus(ids, 1);
    }

    @Override
    public int batchDisable(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return groupMapper.batchUpdateStatus(ids, 0);
    }

    @Override
    public void export(NoticeGroupQueryRequest request, HttpServletResponse response) throws Exception {
        List<NoticeGroup> groups = groupMapper.selectByCondition(request);
        List<String[]> data = new ArrayList<String[]>();
        for (NoticeGroup group : groups) {
            String[] row = new String[]{
                group.getGroupCode(),
                group.getGroupName(),
                group.getDescription() != null ? group.getDescription() : "",
                group.getStatus() != null && group.getStatus() == 1 ? "启用" : "禁用",
                group.getCreateUserId() != null ? group.getCreateUserId() : "",
                group.getCreateTime() != null ? group.getCreateTime().toString() : "",
                group.getUpdateUserId() != null ? group.getUpdateUserId() : "",
                group.getUpdateTime() != null ? group.getUpdateTime().toString() : ""
            };
            data.add(row);
        }
        String[] headers = {"分组编码", "分组名称", "分组描述", "状态", "创建人", "创建时间", "更新人", "更新时间"};
        ExcelUtils.export(response, "消息分组", "分组列表", headers, data);
    }

    @Override
    public int importExcel(InputStream inputStream, String userId) throws Exception {
        List<String[]> data = ExcelUtils.read(inputStream, true);
        int count = 0;
        for (String[] row : data) {
            if (row.length < 2 || StringUtils.isEmpty(row[0])) {
                continue;
            }
            NoticeGroup group = new NoticeGroup();
            group.setId(UUID.randomUUID().toString().replace("-", ""));
            group.setGroupCode(row[0]);
            group.setGroupName(row[1]);
            group.setDescription(row.length > 2 ? row[2] : null);
            group.setStatus(1);
            group.setCreateUserId(userId);
            group.setUpdateUserId(userId);
            group.setCreateTime(new Date());
            group.setUpdateTime(new Date());

            // 检查编码是否已存在
            NoticeGroup exist = groupMapper.selectByCode(row[0]);
            if (exist != null) {
                exist.setGroupName(group.getGroupName());
                exist.setDescription(group.getDescription());
                exist.setUpdateUserId(userId);
                exist.setUpdateTime(new Date());
                groupMapper.updateById(exist);
            } else {
                groupMapper.insert(group);
            }
            count++;
        }
        return count;
    }

    /**
     * 转换为VO
     */
    private NoticeGroupVO convertToVO(NoticeGroup group) {
        NoticeGroupVO vo = new NoticeGroupVO();
        vo.setId(group.getId());
        vo.setGroupCode(group.getGroupCode());
        vo.setGroupName(group.getGroupName());
        vo.setDescription(group.getDescription());
        vo.setStatus(group.getStatus());
        vo.setCreateUserId(group.getCreateUserId());
        vo.setUpdateUserId(group.getUpdateUserId());
        vo.setCreateTime(group.getCreateTime());
        vo.setUpdateTime(group.getUpdateTime());
        return vo;
    }
}
