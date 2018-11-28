// partially taken from https://github.com/seanrowens/oObjLoader/blob/master/com/owens/oobjloader/builder/FaceVertex.java
import org.lwjgl.opengl.GL11;

public class FaceVertex {

    int index = -1;
    public Vector3d v = new Vector3d();
    public VertexTexture t = new VertexTexture(0, 0);
    public VertexNormal n = new VertexNormal(0, 0, 0);

    public String toString() {
        return v + "|" + n + "|" + t;
    }
    
    public void draw() {
		if (t != null)
			GL11.glTexCoord2d(t.u, t.v);
		GL11.glNormal3d(n.x, n.y, n.z);
		GL11.glVertex3d(v.x, v.y, v.z);
	}
}