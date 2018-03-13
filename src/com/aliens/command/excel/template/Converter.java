package com.aliens.command.excel.template;

import com.aliens.command.excel.model.TableData;
import com.aliens.command.excel.template.dialect.Dialect;

import java.util.Collection;

/**
 * Created by hejialin on 2018/3/13.
 */
public interface Converter {

    String convert(Collection<TableData> tableData, Dialect dialect);

    void init(String templateContent);
}
