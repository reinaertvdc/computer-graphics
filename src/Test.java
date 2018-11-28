
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		testXML("/home/brent/Documents/Git/Project-OpenGL/testfiles/xml/universe.xml");
	}
	
	public static void testXML(String filepath) {
		UniverseParser parser = new UniverseParser(filepath);
		parser.setVerbose(true);
		parser.parse();
	}
}
