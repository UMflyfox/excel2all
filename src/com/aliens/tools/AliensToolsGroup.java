package com.aliens.tools;


import com.aliens.tools.excel.ConvertAction;
import com.intellij.ide.projectView.impl.ModuleGroup;
import com.intellij.openapi.actionSystem.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hejialin on 2018/3/9.
 */

public class AliensToolsGroup extends ActionGroup {

    private ModuleGroup myModuleGroup;


    @Override
    public void update(AnActionEvent e) {
//        final DataContext dataContext = e.getDataContext();
//        final Project project = CommonDataKeys.PROJECT.getData(dataContext);
//        e.getPresentation().setVisible(true);
//        final ConfigurationContext context = ConfigurationContext.getFromContext(e.getDataContext());
//        final Presentation presentation = e.getPresentation();
//        presentation.setEnabled(true);
//        presentation.setVisible(true);
    }

    public AliensToolsGroup() {
        super("Aliens tools", true);
    }

    public AliensToolsGroup(ModuleGroup moduleGroup) {
        myModuleGroup = moduleGroup;
    }

    @NotNull
    @Override
    public AnAction[] getChildren(AnActionEvent anActionEvent) {
        List<AnAction> actionList = new ArrayList<AnAction>();
        actionList.add(new ConvertAction());

        AnAction[] actions = new AnAction[actionList.size()];
        actionList.toArray(actions);
        return actions;
    }

}
