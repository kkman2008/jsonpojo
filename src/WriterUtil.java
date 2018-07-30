import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import config.Config;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 18-6-30
 * Time: 下午1:20
 * To change this template use File | Settings | File Templates.
 */
public class WriterUtil extends WriteCommandAction.Simple {

    protected PsiClass mClass;
    private PsiElementFactory mFactory;
    private Project project;

    private PsiFile mFile;
    private String jsonStr;
    private JFrame jDialog;
    private JLabel jLabel;
    private boolean isBinding;
    private List<String> keyWordList = new ArrayList<String>();

    public WriterUtil(  JFrame jDialog, JLabel jLabel,
                      String jsonStr, PsiFile mFile, Project project, PsiClass mClass, PsiFile... files) {
        super(project, files);

        this.isBinding = isBinding;
        mFactory = JavaPsiFacade.getElementFactory(project);
        this.mFile = mFile;
        this.jLabel = jLabel;
        this.jDialog = jDialog;
        this.jsonStr = jsonStr;
        this.project = project;
        this.mClass = mClass;
    }

    @Override
    protected void run() {


        JSONObject json = null;

        try {
            //直接解析
            json = new JSONObject(jsonStr);

        } catch (Exception e) {
            //删除注释代码再解析
            String temp = jsonStr.replaceAll("/\\*\\*" +
                    "[\\S\\s]*?" +
                    "\\*/", "");
            String jsonTS = temp.replaceAll("//[^\"\\]\\}']*\\s+", "");
            try {
                json = new JSONObject(jsonTS);

            } catch (Exception e2) {
                e2.printStackTrace();
                jLabel.setText("Json格式错误!");
            }
        }

        if (json != null) {
            //如果是从服务器返回的json 带有状态码Code:200和内容Content:[] 则只解析 Content里的json
            try {
                if (!json.get("Code").equals(null) && !json.get("Content").equals(null)) {
                    String jss = json.get("Content").toString();
                    JSONObject jsonObject = null;
                    if (jss.startsWith("[") && jss.endsWith("]")) {
                        JSONArray jsonArray = new JSONArray(jss);
                        jsonObject = jsonArray.getJSONObject(0);
                    } else {
                        jsonObject = new JSONObject(jss);
                    }

                    if (jsonObject != null) {
                        json = jsonObject;
                    }

                }

            } catch (Exception e) {
            }


            try {
                parseJson(json);
            } catch (Exception e2) {
                e2.printStackTrace();
                jLabel.setText("解析出错 !!");
            }
        }

    }


    public void parseJson(JSONObject json) {

        /**
         * 关键词
         *
         * */
        keyWordList.add("default");
        keyWordList.add("public");
        keyWordList.add("abstract");
        keyWordList.add("null");
        keyWordList.add("final");
        keyWordList.add("void");
        keyWordList.add("implements");
        keyWordList.add("this");
        keyWordList.add("instanceof");
        keyWordList.add("native");
        keyWordList.add("new");
        keyWordList.add("goto");
        keyWordList.add("const");
        keyWordList.add("volatile");
        keyWordList.add("return");
        keyWordList.add("finally");

        Set<String> set = json.keySet();
        List<String> list = new ArrayList<String>(set);
        List<String> fields = createField(json, list, mClass);

        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(project);
        styleManager.optimizeImports(mFile);
        styleManager.shortenClassReferences(mClass);
        jDialog.dispose();
    }

    private List<String> createField(JSONObject json, List<String> list, PsiClass mClass)
    {
        List fields = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("/** \n");
        for (int i = 0; i < list.size(); i++) {
            String key = (String)list.get(i);
            sb.append("* " + key + " : " + json.get(key) + "\n");
        }
        sb.append("*/ \n");

        for (int i = 0; i < list.size(); i++) {
            String key = (String)list.get(i);
            Object type = json.get(key);
            StringBuilder filedSb = new StringBuilder();
            fields.add(key);
            String typeStr = typeByValue(mClass, key, type, true);
                filedSb.append("public  ").append(typeStr).append(key).append(" ; ");

            String filedStr = null;
            if (i == 0)
                filedStr = filedSb.toString();
            else {
                filedStr = filedSb.toString();
            }


            mClass.add(this.mFactory.createFieldFromText(filedStr, mClass));

        }
        return fields;
    }


    public boolean checkKeyWord(String key) {

        return keyWordList.contains(key);
    }


    @NotNull
    private String typeByValue(PsiClass mClass, String key, Object type) {
        return typeByValue(mClass, key, type, false);

    }

    @NotNull
    private String typeByValue(PsiClass mClass, String key, Object type, boolean createClassSub) {
        String typeStr;
        if (type instanceof Boolean) {
            typeStr = " boolean ";
        } else if (type instanceof Integer) {
            typeStr = " int ";
        } else if (type instanceof Double) {
            typeStr = " double ";
        } else if (type instanceof Long) {
            typeStr = " long ";
        } else if (type instanceof String) {
            typeStr = " String ";
        } else if (type instanceof Character) {
            typeStr = " char ";
        } else if (type instanceof JSONObject) {
            typeStr = " " + createClassSubName(mClass, key, type, mClass, createClassSub) + " ";
            if (createClassSub) {
                createClassSub(typeStr, type, mClass);
            }
        } else if (type instanceof JSONArray) {
            typeStr = " java.util.List<" + createClassSubName(mClass, key, type, mClass, createClassSub) + "> ";
        } else {
            typeStr = " String ";
        }
        return typeStr;
    }


    private void createClassSub(String className, Object o, PsiClass mClass) {


        if (o instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) o;
            createClassSub(className, jsonObject, mClass);
        }

    }

    private void createClassSub(String className, JSONObject json, PsiClass mClass) {



        PsiClass subClass = mFactory.createClass(className.trim());
        subClass.setName(className.trim());

        Set<String> set = json.keySet();
        List<String> list = new ArrayList<String>(set);
        List<String> fields = createField(json, list, subClass);

        mClass.add(subClass);

    }

    private String createClassSubName(PsiClass aClass, String key, Object o, PsiClass mClass, boolean createClassSUb) {


        String name = "";
        if (o instanceof JSONObject) {
            name = key.substring(0, 1).toUpperCase() + key.substring(1) + "";
        } else if (o instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) o;
            if (jsonArray.length() > 0) {
                Object item = jsonArray.get(0);
                name = typeByValue(mClass, key, item, createClassSUb);
            } else {
                name = "?";
            }
        }
        return name;

    }




 }
