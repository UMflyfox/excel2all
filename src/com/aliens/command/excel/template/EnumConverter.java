package com.aliens.command.excel.template;

import com.aliens.command.excel.model.TableData;
import com.aliens.command.excel.model.TableField;
import com.aliens.command.excel.template.constant.Constants;
import com.aliens.command.excel.template.dialect.Dialect;
import com.aliens.command.excel.template.model.Template;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hejialin on 2018/3/13.
 */
public class EnumConverter implements Converter {

    public static final Pattern ENUM_MATCH_REG = Pattern.compile("([\\s\\S]*)<enum>([\\s\\S]*)</enum>([\\s\\S]*)");
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
            if (body == null || body.isEmpty()) {
               continue;
            }
            for (TableField field : data.getFieldInfo()) {
                if (!field.isEnum()) {
                    continue;
                }

                String currTablePrefix = template.getBodyPrefix().replace(Constants.PARAM_TABLE_ALIAS, data.getAlias());
                currTablePrefix = currTablePrefix.replace(Constants.PARAM_TABLE_NAME, data.getName());
                currTablePrefix = currTablePrefix.replace(Constants.PARAM_TABLE_FIX_NAME, data.getFixName());
                currTablePrefix = currTablePrefix.replace(Constants.PARAM_FIELD_ALIAS, field.getAlias());
                content.append(currTablePrefix);

                for (Map.Entry<String, String> enumInfo : field.getEnums().entrySet()) {
                    String currEnumContent = body.replace(Constants.PARAM_ENUM_ALIAS, enumInfo.getKey());
                    currEnumContent = currEnumContent.replace(Constants.PARAM_ENUM_NAME, enumInfo.getValue());
                    currEnumContent = currEnumContent.replace(Constants.PARAM_ENUM_TYPE, dialect.getType(field.getFieldType()));
                    currEnumContent = currEnumContent.replace(Constants.PARAM_ENUM_VALUE, String.valueOf(field.getEnum(enumInfo.getKey())));
                    content.append(currEnumContent);
                }
            }
            content.append(template.getBodySuffix());
        }
        content.append(template.getTail());
        return content.toString();
    }

    @Override
    public void init(String templateContent) {
        this.template = parseTemplate(templateContent);
    }

    public Template parseTemplate(String content) {
        Matcher matcher = ENUM_MATCH_REG.matcher(content);
        if (!matcher.find()) {
            return null;
        }

        Template template = new Template();
        template.setHeader(matcher.group(1));
        template.setTail(matcher.group(3));
        String tableBody = matcher.group(2);

        Matcher bodyMatcher =  FIELD_MATCH_REG.matcher(tableBody);

        if (bodyMatcher.find()) {
            template.setBodyPrefix(bodyMatcher.group(1));
            template.setBody(bodyMatcher.group(2));
            template.setBodySuffix(bodyMatcher.group(3));
        } else {
            template.setBodyPrefix(tableBody);
            template.setBody("");
            template.setBodySuffix("");
        }
        return template;
    }
}
