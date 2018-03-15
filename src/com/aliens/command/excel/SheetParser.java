package com.aliens.command.excel;

import com.alibaba.fastjson.JSONObject;
import com.aliens.command.excel.model.FieldType;
import com.aliens.command.excel.model.TableData;
import com.aliens.command.excel.model.TableEnum;
import com.aliens.command.excel.model.TableField;
import com.aliens.command.log.ILogger;
import com.aliens.command.log.SystemLogger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.*;

/**
 * Created by hejialin on 2018/3/9.
 */
public class SheetParser {

    public static final String TABLE_NAME_KEY = "C";

    public static final String ENUM_SPLIT_CHAR = ":";

    public static final String FILTER_CHAR = "#";

    public static final String ARRAY_SPLIT = ",";

    private ILogger log = new SystemLogger();

    public TableData parse(Sheet sheet) {
        TableData data = new TableData(sheet.getSheetName());
        int fieldRowNo = sheet.getFirstRowNum();
        int descRowNo = sheet.getFirstRowNum() + 1;

        for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if(row == null){
                continue;
            }

            if (rowIndex == fieldRowNo){
                loadFieldHeaderInfo(data, row);
            } else if (rowIndex == descRowNo) {
                loadFieldDesc(data, row);
            } else {
                Cell cell = row.getCell(0);
                if (cell == null) {
                    continue;
                }
                String cellContent = getStringValue(cell);
                if (!cellContent.equals(FILTER_CHAR)) {
                    loadFieldData(data, row);
                }
            }
        }
        return data;
    }

    private void loadFieldDesc(TableData data, Row dataRow) {
        TableField field = null;
        List<TableField> fieldInfo = data.getFieldInfo();
        for (int i = 0; i < fieldInfo.size(); i++) {
            field = fieldInfo.get(i);
            Cell cell = dataRow.getCell(i);
            if (cell != null) {
                field.setAlias(cell.getStringCellValue());
            }
        }
    }

    private void loadFieldData(TableData data, Row dataRow) {
        TableField field = null;
        List<TableField> fieldInfo = data.getFieldInfo();
        Map<String, Object> fieldData = new LinkedHashMap<String, Object>();
        Object id = null;
        String name = null;
        for (int i = 0; i < fieldInfo.size(); i++) {
            field = fieldInfo.get(i);
            Cell cell = dataRow.getCell(i);
            if (cell == null) {
                if (field.getFieldType() == FieldType.ID) {
                    return;
                }
                continue;
            }
            String key = getStringValue(cell).trim();
            Object value = getFieldValue(field.getFieldType(), field.getSubType(), key, field);

            if (field.getFieldType() == FieldType.ID) {
                id = value;
            } else if (field.getFieldType() == FieldType.NAME) {
                name = String.valueOf(value);
            }

            fieldData.put(field.getName(), value);
        }

        if (id != null && name != null) {
            data.addTableIDMapping(id, name);
        }
        data.addData(fieldData);
    }

    private Object getFieldValue(FieldType fieldType, FieldType subType, String content, TableField field) {
        switch(fieldType) {
            case STRING:
                return content;
            case BOOL:
                return Boolean.parseBoolean(content);
            case INT:
                try {
                    return Integer.parseInt(content);
                } catch (NumberFormatException e) {
                    return 0;
                }
            case FLOAT:
                try {
                    return Float.parseFloat(content);
                } catch (NumberFormatException e) {
                    return 0.0f;
                }
            case ENUM:
                Integer enumValue = field.getEnum(content);
                if (enumValue == null) {
                    log.Error("unexpect field " + field.getName() + " enum " + content);
                    enumValue = Integer.valueOf(0);
                }
                return enumValue;
            case ID:
                try {
                    return Integer.parseInt(content);
                } catch (NumberFormatException e) {
                    return content;
                }
            case ARRAY:
                String[] arrayInfo = content.split(ARRAY_SPLIT);
                if (subType != null && subType != FieldType.REFER) {
                    List<Object> result = new ArrayList<Object>();
                    for (String info : arrayInfo) {
                        result.add(getFieldValue(subType, null, info, field));
                    }
                    return result.toArray();
                } else {
                    return arrayInfo;
                }
            case NAME:
                return content;
            case JSON:
                return JSONObject.parse(content);
            default:
                return content;
        }
    }

    private String getStringValue(Cell cell) {
        int type = cell.getCellType();
        if (type == Cell.CELL_TYPE_NUMERIC) {
            return String.valueOf(Double.valueOf(cell.getNumericCellValue()).intValue());
        } else if (type == Cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }
        return cell.getStringCellValue();
    }

    private String[] parseFieldText(String fieldText) {
        char[] chars = fieldText.toCharArray();
        List<String> trimContents = new ArrayList<String>();
        StringBuilder currStr = new StringBuilder();
        for (char currChar : chars) {
            if (currChar == ' ' || currChar == '\n' || currChar == '\r') {
                if (currStr.length() != 0) {
                    trimContents.add(currStr.toString());
                    currStr = new StringBuilder();
                }
                continue;
            }
            currStr.append(currChar);
        }
        if (currStr.length() != 0) {
            trimContents.add(currStr.toString());
        }
        return trimContents.toArray(new String[trimContents.size()]);
    }

    //load field meta info
    private void loadFieldHeaderInfo(TableData data, Row row) {
        List<TableField> fields = new ArrayList<TableField>();
        TableField field = null;

        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            String parseField = row.getCell(i).getStringCellValue();
            if(parseField.isEmpty()) {
                break;
            }

            String[] fieldInfo = parseFieldText(parseField);
            if (fieldInfo == null || fieldInfo.length < 2) {
                log.Warning("invalid field format : " + parseField + " at column " + i);
                break;
            }

            //parseTemplate field type
            String fieldInfoText = fieldInfo[1];
            FieldType fieldType = getType(fieldInfoText);
            FieldType subFieldType = null;
            if (fieldType == null) {
                log.Warning("unexpect field type : " + fieldInfoText + " at column " + i);
            }

            if (fieldType == FieldType.ARRAY) {
                fieldInfoText = fieldInfoText.substring(1, fieldInfoText.length() - 1);
                subFieldType = getType(fieldInfoText);
            }


            field = new TableField(fieldInfo[0], fieldType);
            field.setSubType(subFieldType);
            fields.add(field);

            updateData(fieldType, data, field, fieldInfoText, fieldInfo);
            updateData(subFieldType, data, field, fieldInfoText, fieldInfo);
        }

        data.setFieldInfo(fields);
    }

    private FieldType getType(String fieldInfoText) {
        FieldType fieldType = null;
        //FieldType subFieldType = null;
        if (fieldInfoText.startsWith("$")) {
            fieldType = FieldType.REFER;
        } else if (fieldInfoText.startsWith("[") && fieldInfoText.endsWith("]")){
            fieldType = FieldType.ARRAY;
        } else {
            fieldType = FieldType.getFieldType(fieldInfoText);
        }
        return fieldType;

    }

    private void updateData( FieldType fieldType, TableData data, TableField field, String fieldInfoText, String[] fieldInfo) {
        if (fieldType == FieldType.REFER) {
            field.setRef(fieldInfoText.substring(1));
            data.addRefField(field.getName(), field.getRef());
        } else if (fieldType == FieldType.ENUM) {
            Map<String, TableEnum> enumMapping = readEnum(fieldInfo);
            field.setEnums(enumMapping);
        } else if (fieldType == FieldType.ID) {
            Map<String, TableEnum> enumMapping = readEnum(fieldInfo);
            if (enumMapping != null) {
                String tableName = enumMapping.get(TABLE_NAME_KEY).getName();
                if (tableName != null) {
                    data.setName(tableName);
                }
            }
        }

    }
    private Map<String, TableEnum> readEnum(String[] fieldInfo) {
        if (fieldInfo.length <= 2) {
            return null;
        }
        Map<String, TableEnum> enumMapping = new LinkedHashMap<String, TableEnum>();
        int index = 1;

        for (int j = 2; j<fieldInfo.length; j++) {
            String enumInfo = fieldInfo[j];
            String[] enums = enumInfo.trim().split(ENUM_SPLIT_CHAR);
            if (enums.length < 2) {
                continue;
            }
            if (enums.length == 3) {
                try {
                    index = Integer.parseInt(enums[2].trim());
                } catch(NumberFormatException e) {

                }
            }
            TableEnum tableEnum = new TableEnum(enums[1].trim(), enums[0].trim(), index);
            enumMapping.put(tableEnum.getAlias(), tableEnum);
            index ++;
        }
        return enumMapping;
    }

}
