
public class Mouse {
	private int $xPos;
	private int $yPos;
	
	private int $lastDeltaX;
	private int $lastDeltaY;
	
	private boolean $leftMouseDown;
	private boolean $rightMouseDown;
	private boolean $middleMouseDown;
	private boolean $leftMouseDownLast;
	private boolean $rightMouseDownLast;
	private boolean $middleMouseDownLast;
	
	public Mouse() {
	}
	
	public Mouse(int xPos, int yPos) {
		$xPos = xPos;
		$yPos = yPos;
	}
	
	public void setX(int x) {
		$xPos = x;
	}
	
	public void setY(int y) {
		$yPos = y;
	}
	
	public void setXY(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public int getX() {
		return $xPos;
	}
	
	public int getY() {
		return $yPos;
	}
	
	public void setDeltaX(int delta) {
		$lastDeltaX = delta;
	}
	
	public void setDeltaY(int delta) {
		$lastDeltaY = delta;
	}
	
	public void setDeltaXY(int x, int y) {
		setDeltaX(x);
		setDeltaY(y);
	}
	
	public int getDeltaX() {
		return $lastDeltaX;
	}
	
	public int getDeltaY() {
		return $lastDeltaY;
	}
	
	public void setLeftMouseDown(boolean state) {
		$leftMouseDownLast = $leftMouseDown;
		$leftMouseDown = state;
	}
	
	public void setRightMouseDown(boolean state) {
		$rightMouseDownLast = $rightMouseDown;
		$rightMouseDown = state;
	}
	
	public void setMiddleMouseDown(boolean state) {
		$middleMouseDownLast = $middleMouseDown;
		$middleMouseDown = state;
	}
	
	public void setMouseDown(boolean left, boolean mid, boolean right) {
		setLeftMouseDown(left);
		setMiddleMouseDown(mid);
		setRightMouseDown(right);
	}
	
	public boolean isLeftMousePressed() {
		return $leftMouseDown && !$leftMouseDownLast;
	}
	
	public boolean isLeftMouseDown() {
		return $leftMouseDown && $leftMouseDownLast;
	}
	
	public boolean isLeftMouseReleased() {
		return !$leftMouseDown && $leftMouseDownLast;
	}
	
	public boolean isRightMousePressed() {
		return $rightMouseDown && !$rightMouseDownLast;
	}
	
	public boolean isRightMouseDown() {
		return $rightMouseDown && $rightMouseDownLast;
	}
	
	public boolean isRightMouseReleased() {
		return !$rightMouseDown && $rightMouseDownLast;
	}
	
	public boolean isMiddleMousePressed() {
		return $middleMouseDown && !$middleMouseDownLast;
	}
	
	public boolean isMiddleMouseDown() {
		return $middleMouseDown && $middleMouseDownLast;
	}
	
	public boolean isMiddleMouseReleased() {
		return !$middleMouseDown && $middleMouseDownLast;
	}
}
