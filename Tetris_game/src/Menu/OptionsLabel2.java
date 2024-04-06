package Menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class OptionsLabel2 extends JPanel implements KeyListener {
    private int currentIndex = 1; // 현재 선택된 메뉴 인덱스
    private final String cursorSymbol = "> "; // 현재 선택된 메뉴룰 따라갈 커서
    private final String nonSelected = "  "; // 커서가 있을 위치
    private final String[] labels = {"Main Menu", String.format("Screen : %d x %d",  Main.SCREEN_WIDTH[1], Main.SCREEN_HEIGHT[1]), "Controls", "Score Board", "Color Blindness Mode", "Reset"}; // 메인 메뉴에 있을 서브 메뉴들.
    java.util.List<JLabel> menuItems;
    public final JLabel optionLabel2;

    private JLabel keyMessage;
    private javax.swing.Timer messageTimer;
    public OptionsLabel2() {
        setSize(Main.SCREEN_WIDTH[1], Main.SCREEN_HEIGHT[1]);
        setLayout(null);

        menuItems = new ArrayList<>();

        ImageIcon backgroundIcon = new ImageIcon(Main.class.getResource("../images/introBackground2.jpg"));
        optionLabel2 = new JLabel(new ImageIcon(backgroundIcon.getImage().getScaledInstance(Main.SCREEN_WIDTH[1], Main.SCREEN_HEIGHT[1], Image.SCALE_SMOOTH)));
        optionLabel2.setSize(Main.SCREEN_WIDTH[1], Main.SCREEN_HEIGHT[1]);

        JLabel title = new JLabel("Options");
        title.setFont(new Font("Arial", Font.BOLD, 40)); // 폰트 설정
        title.setForeground(Color.BLACK); // 텍스트 색상 설정
        title.setBounds(50, Main.SCREEN_HEIGHT[1] / 8, 400, 50); // 위치와 크기 설정
        optionLabel2.add(title);

        keyMessage = new JLabel(" ");
        keyMessage.setFont(new Font("Arial", Font.BOLD, 40)); // 폰트 설정
        keyMessage.setForeground(Color.BLACK); // 텍스트 색상 설정
        keyMessage.setBounds(Main.SCREEN_WIDTH[1]/2 - 300, Main.SCREEN_HEIGHT[1] / 2 - 100, 600, 100); // 위치와 크기 설정
        add(keyMessage);

        messageTimer = new javax.swing.Timer(3000, e -> keyMessage.setVisible(false));
        messageTimer.setRepeats(false); // 타이머가 한 번만 실행되도록 설정


        int Start_y = Main.SCREEN_HEIGHT[1] * 5 / 9;
        for (String i : labels) {
            addMenuItem(i, Start_y);
            Start_y += Main.SCREEN_HEIGHT[1] / 18;
        }

        updateMenuDisplay(); // 메뉴 디스플레이 업데이트
        add(optionLabel2);
        addKeyListener(this);
        setFocusable(true);
        setVisible(true);
    }

    private void updateMenuDisplay() {
        for (int i = 0; i < menuItems.size(); i++) {
            if (i == currentIndex) {
                menuItems.get(i).setText(cursorSymbol + labels[i]);
            } else {
                menuItems.get(i).setText(nonSelected + labels[i]);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if(keyCode == ((Number)(Main.jsonObject.get("K_UP"))).intValue())
            currentIndex = (currentIndex - 1 + menuItems.size()) % menuItems.size();
        else if(keyCode == ((Number)(Main.jsonObject.get("K_DOWN"))).intValue())
            currentIndex = (currentIndex + 1) % menuItems.size();
        else if(keyCode == ((Number)(Main.jsonObject.get("K_ENTER"))).intValue())
            activateMenuItem(currentIndex);
        else
            showTemporaryMessage("<html>Invalid Key Input. <br>Please press W, S, Enter</html>");
        updateMenuDisplay();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private void activateMenuItem(int index) {
        switch (index) {
            case 0: // MainMenu
                System.out.println("Main Menu");
                switchToScreen(Main.mainMenu2);
                // 다시 메인메뉴로 이동함.
                break;
            case 1:
                System.out.println("Screen"); // 키설정을 바꾸는 로직 추가.
                Main.frame.setSize(Main.SCREEN_WIDTH[2], Main.SCREEN_HEIGHT[2]);
                switchToScreen(Main.optionMenu3);
                break;
            case 2: // Exits
                System.out.println("Controls");
                switchToScreen(Main.keyControl2);
                break;
            case 3:
                System.out.println("Score Board");
                break;
            case 4:
                System.out.println("Color Blindness Mode");
                break;
            case 5:
                System.out.println("Reset");
                break;
            default:
                break;
        }
    }

    private void addMenuItem(String text, int y) {
        JLabel menuItem = new JLabel(text);
        menuItem.setFont(new Font("Arial", Font.BOLD, Main.SCREEN_HEIGHT[1] / 24)); // 폰트 설정
        menuItem.setForeground(Color.BLACK); // 텍스트 색상 설정
        menuItem.setBounds((Main.SCREEN_HEIGHT[1] / 24) + Main.SCREEN_HEIGHT[1] / 72, y, 400, Main.SCREEN_HEIGHT[1] / 24);
        menuItems.add(menuItem);
        optionLabel2.add(menuItem);
    }
    private void showTemporaryMessage(String message)
    { // 화면에 키입력 메시지를 띄움
        keyMessage.setText(message); // 메시지 표시
        keyMessage.setVisible(true); // 라벨을 보이게 설정
        messageTimer.restart(); // 타이머 시작 (이전 타이머가 실행 중이었다면 재시작)
    }
    public void switchToScreen(JPanel newScreen) {
        Main.cardLayout.show(Main.mainPanel, newScreen.getName()); // 화면 전환
        newScreen.setFocusable(true); // 새 화면이 포커스를 받을 수 있도록 설정
        newScreen.requestFocusInWindow(); // 새 화면에게 포커스 요청
    }
}
