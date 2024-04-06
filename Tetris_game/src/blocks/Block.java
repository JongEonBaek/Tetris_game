package blocks;

import java.awt.Color;

//색상을 다루기 위한 기능 사용

//추상클래스 정의하여 Block을 상속받을 수 있게 함
public abstract class Block {
		
	protected int[][] shape; //테트리스 블록의 형태를 나타냄. 예를 들어, 2x2 크기의 정사각형 블록은 {{1,1}, {1,1}}
	protected Color color; // 블록의 색상 저장하는 color객체


	//생성자 Block, 모든 블록의 기본 형태와 색상을 초기화, 2x2형태, 노란색으로 설정
	public Block() {
		shape = new int[][]{ 
				{1, 1}, 
				{1, 1}
		};
		color = Color.YELLOW;
	}

	//getShape 메서드는 주어진 좌표에 해당하는 블록의 형태 값을 반환
	public int getShape(int x, int y) {
		return shape[y][x];
	}


	//getColor 메서드는 블록의 색상을 반환
	public Color getColor() {
		return color;
	}


	//rotate 메서드는 블록을 90도 시계 방향으로 회전시키기 위한 메서드
	public void rotate() {
		int originalHeight = height();
		int originalWidth = width();
		int[][] newShape = new int[originalWidth][originalHeight];

		for (int i = 0; i < originalHeight; i++) {
			for (int j = 0; j < originalWidth; j++) {
				newShape[j][originalHeight - 1 - i] = shape[i][j];
			}
		}

		shape = newShape;


	}


	//height 메서드는 블록의 높이(세로 길이)를 반환
	public int height() {
		return shape.length;
	}


	//width 메서드는 블록의 너비(가로 길이)를 반환. 만약 shape 배열이 비어있지 않다면 첫 번째 행의 길이를 너비로 반환
	public int width() {
		if(shape.length > 0)
			return shape[0].length;
		return 0;
	}
}
