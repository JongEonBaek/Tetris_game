package Menu;

import component.Board;

import javax.swing.*;
import java.awt.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class Main {
    public static JFrame frame;
    public static JPanel mainPanel;
    public static CardLayout cardLayout;
    public static final int SCREEN_WIDTH[] = {1280, 1024, 960};
    public static final int SCREEN_HEIGHT[] = {720, 576, 540};
    public static MainMenuLabel1 mainMenu1;
    public static OptionsLabel1 optionMenu1;
    public static MainMenuLabel2 mainMenu2;
    public static OptionsLabel2 optionMenu2;
    public static MainMenuLabel3 mainMenu3;
    public static OptionsLabel3 optionMenu3;
    public static Board gamePanel;
    public static KeyControl1 keyControl1;
    public static KeyControl2 keyControl2;
    public static KeyControl3 keyControl3;


    /////////////////////////////설정값들 관리.
    public static JSONParser parser;
    public static JSONObject jsonObject;
    public static boolean isInputing = false; // 사용자가 키값을 바꾸려고 할 때인가?
    public static String currentChangingKey = "";

    public static void main(String[] args) throws IOException {
        parser = new JSONParser();
        System.out.println();


        try (FileReader reader = new FileReader("src/Settings.json")) {
            // 파일로부터 JSON 객체를 읽어오기
            jsonObject = (JSONObject) parser.parse(reader);

            // 데이터 읽기
            System.out.println("Screen size : " + jsonObject.get("Screen"));
            System.out.println("K_UP : " + jsonObject.get("K_UP"));
            System.out.println("K_DOWN : " + jsonObject.get("K_DOWN"));
            System.out.println("K_LEFT : " + jsonObject.get("K_LEFT"));
            System.out.println("K_RIGHT : " + jsonObject.get("K_RIGHT"));
        } catch (Exception e) {
            e.printStackTrace();
        }


        frame = new JFrame("Tetris Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(SCREEN_WIDTH[0], SCREEN_HEIGHT[0]);
        frame.setLocationRelativeTo(null);

        // 메인 패널 초기화 및 레이아웃 설정
        cardLayout = new CardLayout();
        mainPanel = new JPanel();


        mainPanel.setLayout(cardLayout);

        // 메뉴와 옵션 패널 추가
        mainMenu1 = new MainMenuLabel1();
        mainMenu1.setName("MainMenu1");
        optionMenu1 = new OptionsLabel1();
        optionMenu1.setName("Options1");
        mainMenu2 = new MainMenuLabel2();
        mainMenu2.setName("MainMenu2");
        optionMenu2 = new OptionsLabel2();
        optionMenu2.setName("Options2");
        mainMenu3 = new MainMenuLabel3();
        mainMenu3.setName("MainMenu3");
        optionMenu3 = new OptionsLabel3();
        optionMenu3.setName("Options3");

        gamePanel = new Board();
        gamePanel.setSize(SCREEN_WIDTH[0], SCREEN_HEIGHT[0]);
        gamePanel.setVisible(true);
        gamePanel.setName("game");

        keyControl1 = new KeyControl1();
        keyControl1.setName("Control1");
        keyControl2 = new KeyControl2();
        keyControl2.setName("Control2");
        keyControl3 = new KeyControl3();
        keyControl3.setName("Control3");


        mainPanel.add(mainMenu1, "MainMenu1");
        mainPanel.add(optionMenu1, "Options1");
        mainPanel.add(mainMenu2, "MainMenu2");
        mainPanel.add(optionMenu2, "Options2");
        mainPanel.add(mainMenu3, "MainMenu3");
        mainPanel.add(optionMenu3, "Options3");
        mainPanel.add(gamePanel, "game");
        mainPanel.add(keyControl1, "Control1");
        mainPanel.add(keyControl2, "Control2");
        mainPanel.add(keyControl3, "Control3");

        cardLayout.show(mainPanel, "MainMenu1");

        frame.add(mainPanel);
        frame.setVisible(true);

        try (FileWriter file = new FileWriter("src/Settings.json")) {
            file.write(jsonObject.toJSONString());
            file.flush();
        } catch (Exception e) {
        e.printStackTrace();
        }


    }
}
