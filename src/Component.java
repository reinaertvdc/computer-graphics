
public abstract class Component implements LoopItem, InputListener {
	private Model $parent;
	private Game $game;
	
	public abstract void onCollision(Model collisionModel, ColBox collisionColbox);
	public abstract void onPicked();

	public void setParent(Model parent) {
		$parent = parent;
	}
	
	public void setGame(Game game) {
		$game = game;
	}
	
	public void move(Vector3d move) {
		$parent.move(move);
	}
	
	public void moveTo(Vector3d move, long duration) {
		$parent.moveTo(move, duration);
	}
	
	public void switchWorld(String name) {
		$game.switchWorld(name);
	}
	
	public World getActiveWorld() {
		return $game.getActiveWorld();
	}
	
	public World getWorld(String name) {
		return $game.getWorld(name);
	}
	
	public Camera getActiveCamera() {
		return $game.getActiveWorld().getActiveCamera();
	}
	
	public Camera getCamera(String name) {
		return $game.getActiveWorld().getCamera(name);
	}
	
	public void switchCamera(String name) {
		$game.getActiveWorld().setCamera(name);
	}
	
	public void setRotation(Vector3d rotation) {
		$parent.setRotation(rotation);
	}
	
	public void setRotationX(float x) {
		Vector3d rotation = getRotation();
		$parent.setRotation(x, rotation.y, rotation.z);
	}
	
	public void setRotationY(float y) {
		Vector3d rotation = getRotation();
		$parent.setRotation(rotation.x, y, rotation.z);
	}
	
	public void setRotationZ(float z) {
		Vector3d rotation = getRotation();
		$parent.setRotation(rotation.x, rotation.y, z);
	}
	
	public void addRotation(Vector3d rotation) {
		$parent.addRotation(rotation);
	}
	
	public Vector3d getRotation() {
		return $parent.getRotation();
	}
	
	public Vector3d getScale() {
		return $parent.getScale();
	}
	
	public void setScale(float scale) {
		setScale(new Vector3d(scale, scale, scale));
	}
	
	public void setScale(Vector3d scale) {
		$parent.setScale(scale);
	}
	
	public void setPosition(Vector3d rotation) {
		$parent.setPos(rotation);
	}
	
	public void setPositionX(float x) {
		Vector3d position = getPosition();
		position.x = x;
		$parent.setPos(position);
	}
	
	public void setPositionY(float y) {
		Vector3d position = getPosition();
		position.y = y;
		$parent.setPos(position);
	}
	
	public void setPositionZ(float z) {
		Vector3d position = getPosition();
		position.z = z;
		$parent.setPos(position);
	}
	
	public Vector3d getPosition() {
		return $parent.getPosition();
	}
	
	public ColBox getColBox() {
		return $parent.getColBox();
	}
}
