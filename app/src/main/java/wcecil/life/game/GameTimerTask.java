package wcecil.life.game;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import wcecil.life.ui.LifeActivity;


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
        l.log(Level.INFO,"Starting GameTimerTask");
        if(state.running){
            state.nextState();
        }
        state.displayCurrentStateOfBoard();

        l.log(Level.INFO,"Done GameTimerTask");

    }
}
