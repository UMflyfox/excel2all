package com.aliens.command.excel;

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
                loadFieldData(data, row);
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
        String fieldName = "";
        List<TableField> fieldInfo = data.getFieldInfo();
        Map<String, Object> fieldData = new LinkedHashMap<String, Object>();
        Object id = null;
        String name = null;
        for (int i = 0; i < fieldInfo.size(); i++) {
            field = fieldInfo.get(i);
            fieldName = field.getName();
            Cell cell = dataRow.getCell(i);
            if(cell == null){
                if (field.getFieldType() == FieldType.ID) {
                    return;
                }
                continue;
            }
            switch(field.getFieldType()) {
                case STRING:
                    fieldData.put(fieldName,  getStringValue(cell).trim());
                    break;
                case BOOL:
                    fieldData.put(fieldName, getBoolValue(cell));
                    break;
                case INT:
                    fieldData.put(field.getName(), getNumberValue(cell).intValue());
                    break;
                case FLOAT:
                    fieldData.put(field.getName(), getNumberValue(cell).floatValue());
                    break;
                case ENUM:
                    String enumName = getStringValue(cell).trim();
                    Integer enumValue = field.getEnum(enumName);
                    if (enumValue == null) {
                        log.Error("unexpect field " + field.getName() + " enum " + enumName);
                        enumValue = Integer.valueOf(0);
                    }
                    fieldData.put(field.getName(), enumValue);
                    break;
                case ID:
                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        id = cell.getStringCellValue().trim();
                    } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        id = new Double(cell.getNumericCellValue()).intValue();
                    }
                    break;
                case NAME:
                    name = getStringValue(cell).trim();
                    fieldData.put(fieldName, name);
                    break;
                default:
                    fieldData.put(fieldName,  getStringValue(cell).trim());

            }
        }
        if (id != null && name != null) {
            data.addTableIDMapping(id, name);
        }
        data.addData(fieldData);

    }

    private Double getNumberValue(Cell cell) {
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return cell.getNumericCellValue();
        }
        try {
            return Double.valueOf(getStringValue(cell));
        } catch (NumberFormatException e) {
            return 0d;
        }
    }

    private Boolean getBoolValue(Cell cell) {
        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return cell.getBooleanCellValue();
        }
        return Boolean.valueOf(getStringValue(cell));
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
            FieldType fieldType = null;
            if (fieldInfoText.startsWith("$")) {
                fieldType = FieldType.REFER;
            } else if (fieldInfoText.startsWith("[") && fieldInfoText.endsWith("]")){
                fieldInfoText = fieldInfoText.substring(1, fieldInfoText.length() - 1);
                fieldType = FieldType.getFieldType(fieldInfoText);
            }

            if (fieldType == null) {
                log.Warning("unexpect field type : " + fieldInfoText + " at column " + i);
                break;
            }
            field = new TableField(fieldInfo[0], fieldType);
            fields.add(field);

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

        data.setFieldInfo(fields);
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
