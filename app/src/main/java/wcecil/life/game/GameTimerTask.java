package wcecil.life.game;

import java.util.TimerTask;
import java.util.logging.Logger;


public class GameTimerTask extends TimerTask {
    Logger l = Logger.getLogger(this.getClass().getCanonicalName());
    static GameTimerTask instance = new GameTimerTask();

    public static GameTimerTask getInstance() {
        return instance;
    }

    private GameTimerTask() {
    }

    @Override
    public void run() {
        GameState state = GameState.getInstance();
        if(state.running){
            state.nextState();
            state.displayCurrentStateOfBoard();
        }
    }
}
