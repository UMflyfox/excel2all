package com.aliens.tools.excel.form;
import com.aliens.command.excel.ExcelParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by hejialin on 2018/3/9.
 */
public class ConverterUI {
    private static JFrame frame;

    private JButton ok;
    private JTextPane output;
    private JPanel mainPanel;
    private JComboBox comboBox1;
    private JTextField inputText;
    private JTextField outputText;
    private JButton inputButton;
    private JButton outputButton;


    public JButton getOk() {
        return ok;
    }

    public JTextPane getOutput() {
        return output;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void init() {
        addFileSelect(inputButton, inputText);
        addFileSelect(outputButton, outputText);
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExcelParser parser = new ExcelParser();
                try {
                    parser.parse(new File(inputText.getText()));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                output.setText("ok!");
            }
        });
    }

    private void addFileSelect(JButton button, JTextField text) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setDialogTitle("选择文件路径");

                int result = chooser.showOpenDialog(mainPanel);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    if (file != null && file.exists()) {
                        text.setText(chooser.getSelectedFile().getAbsolutePath());
                    }
                }
            }
        });
    }



    public static void Open() {
        if (frame == null) {
            frame = new JFrame("ConverterUI");
            ConverterUI ui = new ConverterUI();
            frame.setContentPane(ui.mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //frame.setLocationRelativeTo(null);
            frame.setSize(650, 500);
            ui.init();
        }


        int windowWidth = frame.getWidth(); // 获得窗口宽
        int windowHeight = frame.getHeight(); // 获得窗口高
        Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
        Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
        int screenWidth = screenSize.width; // 获取屏幕的宽
        int screenHeight = screenSize.height; // 获取屏幕的高
        frame.setLocation(screenWidth / 2- windowWidth / 2, screenHeight / 2- windowHeight / 2);// 设置窗口居中显示

        //frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Open();
    }
}
