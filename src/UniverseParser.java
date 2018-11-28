import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class UniverseParser {
	private List<World> $worlds;
	private Map<String, Model> $models;
	private Game $game;
	private String $filepath;
	private boolean $verbose;
	private TextureLoader textureLoader = new TextureLoader();
	
	private enum Origin {LEFTDOWN, MIDDLE}

	public UniverseParser(String filepath) {
		$filepath = filepath;
		$verbose = false;
		$worlds = new ArrayList<>();
		$models = new HashMap<>();
		$game = new Game();
	}
	
	public UniverseParser() {
		$verbose = false;
		$worlds = new ArrayList<>();
		$models = new HashMap<>();
	}
	
	public void setVerbose(boolean state) {
		$verbose = state;
	}
	
	public void printDebug(String text) {
		if($verbose) {
			System.out.println(text);
		}
	}
	
	public void setFilepath(String filepath) {
		$filepath = filepath;
	}
	
	public void parse() {
		if($filepath != null) {
			parseFile($filepath);
		}
	}
	
	private void parseFile(String filepath) {
		
		//Get the factory
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		
		
		DocumentBuilder builder;
		try {
			builder = docBuilderFactory.newDocumentBuilder();
			Document dom = builder.parse(filepath);
			parseUniverse(dom);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void parseUniverse(Document dom) {
		Element root = dom.getDocumentElement();
		if(root.getNodeName() == "Universe") {
			printDebug("Found root.");
			parseWorlds(root);
		}
		
	}
	
	private void parseWorlds(Element universe) {
		NodeList worlds = universe.getElementsByTagName("World");
		if(worlds != null && worlds.getLength() != 0) {
			printDebug("Found " + worlds.getLength() + " worlds.");
			for(int i = 0; i < worlds.getLength(); i++) {
				Element world = (Element) worlds.item(i);
				parseWorld(world, i);
			}
		}
	}
	
	private void parseWorld(Element world, int index) {
		int x = 0, y = 0, z = 0;
		float width = 0, height = 0, depth = 0;
		String name = null, texture = null, temp;
		Origin alignment;
		
		if(world.hasAttribute("name"))
			name = world.getAttribute("name");
		else
			name = "" + index + System.currentTimeMillis();
		
		if(world.hasAttribute("texture"))
			texture = world.getAttribute("texture");
		
		if(world.hasAttribute("x"))
			x = Integer.parseInt(world.getAttribute("x"));
		
		if(world.hasAttribute("y"))
			x = Integer.parseInt(world.getAttribute("y"));
		
		if(world.hasAttribute("z"))
			x = Integer.parseInt(world.getAttribute("z"));
		
		if(world.hasAttribute("width"))
			width = Integer.parseInt(world.getAttribute("width"));
		
		if(world.hasAttribute("height"))
			height = Integer.parseInt(world.getAttribute("height"));
		
		if(world.hasAttribute("depth"))
			depth = Integer.parseInt(world.getAttribute("depth"));
			
		World gameWorld = new World(new Vector3d(width, height, depth));
		int image = 0;
		try {
			image = textureLoader.load(texture, true);
		} catch (Exception e) {}
		gameWorld.setTexture(image);
		gameWorld.setName(name);
		gameWorld.setGame($game);
		$game.addWorld(name, gameWorld);
		if(index == 0)
			$game.switchWorld(name);
		parseWorldComponents(gameWorld, world);
		parseGameObjects(gameWorld, world);
	}
	
	private void parseCameras(World gameWorld, Element world) {
		float x, y, z;
		String activeCamera = null, temp;
		
		NodeList cameras = world.getElementsByTagName("Camera");
		
		printDebug("Parsing Camera's from world: " + gameWorld.getName());
		if(cameras != null && cameras.getLength() != 0) {
			printDebug("Found " + cameras.getLength() + " cameras.");
			
			for(int i = 0; i < cameras.getLength(); i++) {
				Element camera = (Element) cameras.item(i);
				String name = getStringFromElement(camera, "name", "" + i + System.currentTimeMillis());
				String type = getStringFromElement(camera, "type", "fps");
				String active = getStringFromElement(camera, "active", "false");
				Camera gameCamera = new Camera();
				
				x = getFloatFromElement(camera, "x", 0);
				y = getFloatFromElement(camera, "y", 0);
				z = getFloatFromElement(camera, "z", 0);
				gameCamera.setPos(x, y, z);
				x = getFloatFromElement(camera, "rotationX", 0);
				y = getFloatFromElement(camera, "rotationY", 0);
				z = getFloatFromElement(camera, "rotationZ", 0);
				gameCamera.rotateDegreesXYZ(x, y, z);
				temp = getStringFromElement(camera, "collision", "dynamic");
				if(temp.compareTo("static") == 0)
					temp = "dynamic";
				gameCamera.getColBox().setType(temp);
				
				if(type.compareTo("static") == 0)
					gameCamera.setMoveable(false);
				if(active.compareTo("true") == 0 || activeCamera == null)
					activeCamera = name;
				
				gameWorld.addCamera(name, gameCamera);
			}
		}
		
		gameWorld.setCamera(activeCamera);
	}
	
	private void parseWorldComponents(World gameWorld, Element world) {
		parseCameras(gameWorld, world);
	}
	
	private void parseGameObjects(World gameWorld, Element world) {
		NodeList gameObjs = world.getElementsByTagName("GameObject");
		printDebug("Parsing GameObjects");
		if(gameObjs != null && gameObjs.getLength() != 0) {
			printDebug("Found " + gameObjs.getLength() + " GameObjects in world " + gameWorld.getName());
			for(int i = 0; i < gameObjs.getLength(); i++) {
				Element gameObj = (Element) gameObjs.item(i);
				parseGameObject(gameWorld, gameObj, i);
			}
		}
	}
	
	private void parseGameObject(World gameWorld, Element gameObject, int index) {
		String temp, model3d, name = null;
		Model model;
		Model3d graphicModel;
		float x = 0, y = 0, z = 0, scale;
		
		if(gameObject.hasAttribute("model"))
			model3d = gameObject.getAttribute("model");
		else {
			printDebug("Invalid GameObject:\n" + gameObject.toString() + "\n\n");
			return;
		}
		
		try {
			graphicModel = Model3dParser.getInstance().parse3dModel(model3d);
			model = new Model(graphicModel);
			model.setGame($game);
			name = getStringFromElement(gameObject, "name", "" + index + System.currentTimeMillis());
			
			x = getFloatFromElement(gameObject, "x", 0);
			y = getFloatFromElement(gameObject, "y", 0);
			z = getFloatFromElement(gameObject, "z", 0);
			model.setPos(new Vector3d(x, y, z));
			x = getFloatFromElement(gameObject, "rotationX", 0);
			y = getFloatFromElement(gameObject, "rotationY", 0);
			z = getFloatFromElement(gameObject, "rotationZ", 0);
			model.setRotation(x, y, z);
			scale = getFloatFromElement(gameObject, "scale", 1);
			x = getFloatFromElement(gameObject, "scaleX", scale);
			y = getFloatFromElement(gameObject, "scaleY", scale);
			z = getFloatFromElement(gameObject, "scaleZ", scale);
			model.setScale(x, y, z);
			temp = getStringFromElement(gameObject, "collision", "static");
			model.getColBox().setType(temp);
			model.setName(name);
			gameWorld.addModel(name, model);
			if(name != null)
				$models.put(name, model);
			
			parseGameObjectComponents(model, gameObject);
		} catch (IOException e) {
			printDebug("Invalid GameObject, model path does not exist: " + model3d);
		}
	}
	
	private void parseGameObjectComponents(Model model, Element gameObject) {
		NodeList list = gameObject.getElementsByTagName("Component");
		printDebug("Parsing Componenets");
		if(list != null && list.getLength() != 0) {
			printDebug("Found: " + list.getLength() + " components.");
			for(int i = 0; i < list.getLength(); i++) {
				Element component = (Element) list.item(i);
				parseComponent(model, component);
			}
		}
	}
	
	private void parseComponent(Model model, Element component) {
		String componentPath;
		
		componentPath = getStringFromElement(component, "class", null);
		if(componentPath != null) {
			try {
				File file = new File(componentPath);
				URL url = file.getParentFile().toURL();
				URL[] urls = new URL[] { url };
				
				ClassLoader cl = new URLClassLoader(urls);
				Class<?> cls = cl.loadClass(file.getName());
				
				if(cls.getSuperclass() == Component.class) {
					Class<? extends Component> compClass = (Class<? extends Component>) cls;
					Component comp = parseComponentConstructor(compClass, component);
					if(comp == null) {
						comp = compClass.newInstance();
						printDebug("Component creation with constructor failed.");
					}
					comp.setGame($game);
					comp.setParent(model);
					model.addComponent(comp);
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private Component parseComponentConstructor(Class<? extends Component> compClass, Element component) {
		NodeList nodes = component.getElementsByTagName("Constructor");
		if(nodes != null && nodes.getLength() != 0) {
			Element constructor = (Element) nodes.item(0);
			NodeList parameters = constructor.getElementsByTagName("Parameter");
			Class<?> type[] = new Class<?>[parameters.getLength()];
			Object values[] = new Object[type.length];
			
			for(int i = 0; i < type.length; i++) {
				Element xmlType = (Element) parameters.item(i);
				type[i] = getType(xmlType);
				if(type[i] == null)
					return null;
				else
					values[i] = getValue(type[i], xmlType);
				
				if(values[i] == null)
					return null;
					
			}
			
			try {
				Component comp = compClass.getConstructor(type).newInstance(values);
				return comp;
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private Class<?> getType(Element xmlType) {
		String type = this.getStringFromElement(xmlType, "type", null);
		printDebug("Type is: " + type);
		if(type == null) {
			printDebug("Type is null");
			return null;
		}
		
		if(type.compareTo("float") == 0) 
			return float.class;
		else if(type.compareTo("double") == 0)
			return double.class;
		else if(type.compareTo("int") == 0)
			return int.class;
		else if(type.compareTo("String") == 0)
			return String.class;
		else if(type.compareTo("char") == 0)
			return char.class;
		else if(type.compareTo("boolean") == 0)
			return boolean.class;
		else if(type.compareTo("long") == 0)
			return long.class;
		
		printDebug("Found no primitive type.");
		return null;
	}
	
	private Object getValue(Class<?> type, Element xml) {
		String value = getStringFromElement(xml, "value", null);
		if(value == null)
			return null;
		
		if(type == int.class)
			return Integer.parseInt(value);
		else if(type == long.class)
			return Long.parseLong(value);
		else if(type == double.class)
			return Double.parseDouble(value);
		else if(type == short.class)
			return Short.parseShort(value);
		else if(type == boolean.class)
			return Boolean.parseBoolean(value);
		else if(type == float.class)
			return Float.parseFloat(value);
		else if(type == char.class)
			return value.charAt(0);
		else if(type == String.class)
			return value;
		
		return null;
	}
	
	public int getIntFromElement(Element element, String attributeName, int defaultValue) {
		if(element.hasAttribute(attributeName))
			return Integer.parseInt(element.getAttribute(attributeName));
		else 
			return defaultValue;
	}
	
	public float getFloatFromElement(Element element, String attributeName, float defaultValue) {
		if(element.hasAttribute(attributeName))
			return Float.parseFloat(element.getAttribute(attributeName));
		else 
			return defaultValue;
	}
	
	public String getStringFromElement(Element element, String attributeName, String defaultValue) {
		if(element.hasAttribute(attributeName))
			return element.getAttribute(attributeName);
		else
			return defaultValue;
	}
	
	public List<World> getWorlds() {
		return $worlds;
	}
	
	public Game getGame() {
		return $game;
	}
}
