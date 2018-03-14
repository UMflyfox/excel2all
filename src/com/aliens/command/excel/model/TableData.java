package com.aliens.command.excel.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliens.util.CharacterUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hejialin on 2018/3/10.
 */
public class TableData {

    private String alias;

    private String name;

    private String fixName;

    private String upperName;

    //field header
    private List<TableField> fieldInfo = new ArrayList<TableField>();

    //refer other table field   field alias- mapping table alias
    private Map<String, String> refField = new HashMap<String, String>();

    //all data
    private List<Map<String, Object>> dataArray = new ArrayList<Map<String, Object>>();


    //id - alias mapping
    private Map<String, Object> idMapping = new HashMap<String, Object>();

    public TableData(String alias) {
        this.alias = alias;
        this.name = alias;
    }

    public void addRefField(String fieldName, String mappingTableName) {
        refField.put(fieldName, mappingTableName);
    }

    public boolean haveRef() {
        return !this.refField.isEmpty();
    }

    public Map<String, String> getRefField() {
        return refField;
    }

    public String getAlias() {
        return alias;
    }

    public String getName() {
        return name;
    }

    public String getFixName() {
        if (this.fixName == null) {
            this.fixName = CharacterUtil.instance.transferCamelCasing(this.name);
        }
        return this.fixName;
    }

    public String getUpperName() {
        if (this.upperName == null) {
            this.upperName = this.name.toUpperCase();
        }
        return this.upperName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getIdMapping() {
        return idMapping;
    }

    public void addTableIDMapping(Object id, String name) {
        idMapping.put(name, id);
    }

    public List<TableField> getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(List<TableField> fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public List<Map<String, Object>> getDataArray() {
        return dataArray;
    }

    public void addData(Map<String, Object> rowData) {
        this.dataArray.add(rowData);
    }

    public String getJsonData() {
        JSONArray array = new JSONArray();
        JSONObject rowData = null;
        for (Map<String, Object> data : dataArray) {
            rowData = new JSONObject(true);
            rowData.putAll(data);
            array.add(rowData);
        }
        return array.toJSONString();
    }

    @Override
    public String toString() {
        return "TableData{" +
                "fieldInfo=" + fieldInfo +
                ", dataArray=" + dataArray +
                '}';
    }
}
