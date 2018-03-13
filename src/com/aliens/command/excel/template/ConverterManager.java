package com.aliens.command.excel.template;

import com.aliens.command.excel.ExcelParser;
import com.aliens.command.excel.model.TableData;
import com.aliens.command.excel.model.TableField;
import com.aliens.command.excel.template.constant.Constants;
import com.aliens.command.excel.template.dialect.Dialect;
import com.aliens.command.excel.template.dialect.GolandDialect;
import com.aliens.command.excel.template.model.Template;
import com.aliens.util.FileUtil;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hejialin on 2018/3/12.
 */
public class ConverterManager {

    private String templatePath;

    private String templateContent;

    private List<Converter> converters = new ArrayList<Converter>();

    public ConverterManager(String templatePath) {
        this.templatePath = templatePath;
        init();
    }

    public void init() {
        this.templateContent = FileUtil.instance.readContent(templatePath);
        converters.add(new EnumConverter());
        converters.add(new TableConverter());
    }

    public String convert(Collection<TableData> data, Dialect dialect) {
        if (templateContent == null || templateContent.isEmpty()) {
            return "";
        }
        String content = this.templateContent;
        for (Converter converter : converters) {
            converter.init(content);
            String result = converter.convert(data, dialect);
            if (result != null) {
                content = result;
            }
        }
        return content;
    }


    public static void main(String[] args) {

        ExcelParser parser = new ExcelParser();
        parser.parse(new File("/Users/hejialin/Downloads/testconvert"));

        ConverterManager template = new ConverterManager("/Users/hejialin/Downloads/testconvert/js.template");

        String content = template.convert(parser.getData().values(), GolandDialect.getInstance());

        FileUtil.instance.writeContent("/Users/hejialin/Downloads/testconvert/js.out", content);
    }
}
