// partially taken from https://github.com/seanrowens/oObjLoader/blob/master/com/owens/oobjloader/builder/Face.java
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

public class Face {

 public ArrayList<FaceVertex> vertices = new ArrayList<FaceVertex>();
 public Material material = null;
 public Material map = null;
 private static TextureLoader textureLoader = new TextureLoader();
 static int lastImage = 0;
 int image = -1;
 
 public int loadImage() {
	 try {
	 if (material.mapKaFilename != null) {
			image = textureLoader.load(material.mapKaFilename, true);
		} else if (material.mapKdFilename != null) {
			image = textureLoader.load(material.mapKdFilename, true);
		} else if (material.mapKsFilename != null) {
			image = textureLoader.load(material.mapKsFilename, true);
		} else if (material.mapNsFilename != null) {
			image = textureLoader.load(material.mapNsFilename, true);
		} else if (material.mapDFilename != null) {
			image = textureLoader.load(material.mapDFilename, true);
		} else if (material.decalFilename != null) {
			image = textureLoader.load(material.decalFilename, true);
		} else if (material.dispFilename != null) {
			image = textureLoader.load(material.dispFilename, true);
		} else if (material.bumpFilename != null) {
			image = textureLoader.load(material.bumpFilename, true);
		}
	} catch (Exception e) {}
	 return image;
 }

 public Face() {
 }

 public void add(FaceVertex vertex) {
     vertices.add(vertex);
 }
 public VertexNormal faceNormal = new VertexNormal(0, 0, 0);

 // @TODO: This code assumes the face is a triangle.  
 public void calculateTriangleNormal() {
     float[] edge1 = new float[3];
     float[] edge2 = new float[3];
     float[] normal = new float[3];
     Vector3d v1 = vertices.get(0).v;
     Vector3d v2 = vertices.get(1).v;
     Vector3d v3 = vertices.get(2).v;
     float[] p1 = {v1.x, v1.y, v1.z};
     float[] p2 = {v2.x, v2.y, v2.z};
     float[] p3 = {v3.x, v3.y, v3.z};

     edge1[0] = p2[0] - p1[0];
     edge1[1] = p2[1] - p1[1];
     edge1[2] = p2[2] - p1[2];

     edge2[0] = p3[0] - p2[0];
     edge2[1] = p3[1] - p2[1];
     edge2[2] = p3[2] - p2[2];

     normal[0] = edge1[1] * edge2[2] - edge1[2] * edge2[1];
     normal[1] = edge1[2] * edge2[0] - edge1[0] * edge2[2];
     normal[2] = edge1[0] * edge2[1] - edge1[1] * edge2[0];

     faceNormal.x = normal[0];
     faceNormal.y = normal[1];
     faceNormal.z = normal[2];
 }
 
 public String toString() { 
     String result = "\tvertices: "+vertices.size()+" :\n";
     for(FaceVertex f : vertices) {
         result += " \t\t( "+f.toString()+" )\n";
     }
     return result;
 }

	public void draw() {
		if (image != lastImage) {
			lastImage = image;
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, image);
		}

		GL11.glBegin(GL11.GL_POLYGON);
		for(FaceVertex vertex: vertices) {
			vertex.draw();
		}
		GL11.glEnd();
	}
}   