import java.util.List;

import org.lwjgl.opengl.GL11;


public class Main {

	public static void main(String[] args) {
		System.out.println("Starting game...");
		Game game;
		UniverseParser parser = new UniverseParser("C:/Users/Reinaert/programming/java/Project OpenGL/testfiles/xml/universe.xml");
		parser.setVerbose(true);
		parser.parse();
		
		game = parser.getGame();
		game.run();
	
		try {
			System.out.println("Started game.");
			game.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Game finished");
	}
}
