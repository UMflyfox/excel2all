package com.aliens.command.excel.model;

import com.aliens.util.CharacterUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hejialin on 2018/3/10.
 */
public class TableField {

    //the field name
    private String name;

    private String fixName;

    private String alias;

    //the field Type
    private FieldType fieldType;

    //refer other sheet
    private String ref;

    //the field is enum;
    private Map<String, String> enums;

    private  Map<String, Integer> enumSeqs;

    public TableField(String name, FieldType fieldType) {
        this.name = name;
        this.alias = name;
        this.fieldType = fieldType;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public Map<String, String> getEnums() {
        return enums;
    }

    public Map<String, Integer> getEnumSeqs() {
        return enumSeqs;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Integer getEnum(String enumName) {
        if (enumSeqs == null) {
            return 0;
        }
        return enumSeqs.get(enumName);
    }

    public void setEnums(Map<String, String> enums) {
        this.enums = enums;
        if (this.enums != null) {
            enumSeqs = new HashMap<String, Integer>(enums.size());
            int index = 1;
            for (String enumName : this.enums.keySet()) {
                enumSeqs.put(enumName, index);
                index++;
            }
        }
    }

    public boolean isEnum() {
        return enums != null && !enums.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableField field = (TableField) o;

        return name != null ? name.equals(field.name) : field.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TableField{" +
                "name='" + name + '\'' +
                ", fieldType=" + fieldType +
                ", ref='" + ref + '\'' +
                ", enums=" + enums +
                ", enumSeqs=" + enumSeqs +
                '}';
    }
}
