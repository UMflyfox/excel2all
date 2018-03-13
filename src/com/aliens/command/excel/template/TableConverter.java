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
public class TableConverter implements Converter {

    public static final Pattern TABLE_MATCH_REG = Pattern.compile("([\\s\\S]*)<table>([\\s\\S]*)</table>([\\s\\S]*)");
    public static final Pattern FIELD_MATCH_REG = Pattern.compile("([\\s\\S]*)<field>([\\s\\S]*)</field>([\\s\\S]*)");


    private Template template;

    @Override
    public String convert(Collection<TableData> tableData, Dialect dialect) {
        if (template == null) {
            return null;
        }
        StringBuilder content = new StringBuilder();
        content.append(template.getHeader());
        String body = template.getBody();
        for (TableData data : tableData) {
            String currTablePrefix = template.getBodyPrefix().replace(Constants.PARAM_TABLE_ALIAS, data.getAlias());
            currTablePrefix = currTablePrefix.replace(Constants.PARAM_TABLE_NAME, data.getName());
            currTablePrefix = currTablePrefix.replace(Constants.PARAM_TABLE_FIX_NAME, data.getFixName());

            currTablePrefix = currTablePrefix.replace(Constants.PARAM_TABLE_DATA, data.getJsonData());


            content.append(currTablePrefix);

            if (body != null && !body.isEmpty()) {
                for (TableField field : data.getFieldInfo()) {
                    String currFieldContent = body.replace(Constants.PARAM_FIELD_ALIAS, field.getAlias());
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

    @Override
    public void init(String templateContent) {
        this.template = parseTemplate(templateContent);
    }

    public Template parseTemplate(String content) {
        Matcher matcher = TABLE_MATCH_REG.matcher(content);
        if (!matcher.find()) {
            return null;
        }

        Template tableTemplate = new Template();
        tableTemplate.setHeader(matcher.group(1));
        tableTemplate.setTail(matcher.group(3));
        String tableBody = matcher.group(2);

        Matcher bodyMatcher =  FIELD_MATCH_REG.matcher(tableBody);

        if (bodyMatcher.find()) {
            tableTemplate.setBodyPrefix(bodyMatcher.group(1));
            tableTemplate.setBody(bodyMatcher.group(2));
            tableTemplate.setBodySuffix(bodyMatcher.group(3));
        } else {
            tableTemplate.setBodyPrefix(tableBody);
            tableTemplate.setBody("");
            tableTemplate.setBodySuffix("");
        }
        return tableTemplate;
    }
}
