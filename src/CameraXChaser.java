
public class CameraXChaser extends Component {

	@Override
	public void update(long delta) {
		Camera cam = this.getActiveCamera();
		setPositionX(cam.getPosition().x);
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
