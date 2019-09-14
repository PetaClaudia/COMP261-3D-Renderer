package renderer;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import renderer.Scene.Polygon;

public class Renderer extends GUI {
	Scene sc;
	@Override
	protected void onLoad(File file) {
		// TODO fill this in.

		/*
		 * This method should parse the given file into a Scene object, which
		 * you store and use to render an image.
		 */
		
		List<Scene.Polygon> polygons = new ArrayList<Scene.Polygon>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader (file));
			String line;
			Vector3D light = null;
			// read in each line of the file
			while ((line = reader.readLine()) != null) {
				
				// tokenise the line by splitting it at the commas.
				String[] tokens = line.split(",");
				//System.out.println(tokens.length);
				if(tokens.length == 1) {
					int polyCount = Integer.parseInt(tokens[0]);
					//System.out.println("polyCount: "+polyCount);
				}
				if (tokens.length>3) {
					//colour
					int[] colour = new int[3];
					for (int i = 0; i<3; i++) {
						colour[i] = Integer.parseInt(tokens[i]);
					}
					//coords
					float[] coords = new float[9];
					int count = 0;
					for(int i = 3; i<12; i++) {
						coords[count] = Float.parseFloat(tokens[i]);
						count++;
					}
					polygons.add(new Polygon(coords, colour));
				}
				if (tokens.length == 3) {
					//light
					float x = Float.parseFloat(tokens[0]);
					float y = Float.parseFloat(tokens[1]);
					float z = Float.parseFloat(tokens[2]);
					light = new Vector3D(x, y, z);
				}
			}
			//if(light != null) {
				sc = new Scene(polygons, light);
				//System.out.println(light.toString());
				sc = Pipeline.scaleScene(sc);
				sc = Pipeline.translateScene(sc);
			//}
			reader.close();
			}catch (IOException e) {
				throw new RuntimeException("file reading failed.");
			}
	}

	@Override
	protected void onKeyPress(KeyEvent ev) {
		// TODO fill this in.

		/*
		 * This method should be used to rotate the user's viewpoint.
		 */
		if(ev.getKeyCode() == KeyEvent.VK_UP) {
			sc = Pipeline.rotateScene(sc, -0.3f, 0);
		}
		else if (ev.getKeyCode() == KeyEvent.VK_DOWN) {
			sc = Pipeline.rotateScene(sc, 0.3f, 0);
		}
		else if (ev.getKeyCode() == KeyEvent.VK_LEFT) {
			sc = Pipeline.rotateScene(sc, 0, 0.3f);
		}
		else if (ev.getKeyCode() == KeyEvent.VK_RIGHT) {
			sc = Pipeline.rotateScene(sc, 0, -0.3f);
		}
	}

	@Override
	protected BufferedImage render() {
		// TODO fill this in.

		/*
		 * This method should put together the pieces of your renderer, as
		 * described in the lecture. This will involve calling each of the
		 * static method stubs in the Pipeline class, which you also need to
		 * fill in.
		 */
		Color[][] zbuffer = new Color[CANVAS_WIDTH][CANVAS_HEIGHT];
		float[][] zdepth = new float[CANVAS_WIDTH][CANVAS_HEIGHT];
		
		//default
		for(int i = 0; i < CANVAS_WIDTH; i++) {
			for(int j = 0; j < CANVAS_HEIGHT; j++) {
				zbuffer[i][j] = Color.WHITE;
				zdepth[i][j] = Float.POSITIVE_INFINITY;
			}
		}
		
		if(sc != null) {
			for(Polygon polygon: sc.getPolygons()) {
				if(!Pipeline.isHidden(polygon)) {	//only what we can see
					EdgeList edgeList = Pipeline.computeEdgeList(polygon);
					int[] ambient = getAmbientLight();
					Color AL = new Color(ambient[0], ambient[1], ambient[2]);
					Color shading = Pipeline.getShading(polygon, sc.getLight(), Color.white, AL);
					Pipeline.computeZBuffer(zbuffer, zdepth, edgeList, shading);
				}
			}
		}
		return convertBitmapToImage(zbuffer);
	}

	/**
	 * Converts a 2D array of Colors to a BufferedImage. Assumes that bitmap is
	 * indexed by column then row and has imageHeight rows and imageWidth
	 * columns. Note that image.setRGB requires x (col) and y (row) are given in
	 * that order.
	 */
	private BufferedImage convertBitmapToImage(Color[][] bitmap) {
		BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < CANVAS_WIDTH; x++) {
			for (int y = 0; y < CANVAS_HEIGHT; y++) {
				image.setRGB(x, y, bitmap[x][y].getRGB());
			}
		}
		return image;
	}

	public static void main(String[] args) {
		new Renderer();
	}
}

// code for comp261 assignments
