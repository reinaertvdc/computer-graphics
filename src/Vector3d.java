import org.lwjgl.opengl.*;

/**
 * This class represents a 3d vector.
 */
public class Vector3d {
	/**
	 * Returns whether 'min' has a smaller or equal value than 'max' in all
	 * dimensions.
	 * @param min the vector for which to check if it is the smallest all round
	 * @param max the vector for which to check if it is the largest all round
	 * @return true if 'min' has a smaller or equal value than 'max' in all
	 * dimensions, false otherwise
	 */
	public static boolean isMinMax(Vector3d min, Vector3d max) {
		return min.x <= max.x && min.y <= max.y && min.z <= max.z;
	}
	/**
	 * For every dimension, puts the smallest of the two values in 'min' and the
	 * largest in max.
	 * @param min will contain the smallest value in all dimensions
	 * @param max will contain the largest value in all dimensions
	 */
	public static void toMinMax(Vector3d min, Vector3d max) {
		// In every dimension, if 'min' is larger than 'max', swap the values.
		if (min.x > max.x) {
			float temp = min.x;
			min.x = max.x;
			max.x = temp;
		}
		if (min.y > max.y) {
			float temp = min.y;
			min.y = max.y;
			max.y = temp;
		}
		if (min.z > max.z) {
			float temp = min.z;
			min.z = max.z;
			max.z = temp;
		}
	}
	/** The value in the x-dimension. */
	public float x;
	/** The value in the y-dimension. */
	public float y;
	/** The value in the z-dimension. */
	public float z;
	/**
	 * Constructs a Vector3d.
	 */
	Vector3d() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	/**
	 * Constructs a Vector3d with for every dimension the given value.
	 * @param x the value in the x-dimension
	 * @param y the value in the y-dimension
	 * @param z the value in the z-dimension
	 */
	Vector3d(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	/**
	 * Constructs a copy of the given Vector3d.
	 * @param other Vector3d to copy
	 */
	Vector3d(Vector3d other) {
		set(other);
	}
	/**
	 * For every dimension, adds the value of the given Vector3d to the current
	 * value.
	 * @param add the Vector3d to add the values of
	 */
	public void add(Vector3d add) {
		this.x += add.x;
		this.y += add.y;
		this.z += add.z;
	}
	
	public void add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}
	
	/**
	 * Divides the value of this vector in every dimension by the given divisor.
	 * @param divisor the divisor to divide this vector by
	 */
	public void divide(long divisor) {
		x /= divisor;
		y /= divisor;
		z /= divisor;
	}
	/**
	 * Returns the difference between this vector and the given vector. If this
	 * vector has a larger value than the other vector for a given dimension,
	 * the resulting vector will have a positive value for that dimension and
	 * vice versa.
	 * @param other the vector to get the difference with
	 * @return a vector representing the difference between this vector and the
	 * given vector
	 */
	public Vector3d getDifference(Vector3d other) {
		return new Vector3d(x - other.x, y - other.y, z - other.z);
	}
	/**
	 * Multiplies the value of this vector in every dimension by the given
	 * factor.
	 * @param factor the factor to multiply this vector by.
	 */
	public void multiply(long factor) {
		x *= factor;
		y *= factor;
		z *= factor;
	}
	/**
	 * For every dimension, sets the value to the value of the given Vector3d.
	 * @param other the Vector3d to copy the values of
	 */
	public void set(Vector3d other) {
		x = other.x;
		y = other.y;
		z = other.z;
	}
	/**
	 * For every dimension, subtracts the value of the given vector from the
	 * value of this vector.
	 * @param subtractor the vector to subtract from this vector
	 */
	public void subtract(Vector3d subtractor) {
		x -= subtractor.x;
		y -= subtractor.y;
		z -= subtractor.z;
	}
	
	public void rotate(Vector3d rotation) {
		// Get rotations in radians.
		double rotX = Math.toRadians(rotation.x);
		double rotY = Math.toRadians(rotation.y);
		double rotZ = Math.toRadians(rotation.z);
		float baseX = x, baseY = y, baseZ = z;
		// Rotate around the X-axis.
		y = (float)((Math.cos(rotX) * baseY) - (Math.sin(rotX) * baseZ));
		z = (float)((Math.sin(rotX) * baseY) + (Math.cos(rotX) * baseZ));
		// Rotate around the Y-axis.
		baseZ = z;
		x = (float)((Math.cos(rotY) * baseX) + (Math.sin(rotY) * baseZ));
		z = (float)(-(Math.sin(rotY) * baseX) + (Math.cos(rotY) * baseZ));
		// Rotate around the Z-axis.
		baseX = x;
		baseY = y;
		x = (float)((Math.cos(rotZ) * baseX) - (Math.sin(rotZ) * baseY));
		y = (float)((Math.sin(rotZ) * baseX) + (Math.cos(rotZ) * baseY));
	}
	
	public boolean isSmallerThan(Vector3d other) {
		return (this.x <= other.x && this.y <= other.y && this.z <= other.z);
	}
	
	public boolean isBiggerThan(Vector3d other) {
		return (this.x > other.x && this.y > other.y && this.z > other.z);
	}
	
	public boolean equals(Vector3d other) {
		return (this.x == other.x && this.y == other.y && this.z == other.z);
	}
	
	public String toString() {
		return "X: " + x + ",   Y: "  + y + ",   Z: " + z;
	}
	
	public static double distanceBetween(Vector3d one, Vector3d two) {
		double distance = Math.sqrt(Math.pow(one.x - two.x, 2) + Math.pow(one.y - two.y, 2) + Math.pow(one.z - two.z, 2));
		return distance;
	}
}