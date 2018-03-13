package com.aliens.tools.excel;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;

/**
 * Created by hejialin on 2018/3/9.
 */
public class ConvertAction extends AnAction {


    public ConvertAction(){
        super("xlsx convert");
    }


    @Override
    public void actionPerformed(AnActionEvent e) {
        ConverterUI.Open();
        //ConverterUI.Open();
    }
}
