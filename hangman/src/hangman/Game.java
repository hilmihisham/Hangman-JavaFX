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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {

	private String answer;
	private String tmpAnswer;
	private String missedLetters;
	private String[] letterAndPosArray;
	private String[] words;
	private String hint = "";
	private int moves;
	private int index;
	private int tries = 7;
	private boolean gameOver;
	private boolean newGame;
	private String lastLetter;
	private final ReadOnlyObjectWrapper<GameStatus> gameStatus;
	private ObjectProperty<Boolean> gameState = new ReadOnlyObjectWrapper<Boolean>();
	private StringProperty missed = new SimpleStringProperty();
	private StringProperty Hint = new SimpleStringProperty();
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

		words = createWordBank();
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
					missedLetters += lastLetter;
                    missed.set("Missed Letters: " + missedLetters);
                    movesLeft.set("You have " + (numOfTries()-moves) + " bad guesses left.");
					check = checkForWinner(index);
					if(check != null ) {
						return check;
					}
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

    public StringProperty getHint(){return Hint;}


	private String[] createWordBank()
	{
		int ctr = 0;
		String[] wordsReadFromFile;

		try
		{
			Scanner scanner = new Scanner(new File("words.txt"));
			while (scanner.hasNext())
			{
				ctr++;
				scanner.next();
			}
			wordsReadFromFile = new String[ctr];

			Scanner sc = new Scanner(new File("words.txt"));
			for (int i = 0; i <= ctr; i++)
			{
				if(sc.hasNext())
				{
					wordsReadFromFile[i] = sc.next();
				}
			}
			return  wordsReadFromFile;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;

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

	private static void drawHangmanFrame()
	{

	}

	public void makeMove(String letter) {
		log("\nin makeMove: " + letter);
		if(gameOver)
		    return;

		letter = letter.toLowerCase();

		if(!letter.matches("[A-Za-z]"))
		    return;
		if(tmpAnswer.contains(letter) || missedLetters.contains(letter))
		    return;

		Hint.set("");
		index = update(letter);
		int temp;
		while((temp = update(letter)) != -1);
		lastLetter = letter;
		// this will toggle the state of the game
		gameState.setValue(!gameState.getValue());
	}

	public void reset() {
        setRandomWord();
        prepTmpAnswer();
        prepLetterAndPosArray();
        //Hint.set("Hint: ");
        Hint.set("");
        missed.set("Missed Letters: ");
        missedLetters = "";
        prepTargetField();
        movesLeft.set("You have " + numOfTries() + " bad guesses left.");
        moves = 0;
        newGame = true;
        gameOver = false;
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

		return tries; // the length of the answer plus one free letter for a mistake
	}

	public static void log(String s) {
		System.out.println(s);
	}

	private GameStatus checkForWinner(int status) {
		log("in checkForWinner");
		if(tmpAnswer.equals(answer)) {
			log("won");
			gameOver = true;
			return GameStatus.WON;
		}
		else if(moves == numOfTries()) {
			log("game over");
            target.set(target.get() + "  Answer: " + answer);
            gameOver = true;
			return GameStatus.GAME_OVER;
		}
		else {
		    log("checkForWinner = null");
			return null;
		}
	}

	public void setHint()
	{
	    if(gameOver)
	        return;

		if((numOfTries()-moves)==1) {
		    Hint.set("No More Hints!");
            return;
        }

        String hintLetter = "";
        for (int i = 0; i < answer.length(); i++) {
            hintLetter = String.valueOf(answer.charAt(i));
            if (!tmpAnswer.contains(hintLetter)) {
                break;
            }
        }

        if(Hint.get().length()==7 && Hint.get().charAt(6)==hintLetter.charAt(0))
            return;

        moves++;
        movesLeft.set("You have " + (numOfTries() - moves + " bad guesses left."));
        Hint.set("Hint: " + hintLetter);
	}

	public int getMoves() {
	    return moves;
    }
}
