package blocks;

import java.awt.Color;

public class TBlock extends Block {
	
	public TBlock() {
		//2X3 크기의 2차원 정수 배열 설정
		shape = new int[][] { 
			{0, 1, 0},
			{1, 1, 1}
		};
		color = Color.MAGENTA;
	}
}
