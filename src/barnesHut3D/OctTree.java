package barnesHut3D;

import java.util.ArrayList;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.*;

public class OctTree {
	OctTree frontnortheast;
	OctTree backnortheast;
	OctTree frontnorthwest;
	OctTree backnorthwest;
	OctTree frontsouthwest;
	OctTree backsouthwest;
	OctTree frontsoutheast;
	OctTree backsoutheast;
	public int id = 0;
	public ArrayList<Point> points = new ArrayList<>();
	public ArrayList<Point> points_Prev = new ArrayList<>();
	public float[] COM;
	public boolean divided = false;
	public boolean isInternal = false;
	public boolean isEmpty = true;
	public int capacity = 1;
	public int depth;
	public float side_length;
	public ArrayList<Float> force = new ArrayList<>();
	public ArrayList<String> memo = new ArrayList<>();
	public ArrayList<float[]> forceVectors = new ArrayList<>();
	public String Iteration = " ";
	public ArrayList<OctTree> OctTreeStore = new ArrayList<>();
	public int mass = 1;

	public int counter = 0;

	ThreeDRectangle boundary;
	public ArrayList<ArrayList<Float>> CubeList = new ArrayList<ArrayList<Float>>();

	public OctTree(ThreeDRectangle newboundary, int depth) {
		this.boundary = newboundary;
		this.side_length = this.boundary.w - this.boundary.x;
		this.depth = depth;
	}


	public int[][] OutlineCube(int[] arr) {
		int x = arr[0];
		int y = arr[1];
		int z = arr[2];
		int w = arr[3];
		int h = arr[4];
		int o = arr[5];

		int[][] list_cubeboundary = { { x, y, z }, { x, y, o }, { w, y, z }, { x, h, o }, { w, h, z }, { w, h, o },
				{ w, y, o } };
		return list_cubeboundary;
	}


	public void subdivide() {
		float x = this.boundary.x;
		float y = this.boundary.y;
		float z = this.boundary.z;
		float w = this.boundary.w;
		float h = this.boundary.h;
		float o = this.boundary.o;

		this.divided = true;
		this.isInternal = true;
		int newdepth = this.depth + 1;
		ThreeDRectangle bsw = new ThreeDRectangle(x, y, z, w / 2 + x / 2, h / 2 + y / 2, z / 2 + o / 2);
		this.backsouthwest = new OctTree(bsw, newdepth);
		ThreeDRectangle fsw = new ThreeDRectangle(x, y, z / 2 + o / 2, w / 2 + x / 2, h / 2 + y / 2, o);
		this.frontsouthwest = new OctTree(fsw, newdepth);
		ThreeDRectangle bnw = new ThreeDRectangle(x, y / 2 + h / 2, z, w / 2 + x / 2, h / 2 + y / 2, z / 2 + o / 2);
		this.backnorthwest = new OctTree(bnw, newdepth);
		ThreeDRectangle fnw = new ThreeDRectangle(x, y / 2 + h / 2, z / 2 + o / 2, w / 2 + x / 2, h, o);
		this.frontnorthwest = new OctTree(fnw, newdepth);
		ThreeDRectangle bse = new ThreeDRectangle(x / 2 + w / 2, y, z, w, h / 2 + y / 2, z / 2 + o / 2);
		this.backsoutheast = new OctTree(bse, newdepth);
		ThreeDRectangle fse = new ThreeDRectangle(x / 2 + w / 2, y, z / 2 + o / 2, w, h / 2 + y / 2, o);
		this.frontsoutheast = new OctTree(fse, newdepth);
		ThreeDRectangle bne = new ThreeDRectangle(x / 2 + w / 2, y / 2 + h / 2, z, w, h, z / 2 + o / 2);
		this.backnortheast = new OctTree(bne, newdepth);
		ThreeDRectangle fne = new ThreeDRectangle(x / 2 + w / 2, y / 2 + h / 2, z / 2 + o / 2, w, h, o);
		this.frontnortheast = new OctTree(fne, newdepth);
	}


	public void insert(Point newPoint) {
		if (this.boundary.contains(newPoint) == false) {
			return;
		}
		if (this.isEmpty) {
			this.points.add(newPoint);
			System.out.println(this + " " + points.get(0).x + " " + points.get(0).y + " " + points.get(0).z);
			this.isEmpty = false;
			this.id = this.id + 1;
		} else {
			if (this.id == 1 && this.divided == false) {
				this.subdivide();
				Point oldpoint = this.points.get(0);
				this.frontnortheast.insert(oldpoint);
				this.frontsoutheast.insert(oldpoint);
				this.frontnorthwest.insert(oldpoint);
				this.frontsouthwest.insert(oldpoint);
				this.backnortheast.insert(oldpoint);
				this.backnorthwest.insert(oldpoint);
				this.backsoutheast.insert(oldpoint);
				this.backsouthwest.insert(oldpoint);
				this.points_Prev.add(oldpoint);
				this.points.clear();
			}
			if (this.divided == true) {
				this.id = this.id + 1;
				this.frontnortheast.insert(newPoint);
				this.frontsoutheast.insert(newPoint);
				this.frontnorthwest.insert(newPoint);
				this.frontsouthwest.insert(newPoint);
				this.backnortheast.insert(newPoint);
				this.backnorthwest.insert(newPoint);
				this.backsoutheast.insert(newPoint);
				this.backsouthwest.insert(newPoint);
				this.points_Prev.add(newPoint);
			}
		}
	}


	public void computeCOM(OctTree q) {
		if (q.isInternal) {
			COM compute1 = new COM(q.points_Prev, q.id);
			this.COM = compute1.getCOM();
			this.frontnortheast.computeCOM(this.frontnortheast);
			this.frontnorthwest.computeCOM(this.frontnorthwest);
			this.frontsouthwest.computeCOM(this.frontsouthwest);
			this.frontsoutheast.computeCOM(this.frontsoutheast);
			this.backnorthwest.computeCOM(this.backnorthwest);
			this.backnortheast.computeCOM(this.backnortheast);
			this.backsouthwest.computeCOM(this.backsouthwest);
			this.backsoutheast.computeCOM(this.backsoutheast);
		}

	}


	public double calculateDistance(ArrayList<Point> subject, float[] object) {
		float x_sub = subject.get(0).x;
		float y_sub = subject.get(0).y;
		float z_sub = subject.get(0).z;
		float x_obj = object[0];
		float y_obj = object[1];
		float z_obj = object[2];

		return (Math.sqrt(Math.pow((x_obj - x_sub), 2) + Math.pow((y_obj - y_sub), 2) + Math.pow((z_obj - z_sub), 2)));

	}


	public String calculateForce(OctTree subject, OctTree object) {
		if (subject == null | object == null) {
			return "o";
		}
		if (subject == object) {
			return "o";
		}
		if (object.isEmpty) {
			return "o";
		}
		if (object.divided == false && object.points != subject.points && object.points.size() == 1) {
			float[] newObj = { object.points.get(0).x, object.points.get(0).y, object.points.get(0).z };
			double distance = calculateDistance(subject.points, newObj);
			double ratio = this.side_length / distance;
			float force = (float) (-(subject.points.get(0).charge * object.points.get(0).charge)
					/ Math.pow(distance, 2));
			if (distance < 10) {
				float[] forceInstance = { 0, 0, 0 };
				subject.force.add(force);
				subject.forceVectors.add(forceInstance);
				return "o";
			}
			subject.force.add(force);
			float[] forceInstance = { (float) ((force / distance) * (subject.points.get(0).x - object.points.get(0).x)),
					(float) ((force / distance) * (subject.points.get(0).y - object.points.get(0).y)),
					(float) ((force / distance) * (subject.points.get(0).z - object.points.get(0).z)) };
			subject.forceVectors.add(forceInstance);

		} else if (object.isInternal && object.points.size() != 1) {
			double distance = calculateDistance(subject.points, object.COM);
			double ratio = (object.side_length) / distance;
			float netCharge = 0;
			for (Point item : object.points_Prev) {
				netCharge = netCharge + item.charge;
			}
			if (ratio < 0.5) {
				float force = (float) (-(subject.points.get(0).charge * netCharge) / Math.pow(distance, 2));
				if (distance < 10) {
					float[] forceInstance = { 0, 0, 0 };
					subject.force.add(force);
					subject.forceVectors.add(forceInstance);
					return "breaker";

				} else {
					subject.force.add(force);
					float[] forceInstance = { (float) ((force / distance) * (subject.points.get(0).x + object.COM[0])),
							(float) ((force / distance) * (subject.points.get(0).y + object.COM[1])),
							(float) ((force / distance) * (subject.points.get(0).z + object.COM[2])) };
					subject.forceVectors.add(forceInstance);
					return "breaker";
				}
			}
			return "o";
		}
		return "o";

	}


	public void iterate(String s, OctTree q, int level) {
		if (this.points.size() == 0 && this.divided == false) {
			this.Iteration = this.Iteration + s;
			q.memo.add(this.Iteration + s);
			return;
		}
		if (this.points.size() > 0) {
			ArrayList<Float> arr1 = new ArrayList<Float>();
			arr1.add(this.boundary.x);
			arr1.add(this.boundary.y);
			arr1.add(this.boundary.z);
			arr1.add(this.boundary.w);
			arr1.add(this.boundary.h);
			arr1.add(this.boundary.o);
			q.CubeList.add(arr1);
			q.points.add(this.points.get(0));
			q.OctTreeStore.add(this);
		} else if (this.isInternal) {
			q.OctTreeStore.add(this);
		}
		if (this.divided == true && !q.memo.contains(this.Iteration)) {
			this.frontnorthwest.iterate(s + " FNW ", q, this.depth);
			this.frontnortheast.iterate(s + " FNE ", q, this.depth);
			this.frontsouthwest.iterate(s + " FSW ", q, this.depth);
			this.frontsoutheast.iterate(s + " FSE ", q, this.depth);
			this.backnorthwest.iterate(s + " BNW ", q, this.depth);
			this.backnortheast.iterate(s + " BNE ", q, this.depth);
			this.backsouthwest.iterate(s + " BSW ", q, this.depth);
			this.backsoutheast.iterate(s + " BSE ", q, this.depth);
		}

	}


	public Point adjustPosition(OctTree subject) {
		float x = 0;
		float y = 0;
		float z = 0;
		float time = 10;
		float startx = subject.points.get(0).x;
		float starty = subject.points.get(0).y;
		float startz = subject.points.get(0).z;
		if (subject.forceVectors == null) {
			return subject.points.get(0);
		}

		for (int i = 0; i < subject.forceVectors.size(); i++) {
			x = x + subject.forceVectors.get(i)[0];
			y = y + subject.forceVectors.get(i)[1];
			z = z + subject.forceVectors.get(i)[2];
		}
		x = (float) (startx - x * (Math.pow(time, 2)));
		y = (float) (starty - y * (Math.pow(time, 2)));
		z = (float) (startz - z * (Math.pow(time, 2)));

		Point newPoint = new Point(x, y, z, subject.points.get(0).charge);
		float x_bound = this.boundary.x;
		float y_bound = this.boundary.x;
		float z_bound = this.boundary.x;

		if (Math.abs(x - x_bound) < 0.15) {
			x = (float) (x + 0.3);
		}
		if (Math.abs(y - y_bound) < 0.15) {
			y = (float) (y + 0.3);
		}
		if (Math.abs(z - z_bound) < 0.15) {
			z = (float) (z + 0.3);
		}
		return newPoint;
	}


	public ArrayList<Point> calculateforce() {
		ArrayList<OctTree> subjectNew = new ArrayList<>();
		ArrayList<Point> newPoints = new ArrayList<>();
		for (int i = 0; i < this.OctTreeStore.size(); i++) {
			OctTree subject = this.OctTreeStore.get(i);
			if (subject.isInternal == false) {
				subjectNew.add(subject);
				forceIterate(subject);
				Point newPoint = adjustPosition(subject);
				newPoints.add(newPoint);
			}
		}
		return newPoints;
	}


	public void forceIterate(OctTree subject) {
		String result = calculateForce(subject, this);
		if (result == "breaker") {
			return;
		}
		if (this.frontnorthwest != null) {
			this.frontnorthwest.forceIterate(subject);
		}
		if (this.frontnortheast != null) {
			this.frontnortheast.forceIterate(subject);
		}
		if (this.frontsouthwest != null) {
			this.frontsouthwest.forceIterate(subject);
		}
		if (this.frontsoutheast != null) {
			this.frontsoutheast.forceIterate(subject);
		}
		if (this.backnorthwest != null) {
			this.backnorthwest.forceIterate(subject);
		}
		if (this.backnortheast != null) {
			this.backnortheast.forceIterate(subject);
		}
		if (this.backsouthwest != null) {
			this.backsouthwest.forceIterate(subject);
		}
		if (this.backsoutheast != null) {
			this.backsoutheast.forceIterate(subject);
		}
	}


	public ArrayList getIteration() {
		return this.CubeList;
	}

}
