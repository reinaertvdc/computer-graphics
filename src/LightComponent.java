import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;


public class LightComponent extends Component {
	private int $light;
	private float $x;
	private float $y;
	private float $z;
	private boolean $enabled;
	private boolean $alignTop;
	private boolean $toggeAble;
	
	public LightComponent(int lightIndex) {
		$enabled = true;
		$light = lightIndex;
		GL11.glEnable(GL11.GL_LIGHT0 + lightIndex);
	}
	
	public LightComponent(int lightIndex, boolean alignTop) {
		$enabled = true;
		$light = lightIndex;
		GL11.glEnable(GL11.GL_LIGHT0 + lightIndex);
		$alignTop = alignTop;
	}
	
	public LightComponent(int lightIndex, boolean alignTop, boolean toggeAble) {
		$enabled = true;
		$light = lightIndex;
		GL11.glEnable(GL11.GL_LIGHT0 + lightIndex);
		$alignTop = alignTop;
		$toggeAble = toggeAble;
	}

	@Override
	public void update(long delta) {
		$z++;
		$z %= 360;
		$x++;
		$x %= 360;
	}
	
	public void setLightAmbient(float one, float two, float three, float four) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
		float array[] = new float[] {one, two, three, four};
		buffer.put(array);
		buffer.flip();
		GL11.glLightfv(GL11.GL_LIGHT0 + $light, GL11.GL_AMBIENT, buffer);
	}

	public void setLightSpecular(float one, float two, float three, float four) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
		float array[] = new float[] {one, two, three, four};
		buffer.put(array);
		buffer.flip();
		GL11.glLightfv(GL11.GL_LIGHT0 + $light, GL11.GL_SPECULAR, buffer);
	}
	
	public void setLightDiffuse(float one, float two, float three, float four) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
		float array[] = new float[] {one, two, three, four};
		buffer.put(array);
		buffer.flip();
		GL11.glLightfv(GL11.GL_LIGHT0 + $light, GL11.GL_DIFFUSE, buffer);
	}
	
	public void setLightPosition(float one, float two, float three, float four) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
		float array[] = new float[] {one, two, three, four};
		buffer.put(array);
		buffer.flip();
		GL11.glLightfv(GL11.GL_LIGHT0 + $light, GL11.GL_POSITION, buffer);
	}
	
	public void setLightDirection(float one, float two, float three) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
		float array[] = new float[] {one, two, three, 0};
		buffer.put(array);
		buffer.flip();
		GL11.glLightfv(GL11.GL_LIGHT0 + $light, GL11.GL_SPOT_DIRECTION, buffer);
	}
	

	@Override
	public void draw() {
		if($enabled) {
			Vector3d vec = getPosition();
			Vector3d cam = getActiveCamera().getPosition();
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_LIGHT0 + $light);
			if($alignTop)
				vec.y += getColBox().getSize().y / 2;
			//setLightPosition(-cam.x + vec.x,-cam.y + vec.y, -cam.z + vec.z, 1f);
			setLightPosition(vec.x, vec.y, vec.z, 1f);
			setLightAmbient(0.5f, 0.5f, 0.5f, 0);
			setLightSpecular(1f, 1f, 1f, 1f);
			setLightDiffuse(1f, 1f, 1f, 1f);
			GL11.glPopMatrix();
		}
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
		if($toggeAble)
			$enabled = !$enabled;
		if($enabled) {
			GL11.glEnable(GL11.GL_LIGHT0 + $light);
		}
		else {
			GL11.glDisable(GL11.GL_LIGHT0 + $light);
		}
	}

}
