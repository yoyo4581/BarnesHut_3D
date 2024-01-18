package org.graphics;

import static java.lang.Math.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import com.jogamp.opengl.*;

import barnesHut3D.*;
import barnesHut3D.Point;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public class App extends AppRoot {
	private float xrotation, yrotation, zrotation;
	private float[][] figureQuads = { { -1, -1, 1 }, { 1, -1, 1 }, { 1, 1, 1 }, { -1, 1, 1 } };
	private float[][] figureTriangle = { { 0, 1, 0 }, { -1, -1, 0 }, { 1, -1, 0 } };
	private static float[][] CubeList;
	private static float[][] Points;
	private static float[][] newPointers;
	private static ArrayList<Point> newPoints;
	private static ThreeDRectangle boundary;
	private int iter = 1;
	private static long start1;

	@Override
	public void display(GLAutoDrawable d) {
		clearCanvas();
		g.glTranslatef(0, 0, -6.6f);
		g.glRotatef(xrotation, 1, 0, 0);
		g.glRotatef(yrotation, 0, 1, 0);
		g.glRotatef(zrotation, 0, 0, 1);

		for (int i = 0; i < CubeList.length; i++) {
			drawFigureCube(CubeList[i]);
		}
		for (int i = 0; i < Points.length; i++) {
			if (newPoints.get(i).charge == 1) {
				String s = "positive";
				drawSphere(Points[i], 0.03, s);
			}
			if (newPoints.get(i).charge == -1) {
				String s = "negative";
				drawSphere(Points[i], 0.03, s);
			}
			if (newPoints.get(i).charge == 0) {
				String s = "neutral";
				drawSphere(Points[i], 0.03, s);
			}
		}
		OctTreeUpdate(boundary, newPoints);
		g.glFlush();

	}


	@Override
	public void registerAllKeyActions() {
		registerKeyAction(KeyEvent.VK_UP, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				xrotation += 3;
			}
		});
		registerKeyAction(KeyEvent.VK_DOWN, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				xrotation -= 3;
			}
		});
		registerKeyAction(KeyEvent.VK_LEFT, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				yrotation += 3;
			}
		});
		registerKeyAction(KeyEvent.VK_RIGHT, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				yrotation -= 3;
			}
		});
		registerKeyAction(KeyEvent.VK_1, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				zrotation += 3;
			}
		});
		registerKeyAction(KeyEvent.VK_2, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				zrotation -= 3;
			}
		});
	}


	public void OctTreeUpdate(ThreeDRectangle boundary, ArrayList<Point> points) {
		if (iter == 2) {
			long end2 = System.currentTimeMillis();
			System.out.println("Elapsed Time in milli seconds: " + (end2 - start1));
			return;
		}
		OctTree qt = new OctTree(boundary, 1);
		for (Point item : points) {
			qt.insert(item);
		}
		qt.computeCOM(qt);
		qt.iterate("root", qt, 0);
		newPoints = qt.calculateforce();
		System.out.println(qt.points_Prev.size());
		ArrayList<ArrayList<Float>> boundaries = qt.getIteration();
		int size1 = boundaries.get(0).size();
		int size2 = boundaries.size();
		int size3 = qt.points.size();
		int size4 = 3;
		CubeList = new float[size2][size1];
		Points = new float[size3][size4];
		newPointers = new float[size3][size4];
		System.out.println(qt.points.size());
		for (int i = 0; i < boundaries.size(); i++) {
			ArrayList<Float> items = boundaries.get(i);
			for (int v = 0; v < boundaries.get(i).size(); v++) {
				CubeList[i][v] = items.get(v) / 100;
			}
		}
		for (int h = 0; h < qt.points.size(); h++) {
			Point items = qt.points.get(h);
			float x = (items.x / 100);
			float y = (items.y / 100);
			float z = (items.z / 100);
			Points[h][0] = x;
			Points[h][1] = y;
			Points[h][2] = z;
		}
		for (int h = 0; h < newPoints.size(); h++) {
			Point items = newPoints.get(h);
			float x = (items.x / 100);
			float y = (items.y / 100);
			float z = (items.z / 100);
			newPointers[h][0] = x;
			newPointers[h][1] = y;
			newPointers[h][2] = z;
		}
		iter = iter + 1;

	}


	public static void main(String[] args) {
		App app = new App();
		app.start();
		start1 = System.currentTimeMillis();
		boundary = new ThreeDRectangle(-200, -200, -200, 200, 200, 200);
		float[][] point_array = { { (float) 38.243103, (float) -60.93662, (float) -89.57925 },
				{ (float) -11.114456, (float) 18.214691, (float) 99.966125 },
				{ (float) 76.85806, (float) 26.783737, (float) 88.06731 },
				{ (float) 69.21445, (float) 72.66795, (float) 32.06967 },
				{ (float) 66.412125, (float) -12.82296, (float) 60.319443 },
				{ (float) -39.30521, (float) -57.095573, (float) 30.92099 },
				{ (float) 29.292282, (float) 74.536285, (float) -95.142914 },
				{ (float) -49.613655, (float) -23.048607, (float) -81.47977 },
				{ (float) 32.31923, (float) 78.77205, (float) 23.643135 },
				{ (float) -70.54918, (float) 58.530777, (float) -44.318806 } };

		ArrayList<Point> Points = new ArrayList<>();
		for (int i = 0; i < point_array.length; i++) {
			float random_int1 = point_array[i][0];
			float random_int2 = point_array[i][1];
			float random_int3 = point_array[i][2];
			float charge = 1;
			// int charge = (int) Math.round(Math.random() * (2 - 1 + 1) - 1);
			Point p = new Point(random_int1, random_int2, random_int3, charge);
			Points.add(p);
		}
		app.OctTreeUpdate(boundary, Points);

	}
}
