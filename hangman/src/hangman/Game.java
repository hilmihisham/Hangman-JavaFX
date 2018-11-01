package hangman;

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.List;

public class Game {

	private String answer;
	private String tmpAnswer;
	private String[] letterAndPosArray;
	private String[] words = {"apple", "pineapple", "christmas", "hangman", "batman", "intellij", "hydrogen",
			"connecticut"};
	private String hint = "";
	private int moves;
	private int index;
	private boolean newGame;
	private String lastLetter;
	private final ReadOnlyObjectWrapper<GameStatus> gameStatus;
	private ObjectProperty<Boolean> gameState = new ReadOnlyObjectWrapper<Boolean>();
	private StringProperty missed = new SimpleStringProperty();
	private StringProperty target = new SimpleStringProperty();
	private StringProperty movesLeft = new SimpleStringProperty();

	public enum GameStatus {
		GAME_OVER {
			@Override
			public String toString() {
				return "Game over!";
			}
		},
		BAD_GUESS {
			@Override
			public String toString() { return "Bad guess..."; }
		},
		GOOD_GUESS {
			@Override
			public String toString() {
				return "Good guess!";
			}
		},
		WON {
			@Override
			public String toString() {
				return "You won!";
			}
		},
		OPEN {
			@Override
			public String toString() {
				return "Game on, let's go!";
			}
		}
	}

	public Game() {
		gameStatus = new ReadOnlyObjectWrapper<GameStatus>(this, "gameStatus", GameStatus.OPEN);
		gameStatus.addListener(new ChangeListener<GameStatus>() {
			@Override
			public void changed(ObservableValue<? extends GameStatus> observable,
								GameStatus oldValue, GameStatus newValue) {
				if (gameStatus.get() != GameStatus.OPEN) {
					log("in Game: in changed");
					//currentPlayer.set(null);
				}
			}

		});

		reset();
	}

	private void createGameStatusBinding() {
		List<Observable> allObservableThings = new ArrayList<>();
		ObjectBinding<GameStatus> gameStatusBinding = new ObjectBinding<GameStatus>() {
			{
				super.bind(gameState);
			}
			@Override
			public GameStatus computeValue() {
				log("in computeValue");
				GameStatus check = checkForWinner(index);
				if(check != null ) {
					return check;
				}

				//if(tmpAnswer.trim().length() == 0){
                if(newGame){
					log("new game");
					newGame = false;
					return GameStatus.OPEN;
				}
				else if (index != -1){
					log("good guess");
					return GameStatus.GOOD_GUESS;
				}
				else {
					moves++;
					log("bad guess");
                    missed.set(missed.get() + lastLetter);
                    movesLeft.set("You have " + (numOfTries()-moves) + " bad guesses left.");
					return GameStatus.BAD_GUESS;
					//printHangman();
				}
			}
		};
		gameStatus.bind(gameStatusBinding);
	}

	public ReadOnlyObjectProperty<GameStatus> gameStatusProperty() {
		return gameStatus.getReadOnlyProperty();
	}
	public GameStatus getGameStatus() {
		return gameStatus.get();
	}

	public StringProperty getMissed(){
	    return missed;
    }

    public StringProperty getTarget(){
	    return target;
    }

    public StringProperty getMovesLeft(){
	    return movesLeft;
    }

	private void setRandomWord() {
		int idx = (int) (Math.random() * words.length);
		answer = words[idx].trim(); // remove new line character
	}

	private void prepTmpAnswer() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < answer.length(); i++) {
			sb.append(" ");
		}
		tmpAnswer = sb.toString();
	}

	private void prepLetterAndPosArray() {
		letterAndPosArray = new String[answer.length()];
		for(int i = 0; i < answer.length(); i++) {
			letterAndPosArray[i] = answer.substring(i,i+1);
		}
	}

	private int getValidIndex(String input) {
		int index = -1;
		for(int i = 0; i < letterAndPosArray.length; i++) {
			if(letterAndPosArray[i].equals(input)) {
				index = i;
				letterAndPosArray[i] = "";
				break;
			}
		}
		return index;
	}

	private int update(String input) {
		int index = getValidIndex(input);
		if(index != -1) {
			StringBuilder sb = new StringBuilder(tmpAnswer);
			sb.setCharAt(index, input.charAt(0));
			tmpAnswer = sb.toString();

			sb = new StringBuilder(target.get());
			int idx = 8 + (index * 2);
			sb.setCharAt(idx, input.charAt(0));
			target.set(sb.toString());
    }
		return index;
	}

	private static void drawHangmanFrame() {}

	public void makeMove(String letter) {
		log("\nin makeMove: " + letter);
		index = update(letter);
		lastLetter = letter;
		// this will toggle the state of the game
		gameState.setValue(!gameState.getValue());
	}

	public void reset() {
        setRandomWord();
        prepTmpAnswer();
        prepLetterAndPosArray();
        missed.set("Missed Letters: ");
        prepTargetField();
        movesLeft.set("You have " + numOfTries() + " bad guesses left.");
        moves = 0;
        newGame = true;
        gameState.setValue(false); // initial state
        createGameStatusBinding();
    }

    private void prepTargetField(){
        target.set("Target: ");
        for(int i=0; i<answer.length(); ++i){
            target.set(target.get() + "_ ");
        }
    }

	private int numOfTries() {
		return 6; // the length of the answer plus one free letter for a mistake
	}

	public static void log(String s) {
		System.out.println(s);
	}

	private GameStatus checkForWinner(int status) {
		log("in checkForWinner");
		if(tmpAnswer.equals(answer)) {
			log("won");
			return GameStatus.WON;
		}
		else if(moves == numOfTries()) {
			log("game over");
			if(!target.get().contains("Answer: ")) {
                target.set(target.get() + "  Answer: " + answer);
            }
			return GameStatus.GAME_OVER;
		}
		else {
			return null;
		}
	}

	public void setHint()
	{
		switch (answer)
		{
			case "apple":
				hint = "Found by Steve Jobs!";
				break;
			case "Pineapple":
				hint = "Fruit that goes with pizza!";
				break;
			case "christmas":
				hint = "A religious holiday!";
				break;
			case "hangman":
				hint = "What are you playing!";
				break;
			case "batman":
				hint = "Night super hero!";
				break;
			case "intellij":
				hint = "The Professor's favorite IDE!";
				break;
			case "hydrogen":
				hint = "The first element of the periodic table!";
				break;
			case "connecticut":
				hint = "The fifth state of the United States!";
				break;
		}
		System.out.println(hint);
	}
}
