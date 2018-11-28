import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;


public class Camera implements LoopItem, InputListener {
	private double $x;
	private double $y;
	private double $z;
	
	private double $xRot;
	private double $yRot;
	private double $zRot;
	
	final private static double RAD_TO_DEGREE = 57.2957795131;
	final private static double DEGREE_TO_RAD = 0.01745329251;
	final private static double RADIAL_LOOP = 6.28318530718;
	final private static double DEGREE_LOOP = 360;
	
	private float $sensitivity;
	private float $yFov;
	private ColBox $colBox;
	
	private boolean $moveable;
	private boolean $spotLightOn;
	private int $light;
	
	public Camera() {
		$sensitivity = 10.0f;
		$yFov = 45f;
		$moveable = true;
		$colBox = new ColBox(new Vector3d(-5f, -5f, -5f), new Vector3d(5f, 5f, 5f));
		$colBox.setType("dynamic");
		$spotLightOn = true;
		$light = 1;
		GL11.glEnable(GL11.GL_LIGHT0 + $light);
	}
	
	public void addX(double distance) {
		$x += distance;
		$colBox.add(new Vector3d((float) distance, 0, 0));
	}
	
	public void addY(double distance) {
		$y += distance;
		$colBox.add(new Vector3d(0, (float) distance, 0));
	}
	
	public void addZ(double distance) {
		$z += distance;
		$colBox.add(new Vector3d(0, 0, (float) distance));
	}
	
	public void addXYZ(double x, double y, double z) {
		addX(x);
		addY(y);
		addZ(z);
	}
	
	public void rotateDegreesX(double degrees) {
		$xRot += DEGREE_TO_RAD * degrees;
		$xRot %= RADIAL_LOOP;
	}
	
	public void rotateDegreesY(double degrees) {
		$yRot += DEGREE_TO_RAD * degrees;
		$yRot %= RADIAL_LOOP;
	}

	public void rotateDegreesZ(double degrees) {
		$zRot += DEGREE_TO_RAD * degrees;
		$zRot %= RADIAL_LOOP;
	}
	
	public void rotateDegreesXYZ(double x, double y, double z) {
		rotateDegreesX(x);
		rotateDegreesY(y);
		rotateDegreesZ(z);
	}
	
	public void rotateRadX(double rad) {
		$xRot += rad;
		$xRot %= RADIAL_LOOP;
	}
	
	public void rotateRadY(double rad) {
		$yRot += rad;
		$yRot %= RADIAL_LOOP;
	}
	
	public void rotateRadZ(double rad) {
		$zRot += rad;
		$zRot %= RADIAL_LOOP;
	}
	
	public void rotateRadXYZ(double x, double y, double z) {
		rotateRadX(x);
		rotateRadY(y);
		rotateRadZ(z);
	}
	
	public void update(long delta) {
		if($moveable) {
			Mouse tempMouse = Game.getMouse();
			int dx = tempMouse.getDeltaX();
			int dy = tempMouse.getDeltaY();
			
			rotateDegreesY(-dx / $sensitivity);
			rotateDegreesX(-dy / $sensitivity);
		
		}
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
	
	public void draw() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective($yFov, 1.33f, 1.0f, 10000f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		
		GL11.glPushMatrix();
		
		GL11.glRotated(-$xRot * RAD_TO_DEGREE, 1.0, 0, 0);
		GL11.glRotated(-$yRot * RAD_TO_DEGREE, 0, 1.0, 0);
		GL11.glRotated(-$zRot * RAD_TO_DEGREE, 0, 0, 1.0);
		GL11.glScaled(1, 1, -1);
		
		GL11.glTranslated(-$x, -$y, -$z);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		if($spotLightOn) {
			GL11.glLightf(GL11.GL_LIGHT0 + $light, GL11.GL_SPOT_CUTOFF, 10f);
		}
		
		this.setLightDirection(0f, 0f, -1f);
		this.setLightPosition(0f, 0f, 0f, 1f);
		this.setLightAmbient(0f, 0f, 0f, 0f);
		this.setLightDiffuse(1f, 1f, 1f, 1f);
		this.setLightSpecular(1f, 1f, 1f, 1f);
		
		GL11.glPopMatrix();
	}
	
	public void moveForward(double distance) {		
		Vector3d temp = new Vector3d(0, 0, (float) distance);
		temp.rotate(new Vector3d((float) ($xRot * RAD_TO_DEGREE), (float) ($yRot * RAD_TO_DEGREE), (float) ($zRot * RAD_TO_DEGREE)));
		addXYZ(-temp.x, -temp.y, temp.z);
	}
	
	public Vector3d advanceVector(double distance) {
		Vector3d temp = new Vector3d(0, 0, (float) distance);
		temp.rotate(new Vector3d((float) ($xRot * RAD_TO_DEGREE), (float) ($yRot * RAD_TO_DEGREE), (float) ($zRot * RAD_TO_DEGREE)));
		Vector3d returnVector = new Vector3d((float) $x, (float) $y, (float) $z);
		returnVector.add(-temp.x, -temp.y, temp.z);
		return returnVector;
	}
	
	
	public void moveBackward(double distance) {
		moveForward(-distance);
	}
	
	public void strafeLeft(double distance) {
		Vector3d temp = new Vector3d((float) distance, 0, 0);
		temp.rotate(new Vector3d((float) ($xRot * RAD_TO_DEGREE), (float) ($yRot * RAD_TO_DEGREE), (float) ($zRot * RAD_TO_DEGREE)));
		addXYZ(-temp.x, temp.y, temp.z);
	}
	
	public void strafeRight(double distance) {
		strafeLeft(-distance);
	}
	
	public void moveUp(double distance) {
		Vector3d temp = new Vector3d(0, (float) distance, 0);
		temp.rotate(new Vector3d((float) ($xRot * RAD_TO_DEGREE), (float) ($yRot * RAD_TO_DEGREE), (float) ($zRot * RAD_TO_DEGREE)));
		System.out.println("x: " + temp.x + " y: " + -temp.y + " z: " + temp.z);
		addXYZ(temp.x, temp.y, -temp.z);
	}
	
	public void moveDown(double distance) {
		moveUp(-distance);
	}
	
	public void setPos(float x, float y, float z) {
		$x = x;
		$y = y;
		$z = z;
		$colBox.moveCenter(new Vector3d((float) $x, (float) $y, (float) $z));
	}

	@Override
	public void onKeyInput(long window, int key, int scancode, int action, int mods) {
		if($moveable) {
			int stepSize = 5;
			 if(key == GLFW_KEY_W && (action == GLFW_REPEAT || action == GLFW_PRESS))
	         	moveForward(stepSize);
	         if(key == GLFW_KEY_S && (action == GLFW_REPEAT || action == GLFW_PRESS))
	         	moveBackward(stepSize);
	         if(key == GLFW_KEY_A && (action == GLFW_REPEAT || action == GLFW_PRESS))
	         	strafeLeft(stepSize);
	         if(key == GLFW_KEY_D && (action == GLFW_REPEAT || action == GLFW_PRESS))
	         	strafeRight(stepSize);
	         if(key == GLFW_KEY_Q && (action == GLFW_REPEAT || action == GLFW_PRESS))
	         	rotateDegreesZ(5);
	         if(key == GLFW_KEY_E && (action == GLFW_REPEAT || action == GLFW_PRESS))
	         	rotateDegreesZ(-5);
	         if(key == GLFW_KEY_SPACE && (action == GLFW_REPEAT || action == GLFW_PRESS))
		         moveUp(5);
	         if(key == GLFW_KEY_Z && (action == GLFW_REPEAT || action == GLFW_PRESS))
		         moveDown(5);
	         if(key == GLFW_KEY_TAB && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
	        	 $spotLightOn = !$spotLightOn;
	        	 if($spotLightOn) {
	        		 GL11.glEnable(GL11.GL_LIGHT0 + $light);
	        	 }
	        	 else {
	        		 GL11.glDisable(GL11.GL_LIGHT0 + $light);
	        	 }
	         }
	        	 
		}
	}
	
	public boolean collidesWith(Model model) {
		return $colBox.intersects(model.getColBox());
	}
	
	public void onCollision(Model otherModel, ColBox otherColBox) {
		if($colBox.getType() == ColBox.Type.NOCLIP)
			return;
		
		Vector3d myMin = $colBox.getMin(), myMax = $colBox.getMax();
		Vector3d theirMin = otherColBox.getMin(), theirMax = otherColBox.getMax();
		
		float x1, x2, y1, y2, z1, z2;
		x1 = myMax.x - theirMin.x;
		x2 = theirMax.x - myMin.x;
		y1 = myMax.y - theirMin.y;
		y2 = theirMax.y - myMin.y;
		z1 = myMax.z - theirMin.z;
		z2 = theirMax.z - myMin.z;
		
		float inX, inY, inZ;
		
		if (x1 < x2)
            inX = x1;
        else
            inX = -(x2);

        if (y1 < y2)
            inY = y1;
        else
            inY = -(y2);

        if (z1 < z2)
            inZ = z1;
        else
            inZ = -(z2);

        float penX = Math.abs(inX);
        float penY = Math.abs(inY);
        float penZ = Math.abs(inZ);
	        
        if (penX < penY && penX < penZ) {
        	if($colBox.getType() == ColBox.Type.DYNAMIC && otherColBox.getType() == ColBox.Type.DYNAMIC) {
        		otherModel.move(new Vector3d((inX), 0, 0));
        	}
        	else if($colBox.getType() == ColBox.Type.DYNAMIC && otherColBox.getType() == ColBox.Type.STATIC) {
        		this.addX(-inX);
        	}
        } 
        else if (penY < penZ) {
        	if($colBox.getType() == ColBox.Type.DYNAMIC && otherColBox.getType() == ColBox.Type.DYNAMIC) {
        		otherModel.move(new Vector3d(0, (inY), 0));
        	}
        	else if($colBox.getType() == ColBox.Type.DYNAMIC && otherColBox.getType() == ColBox.Type.STATIC) {
        		addY(-inY);
        	}
        } else {
        	if($colBox.getType() == ColBox.Type.DYNAMIC && otherColBox.getType() == ColBox.Type.DYNAMIC) {
        		otherModel.move(new Vector3d(0, 0, (inZ)));
        	}
        	else if($colBox.getType() == ColBox.Type.DYNAMIC && otherColBox.getType() == ColBox.Type.STATIC) {
        		addZ(-inZ);
        	}
        }
	}
	
	public void setMoveable(boolean state) {
		$moveable = state;
	}
	
	public Vector3d getPosition() {
		return new Vector3d((float) $x, (float) $y, (float) $z);
	}
	
	public ColBox getColBox() {
		return $colBox;
	}
}
