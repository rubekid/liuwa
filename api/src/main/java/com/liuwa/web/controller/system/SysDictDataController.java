package com.liuwa.web.controller.system;

import com.liuwa.common.annotation.Log;
import com.liuwa.common.core.controller.BaseController;
import com.liuwa.common.core.domain.Result;
import com.liuwa.common.core.domain.entity.SysDictData;
import com.liuwa.common.core.domain.entity.SysDictType;
import com.liuwa.common.core.domain.model.SysDictDataOption;
import com.liuwa.common.core.page.PageData;
import com.liuwa.common.enums.BusinessType;
import com.liuwa.common.utils.StringUtils;
import com.liuwa.common.utils.poi.ExcelUtil;
import com.liuwa.common.utils.poi.ExportResult;
import com.liuwa.system.service.SysDictDataService;
import com.liuwa.system.service.SysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public PageData list(SysDictData dictData)
    {
        startPage();
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        return getPageData(list);
    }

    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:dict:export')")
    @GetMapping("/export")
    public ExportResult export(SysDictData dictData)
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
    public SysDictData getInfo(@PathVariable Long dictCode)
    {
        return dictDataService.selectDictDataById(dictCode);
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    public Result.ItemsVo dictType(@PathVariable String dictType, @RequestParam(value="dataType", required = false) String dataType) {
        List<SysDictDataOption> options = dictTypeService.selectDictDataByType(dictType);
        String[] types = {SysDictType.DATA_TYPE_NUMBER, SysDictType.DATA_TYPE_BOOLEAN, SysDictType.DATA_TYPE_STRING};
        if(!StringUtils.equalsAny(dataType, SysDictType.DATA_TYPE_NUMBER, SysDictType.DATA_TYPE_BOOLEAN, SysDictType.DATA_TYPE_STRING)){
            return Result.items(options);
        }
        List<SysDictDataOption> items = new ArrayList<SysDictDataOption>();

        for(SysDictDataOption dataItem : options){
            String label = dataItem.getDictLabel();
            String value = String.valueOf(dataItem.getDictValue());
            if(SysDictType.DATA_TYPE_NUMBER.equals(dataType)){
                SysDictDataOption<Double> option = new SysDictDataOption<Double>();
                option.setDictValue(Double.valueOf(value));
                option.setDictLabel(label);
                items.add(option);
            }
            else if(SysDictType.DATA_TYPE_BOOLEAN.equals(dataType)){
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
        return Result.items(items);
    }

    /**
     * 新增字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(@Validated @RequestBody SysDictData dict)
    {
        dict.setCreateBy(getUserId());
        dictDataService.insertDictData(dict);
    }

    /**
     * 修改保存字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public void edit(@Validated @RequestBody SysDictData dict)
    {
        dict.setUpdateBy(getUserId());
        dictDataService.updateDictData(dict);
    }

    /**
     * 删除字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public void remove(@PathVariable Long[] dictCodes)
    {
        dictDataService.deleteDictDataByIds(dictCodes);
    }
}
