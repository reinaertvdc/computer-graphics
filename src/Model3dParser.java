import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class parses a 3d model from a file.
 */
public class Model3dParser {
	/** The only instance of a Model3dParser. */
	private static Model3dParser instance;
	/** The list of already parsed files mapped on their parsed models. */
	private static Map<String, Model3d> parsed3dModels;
	/**
	 * Returns an instance of a Model3dParser.
	 * @return an instance of a Model3dParser
	 */
	public static Model3dParser getInstance() {
		if (instance == null)
			instance = new Model3dParser();
		return instance;
	}
	/**
	 * Constructs a Model3dParser.
	 */
	protected Model3dParser() {
		parsed3dModels = new HashMap<>();
	}
	/**
	 * Returns a 3d model parsed from the given file.
	 * @param filename name of the file to parse
	 * @return a 3d model parsed from the given file
	 */
	public Model3d parse3dModel(String filename) throws IOException {
		// If the model is already parsed, return the parsed model.
		if (parsed3dModels.containsKey(filename))
			return parsed3dModels.get(filename);
		// Parse a new Model3d from the given file, add it to the list of parsed
		// models and return it.
		Model3d result = new Model3d();
		ObjParser parser = new ObjParser(result, filename);
		result.setColbox(new ColBox(parser.getMin(), parser.getMax()));
		result.getColBox().print();
		result.compileDisplayList();
		parsed3dModels.put(filename, result);
		return result;
	}
}
