
public class Selector extends Component {
	private boolean $picked;
	private double $distance;

	@Override
	public void update(long delta) {
		if($picked) {
			Vector3d newVector = getActiveCamera().advanceVector($distance);
			setPosition(newVector);
		}
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
	
	public void calculateDistance() {
		$distance = Vector3d.distanceBetween(getPosition(), getActiveCamera().getPosition());
	}

	@Override
	public void onPicked() {
		$picked = !$picked;
		if($picked)
			calculateDistance();
	}

}
