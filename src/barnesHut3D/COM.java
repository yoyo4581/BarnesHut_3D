package barnesHut3D;

import java.util.*;

public class COM {
	public float[] COM = new float[3];
	private ArrayList<Float> x_coord = new ArrayList<>();
	private ArrayList<Float> y_coord = new ArrayList<>();
	private ArrayList<Float> z_coord = new ArrayList<>();

	public COM(ArrayList<Point> points, int totalMass) {
		float sum_x = 0;
		float sum_y = 0;
		float sum_z = 0;

		for (int i = 0; i < points.size(); i++) {
			x_coord.add(points.get(i).x);
			y_coord.add(points.get(i).x);
			z_coord.add(points.get(i).x);
		}
		for (int i = 0; i < x_coord.size(); i++) {
			sum_x = sum_x + x_coord.get(i);
		}
		for (int i = 0; i < y_coord.size(); i++) {
			sum_y = sum_y + y_coord.get(i);
		}
		for (int i = 0; i < z_coord.size(); i++) {
			sum_z = sum_z + z_coord.get(i);
		}
		COM[0] = sum_x / totalMass;
		COM[1] = sum_y / totalMass;
		COM[2] = sum_z / totalMass;
	}


	public float[] getCOM() {
		return COM;
	}
}
