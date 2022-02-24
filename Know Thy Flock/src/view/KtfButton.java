package view;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class KtfButton extends StackPane {

  private Text buttonText;
  private Shape buttonBG;
  private Shape button;
  private Shape buttonBorder;
  private EventHandler<? super MouseEvent> onClick;

  private static final String fontFamily = "Verdana";

  public KtfButton(double x, double y, double width, double height, double borderWidth,
            String text,
            EventHandler<? super MouseEvent> onClick) {
    super();
    this.setAlignment(Pos.CENTER);
    buttonBG = new Rectangle(width, height);
    button = new Rectangle(width, height);
    button.setOpacity(0.0);
    this.buttonText = new Text(text);
    this.resizeText(height * 0.6);
    buttonBorder = new Rectangle(width + 2 * borderWidth, height + 2 * borderWidth, Color.DARKSLATEGRAY);
    buttonBorder.setOpacity(0.0);
    this.onClick = onClick;

    this.getChildren().addAll(buttonBorder, buttonBG, buttonText, button);
    buttonText.toFront();
    button.toFront();

    this.setLayoutX(x - borderWidth);
    this.setLayoutY(y - borderWidth);

    this.setActive();
  }

  public void resizeText(double size) {
    buttonText.setFont(Font.font(fontFamily, FontWeight.BOLD, size));
  }

  public void setActive() {
    buttonBG.setFill(Color.DARKSLATEGRAY);
    buttonText.setFill(Color.WHITESMOKE);

    button.setOnMouseEntered(mouseEvent -> {
      buttonBG.setFill(Color.WHITESMOKE);
      buttonText.setFill(Color.DARKSLATEGRAY);
      buttonBorder.setOpacity(1.0);
    });

    button.setOnMouseExited(mouseEvent -> {
      buttonBG.setFill(Color.DARKSLATEGRAY);
      buttonText.setFill(Color.WHITESMOKE);
      buttonBorder.setOpacity(0.0);
    });

    button.setOnMouseClicked(onClick);
  }

  public void setInactive() {
    buttonBG.setFill(Color.GRAY);
    buttonText.setFill(Color.LIGHTGRAY);
    buttonBorder.setOpacity(0.0);

    button.setOnMouseEntered(mouseEvent -> {});

    button.setOnMouseExited(mouseEvent -> {});

    button.setOnMouseClicked(mouseEvent -> {});
  }

  public void setOnClick(EventHandler<? super MouseEvent> onClick) {
    this.onClick = onClick;
    button.setOnMouseClicked(onClick); //(shouldn't need this (pointers...))
  }

  public void setText(String newText) {
    this.buttonText.setText(newText);
  }
}
