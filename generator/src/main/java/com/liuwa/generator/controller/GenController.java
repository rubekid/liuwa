package com.liuwa.generator.controller;

import com.liuwa.common.annotation.Log;
import com.liuwa.common.core.controller.BaseController;
import com.liuwa.common.core.page.PageData;
import com.liuwa.common.core.text.Convert;
import com.liuwa.common.enums.BusinessType;
import com.liuwa.generator.domain.GenTable;
import com.liuwa.generator.domain.GenTableColumn;
import com.liuwa.generator.service.GenTableColumnService;
import com.liuwa.generator.service.GenTableService;
import com.liuwa.generator.vo.TableInfoVo;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 代码生成 操作处理
 * 
 * @author liuwa
 */
@RestController
@RequestMapping("/tool/gen")
public class GenController extends BaseController
{
    @Autowired
    private GenTableService genTableService;

    @Autowired
    private GenTableColumnService genTableColumnService;

    /**
     * 查询代码生成列表
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:list')")
    @GetMapping("/list")
    public PageData genList(GenTable genTable)
    {
        startPage();
        List<GenTable> list = genTableService.selectGenTableList(genTable);
        return getPageData(list);
    }

    /**
     * 修改代码生成业务
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:query')")
    @GetMapping(value = "/{talbleId}")
    public TableInfoVo getInfo(@PathVariable Long talbleId)
    {
        GenTable table = genTableService.selectGenTableById(talbleId);
        List<GenTable> tables = genTableService.selectGenTableAll();
        List<GenTableColumn> columns = genTableColumnService.selectGenTableColumnListByTableId(talbleId);
        TableInfoVo tableInfoVo = new TableInfoVo();
        tableInfoVo.setTable(table);
        tableInfoVo.setColumns(columns);
        tableInfoVo.setTables(tables);
        return tableInfoVo;
    }

    /**
     * 查询数据库列表
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:list')")
    @GetMapping("/db/list")
    public PageData dataList(GenTable genTable)
    {
        startPage();
        List<GenTable> list = genTableService.selectDbTableList(genTable);
        return getPageData(list);
    }

    /**
     * 查询数据表字段列表
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:list')")
    @GetMapping(value = "/column/{talbleId}")
    public PageData columnList(Long tableId)
    {
        PageData dataInfo = new PageData();
        List<GenTableColumn> list = genTableColumnService.selectGenTableColumnListByTableId(tableId);
        dataInfo.setItems(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 导入表结构（保存）
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:import')")
    @Log(title = "代码生成", businessType = BusinessType.IMPORT)
    @PostMapping("/importTable")
    public void importTableSave(String tables)
    {
        String[] tableNames = Convert.toStrArray(tables);
        // 查询表信息
        List<GenTable> tableList = genTableService.selectDbTableListByNames(tableNames);
        genTableService.importGenTable(tableList);
    }

    /**
     * 修改保存代码生成业务
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:edit')")
    @Log(title = "代码生成", businessType = BusinessType.UPDATE)
    @PutMapping
    public void editSave(@Validated @RequestBody GenTable genTable)
    {
        genTableService.validateEdit(genTable);
        genTableService.updateGenTable(genTable);
    }

    /**
     * 删除代码生成
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:remove')")
    @Log(title = "代码生成", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tableIds}")
    public void remove(@PathVariable Long[] tableIds)
    {
        genTableService.deleteGenTableByIds(tableIds);
    }

    /**
     * 预览代码
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:preview')")
    @GetMapping("/preview/{tableId}")
    public Map<String, String> preview(@PathVariable("tableId") Long tableId) throws IOException
    {
        Map<String, String> dataMap = genTableService.previewCode(tableId);
        return dataMap;
    }

    /**
     * 生成代码（下载方式）
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:code')")
    @Log(title = "代码生成", businessType = BusinessType.GENCODE)
    @GetMapping("/download/{tableName}")
    public void download(HttpServletResponse response, @PathVariable("tableName") String tableName) throws IOException
    {
        byte[] data = genTableService.downloadCode(tableName);
        genCode(response, data);
    }

    /**
     * 生成代码（自定义路径）
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:code')")
    @Log(title = "代码生成", businessType = BusinessType.GENCODE)
    @GetMapping("/genCode/{tableName}")
    public void genCode(@PathVariable("tableName") String tableName)
    {
        genTableService.generatorCode(tableName);
    }

    /**
     * 同步数据库
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:edit')")
    @Log(title = "代码生成", businessType = BusinessType.UPDATE)
    @GetMapping("/synchDb/{tableName}")
    public void synchDb(@PathVariable("tableName") String tableName)
    {
        genTableService.synchDb(tableName);
    }

    /**
     * 批量生成代码
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:code')")
    @Log(title = "代码生成", businessType = BusinessType.GENCODE)
    @GetMapping("/batchGenCode")
    public void batchGenCode(HttpServletResponse response, String tables) throws IOException
    {
        String[] tableNames = Convert.toStrArray(tables);
        byte[] data = genTableService.downloadCode(tableNames);
        genCode(response, data);
    }

    /**
     * 生成zip文件
     */
    private void genCode(HttpServletResponse response, byte[] data) throws IOException
    {
        response.reset();
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment; filename=\"liuwa.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }
}