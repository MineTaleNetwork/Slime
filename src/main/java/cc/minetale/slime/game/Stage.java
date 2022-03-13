package cc.minetale.slime.game;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation for stages. <br>
 * It is recommended to use the provided stages whenever you can as Slime might have special/default behaviour for some of them. <br>
 * You can add your own stages and change their order as you wish.
 */
public class Stage {
    /** Setting up the arena like creating teams and spawns. **/
    public static final Stage SETUP = new Stage();
    /** Gathering players in a lobby. **/
    public static final Stage IN_LOBBY = new Stage();
    /** Enough players, starting the game. **/
    public static final Stage STARTING = new Stage();

    /** The game is preparing like explaining the gamemode and counting down. **/
    public static final Stage PRE_GAME = new Stage();
    /**
     * The game is ongoing, this includes the grace period or anything similar.<br>
     * Any additional stages should be between this one and {@linkplain #POST_GAME}.
     */
    public static final Stage IN_GAME = new Stage();
    /** The game has ended, display the scoreboard, play win effects. **/
    public static final Stage POST_GAME = new Stage();

    @Getter private @Nullable Stage next;
    @Getter private @Nullable Stage previous;

    public void setNext(Stage newNext) {
        this.next = newNext;
        if(newNext.getPrevious() != this) {
            newNext.setPrevious(this);
        }
    }

    public void setPrevious(Stage newPrevious) {
        this.previous = newPrevious;
        if(newPrevious.getNext() != this) {
            newPrevious.setNext(this);
        }
    }

    public void replaceNext(Stage newNext) {
        if(this.next != null) {
            this.next.replace(newNext);
        }

        setNext(newNext);
    }

    public void replacePrevious(Stage newPrevious) {
        if(this.previous != null) {
            this.previous.replace(newPrevious);
        }

        setPrevious(newPrevious);
    }

    public void clearNext() {
        if(this.next != null) {
            this.next.clearPrevious();
        }

        this.next = null;
    }

    public void clearPrevious() {
        if(this.previous != null) {
            this.previous.clearNext();
        }

        this.previous = null;
    }

    public void replace(Stage stage) {
        if(this.previous != null) {
            this.previous.setNext(stage);
        }
        if(this.next != null) {
            this.next.setPrevious(stage);
        }
    }

    public void insertNext(Stage newNext) {
        if(this.next != null) {
            this.next.setPrevious(newNext);
        }

        setNext(newNext);
    }

    public void insertPrevious(Stage newPrevious) {
        if(this.previous != null) {
            this.previous.setNext(newPrevious);
        }

        setPrevious(newPrevious);
    }

    static {
        SETUP.setNext(IN_LOBBY);

        IN_LOBBY.setPrevious(SETUP);
        IN_LOBBY.setNext(STARTING);

        STARTING.setPrevious(IN_LOBBY);
        STARTING.setNext(PRE_GAME);

        PRE_GAME.setPrevious(STARTING);
        PRE_GAME.setNext(IN_GAME);

        IN_GAME.setPrevious(PRE_GAME);
        IN_GAME.setNext(POST_GAME);

        POST_GAME.setPrevious(IN_GAME);
    }
}