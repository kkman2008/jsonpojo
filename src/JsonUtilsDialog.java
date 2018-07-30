import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;

import javax.swing.*;
import java.awt.event.*;

public class JsonUtilsDialog extends JFrame {
    private JPanel contentPane2;
    private JButton buttonOK;
    private JButton cancel;
    private JLabel label22;
    private JTextPane textPane1;
    private boolean isbinding=false;


    protected PsiClass mClass;
    protected PsiElementFactory mFactory;
    protected PsiFile mFile;
    protected Project project;

//    private void askBinding() {
//        int i = Messages.showYesNoDialog("你要使用databinding吗", "你要使用databinding吗", Messages.getQuestionIcon());
//        if (i == Messages.OK) {
//            isbinding = true;
//        }else {
//            isbinding=false;
//        }
//
//
//    }

    public JsonUtilsDialog() {
        setContentPane(contentPane2);

        setTitle("JSON");
        getRootPane().setDefaultButton(buttonOK);


        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

//                askBinding();
                onOK();
            }
        });

        textPane1.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }


            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    onOK();


                }
            }


            @Override
            public void keyReleased(KeyEvent e) {
            }
        });


        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });


        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane2.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {

        String jsonSTR = textPane1.getText().toString();
        new WriterUtil(this, label22, jsonSTR, mFile, project, mClass).execute();

    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        JsonUtilsDialog dialog = new JsonUtilsDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public PsiClass getmClass() {
        return mClass;
    }

    public void setmClass(PsiClass mClass) {
        this.mClass = mClass;
    }

    public PsiElementFactory getmFactory() {
        return mFactory;
    }

    public void setmFactory(PsiElementFactory mFactory) {
        this.mFactory = mFactory;
    }


    public void setProject(Project project) {
        this.project = project;
    }

    public void setmFile(PsiFile mFile) {
        this.mFile = mFile;
    }

    private void createUIComponents() {
    }


}
