import org.lwjgl.opengl.GL11;

/**
 * This class represents a collision box.
 */
public class ColBox {
	public enum Type {NOCLIP, STATIC, DYNAMIC}
	
	/** Vector describing the largest point in all dimensions. */
	private Vector3d $max;
	/** Vector describing the smallest point in all dimensions. */
	private Vector3d $min;
	private Type $type;
	
	/**
	 * Constructs a ColBox.
	 */
	public ColBox() {
		$min = new Vector3d();
		$max = new Vector3d();
		$type = Type.STATIC;
	}
	/**
	 * Constructs a copy of the given ColBox.
	 * @param other ColBox to copy
	 */
	public ColBox(ColBox other) {
		set(other);
	}
	/**
	 * Constructs a ColBox described by the two given vectors.
	 * @param vector1 a vector describing one of the corners of the box
	 * @param vector2 a vector describing the opposite corner of the box
	 */
	public ColBox(Vector3d vector1, Vector3d vector2) {
		set(vector1, vector2);
	}
	/**
	 * Moves this ColBox by adding the given vector this ColBox's vectors.
	 * @param add vector to add to all vectors describing this ColBox
	 */
	public void add(Vector3d add) {
		$min.add(add);
		$max.add(add);
	}
	/**
	 * Returns the vector of this ColBox that is the largest in all dimensions.
	 * @return the vector of this ColBox that is the largest in all dimensions
	 */
	public Vector3d getMax() {
		return new Vector3d($max);
	}
	/**
	 * Returns the vector of this ColBox that is the smallest in all dimensions.
	 * @return the vector of this ColBox that is the smallest in all dimensions
	 */
	public Vector3d getMin() {
		return new Vector3d($min);
	}
	
	public Vector3d getSize() {
		Vector3d size = new Vector3d($max);
		size.subtract($min);
		return size;
	}
	
	public Vector3d getCenter() {
		Vector3d center = getSize();
		center.divide(2);
		center.add($min);
		return center;
	}
	
	public void scale(ColBox original, float x, float y, float z) {
		Vector3d size = original.getSize();
		Vector3d center = getCenter();
		Vector3d min = new Vector3d(center), max = new Vector3d(center);
		float sizeX = size.x / 2.0f;
		float sizeY = size.y / 2.0f;
		float sizeZ = size.z / 2.0f;
		sizeX *= x;
		sizeY *= y;
		sizeZ *= z;
		min.subtract(new Vector3d(sizeX, sizeY, sizeZ));
		max.add(new Vector3d(sizeX, sizeY, sizeZ));
		
		set(min, max);
		
	}
	
	public void scale(ColBox original, Vector3d vec) {
		scale(original, vec.x, vec.y, vec.z);
	}
	
	/**
	 * If necessary, extends the size of this ColBox so that the given vector
	 * falls within it.
	 * @param vector the vector to include in this ColBox
	 */
	public void include(Vector3d vector) {
		// Make a copy of the given vector so that we do't modify the original.
		Vector3d temp = new Vector3d(vector);
		// Make sure that the given vector falls within the upper boundary.
		Vector3d.toMinMax(temp, $max);
		// Make sure that the given vector falls within the lower boundary.
		Vector3d.toMinMax($min, temp);
	}
	/**
	 * Returns whether this ColBox intersects the given ColBox.
	 * @param other the ColBox to check for intersection with
	 * @return true if this ColBox intersects with the given ColBox, false
	 * otherwise
	 */
	public boolean intersects(ColBox other) {
        //check if there is an intersection
        if ($max.x < other.$min.x || $min.x > other.$max.x ||
            $max.y < other.$min.y || $min.y > other.$max.y ||
            $max.z < other.$min.z || $min.z > other.$max.z)
            return false;
        else
        	return true;
	}
	
	public boolean inside(float x, float y, float z) {
		return(x > $min.x && x < $max.x && y > $min.y && y < $max.y && z > $min.z && z < $max.z);
	}
	
	/**
	 * Sets this ColBox to the same size and position as the given ColBox.
	 * @param other ColBox to imitate
	 */
	public void set(ColBox other) {
		set(new Vector3d(other.$min), new Vector3d(other.$max));
	}
	/**
	 * Sets the ColBox to the box described by the two given vectors.
	 * @param vector1 a vector describing one of the corners of the box
	 * @param vector2 a vector describing the opposite corner of the box
	 */
	public void set(Vector3d vector1, Vector3d vector2) {
		// Get the smallest and the largest vector of the box.
		Vector3d min = new Vector3d(vector1);
		Vector3d max = new Vector3d(vector2);
		Vector3d.toMinMax(min, max);
		// Store the new vectors.
		$min = min;
		$max = max;
	}
	/**
	 * Sets the vector of this ColBox that is the largest in all dimensions to
	 * the given vector and moves along all other vectors so that the size of
	 * this ColBox does not change.
	 * @param max the vector to be the largest in all dimensions for this box
	 */
	public void moveMax(Vector3d max) {
		Vector3d size = getSize();
		set(max, max);
		$min.subtract(size);
	}
	/**
	 * Sets the vector of this ColBox that is the smallest in all dimensions to
	 * the given vector and moves along all other vectors so that the size of
	 * this ColBox does not change.
	 * @param min the vector to be the smallest in all dimensions for this box
	 */
	public void moveMin(Vector3d min) {
		Vector3d size = getSize();
		set(min, min);
		$max.add(size);
	}
	
	public void moveCenter(Vector3d center) {
		Vector3d size = getSize();
		size.divide(2);
		$min = new Vector3d(center);
		$max = new Vector3d(center);
		$min.subtract(size);
		$max.add(size);
	}
	
	public ColBox getCentered() {
		ColBox centered = new ColBox(this);
		centered.moveCenter(new Vector3d());
		return centered;
	}
	
	public void fit(ColBox original, Vector3d rotation) {
		Vector3d offset = getCenter();
		Vector3d[] corners = original.getCentered().getCorners();
		corners = rotateCorners(corners, rotation);
		Vector3d min = new Vector3d(corners[0]), max = new Vector3d(corners[0]);
		for (int i = 1; i < corners.length; i++) {
			Vector3d temp = corners[i];
			if(temp.x < min.x)
				min.x = temp.x;
			if(temp.y < min.y)
				min.y = temp.y;
			if(temp.z < min.z)
				min.z = temp.z;
			if(temp.x > max.x)
				max.x = temp.x;
			if(temp.y > max.y)
				max.y = temp.y;
			if(temp.z > max.z)
				max.z = temp.z;
		}
		$min = new Vector3d(min);
		$max = new Vector3d(max);
		moveCenter(offset);
	}
	
	public Vector3d[] getCorners() {
		Vector3d[] corners = new Vector3d[8];
		corners[0] = new Vector3d($min.x, $min.y, $min.z);
		corners[1] = new Vector3d($min.x, $min.y, $max.z);
		corners[2] = new Vector3d($min.x, $max.y, $min.z);
		corners[3] = new Vector3d($min.x, $max.y, $max.z);
		corners[4] = new Vector3d($max.x, $min.y, $min.z);
		corners[5] = new Vector3d($max.x, $min.y, $max.z);
		corners[6] = new Vector3d($max.x, $max.y, $min.z);
		corners[7] = new Vector3d($max.x, $max.y, $max.z);
		return corners;
	}
	
	public void subtract(Vector3d difference) {
		subMin(difference);
		subMax(difference);
	}
	
	public void setMin(Vector3d min) {
		$min = new Vector3d(min);
	}
	
	public void setMax(Vector3d max) {
		$max = new Vector3d(max);
	}
	
	public void addMin(Vector3d difference) {
		$min.add(difference);
	}
	
	public void addMax(Vector3d difference) {
		$max.add(difference);
	}
	
	public void subMin(Vector3d difference) {
		$min.subtract(difference);
	}
	
	public void subMax(Vector3d difference) {
		$max.subtract(difference);
	}
	
	public void draw() {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glLineWidth(2.5f);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3d(0, 0, 1.0);
		GL11.glVertex3d($min.x, $min.y, $min.z);
		GL11.glVertex3d($min.x, $max.y, $min.z);
		
		GL11.glVertex3d($max.x, $min.y, $min.z);
		GL11.glVertex3d($max.x, $max.y, $min.z);
		
		GL11.glVertex3d($min.x, $min.y, $max.z);
		GL11.glVertex3d($min.x, $max.y, $max.z);
		
		GL11.glVertex3d($max.x, $min.y, $max.z);
		GL11.glVertex3d($max.x, $max.y, $max.z);
		
		GL11.glVertex3d($min.x, $min.y, $min.z);
		GL11.glVertex3d($max.x, $min.y, $min.z);
		
		GL11.glVertex3d($min.x, $max.y, $min.z);
		GL11.glVertex3d($max.x, $max.y, $min.z);
		
		GL11.glVertex3d($min.x, $min.y, $max.z);
		GL11.glVertex3d($max.x, $min.y, $max.z);
		
		GL11.glVertex3d($min.x, $max.y, $max.z);
		GL11.glVertex3d($max.x, $max.y, $max.z);
		
		GL11.glVertex3d($min.x, $min.y, $min.z);
		GL11.glVertex3d($min.x, $min.y, $max.z);
		
		GL11.glVertex3d($min.x, $max.y, $min.z);
		GL11.glVertex3d($min.x, $max.y, $max.z);
		
		GL11.glVertex3d($max.x, $min.y, $min.z);
		GL11.glVertex3d($max.x, $min.y, $max.z);
		
		GL11.glVertex3d($max.x, $max.y, $min.z);
		GL11.glVertex3d($max.x, $max.y, $max.z);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
	
	public void print() {
		System.out.println("Min: " + $min.x + " " + $min.y + " " + $min.z + " Max: " + $max.x + " " + $max.y + " " + $max.z);
	}
	
	private Vector3d[] rotateCorners(Vector3d[] corners, Vector3d rotation) {
		for (int i = 0; i < 8; i++)
			corners[i].rotate(rotation);
		return corners;
	}
	
	/*private void draw(Vector3d[] corners) {
		GL11.glBegin(GL11.GL_LINES);
		GL11.glLineWidth(2.5f);
		GL11.glColor3d(0, 0, 1.0);
		GL11.glVertex3d(corners[0].x, corners[0].y, corners[0].z);
		GL11.glVertex3d(corners[1].x, corners[1].y, corners[1].z);
		
		GL11.glVertex3d(corners[1].x, corners[1].y, corners[1].z);
		GL11.glVertex3d(corners[5].x, corners[5].y, corners[5].z);
		
		GL11.glVertex3d(corners[5].x, corners[5].y, corners[5].z);
		GL11.glVertex3d(corners[4].x, corners[4].y, corners[4].z);
		
		GL11.glVertex3d(corners[4].x, corners[4].y, corners[4].z);
		GL11.glVertex3d(corners[1].x, corners[1].y, corners[1].z);
		
		GL11.glVertex3d(corners[].x, corners[].y, corners[].z);
		GL11.glVertex3d(corners[].x, corners[].y, corners[].z);
		
		GL11.glVertex3d(corners[].x, corners[].y, corners[].z);
		GL11.glVertex3d(corners[].x, corners[].y, corners[].z);
		
		GL11.glVertex3d(corners[].x, corners[].y, corners[].z);
		GL11.glVertex3d(corners[].x, corners[].y, corners[].z);
		
		GL11.glVertex3d(corners[].x, corners[].y, corners[].z);
		GL11.glVertex3d(corners[].x, corners[].y, corners[].z);
		
		GL11.glVertex3d(corners[].x, corners[].y, corners[].z);
		GL11.glVertex3d(corners[].x, corners[].y, corners[].z);
		
		GL11.glVertex3d(corners[].x, corners[].y, corners[].z);
		GL11.glVertex3d(corners[].x, corners[].y, corners[].z);
		
		GL11.glVertex3d(corners[].x, corners[].y, corners[].z);
		GL11.glVertex3d(corners[].x, corners[].y, corners[].z);
		
		GL11.glVertex3d(corners[].x, corners[].y, corners[].z);
		GL11.glVertex3d(corners[].x, corners[].y, corners[].z);
		GL11.glEnd();
	}*/
	
	public void setType(String type) {
		if(type.compareTo("static") == 0)
			$type = Type.STATIC;
		else if(type.compareTo("dynamic") == 0)
			$type = Type.DYNAMIC;
		else if(type.compareTo("noclip") == 0)
			$type = Type.NOCLIP;
	}
	
	public void setType(Type type) {
		$type = type;
	}
	
	public Type getType() {
		return $type;
	}
}
