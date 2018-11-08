package hangman;

import java.awt.BasicStroke;
import java.io.IOException;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class GameController {

	private final ExecutorService executorService;
	private final Game game;	
	
	public GameController(Game game) {
		this.game = game;
		executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return thread;
			}
		});
	}

	@FXML
	private VBox board ;
	@FXML
	private Label targetLabel;
	@FXML
    private Label missedLabel;
	@FXML
	private Label statusLabel ;
	@FXML
    private Label movesLabel;
	@FXML
	private Label enterALetterLabel ;
	@FXML
	private Label Hint;
	@FXML
	private TextField textField ;

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

	private static final int SPINE_START_X = 100;
	private static final int SPINE_START_Y = 20;
	private static final int SPINE_END_X = SPINE_START_X;
	private static final int SPINE_END_Y = SPINE_START_Y + 50;

	private void drawHangman() {

		Circle head = new Circle(15);
		head.setTranslateX(SPINE_START_X - 100);
		head.setTranslateY(SPINE_START_Y - 90);

		Line spine = new Line();
		spine.setStartX(SPINE_START_X);
		spine.setStartY(SPINE_START_Y);
		spine.setEndX(SPINE_END_X);
		spine.setEndY(SPINE_END_Y);

		Line leftArm = new Line();
		leftArm.setStartX(SPINE_START_X);
		leftArm.setStartY(SPINE_START_Y);
		leftArm.setEndX(SPINE_START_X + 40);
		leftArm.setEndY(SPINE_START_Y + 10);
		leftArm.setTranslateX(SPINE_START_X - 120);
		leftArm.setTranslateY(SPINE_START_Y - 90);

		Line rightArm = new Line();
		rightArm.setStartX(SPINE_START_X);
		rightArm.setStartY(SPINE_START_Y);
		rightArm.setEndX(SPINE_START_X - 40);
		rightArm.setEndY(SPINE_START_Y + 10);
        rightArm.setTranslateX(SPINE_START_X - 80);
        rightArm.setTranslateY(SPINE_START_Y - 101);

		Line leftLeg = new Line();
		leftLeg.setStartX(SPINE_END_X);
		leftLeg.setStartY(SPINE_END_Y);
		leftLeg.setEndX(SPINE_END_X + 25);
		leftLeg.setEndY(SPINE_END_Y + 50);
        leftLeg.setTranslateX(SPINE_START_X - 87);
        leftLeg.setTranslateY(SPINE_START_Y - 75);

		Line rightLeg = new Line();
		rightLeg.setStartX(SPINE_END_X);
		rightLeg.setStartY(SPINE_END_Y);
		rightLeg.setEndX(SPINE_END_X - 25);
		rightLeg.setEndY(SPINE_END_Y + 50);
        rightLeg.setTranslateX(SPINE_START_X - 112);
        rightLeg.setTranslateY(SPINE_START_Y - 128);

        Line frameBase = new Line();
        frameBase.setStartX(0);
        frameBase.setEndX(150);
        frameBase.setStartY(0);
        frameBase.setEndY(0);
		frameBase.setTranslateX(-160);
		frameBase.setTranslateY(80);
		frameBase.setStrokeWidth(5);


		board.getChildren().add(spine);
        board.getChildren().add(head);
        board.getChildren().add(leftArm);
        board.getChildren().add(rightArm);
        board.getChildren().add(leftLeg);
        board.getChildren().add(rightLeg);
        board.getChildren().add(frameBase);
	}
		
	@FXML 
	private void newHangman() {
		game.reset();
	}

	@FXML
	private void hint()
	{
		game.setHint();
	}

	@FXML
	private void quit() {
		board.getScene().getWindow().hide();
	}

}