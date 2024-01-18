package org.graphics;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.util.*;

public abstract class AppRoot implements GLEventListener {

	public static GL2 g;

	private static GLProfile profile;
	private static GLCapabilities capabilities;

	private FPSAnimator fpsAnimator;
	private GLCanvas glcanvas;

	private JFrame frame;
	private JPanel panel;

	private static InputMap inputMap;
	private static ActionMap actionMap;

	private static final GLU glu = new GLU();

	public abstract void registerAllKeyActions();


	@Override
	public abstract void display(GLAutoDrawable d);


	private void initGLObjects() {
		profile = GLProfile.get(GLProfile.GL2);
		capabilities = new GLCapabilities(profile);
	}


	private void createWindow() {
		setGlcanvas(new GLCanvas(capabilities));
		getGlcanvas().addGLEventListener(this);
		getGlcanvas().setSize(800, 600);

		frame = new JFrame("BarnesHut3D");
		frame.getContentPane().add(getGlcanvas());
		frame.setSize(frame.getContentPane().getPreferredSize());

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (d.width - frame.getWidth()) / 2;
		int y = (d.height - frame.getHeight()) / 2;
		frame.setLocation(x, y);

		panel = new JPanel();
		panel.setPreferredSize(new Dimension(0, 0));
		frame.add(panel);

		actionMap = panel.getActionMap();
		inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowIsClosing() {
				if (fpsAnimator.isStarted()) {
					fpsAnimator.stop();
				}
				System.exit(0);
			}
		});
	}


	public void registerKeyAction(Integer key, AbstractAction a) {
		inputMap.put(KeyStroke.getKeyStroke(key, 0), key.toString());
		actionMap.put(key.toString(), a);
	}


	public void start() {
		fpsAnimator.start();
	}


	public void clearCanvas() {
		g.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		g.glLoadIdentity();
	}


	@Override
	public void init(GLAutoDrawable d) {
		g = d.getGL().getGL2();
		g.glShadeModel(GL2.GL_SMOOTH);
		g.glClearColor(0, 0, 0, 0);
		g.glClearDepth(1);
		g.glEnable(GL2.GL_DEPTH_TEST);
		g.glDepthFunc(GL2.GL_LEQUAL);
		g.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
	}


	@Override
	public void reshape(GLAutoDrawable d, int x, int y, int width, int height) {
		g.glViewport(0, 0, width, height);
		g.glMatrixMode(GL2.GL_PROJECTION);
		g.glLoadIdentity();
		glu.gluPerspective(45f, (float) width / (float) height, 1, 20);
		g.glMatrixMode(GL2.GL_MODELVIEW);
		g.glLoadIdentity();
	}


	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}


	protected void drawCube3f(int mode, float[][][] figure) {
		if ((null == figure || (figure[0][0].length != 3)))
			return;
		g.glBegin(mode);
		g.glColor3f(0, 1, 0);
		for (int v = 0; v < figure.length; v++) {
			for (int b = 0; b < figure[v].length; b++) {
				g.glVertex3f(figure[v][b][0], figure[v][b][1], figure[v][b][2]);
			}
		}
		g.glEnd();
	}


	protected void drawFigure3f(int mode, float[][] figure) {
		if ((null == figure || (figure[0].length != 3)))
			return;
		g.glBegin(mode);
		for (int v = 0; v < figure.length; v++) {
			g.glVertex3f(figure[v][0], figure[v][1], figure[v][2]);
		}
		g.glEnd();
	}


	public void drawSphere(float[] point, double radius, String color) {
		GLUquadric sphere = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(sphere, GLU.GLU_FILL);
		glu.gluQuadricTexture(sphere, true);
		glu.gluQuadricNormals(sphere, GLU.GLU_SMOOTH);
		// draw a sphere
		g.glPushMatrix();
		g.glTranslated(point[0], point[1], point[2]);

		if (color == "positive") {
			g.glColor3f(0, 0, 1);
		} else if (color == "negative") {
			g.glColor3f(1, 0, 0);
		} else {
			g.glColor3f(1, 1, 1);
		}
		glu.gluSphere(sphere, radius, 16, 16);
		g.glPopMatrix();
	}


	protected void drawFigureCube(float[] figure) {
		float x0 = figure[0];
		float y0 = figure[1];
		float z0 = figure[2];
		float x1 = figure[3];
		float y1 = figure[4];
		float z1 = figure[5];

		float[][] posTZ = { { x1, y1, z1 }, { x0, y1, z1 } };
		float[][] posRZ = { { x1, y1, z1 }, { x1, y0, z1 } };
		float[][] posLZ = { { x0, y1, z1 }, { x0, y0, z1 } };
		float[][] posBZ = { { x0, y0, z1 }, { x1, y0, z1 } };
		float[][] negBZ = { { x0, y0, z0 }, { x1, y0, z0 } };
		float[][] negLZ = { { x0, y0, z0 }, { x0, y1, z0 } };
		float[][] negRZ = { { x1, y0, z0 }, { x1, y1, z0 } };
		float[][] negTZ = { { x1, y1, z0 }, { x0, y1, z0 } };

		float[][] topR = { { x1, y1, z1 }, { x1, y1, z0 } };
		float[][] topL = { { x0, y1, z1 }, { x0, y1, z0 } };
		float[][] botL = { { x0, y0, z0 }, { x0, y0, z1 } };
		float[][] botR = { { x1, y0, z1 }, { x1, y0, z0 } };

		float[][][] figureFinal = { posTZ, posRZ, posLZ, posBZ, negBZ, negLZ, negRZ, negTZ, topR, topL, botL, botR };
		drawCube3f(g.GL_LINES, figureFinal);
	}


	protected void drawFigureLine(float[][] figure) {
		drawFigure3f(g.GL_LINES, figure);
	}


	protected void drawFigureQuads3f(float[][] figure) {
		drawFigure3f(g.GL_QUADS, figure);
	}


	protected void drawFigureTriangles3f(float[][] figure) {
		drawFigure3f(g.GL_TRIANGLES, figure);
	}


	public AppRoot() {
		initGLObjects();
		createWindow();
		registerAllKeyActions();
		fpsAnimator = new FPSAnimator(getGlcanvas(), 300, true);
		frame.setVisible(true);
	}


	public GLCanvas getGlcanvas() {
		return glcanvas;
	}


	public void setGlcanvas(GLCanvas glcanvas) {
		this.glcanvas = glcanvas;
	}
}
