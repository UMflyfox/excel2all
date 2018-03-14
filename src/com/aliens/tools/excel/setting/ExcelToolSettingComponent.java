package com.aliens.tools.excel.setting;


import com.aliens.tools.excel.form.ExcelToolSettingUI;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by hejialin on 2015/2/26.
 */

public class ExcelToolSettingComponent implements ApplicationComponent, Configurable {

    private ExcelToolSettingUI form;

    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "com.aliens.tools.setting.ExcelToolSettingComponent";
    }


    @Nls
    @Override
    public String getDisplayName() {
        return "excel setting";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (form == null) {
            form = new ExcelToolSettingUI();
        }
        return form.getMainPanel();
    }

    @Override
    public boolean isModified() {
//        if (form != null) {
//            form.isDirty();
//        }
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        if (form != null) {
            form.doSave();
        }
    }

    @Override
    public void reset() {
        if (form != null) {
            form.init();
        }
    }

    @Override
    public void disposeUIResources() {
        form = null;
    }

}
