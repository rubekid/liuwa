package com.liuwa.web.controller.system;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.liuwa.common.core.domain.entity.SysDictType;
import com.liuwa.common.core.domain.model.SysDictDataOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.liuwa.common.annotation.Log;
import com.liuwa.common.core.controller.BaseController;
import com.liuwa.common.core.domain.AjaxResult;
import com.liuwa.common.core.domain.entity.SysDictData;
import com.liuwa.common.core.page.TableDataInfo;
import com.liuwa.common.enums.BusinessType;
import com.liuwa.common.utils.StringUtils;
import com.liuwa.common.utils.poi.ExcelUtil;
import com.liuwa.system.service.SysDictDataService;
import com.liuwa.system.service.SysDictTypeService;

/**
 * 数据字典信息
 * 
 * @author liuwa
 */
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController extends BaseController
{
    @Autowired
    private SysDictDataService dictDataService;

    @Autowired
    private SysDictTypeService dictTypeService;

    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysDictData dictData)
    {
        startPage();
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        return getDataTable(list);
    }

    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:dict:export')")
    @GetMapping("/export")
    public AjaxResult export(SysDictData dictData)
    {
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        ExcelUtil<SysDictData> util = new ExcelUtil<SysDictData>(SysDictData.class);
        return util.exportExcel(list, "字典数据");
    }

    /**
     * 查询字典数据详细
     */
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictCode}")
    public AjaxResult getInfo(@PathVariable Long dictCode)
    {
        return AjaxResult.success(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    public AjaxResult dictType(@PathVariable String dictType) {
        List<SysDictDataOption> items = new ArrayList<SysDictDataOption>();
        List<SysDictData> list = dictTypeService.selectDictDataByType(dictType);
        if (!StringUtils.isNull(list)) {
            SysDictType sysDictType = dictTypeService.selectDictTypeByType(dictType);
            for(SysDictData dataItem : list){
                String label = dataItem.getDictLabel();
                String value = dataItem.getDictValue();
                if(SysDictType.JAVA_TYPE_NUMBER.equals(sysDictType.getJavaType())){
                    SysDictDataOption<Double> option = new SysDictDataOption<Double>();
                    option.setDictValue(Double.valueOf(value));
                    option.setDictLabel(label);
                    items.add(option);
                }
                else if(SysDictType.JAVA_TYPE_BOOLEAN.equals(sysDictType.getJavaType())){
                    SysDictDataOption<Boolean> option = new SysDictDataOption<Boolean>();
                    option.setDictValue(Boolean.valueOf(value));
                    option.setDictLabel(label);
                    items.add(option);
                }
                else{
                    SysDictDataOption<String> option = new SysDictDataOption<String>();
                    option.setDictValue(value);
                    option.setDictLabel(label);
                    items.add(option);
                }
            }

        }

        return AjaxResult.success(items);
    }

    /**
     * 新增字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDictData dict)
    {
        dict.setCreateBy(getUserId());
        return toAjax(dictDataService.insertDictData(dict));
    }

    /**
     * 修改保存字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDictData dict)
    {
        dict.setUpdateBy(getUserId());
        return toAjax(dictDataService.updateDictData(dict));
    }

    /**
     * 删除字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public AjaxResult remove(@PathVariable Long[] dictCodes)
    {
        dictDataService.deleteDictDataByIds(dictCodes);
        return success();
    }
}
