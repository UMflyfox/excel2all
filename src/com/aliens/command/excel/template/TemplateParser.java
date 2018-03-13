package com.aliens.command.excel.template;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hejialin on 2018/3/12.
 */
public class TemplateParser {


    List<Template>


    public void Split(String content, String startTag, String endTag) {
        char[] contents = content.toCharArray();
        StringBuilder buff = new StringBuilder();
        StringBuilder prefix = new StringBuilder();
        StringBuilder suffix = new StringBuilder();


        StringBuilder lex = new StringBuilder();
        for (char currChar : contents) {

            if (currChar == ' ' || currChar == '\r' && currChar == '\n' && currChar == '\t') {
                buff.append(currChar);
                continue;
            }

            lex.append(currChar);

            if (lex.equals(startTag)) {

                //prefix.append(/)
            } else if (lex.equals(endTag)) {

            }
        }

    }

}
