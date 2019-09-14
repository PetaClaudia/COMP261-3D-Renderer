package renderer;

import java.awt.Color;
import java.util.ArrayList;

import renderer.Scene.Polygon;

/**
 * The Pipeline class has method stubs for all the major components of the
 * rendering pipeline, for you to fill in.
 * 
 * Some of these methods can get quite long, in which case you should strongly
 * consider moving them out into their own file. You'll need to update the
 * imports in the test suite if you do.
 */
public class Pipeline {

	/**
	 * Returns true if the given polygon is facing away from the camera (and so
	 * should be hidden), and false otherwise.
	 */
	public static boolean isHidden(Polygon poly) {
		// TODO fill this in.
		Vector3D[] vertices = poly.getVertices();
		Vector3D v1 = vertices[1].minus(vertices[0]);
		Vector3D v2 = vertices[2].minus(vertices[1]);
		Vector3D normal = v1.crossProduct(v2);
		if (normal.z >0) {
			return true;
		}
		return false;
	}

	/**
	 * Computes the colour of a polygon on the screen, once the lights, their
	 * angles relative to the polygon's face, and the reflectance of the polygon
	 * have been accounted for.
	 * 
	 * @param lightDirection
	 *            The Vector3D pointing to the directional light read in from
	 *            the file.
	 * @param lightColor
	 *            The color of that directional light.
	 * @param ambientLight
	 *            The ambient light in the scene, i.e. light that doesn't depend
	 *            on the direction.
	 */
	public static Color getShading(Polygon poly, Vector3D lightDirection, Color lightColor, Color ambientLight) {
		// TODO fill this in.
		Vector3D[] vertices = poly.getVertices();
		Vector3D normal = vertices[1].minus(vertices[0]).crossProduct(vertices[2].minus(vertices[1]));
		float cos = normal.cosTheta(lightDirection);
		
		if (cos<0) {
			cos = 0;
		}
		float ambientRed = ambientLight.getRed()*(poly.getReflectance().getRed()/255.0f);
		float incidentRed = lightColor.getRed()*(poly.getReflectance().getRed()/255.0f)*cos;
		int r = (int) (ambientRed + incidentRed);
		
		float ambientGreen = ambientLight.getGreen()*(poly.getReflectance().getGreen()/255.0f);
		float incidentGreen = lightColor.getGreen()*(poly.getReflectance().getGreen()/255.0f)*cos;
		int g = (int) (ambientGreen + incidentGreen);
		
		float ambientBlue = ambientLight.getBlue()*(poly.getReflectance().getBlue()/255.0f);
		float incidentBlue = lightColor.getBlue()*(poly.getReflectance().getBlue()/255.0f)*cos;
		int b = (int) (ambientBlue + incidentBlue);
		
		if(r > 255) {
			r = 255;
		}
		else if(r<0) {
			r = 0;
		}
		if(g > 255) {
			g = 255;
		}
		else if(g<0) {
			g = 0;
		}
		if(b > 255) {
			b = 255;
		}
		else if(b<0) {
			b = 0;
		}
		
		return new Color(r, g, b);
	}

	/**
	 * This method should rotate the polygons and light such that the viewer is
	 * looking down the Z-axis. The idea is that it returns an entirely new
	 * Scene object, filled with new Polygons, that have been rotated.
	 * 
	 * @param scene
	 *            The original Scene.
	 * @param xRot
	 *            An angle describing the viewer's rotation in the YZ-plane (i.e
	 *            around the X-axis).
	 * @param yRot
	 *            An angle describing the viewer's rotation in the XZ-plane (i.e
	 *            around the Y-axis).
	 * @return A new Scene where all the polygons and the light source have been
	 *         rotated accordingly.
	 */
	public static Scene rotateScene(Scene scene, float xRot, float yRot) {
		// TODO fill this in.
		 //Create rotation matrix
		Transform rotateMatrix = Transform.newXRotation(xRot).compose(Transform.newYRotation(yRot));
		
		ArrayList<Polygon> polygons = new ArrayList<Polygon>();
		for(Polygon p: scene.getPolygons()) {
			Vector3D[] v = p.getVertices();
			for(int i = 0; i < v.length; i++) {
				v[i] = rotateMatrix.multiply(v[i]);
			}
			polygons.add(new Polygon(v[0], v[1], v[2], p.getReflectance()));
		}
		return translateScene(scaleScene(new Scene(polygons ,scene.getLight())));
	}

	/**
	 * This should translate the scene by the appropriate amount.
	 * 
	 * @param scene
	 * @return
	 */
	public static Scene translateScene(Scene scene) {
		// TODO fill this in.

		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxZ = Float.MIN_VALUE;

		for(Polygon p: scene.polygons) {
			for(Vector3D v: p.vertices) {
				if(v.x < minX) {
					minX = v.x;
				}
				if(v.y < minY) {
					minY = v.y;
				}
				if(v.z > maxZ) {
					maxZ = v.z;
				}
			}
		}
		Transform translate = Transform.newTranslation(-minX, -minY, 0);
		//Translate light
		translate.multiply(scene.getLight());
		//Translate polygons
		ArrayList<Polygon> polygons = new ArrayList<Polygon>();
		for(Polygon poly : scene.polygons) {
			Vector3D[] vertex = poly.getVertices();
			for(int i = 0; i < vertex.length; i++) {
				vertex[i] = translate.multiply(vertex[i]);
			}
			polygons.add(new Polygon(vertex[0], vertex[1], vertex[2], poly.getReflectance()));
		}
		return new Scene(polygons, scene.getLight());
	}

	/**
	 * This should scale the scene.
	 * 
	 * @param scene
	 * @return
	 */
	public static Scene scaleScene(Scene scene) {
		// TODO fill this in.

		//scale polygons
		Transform scaler = Transform.newScale(1,1,1);
		ArrayList<Polygon> polygons = new ArrayList<Polygon>();
		for(Polygon poly : scene.polygons) {
			Vector3D[] vertex = poly.getVertices();
			for(int i = 0; i < vertex.length; i++) {
				vertex[i] = scaler.multiply(vertex[i]);
			}
			polygons.add(new Polygon(vertex[0], vertex[1], vertex[2], poly.getReflectance()));
		}

		return new Scene(polygons, scaler.multiply(scene.getLight()));
	}

	/**
	 * Computes the edgelist of a single provided polygon, as per the lecture
	 * slides.
	 */
	public static EdgeList computeEdgeList(Polygon poly) {
		// TODO fill this in.
		int startY = Integer.MAX_VALUE;
		int endY = Integer.MIN_VALUE;

		for (Vector3D v: poly.getVertices()) {
			if(v.y<startY) {
				startY = (int) v.y;
			}
			if(v.y>endY) {
				endY = (int) v.y;
			}
		}
		EdgeList edgeList = new EdgeList(startY, endY);

		Vector3D v1, v2;
		
		for (int i = 0; i < poly.getVertices().length; i++) {
			v1 = poly.getVertices()[i];
			v2 = poly.getVertices()[(i+1)%3];
			
			float x = v1.x;
			int y= Math.round(v1.y);
			float z = v1.z;

			float xSlope = (v2.x-v1.x)/(Math.round(v2.y)-Math.round(v1.y));
			float zSlope = (v2.z-v1.z)/(Math.round(v2.y)-Math.round(v1.y));
			
			if(v1.y < v2.y) {	//scanning down, update xMinYCol
				while(y <= Math.round(v2.y)) {
					edgeList.xMinYCol(y, x, z);
					x += xSlope;
					z += zSlope;
					y++;
				}
			}
			else { //scanning up, update xMaxYCol
				while(y >= Math.round(v2.y)) {
					edgeList.xMaxYCol(y, x, z);
					x -= xSlope;
					z -= zSlope;
					y--;
				}
			}
		}
		return edgeList;
	}

	/**
	 * Fills a zbuffer with the contents of a single edge list according to the
	 * lecture slides.
	 * 
	 * The idea here is to make zbuffer and zdepth arrays in your main loop, and
	 * pass them into the method to be modified.
	 * 
	 * @param zbuffer
	 *            A double array of colours representing the Color at each pixel
	 *            so far.
	 * @param zdepth
	 *            A double array of floats storing the z-value of each pixel
	 *            that has been coloured in so far.
	 * @param polyEdgeList
	 *            The edgelist of the polygon to add into the zbuffer.
	 * @param polyColor
	 *            The colour of the polygon to add into the zbuffer.
	 */
	public static void computeZBuffer(Color[][] zbuffer, float[][] zdepth, EdgeList polyEdgeList, Color polyColor) {
		// TODO fill this in.
		for(int y = polyEdgeList.getStartY(); y < polyEdgeList.getEndY(); y++) {
			if (y < 0 || y >= zbuffer.length) {	//if out of bounds, don't draw
				y++;
				continue;
			}

			int x = (int) (polyEdgeList.getLeftX(y));
			float z = polyEdgeList.getLeftZ(y);
			float slope = (polyEdgeList.getRightZ(y) - z) /(polyEdgeList.getRightX(y) - x);
			while(x <= (int)(polyEdgeList.getRightX(y))) {
				if (x < 0 || x >= zbuffer.length) {	//if out of bounds, don't draw
					z += slope;
					x++;
					continue;
				}

				if(z < zdepth[x][y]) {
					zbuffer[x][y] = polyColor;
					zdepth[x][y] = z;
				}
				z += slope;
				x++;
			}
		}
	}
}

// code for comp261 assignments
