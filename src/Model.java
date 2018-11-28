import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;


/**
 * This class represents an object in a world.
 */
public class Model implements LoopItem, InputListener {
	/**
	 * Returns a new model parsed from the given file.
	 * @param file the file to parse.
	 * @return a model parsed from the given file.
	 */
	public static Model createNew(String file) throws IOException {
		return new Model(Model3dParser.getInstance().parse3dModel(file));
	}
	/** The collision box of this model. */
	private ColBox $colBox;
	/** The physical representation of this model. */
	private Model3d $model3d;
	/** The position to which this model is moving. */
	private Vector3d $movePerMillisec;
	/** The number of milliseconds left in which this model moves. */
	private long $moveMillisec;
	/** The position of this model. */
	private Vector3d $pos;
	//Scale of model
	private Vector3d $scale;
	private Vector3d $rotation;
	private Vector3d $offset;
	private String $name;
	
	private Game $game;
	
	private List<Component> $components;
	
	/**
	 * Constructs a Model.
	 */
	protected Model() {
		init(null, null, null);
	}
	/**
	 * Constructs a Model with the given physical representation.
	 * @param model3d the physical representation of the model
	 */
	protected Model(Model3d model3d) {
		init(model3d, null, null);
	}
	/**
	 * Constructs a Model with the given physical representation at the given
	 * position.
	 * @param model3d the physical representation of the model
	 * @param pos the position of the model
	 */
	protected Model(Model3d model3d, Vector3d pos) {
		init(model3d, pos, null);
	}
	/**
	 * Constructs a Model at the given position.
	 * @param pos the position of the model
	 */
	protected Model(Vector3d pos) {
		init(null, pos, null);
	}

	protected Model(Model3d model3d, Vector3d pos, Vector3d scale) {
		init(model3d, pos, scale);
	}

	protected Model(Vector3d pos, Vector3d scale) {
		init(null, pos, scale);
	}

	private void init(Model3d model3d, Vector3d position, Vector3d scale) {
		if(model3d == null) {
			$model3d = new Model3d();
			$colBox = new ColBox();
		} else {
			$model3d = model3d;
			$colBox = new ColBox(model3d.getColBox());
			Vector3d min = $colBox.getMin(), max = $colBox.getMax();
			$offset = new Vector3d(-min.x - ((max.x - min.x) / 2), -min.y - ((max.y - min.y) / 2), -min.z - ((max.z - min.z) / 2));
			$colBox.subtract($offset);
		}

		if(position == null) 
			$pos = new Vector3d();
		else {
			$pos = new Vector3d(position);
			$colBox.moveCenter($pos);
		}

		if(scale == null) 
			$scale = new Vector3d(1.0f, 1.0f, 1.0f);
		else
			$scale = new Vector3d(scale);		
		
		if($offset == null)
			$offset = new Vector3d();

		$rotation = new Vector3d();
		$movePerMillisec = new Vector3d();
		$moveMillisec = 0;
		$components = new ArrayList<>();
	}

	/**
	 * Returns whether this model collides with the given model.
	 * @param other the model to check for collision with.
	 * @return true if this model collides with the given model, false
	 * otherwise
	 */
	public boolean collides(Model other) {
		return $colBox.intersects(other.$colBox);
	}
	
	public ColBox getColBox() {
		return $colBox;
	}
	
	public void onCollision(Model otherModel, ColBox otherColBox) {
		if($colBox.getType() == ColBox.Type.NOCLIP)
			return;
		
		for(Component component: $components) {
			component.onCollision(otherModel, otherColBox);
		}
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
        		this.move(new Vector3d(- (inX / 2.0f), 0, 0));
        		otherModel.move(new Vector3d((inX / 2.0f), 0, 0));
        	}
        	else if($colBox.getType() == ColBox.Type.DYNAMIC && otherColBox.getType() == ColBox.Type.STATIC) {
        		this.move(new Vector3d(-inX, 0, 0));
        	}
        	else if($colBox.getType() == ColBox.Type.STATIC && otherColBox.getType() == ColBox.Type.DYNAMIC) {
        		otherModel.move(new Vector3d(inX, 0, 0));
        	}
        } 
        else if (penY < penZ) {
        	if($colBox.getType() == ColBox.Type.DYNAMIC && otherColBox.getType() == ColBox.Type.DYNAMIC) {
        		this.move(new Vector3d(0, -(inY / 2.0f), 0));
        		otherModel.move(new Vector3d(0, (inY / 2.0f), 0));
        	}
        	else if($colBox.getType() == ColBox.Type.DYNAMIC && otherColBox.getType() == ColBox.Type.STATIC) {
        		this.move(new Vector3d(0, -inY, 0));
        	}
        	else if($colBox.getType() == ColBox.Type.STATIC && otherColBox.getType() == ColBox.Type.DYNAMIC) {
        		otherModel.move(new Vector3d(0, inY, 0));
        	}
        } else {
        	if($colBox.getType() == ColBox.Type.DYNAMIC && otherColBox.getType() == ColBox.Type.DYNAMIC) {
        		this.move(new Vector3d(0, 0, -(inZ / 2.0f)));
        		otherModel.move(new Vector3d(0, 0, (inZ / 2.0f)));
        	}
        	else if($colBox.getType() == ColBox.Type.DYNAMIC && otherColBox.getType() == ColBox.Type.STATIC) {
        		this.move(new Vector3d(0, 0, -inZ));
        	}
        	else if($colBox.getType() == ColBox.Type.STATIC && otherColBox.getType() == ColBox.Type.DYNAMIC) {
        		otherModel.move(new Vector3d(0, 0, inZ));
        	}
        }
	}
	
	public void onPicked() {
		for(Component comp: $components) {
			comp.onPicked();
		}
	}
	
	/**
	 * Draws the physical representation of this model on the correct position
	 * in the world.
	 */
	public void draw() {
		//Store old matrix
		GL11.glPushMatrix();
		GL11.glTranslated($pos.x, $pos.y, $pos.z);	
		GL11.glRotated($rotation.x, 1, 0, 0);
		GL11.glRotated($rotation.y, 0, 1, 0);
		GL11.glRotated($rotation.z, 0, 0, 1);
		GL11.glScaled($scale.x, $scale.y, $scale.z);
		GL11.glTranslated($offset.x, $offset.y, $offset.z);
		
		if($model3d != null) {
			$model3d.draw(true);
			//$model3d.getColBox().draw();
		}
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		if($colBox != null && $game.getDrawColBox()) {
			$colBox.draw();
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		
		for(Component component: $components) {
			component.draw();
		}
	}
	
	/**
	 * Notifies this model to update anything that should be updated.
	 * @param delta the number of milliseconds since the last time this method
	 * was called
	 */
	public void update(long delta) {
		// If this model is moving, update its position.
		if ($moveMillisec > 0) {
			// Get the distance we should move, given the amount of time passed.
			Vector3d move = new Vector3d($movePerMillisec);
			move.multiply($moveMillisec);
			// Move to the new position.
			posAdd(move);
			$colBox.moveMin(move);
			// Decrease the number of milliseconds the object will be moving.
			$moveMillisec -= delta;
		}
		
		for(Component component: $components) {
			component.update(delta);
		}
	}
	
	/**
	 * Returns the position this model is in.
	 * @return the position this model is in
	 */
	public Vector3d getPos() {
		return new Vector3d($pos);
	}
	/**
	 * Moves this model to the given position in the given amount of time.
	 * @param pos the position to move this model to
	 * @param milliSec the time that the move should take, in milliseconds
	 */
	public void moveTo(Vector3d pos, long millisec) {
		// Get where and how far this model should move per millisecond.
		$movePerMillisec.set(pos);
		$movePerMillisec.subtract($pos);
		$movePerMillisec.divide(millisec);
		// Get how long this model should move.
		$moveMillisec = millisec;
	}
	
	
	public void move(Vector3d move) {
		posAdd(move);
	}

	
	/**
	 * 
	 * @param window
	 * @param key
	 * @param scancode
	 * @param action
	 * @param mods
	 */
	@Override
	public void onKeyInput(long window, int key, int scancode, int action, int mods) {
		for(Component component: $components) {
			component.onKeyInput(window, key, scancode, action, mods);
		}
	}
	/**
	 * Moves this model as described by the given vector.
	 * @param add vector to move this model by
	 */
	public void posAdd(Vector3d add) {
		$pos.add(add);
		$colBox.add(add);
	}
	/**
	 * Sets the physical representation of this model to the given 3d model.
	 * @param model3d the 3d model to set for this model
	 */
	public void set3dModel(Model3d model3d) {
		// Set the own 3d model equal to the given 3d model.
		$model3d = model3d;
		// Set the own collision box equal to the that of the new 3d model.
		$colBox = new ColBox(model3d.getColBox());
		// Move the collision box to the position of this model.
		//$colBox.moveMin($pos);
	}
	/**
	 * Sets the position of this model.
	 * @param pos the position to set for this model
	 */
	public void setPos(Vector3d pos) {
		$pos.set(pos);
		$colBox.moveCenter(pos);
	}
	
	public void setRotation(float x, float y, float z) {
		setRotation(new Vector3d(x, y, z));
	}
	
	public void setRotation(Vector3d rot) {
		$rotation.set(rot);
		$colBox.fit($model3d.getColBox(), $rotation);
	}
	
	public void setScale(float x, float y, float z) {
		setScale(new Vector3d(x, y, z));
		$colBox.scale($model3d.getColBox(), x, y, z);
	}
	
	public void addRotation(Vector3d rotation) {
		$rotation.add(rotation);
		$colBox.fit($model3d.getColBox(), $rotation);
	}
	
	public void setScale(Vector3d vector) {
		$scale.set(vector);
		$colBox.scale($model3d.getColBox(), vector);
	}
	
	public Vector3d getRotation() {
		return new Vector3d($rotation);
	}
	
	public Vector3d getScale() {
		return new Vector3d($scale);
	}
	
	public Vector3d getPosition() {
		return new Vector3d($pos);
	}
	
	public void setColBox(ColBox colbox) {
		$colBox = colbox;
	}
	
	public void setGame(Game game) {
		$game = game;
	}
	
	public void addComponent(Component component) {
		$components.add(component);
	}
	
	public void setName(String name) {
		$name = name;
	}
	
	public String getName() {
		return $name;
	}
}
