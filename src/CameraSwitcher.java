import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFW;


public class CameraSwitcher extends Component {
	private boolean $toggled;
	private String $switch1;
	private String $switch2;
	private int $keyInput;
	
	public CameraSwitcher(String one, String two) {
		$switch1 = one;
		$switch2 = two;
		$keyInput = GLFW_KEY_N;
	}
	
	public CameraSwitcher(String one, String two, int keyInput) {
		$switch1 = one;
		$switch2 = two;
		$keyInput = keyInput;;
	}
	
	@Override
	public void update(long delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKeyInput(long window, int key, int scancode, int action, int mods) {
		if(key == $keyInput && action == GLFW.GLFW_RELEASE) {
			if($toggled) {
				this.switchCamera($switch1);
			}
			else {
				this.switchCamera($switch2);
			}
			$toggled = !$toggled;
		}
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
