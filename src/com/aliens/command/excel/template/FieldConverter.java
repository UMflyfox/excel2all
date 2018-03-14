package com.aliens.command.excel.template;

import com.aliens.command.excel.model.TableData;
import com.aliens.command.excel.model.TableField;
import com.aliens.command.excel.template.constant.Constants;
import com.aliens.command.excel.template.dialect.Dialect;
import com.aliens.command.excel.template.model.Template;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hejialin on 2018/3/13.
 */
public class FieldConverter implements Converter {

    @Override
    public String convert(Collection<TableData> tableData, Dialect dialect, Template template) {
        if (template == null) {
            return null;
        }
        StringBuilder content = new StringBuilder();
        content.append(template.getHeader());
        String body = template.getBody();
        for (TableData data : tableData) {
            String currTablePrefix = replaceTableContent(data, template.getBodyPrefix());
            content.append(currTablePrefix);

            if (body != null && !body.isEmpty()) {
                for (TableField field : data.getFieldInfo()) {
                    String currFieldContent = replaceTableContent(data, body);
                    currFieldContent = currFieldContent.replace(Constants.PARAM_FIELD_ALIAS, field.getAlias());
                    currFieldContent = currFieldContent.replace(Constants.PARAM_FIELD_NAME, field.getName());
                    currFieldContent = currFieldContent.replace(Constants.PARAM_FIELD_FIX_NAME, field.getFixName());
                    currFieldContent = currFieldContent.replace(Constants.PARAM_FIELD_TYPE, dialect.getType(field.getFieldType()));
                    content.append(currFieldContent);
                }
                content.append(template.getBodySuffix());
            }
        }
        content.append(template.getTail());
        return content.toString();
    }

    private String replaceTableContent(TableData data, String currTablePrefix) {
        currTablePrefix = currTablePrefix.replace(Constants.PARAM_TABLE_ALIAS, data.getAlias());
        currTablePrefix = currTablePrefix.replace(Constants.PARAM_TABLE_NAME, data.getName());
        currTablePrefix = currTablePrefix.replace(Constants.PARAM_TABLE_FIX_NAME, data.getFixName());
        currTablePrefix = currTablePrefix.replace(Constants.PARAM_TABLE_DATA, data.getJsonData());
        return currTablePrefix;
    }

}
