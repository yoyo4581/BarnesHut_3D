package barnesHut3D;

public class ThreeDRectangle {
	public float x;
	public float y;
	public float w;
	public float h;
	public float z;
	public float o;

	public ThreeDRectangle(float x, float y, float z, float w, float h, float o) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.h = h;
		this.o = o;
	}


	public boolean contains(Point point1) {
		return (point1.x > this.x && point1.x < this.w && point1.y > this.y && point1.y < this.h && point1.z > this.z
				&& point1.z < this.o);
	}

}
