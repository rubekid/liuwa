package com.liuwa.common.core.service.impl;

import com.liuwa.common.core.dao.CurdDao;
import com.liuwa.common.core.domain.BaseEntity;
import com.liuwa.common.core.service.CurdImportService;
import com.liuwa.common.exception.ServiceException;
import com.liuwa.common.utils.StringUtils;

import javax.annotation.Resource;
import java.util.List;

public abstract class CurdImportServiceImpl<Pk, D extends CurdDao<Pk, T>, T extends BaseEntity> extends CurdServiceImpl<Pk, D, T> implements CurdImportService<Pk, D, T> {

    @Resource
    private D curdDao;

    @Override
    public String importData(List<T> list, boolean overwrite) {
        if (list == null || list.size() == 0)
        {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (T item : list)
        {
            try
            {
                // 验证是否存在这个用户
                T existItem = this.findByUniqueKey(item);
                if (existItem == null)
                {
                    this.insert(item);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、名称 " + item.getObjectName() + " 导入成功");
                }
                else if (overwrite)
                {
                    item.setId(existItem.getId());
                    item.preUpdate();
                    this.updateSelective(item);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、名称 " + item.getObjectName() + " 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、名称 " + item.getObjectName() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、名称 " + item.getObjectName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                logger.error(msg, e);
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }
}
