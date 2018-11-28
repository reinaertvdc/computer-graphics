
public class Rotater extends Component {
	Vector3d $rotation = new Vector3d(1, 1, 1);

	public Rotater(float x, float y, float z) {
		$rotation = new Vector3d(x, y, z);
	}
	
	public Rotater() {
		
	}
	
	@Override
	public void update(long delta) {
		this.addRotation($rotation);
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
