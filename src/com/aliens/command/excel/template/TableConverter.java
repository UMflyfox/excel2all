package com.aliens.command.excel.template;

import com.aliens.command.excel.model.TableData;
import com.aliens.command.excel.template.constant.Constants;
import com.aliens.command.excel.template.dialect.Dialect;
import com.aliens.command.excel.template.model.Template;

import java.util.Collection;

/**
 * Created by hejialin on 2018/3/13.
 */
public class TableConverter implements Converter {

    @Override
    public String convert(Collection<TableData> tableData, Dialect dialect, Template template) {
        if (template == null) {
            return null;
        }
        StringBuilder content = new StringBuilder();

        content.append(template.getHeader());

        String body = template.getBody();
        for (TableData currData : tableData) {
            String tablePrefix = body.replace(Constants.PARAM_TABLE_ALIAS, currData.getAlias());
            tablePrefix = tablePrefix.replace(Constants.PARAM_TABLE_NAME, currData.getName());
            tablePrefix = tablePrefix.replace(Constants.PARAM_TABLE_FIX_NAME, currData.getFixName());
            tablePrefix = tablePrefix.replace(Constants.PARAM_TABLE_DATA, currData.getJsonData());

            content.append(tablePrefix);
        }
        content.append(template.getTail());
        return content.toString();
    }

}
