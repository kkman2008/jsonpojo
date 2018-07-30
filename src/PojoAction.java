import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtilBase;

import javax.swing.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 18-6-30
 * Time: 下午1:20
 * To change this template use File | Settings | File Templates.
 */
public class PojoAction extends BaseGenerateAction {
    protected PsiClass mClass;
    private PsiElementFactory mFactory;
    private Project project;

    @SuppressWarnings("unused")
    public PojoAction() {
        super(null);
    }

    @SuppressWarnings("unused")
    public PojoAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    protected boolean isValidForClass(final PsiClass targetClass) {
        return super.isValidForClass(targetClass)     ;
    }

    @Override
    public boolean isValidForFile(Project project, Editor editor, PsiFile file) {

        return super.isValidForFile(project,editor,file);
    }


    public void actionPerformed(AnActionEvent event) {



        project = event.getData(PlatformDataKeys.PROJECT);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        PsiFile mFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        mClass=getTargetClass(editor,mFile);




        JsonUtilsDialog jsonD=new JsonUtilsDialog();
        jsonD.setmClass(mClass);
        jsonD.setmFactory(mFactory);

        jsonD.setmFile(mFile);
        jsonD.setProject(project);

        jsonD.setSize(600 ,400);
        jsonD.setLocationRelativeTo(null);
        jsonD.setVisible(true);




    }




}
