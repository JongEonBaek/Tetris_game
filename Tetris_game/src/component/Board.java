package component;

import blocks.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Random;
import Menu.Main;



// JFrame 상속받은 클래스 Board
public class Board extends JPanel {

	public static final int HEIGHT = 20; // 높이
	//직렬화 역직렬화 과정에서 클래스 버전의 호환성 유지하기 위해 사용됨.
	public static final int WIDTH = 10; // 너비
	public static final char BORDER_CHAR = 'X'; //게임 테두리 문자
	private static final long serialVersionUID = 2434035659171694595L; // 이 클래스의 고유한 serialVersionUID
	private int initInterval = 1000; //블록이 자동으로 아래로 떨어지는 속도 제어 시간, 현재 1초
	private final JTextPane pane; //게임 상태 표시하는 JTextPane 객체
	private final KeyListener playerKeyListener; // 사용자의 키 입력을 처리하는 KeyListener 객체
	private final SimpleAttributeSet styleSet; // 텍스트 스타일 설정하는 SimpleAttributeSet
	public final Timer timer; // 블록이 자동으로 아래로 떨어지게 하는 Timer
	int x = 3; //Default Position. 현재 블록 위치
	int y = 0; // 현재 블록 위치
	int point = 1; // 한칸 떨어질때 얻는 점수
	int scores = 0; // 현재 스코어
	int level = 0; // 현재 레벨
	int lines = 0; // 현재 지워진 라인 수
	int bricks = 0; // 생성된 벽돌의 개수
	String name;

	private boolean isPaused = false; // 게임이 일시 중지되었는지 나타내는 변수
	private JTextPane nextpane;// 넥스트블록 표시하는 판
	private int[][] board; // 게임 보드의 상태를 나타내는 2차원 배열
	private Block curr; // 현재 움직이고 있는 블록
	private Block nextcurr; // 다음 블럭

	int mode = 1; // 난이도 설정 easy == 0, normal == 1, hard == 2;

	public boolean gameOver = false; // 게임오버를 알려주는변수 true == 게임오버



	// 생성자 Board, 게임 창 설정 및 초기게임 보드 준비, 첫 번째 블록 생성하고, 타이머 시작
	public Board() {
		//Board display setting.
		pane = new JTextPane(); // 텍스트 패널 생성
		pane.setEditable(false); // 텍스트 패널 편집 불가하도록 설정
		pane.setBackground(Color.BLACK); // 텍스트 패널의 배경색을 검은색으로 설정
		CompoundBorder border = BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.GRAY, 10),
				BorderFactory.createLineBorder(Color.DARK_GRAY, 5)); // 복합 테두리 생성
		pane.setBorder(border); // 텍스트 패널에 테두리를 설정
		Border innerPadding = new EmptyBorder(0, 0, 0, 0); // 상단, 왼쪽, 하단, 오른쪽 여백 설정
		pane.setPreferredSize(new Dimension(230, 700)); // 가로 300, 세로 200의 크기로 설정


		// 기존 복합 테두리와 내부 여백을 결합한 새로운 복합 테두리 생성
		CompoundBorder newBorder = new CompoundBorder(border, innerPadding);

		// 텍스트 패널에 새로운 테두리 설정
		pane.setBorder(newBorder);
		this.add(pane, BorderLayout.WEST); // 텍스트 패널을 창의 west에 추가.this는 Board클래스의 인스턴스를 지칭
		sideBoard(); // textpane인 sideBoard 생성

		//Document default style.
		styleSet = new SimpleAttributeSet(); // 스타일 설정을 위한 객체 생성
		StyleConstants.setFontSize(styleSet, 25); // 폰트 크기를 18로 설정
		StyleConstants.setFontFamily(styleSet, "consolas");// 폰트 종류를 mac은 Courier로 설정, window는 consolas로 설정
		StyleConstants.setBold(styleSet, true); // 폰트를 굵게 설정
		StyleConstants.setForeground(styleSet, Color.WHITE); // 폰트 색상을 흰색으로 설정


		StyleConstants.setAlignment(styleSet, StyleConstants.ALIGN_CENTER); // 텍스트 정렬을 가운데로 설정

		//Set timer for block drops.
		timer = new Timer(initInterval, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveDown(); // 블록 아래로 이동
				drawBoard(); // 보드 그리기

			}
		});

		//Initialize board for the game.
		board = new int[HEIGHT][WIDTH]; // 게임 보드 초기화
		playerKeyListener = new PlayerKeyListener(); // 플레이어 키 리스너를 생성
		addKeyListener(playerKeyListener); //키 리스너 추가
		setFocusable(true); // 키 입력을 받을 수 있도록 설정
		requestFocus(); //  입력 포커스 요청

		//Create the first block and draw.
		curr = getRandomBlock(); // 첫 번째 블록을 무작위로 선택
		bricks--;
		nextcurr = getRandomBlock(); // 다음 블록을 무작위로 선택

		placeBlock(); //  선택된 블록을 배치합니다.
		drawBoard(); // 보드를 그린다.
		// timer.start(); // 타이머 시작
	}



	private Block getRandomBlock() {

		Random rnd = new Random(System.currentTimeMillis()); // 현재 시간 기준으로 랜덤 객체 생성
		bricks++;
		setLevel();
		int slot = 0;

		switch (mode) {

			case 0:
				slot = rnd.nextInt(36); // 0부터 35사이의 난수를 생성 (총 36개 슬롯)
				if (slot < 6) { // 0번 블럭을 6번 포함 (0, 1, 2, 3, 4, 5)
					return new IBlock(); // I 모양 블록 생성 반환
				} else if (slot < 11) { // 1번 블럭을 5번 포함 (6, 7, 8, 9, 10)
					return new JBlock(); // J 모양 블록 생성 반환
				} else if (slot < 16) { // 2번 블럭을 5번 포함 (11, 12, 13, 14, 15)
					return new LBlock(); // L 모양 블록 생성 반환
				} else if (slot < 21) { // 3번 블럭을 5번 포함 (16, 17, 18, 19, 20)
					return new ZBlock(); // Z 모양 블록 생성 반환
				} else if (slot < 26) { // 4번 블럭을 5번 포함 (21, 22, 23, 24, 25)
					return new SBlock(); // S 모양 블록 생성 반환
				} else if (slot < 31) { // 5번 블럭을 5번 포함 (26, 27, 28, 29, 30)
					return new TBlock(); // T 모양 블록 생성 반환
				} else { // 나머지는 6번 블럭 (31, 32, 33, 34, 35)
					return new OBlock(); // O 모양 블록 생성 반환
				}
			case 1:
				slot = rnd.nextInt(7);
				if (slot == 0) { // 0번 블럭을 4번 포함 (0, 1, 2, 3)
					return new IBlock();
				} else if (slot == 1) { // 1번 블럭
					return new JBlock();
				} else if (slot == 2) { // 2번 블럭
					return new LBlock();
				} else if (slot == 3) { // 3번 블럭
					return new ZBlock();
				} else if (slot == 4) { // 4번 블럭
					return new SBlock();
				} else if (slot == 5) { // 5번 블럭
					return new TBlock();
				} else { // 나머지는 6번 블럭
					return new OBlock();
				}
			case 2:
				slot = rnd.nextInt(34); // 0부터 33사이의 난수를 생성 (총 34개 슬롯)
				if (slot < 4) { // 0번 블럭을 4번 포함 (0, 1, 2, 3)
					return new IBlock();
				} else if (slot < 9) { // 1번 블럭을 5번 포함 (4, 5, 6, 7, 8)
					return new JBlock();
				} else if (slot < 14) { // 2번 블럭을 5번 포함 (9, 10, 11, 12, 13)
					return new LBlock();
				} else if (slot < 19) { // 3번 블럭을 5번 포함 (14, 15, 16, 17, 18)
					return new ZBlock();
				} else if (slot < 24) { // 4번 블럭을 5번 포함 (19, 20, 21, 22, 23)
					return new SBlock();
				} else if (slot < 29) { // 5번 블럭을 5번 포함 (24, 25, 26, 27, 28)
					return new TBlock();
				} else { // 나머지는 6번 블럭 (29, 30, 31, 32, 33)
					return new OBlock();
				}

		}
		return null;
	}

	private void placeBlock() {
		// 현재 떨어지고 있는 블록(curr)을 게임보드(board)에 배치하고, JTextPane(pane)에 해당블록의 시각적 표현을 업데이트 하는 역할
		StyledDocument doc = pane.getStyledDocument(); // 현재 JTextPane의 스타일이 적용된 문서를 가져옵니다.
		SimpleAttributeSet styles = new SimpleAttributeSet(); // 스타일 속성을 설정하기 위한 객체를 생성합니다.
		StyleConstants.setForeground(styles, curr.getColor()); // 현재 블록의 색상을 스타일 속성에 설정합니다.
		StyleConstants.setForeground(styles, nextcurr.getColor()); // 현재 블록의 색상을 스타일 속성에 설정합니다.
		for (int j = 0; j < curr.height(); j++) {// 현재 블록의 높이만큼 반복합니다.
			for (int i = 0; i < curr.width(); i++) {// 현재 블록의 너비만큼 반복합니다.
				if (curr.getShape(i, j) != 0 && board[y + j][x + i] == 0) {// 보드에 0이아니면 그대로 유지해야만 함. 아니면 내려가면서 다른 블럭 지움
					board[y + j][x + i] = curr.getShape(i, j);// 게임 보드 배열에 블록의 모양을 저장합니다.
				}
			}
		}
	}

	private void eraseCurr() {
		// 블록이 이동하거나 회전할 때 이전위치의 블록을 지우는 기능을 수행하는 메소드
		for (int i = x; i < x + curr.width(); i++) {// 현재 블록의 너비만큼 반복합니다.
			for (int j = y; j < y + curr.height(); j++) {// 현재 블록의 높이만큼 반복합니다.
				if (curr.getShape(i - x, j - y) != 0 && board[j][i] != 0) {// 현재 블록의 일부인 경우에만 발동
					board[j][i] = 0;// 게임 보드에서 현재 블록의 위치를 0으로 설정하여 지웁니다.
				}
			}
		}
	}


	private void checkLines() {
		for (int i = HEIGHT - 1; i >= 0; i--) {
			boolean lineFull = true;
			for (int j = 0; j < WIDTH; j++) {
				if (board[i][j] == 0) {
					lineFull = false;
					break;
				}
			}
			if (lineFull) {
				for (int k = i; k > 0; k--) {
					board[k] = Arrays.copyOf(board[k - 1], WIDTH);
				}
				Arrays.fill(board[0], 0);
				scores += 100;
				lines++; // 완성된 라인 수 증가
			}
		}
	}

	// 현재 블록을 아래로 이동할 수 있는지 확인하는 메소드
	private boolean canMoveDown() {
		// 블럭이 아래로 내려갈 수 있는지 확인하는 메소드
		if (y + curr.height() == HEIGHT) return false; // 바닥에 닿은 경우

		for (int i = 0; i < curr.width(); i++) {
			for (int j = 0; j < curr.height(); j++) {
				if (curr.getShape(i, j) != 0) { // 블록의 일부인 경우
					if (board[y + j + 1][x + i] != 0) { // 아래 칸이 비어있지 않은 경우
						return false; // 이동할 수 없음
					}
				}
			}
		}
		return true; // 모든 검사를 통과하면 이동할 수 있음
	}

	protected boolean canMoveLeft() {
		// 블록을 왼쪽으로 이동할 수 있는지 확인하는 메소드
		// 이 메소드는 블록의 왼쪽에 다른 블록이 없고, 블록이 게임 보드의 왼쪽 경계를 넘지 않는 경우에만 true를 반환합니다.
		for (int i = 0; i < curr.height(); i++) {
			for (int j = 0; j < curr.width(); j++) {
				if (curr.getShape(j, i) != 0) {
					if (x + j - 1 < 0 || board[y + i][x + j - 1] != 0) {
						return false;
					}
				}
			}
		}
		return true;
	}

	protected boolean canMoveRight() {
		// 블록을 오른쪽으로 이동할 수 있는지 확인하는 메소드
		// 블록의 오른쪽에 다른 블록이 없고, 블록이 게임 보드의 오른쪽 경계를 넘지 않는 경우에만 true를 반환합니다.
		for (int i = 0; i < curr.height(); i++) {
			for (int j = 0; j < curr.width(); j++) {
				if (curr.getShape(j, i) != 0) {
					if (x + j + 1 >= WIDTH || board[y + i][x + j + 1] != 0) {
						return false;
					}
				}
			}
		}
		return true;
	}

	protected boolean canRotate() {
		curr.rotate();
		for (int i = 0; i < curr.height(); i++) {
			for (int j = 0; j < curr.width(); j++) {
				if (curr.getShape(j, i) != 0) {
					if (x + j < 0 || x + j >= WIDTH || y + i < 0 || y + i >= HEIGHT || board[y + i][x + j] != 0) {
						curr.rotate();
						curr.rotate();
						curr.rotate();
						return false;
					}
				}
			}
		}
		curr.rotate();
		curr.rotate();
		curr.rotate();
		return true;
	}


	// 현재 블록을 아래로 한 칸 이동시킨다. 만약 블록이 바닥이나 다른 블록에 닿았다면, 그 위치에 블록을 고정하고 새로운 블록 생성
	protected void moveDown() {
		eraseCurr(); // 현재 블록의 위치를 한칸 내리기 위해 게임 보드에서 지웁니다.
		if (canMoveDown()) { // 아래로 이동할 수 있는 경우
			y++; // 블록을 아래로 이동
			scores += point;


		} else { // 아래로 이동할 수 없는 경우 (다른 블록에 닿거나 바닥에 닿은 경우)
			placeBlock(); // 현재 위치에 블록을 고정시킵니다.
			checkLines(); // 완성된 라인이 있는지 확인합니다.
			curr = nextcurr; // 다음블록을 현재 블록으로 설정합니다.
			nextcurr = getRandomBlock(); // 새로운 블록을 무작위로 가져옵니다.
			x = 3; // 새 블록의 x좌표를 시작 x 좌표를 설정합니다.
			y = 0; // 새 블록의 y좌표를 시작 y 좌표를 설정합니다.
			if (!canMoveDown()) { // 새 블록이 움직일 수 없는 경우 (게임 오버)
				GameOver();
				

			}
		}
		placeBlock(); // 게임 보드에 현재 블록의 새 위치를 표시합니다.
	}


	protected void moveLeft() {
		// moveLeft 메서드는 현재 블록을 왼쪽으로 한 칸 이동시킵니다.

		eraseCurr(); // 현재 블록의 위치를 게임 보드에서 지웁니다.
		if (canMoveLeft()) {
			x--;
		}
		placeBlock(); // 게임 보드에 현재 블록의 새 위치를 표시합니다.
	}


	protected void moveRight() {
		// moveRight 메서드는 현재 블록을 오른쪽으로 한 칸 이동시킵니다.
		eraseCurr(); // 현재 블록의 위치를 게임 보드에서 지웁니다.
		if (canMoveRight()) {
			x++;
		}
		placeBlock(); // 게임 보드에 현재 블록의 새 위치를 표시합니다.
	}


	public void drawBoard() {
		// drawBoard() 메소드는 게임 보드의 현재 상태를 JTextPane에 그리는 역할을 합니다.
		StringBuffer sb = new StringBuffer(); // StringBuffer 객체를 생성하여 게임 보드의 상태를 문자열로 변환합니다.

		// 상단 경계선을 그립니다.
		for (int t = 0; t < WIDTH + 2; t++) sb.append(BORDER_CHAR); // 보드의 너비만큼 상단에 경계 문자(BORDER_CHAR)를 추가합니다.
		sb.append("\n"); // 줄 바꿈을 추가하여 경계선 다음에 내용이 오도록 합니다.

		// 게임 보드의 각 행을 순회합니다.
		for (int i = 0; i < board.length; i++) {

			sb.append(BORDER_CHAR); // 각 행의 시작에 경계문자(BORDER_CHAR)를 추가합니다.

			// 게임 보드의 각 열을 순회합니다.
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == 1) {
					sb.append("O"); // 블록이 있는 위치는 "O" 문자로 표시합니다.
				} else {
					sb.append(" "); // 블록이 없는 위치는 공백으로 표시합니다.
				}
			}

			sb.append(BORDER_CHAR); // 각 행의 끝에 경계문자(BORDER_CHAR)를 추가합니다.


			sb.append("\n"); // 줄 바꿈을 추가하여 다음 행으로 넘어갑니다.
			NextBlocknscore();// next블럭 및 점수 표시
		}

		// 하단 경계선을 그립니다.
		for (int t = 0; t < WIDTH + 2; t++) sb.append(BORDER_CHAR); // 보드의 너비만큼 하단에 경계문자(BORDER_CHAR)를 추가합니다.

		pane.setText(sb.toString()); // StringBuffer에 저장된 문자열을 JTextPane에 설정합니다.

		StyledDocument doc = pane.getStyledDocument(); // JTextPane의 스타일이 적용된 문서를 가져옵니다.
		doc.setParagraphAttributes(0, doc.getLength(), styleSet, false); // 가져온 문서에 스타일 속성을 적용합니다.
		pane.setStyledDocument(doc); // 스타일이 적용된 문서를 다시 JTextPane에 설정


	}

	public void reset() {
		// 게임 보드를 초기화합니다. 20x10 크기의 2차원 배열을 새로 생성합니다.
		this.board = new int[20][10];
	}


	public void sideBoard() {
		// Next블럭을 그리기 위한 텍스트패널 생성

		nextpane = new JTextPane(); // 텍스트 패널 생성
		nextpane.setEditable(false); // 텍스트 패널 편집 불가하도록 설정
		nextpane.setBackground(Color.ORANGE); // 텍스트 패널의 배경색을 검은색으로 설정

		CompoundBorder border = BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.GRAY, 10),
				BorderFactory.createLineBorder(Color.DARK_GRAY, 5)); // 복합 테두리 생성
		nextpane.setBorder(border); // 텍스트 패널에 테두리를 설정

		Border innerPadding = new EmptyBorder(0, 0, 0, 0); // 상단, 왼쪽, 하단, 오른쪽 여백 설정
		nextpane.setPreferredSize(new Dimension(230, 700)); // 가로 300, 세로 200의 크기로 설정
		// 기존 복합 테두리와 내부 여백을 결합한 새로운 복합 테두리 생성
		CompoundBorder newBorder = new CompoundBorder(border, innerPadding);
		// 텍스트 패널에 새로운 테두리 설정
		nextpane.setBorder(newBorder);
		this.add(nextpane, BorderLayout.EAST); // 텍스트 패널을 창의 EAST에 추가.this는 Board클래스의 인스턴스를 지칭
	}

	// 다음블럭표시 및 점수부분을 담당하는 함수, drawBoard 할 때 호출됨.
	public void NextBlocknscore() {
		StringBuffer nb = new StringBuffer(); // 문자열을 효율적으로 더하기 위한 StringBuffer 인스턴스 생성

		// 상단 경계선을 그립니다.
		nb.append("NEXT");// NEXT블럭의 상단경계선
		nb.append("\n"); // 줄 바꿈을 추가하여 경계선 다음에 내용이 오도록 합니다.
		nb.append("\n"); // 줄 바꿈을 추가하여 경계선 다음에 내용이 오도록 합니다.


		// 다음블럭을 처리하는 로직
		for (int i = 0; i < 2; i++) {
			//NEXT 블럭 표시
			for (int k = 0; k < nextcurr.width(); k++) {
				if (nextcurr.width() == 4 && i == 1) // "OOOO"만 너비가 4이므로 따로 처리
					break;
				if (nextcurr.getShape(k, i) == 1) nb.append("O"); // 나머지 블럭들 표시
				else nb.append(" ");
			}
			nb.append("\n");
		}

		//공백추가
		for (int i = 0; i < 7; i++) {
			nb.append("\n");
		}

		// 블럭,라인,점수,레벨 표시
		nb.append(String.format("BLOCK : %3d\n\n", bricks));
		nb.append(String.format("LINES : %3d\n\n", lines));
		nb.append(String.format("SCORE : %3d\n\n", scores));
		nb.append(String.format("LEVEL : %3d\n\n", level));

		nextpane.setText(nb.toString()); // StringBuffer에 저장된 문자열을 JTextPane에 설정합니다.

		StyledDocument doc = nextpane.getStyledDocument(); // JTextPane의 스타일이 적용된 문서를 가져옵니다.
		doc.setParagraphAttributes(0, doc.getLength(), styleSet, false); // 가져온 문서에 스타일 속성을 적용합니다.
		nextpane.setStyledDocument(doc); // 스타일이 적용된 문서를 다시 JTextPane에 설정
	}

	//일정 점수 도달하면 레벨+, 속도+, 얻는 점수+ 조정하는 함수, moveDown(), TimerAction에 호출됨
	public void setLevel() {
		double decreaseTime = 200; // 일정 블럭 수 도달 시 감소할 값(속도 증가)

		switch (mode) {
			case 0:
				if (bricks == 20 || bricks == 50 || bricks == 100 || bricks == 200) {
					level++;
					point++;
					timer.stop();
					initInterval = (int) (initInterval - decreaseTime);
					timer.setDelay(initInterval);
					timer.start();
				}
				break;
			case 1:
				if (bricks == 20 || bricks == 50 || bricks == 100 || bricks == 200) {
					level++;
					point++;
					timer.stop();
					initInterval = (int) (initInterval - (decreaseTime * 0.8));
					timer.setDelay(initInterval);
					timer.start();
				}
				break;
			case 2:
				if (bricks == 20 || bricks == 50 || bricks == 100 || bricks == 200) {
					level++;
					point++;
					timer.stop();
					initInterval = (int) (initInterval - (decreaseTime * 1.2));
					timer.setDelay(initInterval);
					timer.start();
				}
				break;
		}
	}

	public void GameInit(){
		initInterval = 1000; //블록이 자동으로 아래로 떨어지는 속도 제어 시간, 현재 1초
		timer.setDelay(initInterval);

		x = 3; //Default Position. 현재 블록 위치
		y = 0; // 현재 블록 위치
		scores = 0; // 현재 스코어
		point = 1; // 한칸 떨어질때 얻는 점수
		level = 0; // 현재 레벨
		lines = 0; // 현재 지워진 라인 수
		bricks = 0; // 생성된 벽돌의 개수
		isPaused = false; // 게임이 일시 중지되었는지 나타내는 변수
		curr =  getRandomBlock();// 현재 움직이고 있는 블록
		bricks--;
		nextcurr = getRandomBlock(); // 다음 블럭
		gameOver = false; // 게임오버를 알려주는변수 true == 게임오버

		for(int i = 0; i<HEIGHT; i++) {
			for (int u = 0; u < WIDTH; u++)
				board[i][u] = 0;
		}

		//timer.start();
		placeBlock(); //  선택된 블록을 배치합니다.
		drawBoard(); // 보드를 그린다.
	}

	public void switchToScreen(JPanel newScreen) {
		Main.cardLayout.show(Main.mainPanel, newScreen.getName()); // 화면 전환
		newScreen.setFocusable(true); // 새 화면이 포커스를 받을 수 있도록 설정
		newScreen.requestFocusInWindow(); // 새 화면에게 포커스 요청
	}

	
	// 게임 종료 이벤트
	public void GameOver() {
		timer.stop(); // 타이머를 멈춥니다.
		gameOver = true;
		int response = JOptionPane.showConfirmDialog(this, "점수를 저장하시겠습니까?", "Game Over", JOptionPane.YES_NO_OPTION);

		if (response == JOptionPane.YES_OPTION) {
			//점수 저장 구현 순위, 이름, 점수, 모드
			name = JOptionPane.showInputDialog(this, "이름을 입력하세요:"); // 이름입력하는 대화상자
			//정상적으로 이름을 입력했다면
			if (name != null && !name.isEmpty()) {
				switchToScreen(Main.mainMenu1);

				System.out.println(name);
				System.out.println(scores);
				System.out.println(mode);

			} else // 빈칸을 입력했거나, 이름입력대화상자에서 취소 눌렀을 때
				switchToScreen(Main.mainMenu1);

		} else if (response == JOptionPane.NO_OPTION) //점수 저장하시겠습니까? -> No일 때
			switchToScreen(Main.mainMenu1); // 메인메뉴로 회기

		GameInit();
	}



	
	
	
	public class PlayerKeyListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
			// 키가 타이핑됐을 때의 동작을 정의할 수 있으나, 여기서는 사용하지 않습니다.
		}

		@Override
		public void keyPressed(KeyEvent e) {
			// 키가 눌렸을 때의 동작을 정의합니다.
			switch (e.getKeyCode()) { // 눌린 키에 따라 적절한 동작을 수행합니다.
				case KeyEvent.VK_DOWN:
					moveDown(); // 아래 방향키가 눌렸을 때, 현재 블록을 아래로 이동시킵니다.
					drawBoard(); // 게임 보드를 다시 그립니다.
					break;
				case KeyEvent.VK_RIGHT:
					moveRight(); // 오른쪽 방향키가 눌렸을 때, 현재 블록을 오른쪽으로 이동시킵니다.
					drawBoard(); // 게임 보드를 다시 그립니다.
					break;
				case KeyEvent.VK_LEFT:
					moveLeft(); // 왼쪽 방향키가 눌렸을 때, 현재 블록을 왼쪽으로 이동시킵니다.
					drawBoard(); // 게임 보드를 다시 그립니다.
					break;
				case KeyEvent.VK_UP:
					eraseCurr(); // 현재 블록을 지웁니다.
					if (canRotate()) { // 블록이 회전 가능한 경우에만 회전을 수행합니다.
						curr.rotate(); // 현재 블록을 회전시킵니다.
						placeBlock();
					}

					drawBoard(); // 게임 보드를 다시 그립니다.
					break;
				case KeyEvent.VK_SPACE:
					isPaused = !isPaused; // 게임의 상태를 전환합니다.
					if (isPaused) {
						timer.stop(); // 게임이 일시 중지된 경우, 타이머를 중지합니다.
						pane.setText("Game Paused\nPress SPACE to continue"); // 게임이 일시 중지된 상태를 표시합니다.
					} else {
						timer.start(); // 게임이 재개된 경우, 타이머를 시작합니다.
					}
					break;
				case KeyEvent.VK_ENTER:
					eraseCurr();
					while (canMoveDown()) {
						y++;
					}
					placeBlock();
					checkLines();
					curr = nextcurr;
					nextcurr = getRandomBlock();
					x = 3; // 새 블록의 x좌표를 시작 x 좌표를 설정합니다.
					y = 0; // 새 블록의 y좌표를 시작 y 좌표를 설정합니다.
					placeBlock();
					drawBoard();
					break;
				case KeyEvent.VK_Q:
					System.exit(0); // 'q' 키가 눌렸을 때, 프로그램을 종료합니다.
					break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// 키가 떼어졌을 때의 동작을 정의할 수 있으나, 여기서는 사용하지 않습니다.
		}
	}




}