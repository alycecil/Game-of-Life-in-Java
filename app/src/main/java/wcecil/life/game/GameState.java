package wcecil.life.game;

/**
 * Created by Courtney Cecil on 4/20/2015.
 */
public class GameState {
    static GameState instance = new GameState();

    public boolean running = false;

    private GameState() {
    }

    public static GameState getInstance() {
        return instance;
    }


}
