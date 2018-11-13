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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
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

	private void drawHangman() {

		Circle head = new Circle(15);
		head.setTranslateX(0);
		head.setTranslateY(-100);

		Line spine = new Line(0,0,75,0);
		spine.setRotate(90);
		spine.setTranslateY(-65);

		Line leftArm = new Line(0,0,50,0);
		leftArm.setRotate(30);
		leftArm.setTranslateY(-100);
		leftArm.setTranslateX(-23);

		Line rightArm = new Line(0,0,50,0);
		rightArm.setRotate(-30);
		rightArm.setTranslateY(-100);
		rightArm.setTranslateX(23);

		Line leftLeg = new Line(0,0,70,0);
		leftLeg.setRotate(60);
		leftLeg.setTranslateY(1);
		leftLeg.setTranslateX(17);

		Line rightLeg = new Line(0,0,70,0);
		rightLeg.setRotate(-60);
		rightLeg.setTranslateY(0);
		rightLeg.setTranslateX(-17);

        Line frameBase = new Line();
        frameBase.setStartX(0);
        frameBase.setEndX(150);
        frameBase.setStartY(0);
        frameBase.setEndY(0);
		frameBase.setTranslateX(-160);
		frameBase.setTranslateY(80);
		frameBase.setTranslateY(180);
		frameBase.setStrokeWidth(5);

		Line framePole = new Line(0,0,350,0);
		framePole.setStrokeWidth(5);
		framePole.setRotate(90);
		framePole.setTranslateY(-100);
		framePole.setTranslateY(0);
		framePole.setTranslateX(-160);

		Line frameLine = new Line(0,0,160,0);
		frameLine.setStrokeWidth(5);
		frameLine.setTranslateY(-280);
		frameLine.setTranslateY(-180);
		frameLine.setTranslateX(-80);

		Line rope = new Line(0,0,28,0);
		rope.setStrokeWidth(3);
		rope.setRotate(90);
		rope.setTranslateY(-263);
		rope.setTranslateY(-167);
		rope.setStroke(Color.BROWN);


        board.getChildren().add(head);
		board.getChildren().add(spine);
        board.getChildren().add(leftArm);
        board.getChildren().add(rightArm);
        board.getChildren().add(leftLeg);
        board.getChildren().add(rightLeg);
        board.getChildren().add(frameBase);
		board.getChildren().add(framePole);
		board.getChildren().add(frameLine);
		board.getChildren().add(rope);
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