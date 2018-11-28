// https://github.com/seanrowens/oObjLoader/blob/master/com/owens/oobjloader/builder/VertexTexture.java

// Written by Sean R. Owens, sean at guild dot net, released to the
// public domain. Share and enjoy. Since some people argue that it is
// impossible to release software to the public domain, you are also free
// to use this code under any version of the GPL, LPGL, Apache, or BSD
// licenses, or contact me for use of another license.

public class VertexTexture {

    public float u = 0;
    public float v = 0;

    VertexTexture(float u, float v) {
        this.u = u;
        this.v = v;
    }

    public String toString() {
        if (null == this) {
            return "null";
        } else {
            return u + "," + v;
        }
    }
}