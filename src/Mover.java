
public class Mover extends Component {
	private Vector3d $moveVector;
	
	
	public Mover(float xSpeed, float ySpeed, float zSpeed) {
		$moveVector = new Vector3d(xSpeed, ySpeed, zSpeed);
	}
	
	public Mover() {
		$moveVector = new Vector3d(0, 0, 0);
	}
	
	@Override
	public void update(long delta) {
		this.move($moveVector);
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onKeyInput(long window, int key, int scancode, int action,
			int mods) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCollision(Model collisionModel, ColBox collisionColbox) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPicked() {
		// TODO Auto-generated method stub

	}

}
