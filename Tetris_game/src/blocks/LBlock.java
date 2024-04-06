package blocks;

import java.awt.Color;

public class LBlock extends Block {
	
	public LBlock() {

		//2x3 크기의 2차원 정수 배열 설정
		shape = new int[][] { 
			{1, 1, 1},
			{1, 0, 0}
		};
		color = Color.ORANGE;
	}
}
