package hangman;

import java.awt.BasicStroke;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public class GameController {

	private final ExecutorService executorService;
	private final Game game;	

    // declaring all the shapes and lines needed for hangman
    private ArrayList<Shape> gallows, body;

    @FXML
    private VBox board;
    @FXML
    private Label targetLabel;
    @FXML
    private Label missedLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label movesLabel;
    @FXML
    private Label enterALetterLabel;
    @FXML
    private Label Hint;
    @FXML
    private TextField textField;

	public GameController(Game game) {
		this.game = game;

		// initializing Shape lists
        gallows = new ArrayList<>();
        body = new ArrayList<>();

		executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return thread;
			}
		});
	}

    public void initialize() throws IOException {
		System.out.println("in initialize");
		drawHangman();
		addTextBoxListener();
		setUpStatusLabelBindings();
	}

	private void addTextBoxListener() {
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				if(newValue.length() > 0) {
					System.out.print(newValue);
					game.makeMove(newValue);
					hangmanVisible(game.getMoves());
					textField.clear();
				}
			}
		});
	}

	private void setUpStatusLabelBindings() {

		System.out.println("in setUpStatusLabelBindings");
		targetLabel.textProperty().bind(Bindings.format("%s",game.getTarget()));
        missedLabel.textProperty().bind(Bindings.format("%s",game.getMissed()));
		statusLabel.textProperty().bind(Bindings.format("%s", game.gameStatusProperty()));
        movesLabel.textProperty().bind(Bindings.format("%s",game.getMovesLeft()));
		enterALetterLabel.textProperty().bind(Bindings.format("%s", "Enter a letter:"));
		Hint.textProperty().bind(Bindings.format("%s", game.getHint()));
		/*	Bindings.when(
					game.currentPlayerProperty().isNotNull()
			).then(
				Bindings.format("To play: %s", game.currentPlayerProperty())
			).otherwise(
				""
			)
		);
		*/
	}

	private void drawHangman() {

	    // initializing and drawing all lines and shapes needed here

       // ----- gallow components -----

        Shape frameBase = new Line(0, 0, 150, 0);
        frameBase.setTranslateX(-160);
        frameBase.setTranslateY(240);
        frameBase.setStrokeWidth(10);
        frameBase.setVisible(false); // set visibility to false
        gallows.add(frameBase); // adding to gallows list

        Shape framePole = new Line(0,0,320,0);
        framePole.setRotate(90);
        framePole.setTranslateX(-160);
        framePole.setTranslateY(70);
        framePole.setStrokeWidth(10);
        framePole.setVisible(false);
        gallows.add(framePole);

        Shape frameLine = new Line(0,0,160,0);
        frameLine.setTranslateX(-80);
        frameLine.setTranslateY(-100);
        frameLine.setStrokeWidth(10);
        frameLine.setVisible(false);
        gallows.add(frameLine);

        Shape rope = new Line(0,0,60,0);
        rope.setRotate(90);
        rope.setTranslateY(-70);
        rope.setStrokeWidth(6);
        rope.setStroke(Color.BROWN);
        rope.setVisible(false);
        gallows.add(rope);

        // ----- hangman components -----

        Shape head = new Circle(25);
        head.setTranslateX(0);
        head.setTranslateY(-40);
        head.setVisible(false);
        body.add(head);

        Shape spine = new Line(0,0,70,0);
        spine.setRotate(90);
        spine.setTranslateY(-10);
        spine.setStrokeWidth(12);
        spine.setVisible(false);
        body.add(spine);

        Shape leftArm = new Line(0,0,50,0);
        leftArm.setRotate(-30);
        leftArm.setTranslateY(-35);
        leftArm.setTranslateX(-26);
        leftArm.setStrokeWidth(5);
        leftArm.setVisible(false);
        body.add(leftArm);

        Shape rightArm = new Line(0,0,50,0);
        rightArm.setRotate(30);
        rightArm.setTranslateY(-40);
        rightArm.setTranslateX(26);
        rightArm.setStrokeWidth(5);
        rightArm.setVisible(false);
        body.add(rightArm);

        Shape leftLeg = new Line(0,0,70,0);
        leftLeg.setRotate(-60);
        leftLeg.setTranslateY(40);
        leftLeg.setTranslateX(-23);
        leftLeg.setStrokeWidth(6);
        leftLeg.setVisible(false);
        body.add(leftLeg);

        Shape rightLeg = new Line(0,0,70,0);
        rightLeg.setRotate(60);
        rightLeg.setTranslateY(33);
        rightLeg.setTranslateX(23);
        rightLeg.setStrokeWidth(6);
        rightLeg.setVisible(false);
        body.add(rightLeg);

        // adding all components to board
        board.getChildren().addAll(
                frameBase, framePole, frameLine, rope,
                head, spine, leftArm, rightArm, leftLeg, rightLeg
        );
	}

	private void hangmanVisible(int moves) {

	    // 1st wrong answer: gallows will be visible
        // 2nd to 7th (game over): hangman parts will be seen one-by-one
        // game over: hangman body turns red

	    switch (moves) {
            case 1:
                for (Shape gallow : gallows) {
                    gallow.setVisible(true);
                }
                break;
            case 2: case 3: case 4: case 5: case 6:
                body.get(moves-2).setVisible(true);
                break;
            case 7:
                body.get(0).setFill(Color.CRIMSON);
                for (Shape shape : body) {
                    shape.setStroke(Color.CRIMSON);
                }
                body.get(5).setVisible(true);
                break;
            default:
                break;
        }
    }
		
	@FXML 
	private void newHangman() {
		System.out.println("in newHangman");
	    game.reset();

	    // re-color hangman back to all black
        body.get(0).setFill(Color.BLACK);
        for (Shape shape : body) {
            shape.setStroke(Color.BLACK);
        }

	    // re-hides the hangman drawings
        for (Shape gallow : gallows) {
            gallow.setVisible(false);
        }
        for (Shape body : body) {
            body.setVisible(false);
        }
	}

	@FXML
	private void hint()
	{
		game.setHint();

        // will also draw hangman after every hint
        hangmanVisible(game.getMoves());

	}

	@FXML
	private void quit() {
		board.getScene().getWindow().hide();
	}

}