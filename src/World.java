import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;


public class World implements LoopItem, InputListener{
	private String $name;
	private Camera $activeCamera;
	private Map<String, Model> $objects;
	private Map<String, Camera> $cameras;
	private Game $parent;
	private Map<Integer, Model> $pickMap;
	private Vector3d $size;
	private int $map;
	private ColBox $mapBox;
	private int $mapTexture;
	
	public World(Vector3d size) {
		//$activeCamera = new Camera();
		$objects = new HashMap<>();
		$cameras = new HashMap<>();
		$pickMap = new HashMap<>();
		$size = size;
		$mapBox = new ColBox();
	}
	
	public void setGame(Game game) {
		$parent = game;
	}
	
	public void setName(String name) {
		$name = name;
	}
	
	public void setTexture(int texture) {
		$mapTexture = texture;
	}
	
	public String getName() {
		return $name;
	}
	
	public void generateMap() {
		$map = GL11.glGenLists(1);
		GL11.glNewList($map, GL11.GL_COMPILE);
		
		GL11.glBegin(GL11.GL_QUADS);
		
		$mapBox.setMin(new Vector3d(0, -$size.y, 0));
		$mapBox.setMax(new Vector3d($size.x, 0, $size.z));
		$mapBox.setType("static");
		
		int tileSize = 8;
		
		for (float i = 0; i < $size.x - 1; i += tileSize) {
			for (float j = 0; j < $size.z - 1; j += tileSize) {
				GL11.glTexCoord2f(0, 0);
				GL11.glNormal3d(0, 1, 0);
				GL11.glVertex3d(i, 0, j);
				
				GL11.glTexCoord2f(1, 0);
				GL11.glNormal3d(0, 1, 0);
				GL11.glVertex3d(i + tileSize, 0, j);
				
				GL11.glTexCoord2f(1, 1);
				GL11.glNormal3d(0, 1, 0);
				GL11.glVertex3d(i + tileSize, 0, j + tileSize);
				
				GL11.glTexCoord2f(0, 1);
				GL11.glNormal3d(0, 1, 0);
				GL11.glVertex3d(i, 0, j + tileSize);
			}
		}
		GL11.glEnd();
		
		GL11.glEndList();
	}
	
	public synchronized void setCamera(String name) {
		if($cameras.containsKey(name))
			$activeCamera = $cameras.get(name);
	}
	
	public void update(long delta) {
		$activeCamera.update(delta);
		
		for(Model model: $objects.values()) {
			model.update(delta);
		}
		
		//Vector3d gravity = new Vector3d(0, (float)( * (1.0 / (double) delta)), 0);
		/*for(Model model: $objects.values()) {
			model.move(gravity);
		}*/
	}
	
	public void physicsUpdate(long delta) {
		for(Model model: $objects.values()) {
			for(Model model2: $objects.values()) {
				if(model.collides(model2)) {
					model.onCollision(model2, model2.getColBox());
				}
			}
		}
		for(Model model: $objects.values()) {
			if($activeCamera.collidesWith(model))
				$activeCamera.onCollision(model, model.getColBox());
			
			if($mapBox.intersects(model.getColBox()))
				model.onCollision(null, $mapBox);
		}
	}
	
	public void draw() {
		$activeCamera.draw();
		
		if($parent.getMouse().isLeftMouseReleased()) {
			pickModels();
		}
		
		
		if($parent.getDrawColBox())
			$mapBox.draw();
		
		GL11.glColor3d(0.5, 0.5, 0.5);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, $mapTexture);
		GL11.glCallList($map);
		
	
		
		for(Model model: $objects.values()) {
			model.draw();
		}
		
		
		
		GL11.glPopMatrix();
		
		
	}
	
	private void pickModels() {
		IntBuffer selectBuffer = BufferUtils.createIntBuffer($objects.size() * 4);
		GL11.glSelectBuffer(selectBuffer);
		
		GL11.glRenderMode(GL11.GL_SELECT);
		GL11.glInitNames();
		$pickMap.clear();
		
		IntBuffer buffer = BufferUtils.createIntBuffer(4);
		DoubleBuffer dbuffer = BufferUtils.createDoubleBuffer(16);
		GL11.glGetIntegerv(GL11.GL_VIEWPORT, buffer);
		GL11.glGetDoublev(GL11.GL_PROJECTION_MATRIX, dbuffer);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GLU.gluPickMatrix(($parent.getWidth() / 2), ($parent.getHeight() / 2), 5, 5, buffer);
		GL11.glMultMatrixd(dbuffer);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		int i = 0;
		for(Model model: $objects.values()) {
			$pickMap.put(i, model);
			GL11.glPushName(i);
			model.draw();
			GL11.glPopName();
			i++;
		}
		
		
		//Get hits
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		
		int hits = GL11.glRenderMode(GL11.GL_RENDER);
		System.out.println("Hits: " + hits);
		
		int minMinZ = Integer.MAX_VALUE;
		int firstHit = -1;
		int index = 0;
		int numberNames;
		
		if(hits > 0) {
			for(int j = 0; j < hits; j++) {
				numberNames = selectBuffer.get(index);
				index++;
				int minZ = selectBuffer.get(index);
				index++;
				int maxZ = selectBuffer.get(index);
				index++;
				
				for(int k = 0; k < numberNames; k++) {
					if(minZ < minMinZ) {
						minMinZ = minZ;
						firstHit = selectBuffer.get(index);
					}
					
					index++;
				}
			}
			
			if(firstHit != -1)
				$pickMap.get(new Integer(firstHit)).onPicked();
		}
	}

	@Override
	public void onKeyInput(long window, int key, int scancode, int action, int mods) {
		$activeCamera.onKeyInput(window, key, scancode, action, mods);
		
		for(Model model: $objects.values()) {
			model.onKeyInput(window, key, scancode, action, mods);
		}
	}
	
	public void addModel(String name, Model model) {
		$objects.put(name, model);
	}
	
	public void removeModel(String name) {
		$objects.remove(name);
	}
	
	public void addCamera(String name, Camera camera) {
		$cameras.put(name, camera);
	}
	
	public void removeCamera(String name) {
		$cameras.remove(name);
	}
	
	public Model getModel(String name) {
		return $objects.get(name);
	}
	
	public Camera getCamera(String name) {
		return $cameras.get(name);
	}
	
	public Camera getActiveCamera() {
		return $activeCamera;
	}
	
	public synchronized void setCamera(Camera camera) {
		$activeCamera = camera;
	}
}
