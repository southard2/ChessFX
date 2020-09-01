/**
 * Title: Chess 
 * Files: Main.java, Tile.java 
 * Start Date: 7/17/2020 
 * End Date: - 
 * Author: Danny Southard
 * 
 * Note - I completed this project mostly on my own, but did use google to resolve issues I had in
 * figuring out some of JavaFX's mechanics. I did not copy any code from the internet, a part from
 * tiny bits such as the color red '#ff0000' used in the tryAgain label that I could not figure out
 * otherwise. I do not intend to make any money off of this project, this is/has been a passion
 * project and a way for me to challenge myself.
 */
package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Main - GUI showing the playable chess board. Contains back end tile array as well.
 *
 * @author Danny Southard
 */
public class Main extends Application {
  private List<String> args;

  // represents no button (tile) having been selected
  public static final int[] NONE = new int[] {-1, -1};
  //used to track the last button pressed
  private int[] pressed = NONE;
  
  // variables needed in multiple methods
  private GridPane board;
  private Tile[][] tiles;
  private boolean whiteToPlay = true;

  // creates int values for the position of both kings used in checking for checks/mates
  private int wKingI = 7;
  private int wKingJ = 4;
  private int bKingI = 0;
  private int bKingJ = 4;
  
  // creates boolean values to check if castling is available
  private boolean wZeroRookMoved = false;
  private boolean wSevenRookMoved = false;
  private boolean bZeroRookMoved = false;
  private boolean bSevenRookMoved = false;
  private boolean wKingMoved = false;
  private boolean bKingMoved = false;
  private boolean castling = false;
  
  // creates an int[] to store the last move
  private int[] last = new int[5];
  
  // creates a boolean value to alert the move method of an en passant move
  private boolean passanting = false;

  // general properties to be used in making the gui
  public static final int WINDOW_HEIGHT = 720;
  public static final int WINDOW_WIDTH = 1150;
  public static final String APP_TITLE = "Chess";

  // labels to allow for dynamic words in the gui
  Label toPlay = new Label("White to play.");
  Label tryAgain = new Label("");
  Label check = new Label("");
  
  // used to more easily interpret the int that correspond to each piece
  public final int P = 0;
  public final int N = 1;
  public final int B = 2;
  public final int R = 3;
  public final int Q = 4;
  public final int K = 5;

  public void start(Stage arg0) throws Exception {
    // saves command-line arguments
    args = this.getParameters().getRaw();

    // initializes a gridpane to display a chessboard
    board = new GridPane();
    board.setMaxSize(8, 8);
    board.setMaxHeight(600);
    board.setMaxWidth(600);
    
    Button reset = new Button();
    reset.setGraphic(new Label("RESET"));
    reset.setOnAction(e -> reset(reset, arg0));
    
    // sets up a VBox containing alerts for checks/mates, who is to play, and error messages
    VBox text = new VBox(toPlay, tryAgain, check, reset);
    text.setSpacing(10);
    tryAgain.setTextFill(Color.web("#ff0000")); // sets the color of the 
    
    // sets up a HBox with the board and text alerts, and makes a scene with that HBox
    HBox gui = new HBox(board, text);
    Scene mainScene = new Scene(gui, WINDOW_WIDTH, WINDOW_HEIGHT);

    // initializes a 2D array of tiles to represent the chess board
    tiles = new Tile[8][8];

    // sets up the board display (essentially the 'front-end' of the chess game)
    setupBoard();
    // sets up the tiles (essentially the 'back-end' of the chess game)
    setupTiles();

    arg0.setTitle(APP_TITLE);
    arg0.setScene(mainScene);
    arg0.show();

  }
  
  /**
   * Resets the game
   */
  private void reset(Button reset, Stage arg0) {
    // sets up the playable display as if at the start
    board = new GridPane();
    board.setMaxSize(8, 8);
    board.setMaxHeight(600);
    board.setMaxWidth(600);
    try {
      setupBoard();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    setupTiles();
    
    // sets up a VBox containing alerts for checks/mates, who is to play, and error messages
    VBox text = new VBox(toPlay, tryAgain, check, reset);
    text.setSpacing(10);
    tryAgain.setTextFill(Color.web("#ff0000")); // sets the color of the 
    
    // sets up a HBox with the board and text alerts, and makes a scene with that HBox
    HBox gui = new HBox(board, text);
    Scene mainScene = new Scene(gui, WINDOW_WIDTH, WINDOW_HEIGHT);

    arg0.setTitle(APP_TITLE);
    arg0.setScene(mainScene);
    arg0.show();
    
    // resets all values/textboxes/buttons
    toPlay.setText("White to play.");
    tryAgain.setText("");
    check.setText("");
    
    wKingI = 7;
    wKingJ = 4;
    bKingI = 0;
    bKingJ = 4;
    
    wZeroRookMoved = false;
    wSevenRookMoved = false;
    bZeroRookMoved = false;
    bSevenRookMoved = false;
    wKingMoved = false;
    bKingMoved = false;
    castling = false;
    
    whiteToPlay = true;
  }

  /**
   * Sets up the board display
   * 
   * @param board - the GridPane that holds the buttons displaying tiles on the board
   * @throws FileNotFoundException - if image files cannot be found
   */
  private void setupBoard() throws FileNotFoundException {

    // images used in displaying the chess board
    FileInputStream image = new FileInputStream("./images/DarkTile.png");
    Image DarkTile = new Image(image);
    FileInputStream image1 = new FileInputStream("./images/LightTile.png");
    Image LightTile = new Image(image1);
    FileInputStream image2 = new FileInputStream("./images/DwP.png");
    Image DWP = new Image(image2);
    FileInputStream image3 = new FileInputStream("./images/LWP.png");
    Image LWP = new Image(image3);
    FileInputStream image20 = new FileInputStream("./images/DwB.png");
    Image DWB = new Image(image20);
    FileInputStream image30 = new FileInputStream("./images/LWB.png");
    Image LWB = new Image(image30);
    FileInputStream image21 = new FileInputStream("./images/DwN.png");
    Image DWN = new Image(image21);
    FileInputStream image31 = new FileInputStream("./images/LWN.png");
    Image LWN = new Image(image31);
    FileInputStream image22 = new FileInputStream("./images/DwR.png");
    Image DWR = new Image(image22);
    FileInputStream image32 = new FileInputStream("./images/LWR.png");
    Image LWR = new Image(image32);
    FileInputStream image23 = new FileInputStream("./images/DwQ.png");
    Image DWQ = new Image(image23);
    FileInputStream image33 = new FileInputStream("./images/LWQ.png");
    Image LWQ = new Image(image33);
    FileInputStream image24 = new FileInputStream("./images/DwK.png");
    Image DWK = new Image(image24);
    FileInputStream image34 = new FileInputStream("./images/LWK.png");
    Image LWK = new Image(image34);
    FileInputStream image25 = new FileInputStream("./images/DBP.png");
    Image DBP = new Image(image25);
    FileInputStream image35 = new FileInputStream("./images/LBP.png");
    Image LBP = new Image(image35);
    FileInputStream image26 = new FileInputStream("./images/DBB.png");
    Image DBB = new Image(image26);
    FileInputStream image36 = new FileInputStream("./images/LBB.png");
    Image LBB = new Image(image36);
    FileInputStream image27 = new FileInputStream("./images/DBN.png");
    Image DBN = new Image(image27);
    FileInputStream image37 = new FileInputStream("./images/LBN.png");
    Image LBN = new Image(image37);
    FileInputStream image28 = new FileInputStream("./images/DBR.png");
    Image DBR = new Image(image28);
    FileInputStream image38 = new FileInputStream("./images/LBR.png");
    Image LBR = new Image(image38);
    FileInputStream image29 = new FileInputStream("./images/DBQ.png");
    Image DBQ = new Image(image29);
    FileInputStream image39 = new FileInputStream("./images/LBQ.png");
    Image LBQ = new Image(image39);
    FileInputStream image210 = new FileInputStream("./images/DBK.png");
    Image DBK = new Image(image210);
    FileInputStream image310 = new FileInputStream("./images/LBK.png");
    Image LBK = new Image(image310);
    
    // This puts the empty center of the board into the gridpane
    for (int i = 0; i < 8; i++) {
      for (int j = 2; j < 6; j++) {
        ImageView light = new ImageView(LightTile);
        ImageView dark = new ImageView(DarkTile);
        light.setFitHeight(80);
        light.setFitWidth(80);
        dark.setFitHeight(80);
        dark.setFitWidth(80);
        if (i % 2 == 0 && j % 2 == 0) {
          Button btn = new Button();
          btn.setGraphic(light);
          btn.setMaxHeight(80);
          btn.setMaxWidth(80);
          // done to avoid an error
          int I = i;
          int J = j;
          // sets up the button to be functional
          btn.setOnAction(e -> buttonPressed(J, I));
          board.add(btn, i, j);
        } else if (i % 2 == 1 && j % 2 == 0) {
          Button btn = new Button();
          btn.setMaxHeight(80);
          btn.setMaxWidth(80);
          btn.setGraphic(dark);
          // done to avoid an error
          int I = i;
          int J = j;
          // sets up the button to be functional
          btn.setOnAction(e -> buttonPressed(J, I));
          board.add(btn, i, j);
        } else if (i % 2 == 0 && j % 2 == 1) {
          Button btn = new Button();
          btn.setMaxHeight(80);
          btn.setMaxWidth(80);
          btn.setGraphic(dark);
          // done to avoid an error
          int I = i;
          int J = j;
          // sets up the button to be functional
          btn.setOnAction(e -> buttonPressed(J, I));
          board.add(btn, i, j);
        } else {
          Button btn = new Button();
          btn.setMaxHeight(80);
          btn.setMaxWidth(80);
          btn.setGraphic(light);
          // done to avoid an error
          int I = i;
          int J = j;
          // sets up the button to be functional
          btn.setOnAction(e -> buttonPressed(J, I));
          board.add(btn, i, j);
        }
      }
    }

    // NOTE: I switched around i and j a bunch in the next sections by mistake, but it doesn't lead
    // to any functional differences

    // this sets up the white side of the board using two for loops to display each tile in the 1
    // and 2 ranks of the chess board
    for (int i = 6; i < 8; i++) {
      for (int j = 0; j < 8; j++) {

        // if i is 6 (which represents the 2 rank on a board in the grid pane), a pawn will be
        // placed
        if (i == 6) {
          Button btn = new Button();
          btn.setMaxHeight(80);
          btn.setMaxWidth(80);
          // done to avoid an error
          int I = i;
          int J = j;
          // sets up the button to be functional
          btn.setOnAction(e -> buttonPressed(I, J));

          // if the tile is meant to be light, have the button display a white pawn on a light tile
          if (j % 2 == 0) {
            ImageView lwp = new ImageView(LWP);
            lwp.setFitHeight(80);
            lwp.setFitWidth(80);
            btn.setGraphic(lwp);
            board.add(btn, j, i);
          }
          // else, have the button display a white pawn on a dark tile
          else {
            ImageView dwp = new ImageView(DWP);
            dwp.setFitHeight(80);
            dwp.setFitWidth(80);
            btn.setGraphic(dwp);
            board.add(btn, j, i);
          }
        }

        // if i is 7 (which represents the 1 rank on a chess board), you must display a piece that
        // corresponds to the file it is found in
        else {
          Button btn = new Button();
          btn.setMaxHeight(80);
          btn.setMaxWidth(80);
          // done to avoid an error
          int I = i;
          int J = j;
          // sets up the button to be functional
          btn.setOnAction(e -> buttonPressed(I, J));

          // if the tile is on the 'a' file, display a white rook on a dark tile
          if (j == 0) {
            ImageView dwr = new ImageView(DWR);
            dwr.setFitHeight(80);
            dwr.setFitWidth(80);
            btn.setGraphic(dwr);
            board.add(btn, j, i);
          }
          // if the tile is on the 'b' file, display a white knight on a light tile
          if (j == 1) {
            ImageView lwn = new ImageView(LWN);
            lwn.setFitHeight(80);
            lwn.setFitWidth(80);
            btn.setGraphic(lwn);
            board.add(btn, j, i);
          }
          // if the tile is on the 'c' file, display a white bishop on a dark tile
          if (j == 2) {
            ImageView dwb = new ImageView(DWB);
            dwb.setFitHeight(80);
            dwb.setFitWidth(80);
            btn.setGraphic(dwb);
            board.add(btn, j, i);
          }
          // if the tile is on the 'd' file, display a white queen on a light tile
          if (j == 3) {
            ImageView lwq = new ImageView(LWQ);
            lwq.setFitHeight(80);
            lwq.setFitWidth(80);
            btn.setGraphic(lwq);
            board.add(btn, j, i);
          }
          // if the tile is on the 'e' file, display a white king on a dark tile
          if (j == 4) {
            ImageView dwk = new ImageView(DWK);
            dwk.setFitHeight(80);
            dwk.setFitWidth(80);
            btn.setGraphic(dwk);
            board.add(btn, j, i);
          }
          // if the tile is on the 'f' file, display a white bishop on a light tile
          if (j == 5) {
            ImageView lwb = new ImageView(LWB);
            lwb.setFitHeight(80);
            lwb.setFitWidth(80);
            btn.setGraphic(lwb);
            board.add(btn, j, i);
          }
          // if the tile is on the 'g' file, display a white knight on a dark tile
          if (j == 6) {
            ImageView dwn = new ImageView(DWN);
            dwn.setFitHeight(80);
            dwn.setFitWidth(80);
            btn.setGraphic(dwn);
            board.add(btn, j, i);
          }
          // if the tile is on the 'h' file, display a white rook on a light tile
          if (j == 7) {
            ImageView lwr = new ImageView(LWR);
            lwr.setFitHeight(80);
            lwr.setFitWidth(80);
            btn.setGraphic(lwr);
            board.add(btn, j, i);
          }
        }
      }
    }

    // this sets up the black side of the board using two for loops to display each tile in the 7
    // and 8 ranks of the chess board
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 8; j++) {

        // if i is 1 (which represents the 7 rank on a board in the grid pane), a pawn will be
        // placed
        if (i == 1) {
          Button btn = new Button();
          btn.setMaxHeight(80);
          btn.setMaxWidth(80);
          // done to avoid an error
          int I = i;
          int J = j;
          // sets up the button to be functional
          btn.setOnAction(e -> buttonPressed(I, J));

          // if the tile is meant to be dark, have the button display a black pawn on a dark tile
          if (j % 2 == 0) {
            ImageView dbp = new ImageView(DBP);
            dbp.setFitHeight(80);
            dbp.setFitWidth(80);
            btn.setGraphic(dbp);
            board.add(btn, j, i);
          }
          // else, have the button display a black pawn on a light tile
          else {
            ImageView lbp = new ImageView(LBP);
            lbp.setFitHeight(80);
            lbp.setFitWidth(80);
            btn.setGraphic(lbp);
            board.add(btn, j, i);
          }
        }

        // if i is 0 (which represents the 8 rank on a chess board), you must display a piece that
        // corresponds to the file it is found in
        else {
          Button btn = new Button();
          btn.setMaxHeight(80);
          btn.setMaxWidth(80);
          // done to avoid an error
          int I = i;
          int J = j;
          // sets up the button to be functional
          btn.setOnAction(e -> buttonPressed(I, J));

          // if the tile is on the 'a' file, display a black rook on a light tile
          if (j == 0) {
            ImageView lbr = new ImageView(LBR);
            lbr.setFitHeight(80);
            lbr.setFitWidth(80);
            btn.setGraphic(lbr);
            board.add(btn, j, i);
          }
          // if the tile is on the 'b' file, display a black knight on a dark tile
          if (j == 1) {
            ImageView dbn = new ImageView(DBN);
            dbn.setFitHeight(80);
            dbn.setFitWidth(80);
            btn.setGraphic(dbn);
            board.add(btn, j, i);
          }
          // if the tile is on the 'c' file, display a black bishop on a light tile
          if (j == 2) {
            ImageView lbb = new ImageView(LBB);
            lbb.setFitHeight(80);
            lbb.setFitWidth(80);
            btn.setGraphic(lbb);
            board.add(btn, j, i);
          }
          // if the tile is on the 'd' file, display a black queen on a dark tile
          if (j == 3) {
            ImageView dbq = new ImageView(DBQ);
            dbq.setFitHeight(80);
            dbq.setFitWidth(80);
            btn.setGraphic(dbq);
            board.add(btn, j, i);
          }
          // if the tile is on the 'e' file, display a black king on a light tile
          if (j == 4) {
            ImageView lbk = new ImageView(LBK);
            lbk.setFitHeight(80);
            lbk.setFitWidth(80);
            btn.setGraphic(lbk);
            board.add(btn, j, i);
          }
          // if the tile is on the 'f' file, display a black bishop on a dark tile
          if (j == 5) {
            ImageView dbb = new ImageView(DBB);
            dbb.setFitHeight(80);
            dbb.setFitWidth(80);
            btn.setGraphic(dbb);
            board.add(btn, j, i);
          }
          // if the tile is on the 'g' file, display a black knight on a light tile
          if (j == 6) {
            ImageView lbn = new ImageView(LBN);
            lbn.setFitHeight(80);
            lbn.setFitWidth(80);
            btn.setGraphic(lbn);
            board.add(btn, j, i);
          }
          // if the tile is on the 'h' file, display a black rook on a dark tile
          if (j == 7) {
            ImageView dbr = new ImageView(DBR);
            dbr.setFitHeight(80);
            dbr.setFitWidth(80);
            btn.setGraphic(dbr);
            board.add(btn, j, i);
          }
        }
      }
    }
  }

  /**
   * Sets up the 2D array of tiles with the correct set of tiles to start off the game
   * 
   * The array of tiles corresponds directly to the gridpane of buttons displayed in the UI
   * 
   * @param tiles - the 2D array of Tile objects to be filled
   */
  private void setupTiles() {
    for (int i = 0; i < 8; i++) { 
      for (int j = 0; j < 8; j++) { 
        // for ranks 3 - 6 (1<j<6), set up the tiles such that they don't have pieces on them
        if (j > 1 && j < 6) {
          // creates light tiles and places them into the tile array
          if ((i % 2 == 0 && j % 2 == 1) || (i % 2 == 1 && j % 2 == 0)) {
            Tile tile = new Tile(0, 0, 0);
            tiles[j][i] = tile;
          }
          // creates dark tiles and places them into the tile array
          else {
            Tile tile = new Tile(1, 0, 0);
            tiles[j][i] = tile;
          }
        }

        // for the 7th rank (j == 1), sets up alternating light/dark tiles with black pawns
        if (j == 1) {
          if (j % 2 == 0) {
            Tile tile = new Tile(1, 2, P);
            tiles[j][i] = tile;
          } else {
            Tile tile = new Tile(0, 2, P);
            tiles[j][i] = tile;
          }
        }
        // for the 2nd rank (j == 6), sets up alternating light/dark tiles with white pawns
        if (j == 6) {
          if (j % 2 == 0) {
            Tile tile = new Tile(0, 1, P);
            tiles[j][i] = tile;
          } else {
            Tile tile = new Tile(1, 1, P);
            tiles[j][i] = tile;
          }
        }

        // sets up black pieces on the 8th rank
        if (j == 0) {
          if (i == 0) {
            Tile tile = new Tile(0, 2, R);
            tiles[j][i] = tile;
          } else if (i == 1) {
            Tile tile = new Tile(1, 2, N);
            tiles[j][i] = tile;
          } else if (i == 2) {
            Tile tile = new Tile(0, 2, B);
            tiles[j][i] = tile;
          } else if (i == 3) {
            Tile tile = new Tile(1, 2, Q);
            tiles[j][i] = tile;
          } else if (i == 4) {
            Tile tile = new Tile(0, 2, K);
            tiles[j][i] = tile;
          } else if (i == 5) {
            Tile tile = new Tile(1, 2, B);
            tiles[j][i] = tile;
          } else if (i == 6) {
            Tile tile = new Tile(0, 2, N);
            tiles[j][i] = tile;
          } else {
            Tile tile = new Tile(1, 2, R);
            tiles[j][i] = tile;
          }
        }

        // sets up white pieces on the 1st rank
        if (j == 7) {
          if (i == 0) {
            Tile tile = new Tile(1, 1, R);
            tiles[j][i] = tile;
          } else if (i == 1) {
            Tile tile = new Tile(0, 1, N);
            tiles[j][i] = tile;
          } else if (i == 2) {
            Tile tile = new Tile(1, 1, B);
            tiles[j][i] = tile;
          } else if (i == 3) {
            Tile tile = new Tile(0, 1, Q);
            tiles[j][i] = tile;
          } else if (i == 4) {
            Tile tile = new Tile(1, 1, K);
            tiles[j][i] = tile;
          } else if (i == 5) {
            Tile tile = new Tile(0, 1, B);
            tiles[j][i] = tile;
          } else if (i == 6) {
            Tile tile = new Tile(1, 1, N);
            tiles[j][i] = tile;
          } else {
            Tile tile = new Tile(0, 1, R);
            tiles[j][i] = tile;
          }
        }
      }
    }

  }

  /**
   * Deals with the pressing of buttons on the chess board
   * 
   * @param i - value representing the row of the button pushed
   * @param j - value representing the column of the button pushed
   */
  private void buttonPressed(int i, int j) {
    if (pressed == NONE) {
      // if there isn't a selected piece, and there isn't a piece on the selected button, nothing
      // happens
      if (tiles[i][j].getPieceColor() == 0) {
        tryAgain.setText("No piece found, try again!");
        return;
      }
      // if a black piece is selected on whites turn, or a white piece selected on blacks turn,
      // nothing happens
      else if (tiles[i][j].getPieceColor() == 2 && whiteToPlay) {
        tryAgain.setText("A white piece must be moved on white's turn.");
        return;
      } else if (tiles[i][j].getPieceColor() == 1 && !whiteToPlay) {
        tryAgain.setText("A black piece must be moved on black's turn.");
        return;
      } else { // else if a 'correct' piece is selected, clear the error message and set the piece
               // as the selected one
        tryAgain.setText("");
        pressed = new int[] {i, j, tiles[i][j].getPieceColor(), tiles[i][j].getPiece()};
      }
    } else {
      // check if the move is legal (not including special moves
      if (isLegalMove(pressed, i, j)) {
        try {
          move(pressed[0], pressed[1], i, j);
          pressed = NONE;
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
        if (whiteToPlay) {
          whiteToPlay = false;
          toPlay.setText("Black to play");
          tryAgain.setText("");
          if (isInCheck(bKingI, bKingJ, 2, tiles)) {
            if (isCheckMate(2, tiles)) {
              check.setText("Checkmate! White wins!");
              toPlay.setText("");
            } else {
              check.setText("CHECK!");
            }
          } else {
            check.setText("");
          }
        } else {
          whiteToPlay = true;
          toPlay.setText("White to play.");
          tryAgain.setText("");
          if (isInCheck(wKingI, wKingJ, 1, tiles)) {
            if (isCheckMate(1, tiles)) {
              check.setText("Checkmate! Black wins!");
              toPlay.setText("");
            } else {
              check.setText("CHECK!");
            }
          } else {
            check.setText("");
          }
        }
      }
      else {
        pressed = NONE;
        tryAgain.setText(
            "Illegal move, try again!\n\n(You must select the piece you intend to move again)");
        return;
      }
    }
  }

  /**
   * Checks that a selected move is legal
   * 
   * @param piece - array showing the piece selected to be moved
   * @param i     - value representing the row of the button pushed
   * @param j     - value representing the column of the button pushed
   * @return
   */
  private boolean isLegalMove(int[] piece, int i, int j) {
    int fromI = piece[0];
    int fromJ = piece[1];
    int pieceType = piece[3];
    int pieceColor = piece[2];
    
    // initializes a list to be used to store all legal moves for a piece
    List<int[]> legalMoves = null;
    
    // gets the list of all legal moves dependant on piece type via helper methods
    if (pieceType == P) {
      legalMoves = getPawnMoves(fromI, fromJ, pieceColor, tiles);
      if (canEnPassant(j, pieceColor) && fromJ != j && tiles[i][j].getPieceColor() == 0) {
        passanting = true;
      } else {
        passanting = false;
      }
      castling = false;
    } else if (pieceType == N) {
      legalMoves = getKnightMoves(fromI, fromJ, pieceColor, tiles);
      castling = false;
      passanting = false;
    } else if (pieceType == B) {
      legalMoves = getBishopMoves(fromI, fromJ, pieceColor, tiles);
      castling = false;
      passanting = false;
    } else if (pieceType == R) {
      legalMoves = getRookMoves(fromI, fromJ, pieceColor, tiles);
      castling = false;
      passanting = false;
    } else if (pieceType == Q) {
      legalMoves = getQueenMoves(fromI, fromJ, pieceColor, tiles);
      castling = false;
      passanting = false;
    } else if (pieceType == K) {
      legalMoves = getKingMoves(fromI, fromJ, pieceColor, tiles);
      // sets castling to either true or false to help with move method
      if (fromJ == 4 && (j == 2 || j == 6)) {
        castling = true;
        if (j == 2) {
          // checks that a potential castle would not be through/away from check (which is illegal)
          if (isInCheck(i, j, pieceColor, tiles) || isInCheck(i, 3, pieceColor, tiles)
              || isInCheck(i, 4, pieceColor, tiles)) {
            return false;
          }
        } else if (j == 6) {
          if (isInCheck(i, j, pieceColor, tiles) || isInCheck(i, 5, pieceColor, tiles)
              || isInCheck(i, 4, pieceColor, tiles)) {
            return false;
          }
        }
      } else {
        castling = false;
        if (isInCheck(i, j, pieceColor, tiles)) {
          return false;
        }
      }
      passanting = false;
    }
    
    // returns false if the list of legal moves is null
    if (legalMoves == null) {
      return false;
    } else { // else returns true if the move is found in the list of legal moves, and false if not
             // found
      for (int[] move : legalMoves) {
        if (Arrays.equals(move, new int[] {i, j})) {
          // if the king is in check after the given move, return false
          if (pieceColor == 1) {
            if (!castling && wouldBeCheck(piece, move)) {
              return false;
            }
          } else if (pieceColor == 2) {
            if (!castling && wouldBeCheck(piece, move)) {
              return false;
            }
          }
          return true;
        }
      }
      return false;
    }
  }

  /**
   * Gets a list of all moves a selected pawn can make
   * 
   * @param i - value representing the row of the button pushed
   * @param j - value representing the column of the button pushed
   * @return - a list of int arrays, each representing a move
   */
  private List<int[]> getPawnMoves(int i, int j, int pieceColor, Tile[][] tiles) {
    // creates a list to store all moves the pawn can make
    List<int[]> moves = new ArrayList<int[]>();
    
    // pawns move in different directions depending on if they are white/black
    if (pieceColor == 1) {
      // if the tile in front of the pawn is empty, add that tile to the list
      if (tiles[i - 1][j].getPieceColor() == 0) { 
        moves.add(new int[] {i - 1, j});
        // if the pawn is at its starting i, and the tile two spaces in front of it is empty, add
        // that tile
        if (i == 6 && tiles[i - 2][j].getPieceColor() == 0) {
          moves.add(new int[] {i - 2, j});
        }
      }
      // if there is a black piece on either of the diagonals in front of the pawn, add the tile
      // that piece is on to the list
      if ((j - 1 > 0) && tiles[i - 1][j - 1].getPieceColor() == 2) {
        moves.add(new int[] {i - 1, j - 1});
      }
      if ((j + 1 < 8) && tiles[i - 1][j + 1].getPieceColor() == 2) {
        moves.add(new int[] {i - 1, j + 1});
      }
      if (i == 3) {
        if (j + 1 < 8 && canEnPassant(j + 1, 1)) {
          moves.add(new int[] {i - 1, j + 1});
          passanting = true;
        } 
        if (j - 1 >= 0 && canEnPassant(j - 1, 1)) {
          moves.add(new int[] {i - 1, j - 1});
          passanting = true;
        }
      }
    } else {
      // if the tile in front of the pawn is empty, add that tile to the list
      if (tiles[i + 1][j].getPieceColor() == 0) {
        moves.add(new int[] {i + 1, j});
        // if the pawn is at its starting i, and the tile two spaces in front of it is empty, add
        // that tile
        if (i == 1 && tiles[i + 2][j].getPieceColor() == 0) {
          moves.add(new int[] {i + 2, j});
        }
      }
      // if there is a white piece on either of the diagonals in front of the pawn, add the tile
      // that piece is on to the list
      if ((j - 1 > 0) && tiles[i + 1][j - 1].getPieceColor() == 1) {
        moves.add(new int[] {i + 1, j - 1});
      }
      if ((j + 1 < 8) && tiles[i + 1][j + 1].getPieceColor() == 1) {
        moves.add(new int[] {i + 1, j + 1});
      }
      if (i == 4) {
        if (j + 1 < 8 && canEnPassant(j + 1, 2)) {
          moves.add(new int[] {i + 1, j + 1});
        } 
        if (j - 1 >= 0 && canEnPassant(j - 1, 2)) {
          moves.add(new int[] {i + 1, j - 1});
        }
      }
    }
    return moves;
  }
  
  /**
   * Checks to see if an en passant move is available for a pawn
   * 
   * Note: it is assumed that the pawn is in the correct i (3 for w, 4 for b)
   * 
   * @param j - the column being checked for an en passant move
   * @param color - the color of the pawn checking for the move
   * @return true if the pawn can en passant to that j, else false
   */
  private boolean canEnPassant(int j, int color) {
    if (last[4] != P) {
      return false;
    }
    if (color == 1) {
      if (last[1] != j || last[3] != j) {
        return false;
      } else if (last[0] != 1 || last[2] != 3) {
        return false;
      } else {
        return true;
      }
    } else {
      if (last[1] != j || last[3] != j) {
        return false;
      } else if (last[0] != 6 || last[2] != 4) {
        return false;
      } else {
        return true;
      }
    }
  }

  /**
   * Gets a list of all moves a selected knight can make
   * 
   * @param i - value representing the row of the button pushed
   * @param j - value representing the column of the button pushed
   * @return - a list of int arrays, each representing a move
   */
  private List<int[]> getKnightMoves(int i, int j, int pieceColor, Tile[][] tiles) {
    // creates a list to store all moves the knight can make
    List<int[]> moves = new ArrayList<int[]>();
    
    
    // checks individually each of eight possible moves for the knight
    
    int i1 = i + 1;
    int j1 = j + 2;
    if (i1 > 7 || i1 < 0 || j1 > 7 || j1 < 0) {
      ;
    } else if (tiles[i1][j1].getPieceColor() == pieceColor) {
      ;
    } else {
      moves.add(new int[] {i1, j1});
    }
    
    i1 = i + 1;
    j1 = j - 2;
    if (i1 > 7 || i1 < 0 || j1 > 7 || j1 < 0) {
      ;
    } else if (tiles[i1][j1].getPieceColor() == pieceColor) {
      ;
    } else {
      moves.add(new int[] {i1, j1});
    }
    
    i1 = i + 2;
    j1 = j + 1;
    if (i1 > 7 || i1 < 0 || j1 > 7 || j1 < 0) {
      ;
    } else if (tiles[i1][j1].getPieceColor() == pieceColor) {
      ;
    } else {
      moves.add(new int[] {i1, j1});
    }
    
    i1 = i + 2;
    j1 = j - 1;
    if (i1 > 7 || i1 < 0 || j1 > 7 || j1 < 0) {
      ;
    } else if (tiles[i1][j1].getPieceColor() == pieceColor) {
      ;
    } else {
      moves.add(new int[] {i1, j1});
    }
    
    i1 = i - 1;
    j1 = j + 2;
    if (i1 > 7 || i1 < 0 || j1 > 7 || j1 < 0) {
      ;
    } else if (tiles[i1][j1].getPieceColor() == pieceColor) {
      ;
    } else {
      moves.add(new int[] {i1, j1});
    }
    
    i1 = i - 1;
    j1 = j - 2;
    if (i1 > 7 || i1 < 0 || j1 > 7 || j1 < 0) {
      ;
    } else if (tiles[i1][j1].getPieceColor() == pieceColor) {
      ;
    } else {
      moves.add(new int[] {i1, j1});
    }
    
    i1 = i - 2;
    j1 = j + 1;
    if (i1 > 7 || i1 < 0 || j1 > 7 || j1 < 0) {
      ;
    } else if (tiles[i1][j1].getPieceColor() == pieceColor) {
      ;
    } else {
      moves.add(new int[] {i1, j1});
    }
    
    i1 = i - 2;
    j1 = j - 1;
    if (i1 > 7 || i1 < 0 || j1 > 7 || j1 < 0) {
      ;
    } else if (tiles[i1][j1].getPieceColor() == pieceColor) {
      ;
    } else {
      moves.add(new int[] {i1, j1});
    }

    // after checking all eight possible moves, returns the list of all legal moves for the knight
    return moves;
  }

  /**
   * Gets a list of all moves a selected bishop can make
   * 
   * @param i - value representing the row of the button pushed
   * @param j - value representing the column of the button pushed
   * @return - a list of int arrays, each representing a move
   */
  private List<int[]> getBishopMoves(int i, int j, int pieceColor, Tile[][] tiles) {
    // creates a list used to store moves found for the bishop
    List<int[]> moves = new ArrayList<int[]>();
    
    // checks which moves can be made moving down and right across the board
    for (int x = i + 1, y = j + 1; x < 8 && y < 8; x++, y++) {
      int[] tile = new int[] {x, y};
      if (tiles[x][y].getPieceColor() == 0) {
        moves.add(tile);
      } else if (tiles[x][y].getPieceColor() == pieceColor) {
        break;
      } else {
        moves.add(tile);
        break;
      }
    }
    
    // checks which moves can be made moving down and left across the board
    for (int x = i + 1, y = j - 1; x < 8 && y >= 0; x++, y--) {
      int[] tile = new int[] {x, y};
      if (tiles[x][y].getPieceColor() == 0) {
        moves.add(tile);
      } else if (tiles[x][y].getPieceColor() == pieceColor) {
        break;
      } else {
        moves.add(tile);
        break;
      }
    }
    
    // checks which moves can be made moving up and right across the board
    for (int x = i - 1, y = j + 1; x >= 0 && y < 8; x--, y++) {
      int[] tile = new int[] {x, y};
      if (tiles[x][y].getPieceColor() == 0) {
        moves.add(tile);
      } else if (tiles[x][y].getPieceColor() == pieceColor) {
        break;
      } else {
        moves.add(tile);
        break;
      }
    }
    
    // checks which moves can be made moving up and left across the board
    for (int x = i - 1, y = j - 1; x >= 0 && y >= 0; x--, y--) {
      int[] tile = new int[] {x, y};
      if (tiles[x][y].getPieceColor() == 0) {
        moves.add(tile);
      } else if (tiles[x][y].getPieceColor() == pieceColor) {
        break;
      } else {
        moves.add(tile);
        break;
      }
    }
    // returns the list of moves
    return moves;
  }

  /**
   * Gets a list of all moves a selected rook can make
   * 
   * @param i - value representing the row of the button pushed
   * @param j - value representing the column of the button pushed
   * @return - a list of int arrays, each representing a move
   */
  private List<int[]> getRookMoves(int i, int j, int pieceColor, Tile[][] tiles) {
    // creates a list used to store moves found for the rook
    List<int[]> moves = new ArrayList<int[]>();
    
    // checks for moves made moving down on the board
    for (int x = i + 1; x < 8; x++) {
      int[] tile = new int[] {x, j};
      if (tiles[x][j].getPieceColor() == 0) {
        moves.add(tile);
      } else if (tiles[x][j].getPieceColor() == pieceColor) {
        break;
      } else {
        moves.add(tile);
        break;
      }
    }
    
    // checks for moves made moving up on the board
    for (int x = i - 1; x >= 0; x--) {
      int[] tile = new int[] {x, j};
      if (tiles[x][j].getPieceColor() == 0) {
        moves.add(tile);
      } else if (tiles[x][j].getPieceColor() == pieceColor) {
        break;
      } else {
        moves.add(tile);
        break;
      }
    }
    
    // checks for moves made moving right on the board
    for (int y = j + 1; y < 8; y++) {
      int[] tile = new int[] {i, y};
      if (tiles[i][y].getPieceColor() == 0) {
        moves.add(tile);
      } else if (tiles[i][y].getPieceColor() == pieceColor) {
        break;
      } else {
        moves.add(tile);
        break;
      }
    }
    
    // checks for moves made moving left on the board
    for (int y = j - 1; y >= 0; y--) {
      int[] tile = new int[] {i, y};
      if (tiles[i][y].getPieceColor() == 0) {
        moves.add(tile);
      } else if (tiles[i][y].getPieceColor() == pieceColor) {
        break;
      } else {
        moves.add(tile);
        break;
      }
    }
    
    return moves;
  }

  /**
   * Gets a list of all moves a selected queen can make
   * 
   * @param i - value representing the row of the button pushed
   * @param j - value representing the column of the button pushed
   * @return - a list of int arrays, each representing a move
   */
  private List<int[]> getQueenMoves(int i, int j, int pieceColor, Tile[][] tiles) {
    // creates a list to store all moves the queen can make
    List<int[]> moves = new ArrayList<int[]>();

    // adds all moves that can be done by a bishop, rook as queen can move both straight and
    // diagonally
    moves.addAll(getBishopMoves(i, j, pieceColor, tiles));
    moves.addAll(getRookMoves(i, j, pieceColor, tiles));
    
    return moves;
  }

  /**
   * Gets a list of all moves a selected king can make
   * 
   * @param i - value representing the row of the button pushed
   * @param j - value representing the column of the button pushed
   * @ @return - a list of int arrays, each representing a move
   */
  private List<int[]> getKingMoves(int i, int j, int pieceColor, Tile[][] tiles) {
    // creates a list to store all moves the queen can make
    List<int[]> moves = new ArrayList<int[]>();
    
    // the king can move one space in any direction (including diagonals)
    // checks each possible move, adds all eligible moves to the list
    if (i - 1 >= 0) {
      if (tiles[i - 1][j].getPieceColor() != pieceColor) {
        moves.add(new int[] {i - 1, j});
      }
    }
    
    if (i - 1 >= 0 && j - 1 >= 0) {
      if (tiles[i - 1][j - 1].getPieceColor() != pieceColor) {
        moves.add(new int[] {i - 1, j - 1});
      }
    }
    
    if (i - 1 >= 0 && j + 1 < 8) {
      if (tiles[i - 1][j + 1].getPieceColor() != pieceColor) {
        moves.add(new int[] {i - 1, j + 1});
      }
    }
    
    if (j + 1 < 8) {
      if (tiles[i][j + 1].getPieceColor() != pieceColor) {
        moves.add(new int[] {i, j + 1});
      }
    }
    
    if (j - 1 >= 0) {
      if (tiles[i][j - 1].getPieceColor() != pieceColor) {
        moves.add(new int[] {i, j - 1});
      }
    }
    
    if (i + 1 < 8) {
      if (tiles[i + 1][j].getPieceColor() != pieceColor) {
        moves.add(new int[] {i + 1, j});
      }
    }
    
    if (i + 1 < 8 && j - 1 >= 0) {
      if (tiles[i + 1][j - 1].getPieceColor() != pieceColor) {
        moves.add(new int[] {i + 1, j - 1});
      }
    }
    
    if (i + 1 < 8 && j + 1 < 8) {
      if (tiles[i + 1][j + 1].getPieceColor() != pieceColor) {
        moves.add(new int[] {i + 1, j + 1});
      }
    }

    if (canCastle(0, pieceColor)) {
      if (tiles[i][3].getPieceColor() == 0 && tiles[i][2].getPieceColor() == 0
          && tiles[i][1].getPieceColor() == 0) {
        moves.add(new int[] {i, 2});
      }
    } 
    if (canCastle(7, pieceColor)) {
      if (tiles[i][5].getPieceColor() == 0 && tiles[i][6].getPieceColor() == 0) {
        moves.add(new int[] {i, 6});
      }
    }
    
    return moves;
  }
  
  /**
   * Checks to see if a piece at tiles[i][j] is in "check" (whether it is a king or not)
   * 
   * @param i - tiles[i][] - the 'i' value of the tile the piece is on
   * @param j - tiles [][j] - the 'j' value of the tile the piece is on
   * @param pieceColor - the color of the piece (1 is white, 2 is black)
   * @return true if the piece is in check, else false
   */
  private boolean isInCheck(int i, int j, int pieceColor, Tile[][] tiles) {
    // creates a list to store all moves in
    List<int[]> moves;

    if (pieceColor == 1) { // if the piece is white
      moves = getAllMoves(2, tiles); // gets all black moves
      // checks each move to see if it could take the piece (if the piece's location is a possible
      // move)
      for (int[] move : moves) {
        if (Arrays.equals(move, new int[] {i, j})) {
          return true;
        }
      }
    } else { // else the piece is black
      moves = getAllMoves(1, tiles); // gets all white moves
      // checks each move to see if it could take the piece (if the piece's location is a possible
      // move)
      for (int[] move : moves) {
        if (Arrays.equals(move, new int[] {i, j})) {
          return true;
        }
      }
    }
    
    // return false if no move made by the other player could take the piece at [i][j]
    return false;
  }
  
  /**
   * Checks if the color's king would be in check after a given move
   * 
   * @param moved - the piece being moved (0 = i, 1 = j, 2 = color, 3 = piece type)
   * @param move - the tile the piece is being moved to (0 = i, 1 = j)
   * @return - true if the king would be in check after the move, else false
   */
  private boolean wouldBeCheck(int[] moved, int[] move) {
    // gets a copy of the tiles array so that this method does not affect the playable board
    Tile[][] fakeTiles = copyTiles();
    
    int fromI = moved[0];
    int fromJ = moved[1];
    int color = moved[2];
    int piece = moved[3];
    
    int toI = move[0];
    int toJ = move[1];
    
    // sets up new tiles in the array so that this method does not adjust the tiles in the other array
    fakeTiles[fromI][fromJ] = new Tile(fakeTiles[fromI][fromJ].getColor(), color, piece);
    fakeTiles[toI][toJ] = new Tile(fakeTiles[toI][toJ].getColor(), color, piece);
    
    
    fakeTiles[fromI][fromJ].setPiece(0, 0);
    fakeTiles[toI][toJ].setPiece(color, piece);
    
    int kingI = 0;
    int kingJ = 0;
    
    if (color == 1 && piece == K) {
      kingI = toI;
      kingJ = toJ;
    } else if (color == 2 && piece == K) {
      kingI = toI;
      kingJ = toJ;
    } else {
      if (color == 1) {
        kingI = wKingI;
        kingJ = wKingJ;
      } else if (color == 2) {
        kingI = bKingI;
        kingJ = bKingJ; 
      }
    }
    
    if (isInCheck(kingI, kingJ, color, fakeTiles)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Checks to see if the color in question is in checkmate (which would end the game), assumes the
   * king is already in check
   * 
   * @param color - the color of the king in check
   * @param tiles - the tiles being checked for checkmate
   * @return - true if a checkmate is found, else false
   */
  private boolean isCheckMate(int color, Tile[][] tiles) {
    // gets a list of all possible moves (0 = fromI, 1 = fromJ, 2 = toI, 3 = toJ)
    List<int[]> moves = getAllMovesDetailed(color, tiles);
    
    for (int[] move : moves) {
      int piece = tiles[move[0]][move[1]].getPiece();
      if (!wouldBeCheck(new int[] {move[0], move[1], color, piece}, new int[] {move[2], move[3]})) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Helper method yet to be implemented used in helping the computer detect which moves are good
   * 
   * @param color - the color being checked for check mate
   * @param move - int representation of the move being made by the other color
   * @return - true if the move would be checkmate, else false
   */
  private boolean wouldBeCheckMate(int[] moved, int[] move) {
    // gets a copy of the tiles array so that this method does not affect the playable board
    Tile[][] fakeTiles = copyTiles();
    
    int fromI = moved[0];
    int fromJ = moved[1];
    int color = moved[2];
    int piece = moved[3];
    
    int toI = move[0];
    int toJ = move[1];
    
    // sets up new tiles in the array so that this method does not adjust the tiles in the other array
    fakeTiles[fromI][fromJ] = new Tile(fakeTiles[fromI][fromJ].getColor(), color, piece);
    fakeTiles[toI][toJ] = new Tile(fakeTiles[toI][toJ].getColor(), color, piece);
    
    
    fakeTiles[fromI][fromJ].setPiece(0, 0);
    fakeTiles[toI][toJ].setPiece(color, piece);
    
    if (color == 1 ) { // if white moves, checks to see if black is check mated
      if (isCheckMate(2, fakeTiles)) {
        return true;
      } else {
        return false;
      }
    } else {// else black moves, checks to see if black is check mated
      if (isCheckMate(1, fakeTiles)) {
        return true;
      } else {
        return false;
      }
    }
    
  }
  /**
   * Gets a copy of the tiles array to be used in checking for checks
   * 
   * @return - a list of tiles that is a copy of the tiles list for the current board state
   */
  private Tile[][] copyTiles() {
    Tile[][] fakeTiles = new Tile[8][8];
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        fakeTiles[i][j] = tiles[i][j];
      }
    }
    
    return fakeTiles;
  }
  
  /**
   * Gets all the possible moves for a player 
   * 
   * @param color - the color of the player's pieces whose moves are being gotten
   * @return a list of all possible moves for the given color
   */
  private List<int[]> getAllMoves(int color, Tile[][] tiles) {
    // creates a list to store all the legal moves a color has
    List<int[]> moves = new ArrayList<int[]>();

    // goes through each tile, checking to see if the piece color on the tile matches the given
    // color, if it does, it checks for moves that can be made by the piece on the tile and adds
    // them to the list
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        if (tiles[i][j].getPieceColor() == color) {
          if (tiles[i][j].getPiece() == P) {
            moves.addAll(getPawnMoves(i, j, color, tiles));
          } else if (tiles[i][j].getPiece() == N) {
            moves.addAll(getKnightMoves(i, j, color, tiles));
          } else if (tiles[i][j].getPiece() == B) {
            moves.addAll(getBishopMoves(i, j, color, tiles));
          } else if (tiles[i][j].getPiece() == R) {
            moves.addAll(getRookMoves(i, j, color, tiles));
          } else if (tiles[i][j].getPiece() == Q) {
            moves.addAll(getQueenMoves(i, j, color, tiles));
          } else if (tiles[i][j].getPiece() == K) {
            moves.addAll(getKingMoves(i, j, color, tiles));
          }
        }
      }
    }
    
    return moves;
  }
  
  /**
   * Gets a list of possible moves for a color that includes the tile the piece is being moved from
   * 
   * NOTE: this was made when I realized the getAllMoves method did not work for the isCheckMate method,
   * and may be redundant, but I did not want to adjust all of my other methods to work for this version
   * of getAllMoves.
   * 
   * @param color - the color whose moves are being gotten
   * @param tiles - the array of tiles
   * @return - a list of all possible moves for the color
   */
  private List<int[]> getAllMovesDetailed(int color, Tile[][] tiles) {
    // creates a list to store all the legal moves a color has
    List<int[]> moves = new ArrayList<int[]>();

    // goes through each tile, checking to see if the piece color on the tile matches the given
    // color, if it does, it checks for moves that can be made by the piece on the tile and adds
    // them to the list
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        if (tiles[i][j].getPieceColor() == color) {
          if (tiles[i][j].getPiece() == P) {
            for (int[] movedTo : getPawnMoves(i, j, color, tiles)) {
              moves.add(new int[] {i , j, movedTo[0], movedTo[1]});
            }
          } else if (tiles[i][j].getPiece() == N) {
            for (int[] movedTo : getKnightMoves(i, j, color, tiles)) {
              moves.add(new int[] {i , j, movedTo[0], movedTo[1]});
            }
          } else if (tiles[i][j].getPiece() == B) {
            for (int[] movedTo : getBishopMoves(i, j, color, tiles)) {
              moves.add(new int[] {i , j, movedTo[0], movedTo[1]});
            }
          } else if (tiles[i][j].getPiece() == R) {
            for (int[] movedTo : getRookMoves(i, j, color, tiles)) {
              moves.add(new int[] {i , j, movedTo[0], movedTo[1]});
            }
          } else if (tiles[i][j].getPiece() == Q) {
            for (int[] movedTo : getQueenMoves(i, j, color, tiles)) {
              moves.add(new int[] {i , j, movedTo[0], movedTo[1]});
            }
          } else if (tiles[i][j].getPiece() == K) {
            for (int[] movedTo : getKingMoves(i, j, color, tiles)) {
              moves.add(new int[] {i , j, movedTo[0], movedTo[1]});
            }
          }
        }
      }
    }
    
    return moves;
  }
  
  /**
   * Checks to see if a castle is possible given the color and j value of the rook being used
   * 
   * @param j - the file the rook is on
   * @param pieceColor - the color of the king being castled
   * @return true if a castle is possible, else false
   */
  private boolean canCastle(int j, int pieceColor) {
    // if the king is white, use the 'w' boolean values
    if (pieceColor == 1) {
      // if the rook's j value is seven, use the 'Seven' boolean value
      if (j == 7) {
        if (!wKingMoved && !wSevenRookMoved) { 
          return true;
        }
      // else if the rook's j value is zero, use the 'Zero' boolean value
      } else if (j == 0) {
        if (!wKingMoved && !wZeroRookMoved) {
          return true;
        }
      }
    } else { // else use the 'b' values
      // if the rook's j value is seven, use the 'Seven' boolean value
      if (j == 7) {
        if (!bKingMoved && !bSevenRookMoved) {
          return true;
        } 
      // else if the rook's j value is zero, use the 'Zero' boolean value
      } else if (j == 0) {
        if (!bKingMoved && !bZeroRookMoved) {
          return true;
        }
      }
    }
    
    // returns false if the castle move cannot be completed
    return false;
  }

  /**
   * "Moves" a piece by changing the image views shown on both the tile it was on and the tile it is
   * moved to
   * 
   * @param fromI
   * @param fromJ
   * @param toI
   * @param toJ
   * @throws FileNotFoundException
   */
  private void move(int fromI, int fromJ, int toI, int toJ) throws FileNotFoundException {
    // use tiles array to get piece at fromI/J, set gridpane button ImageView at fromI/J to a blank
    // tile, and gridpane button ImageView at toI/J to a tile with that piece on it
    
    // images used in displaying the chess board
    FileInputStream image = new FileInputStream("./images/DarkTile.png");
    Image DarkTile = new Image(image);
    FileInputStream image1 = new FileInputStream("./images/LightTile.png");
    Image LightTile = new Image(image1);
    FileInputStream image2 = new FileInputStream("./images/DwP.png");
    Image DWP = new Image(image2);
    FileInputStream image3 = new FileInputStream("./images/LWP.png");
    Image LWP = new Image(image3);
    FileInputStream image20 = new FileInputStream("./images/DwB.png");
    Image DWB = new Image(image20);
    FileInputStream image30 = new FileInputStream("./images/LWB.png");
    Image LWB = new Image(image30);
    FileInputStream image21 = new FileInputStream("./images/DwN.png");
    Image DWN = new Image(image21);
    FileInputStream image31 = new FileInputStream("./images/LWN.png");
    Image LWN = new Image(image31);
    FileInputStream image22 = new FileInputStream("./images/DwR.png");
    Image DWR = new Image(image22);
    FileInputStream image32 = new FileInputStream("./images/LWR.png");
    Image LWR = new Image(image32);
    FileInputStream image23 = new FileInputStream("./images/DwQ.png");
    Image DWQ = new Image(image23);
    FileInputStream image33 = new FileInputStream("./images/LWQ.png");
    Image LWQ = new Image(image33);
    FileInputStream image24 = new FileInputStream("./images/DwK.png");
    Image DWK = new Image(image24);
    FileInputStream image34 = new FileInputStream("./images/LWK.png");
    Image LWK = new Image(image34);
    FileInputStream image25 = new FileInputStream("./images/DBP.png");
    Image DBP = new Image(image25);
    FileInputStream image35 = new FileInputStream("./images/LBP.png");
    Image LBP = new Image(image35);
    FileInputStream image26 = new FileInputStream("./images/DBB.png");
    Image DBB = new Image(image26);
    FileInputStream image36 = new FileInputStream("./images/LBB.png");
    Image LBB = new Image(image36);
    FileInputStream image27 = new FileInputStream("./images/DBN.png");
    Image DBN = new Image(image27);
    FileInputStream image37 = new FileInputStream("./images/LBN.png");
    Image LBN = new Image(image37);
    FileInputStream image28 = new FileInputStream("./images/DBR.png");
    Image DBR = new Image(image28);
    FileInputStream image38 = new FileInputStream("./images/LBR.png");
    Image LBR = new Image(image38);
    FileInputStream image29 = new FileInputStream("./images/DBQ.png");
    Image DBQ = new Image(image29);
    FileInputStream image39 = new FileInputStream("./images/LBQ.png");
    Image LBQ = new Image(image39);
    FileInputStream image210 = new FileInputStream("./images/DBK.png");
    Image DBK = new Image(image210);
    FileInputStream image310 = new FileInputStream("./images/LBK.png");
    Image LBK = new Image(image310);
    
    
    // saves the piece/piece color of the "to"/"from" tiles, and adjusts the values in that tile to
    // be a blank dark/light tile
    int piece = tiles[fromI][fromJ].getPiece();
    int pieceColor = tiles[fromI][fromJ].getPieceColor();
    tiles[fromI][fromJ].setPiece(0, 0);
    
    // saves the move as the new "last" move
    last = new int[] {fromI, fromJ, toI, toJ, piece};
    
    if (castling && piece == K) {
      if (toJ == 2) {
        tiles[fromI][3].setPiece(pieceColor, R);
        tiles[fromI][0].setPiece(0, 0);
      } else if (toJ == 6) {
        tiles[fromI][5].setPiece(pieceColor, R);
        tiles[fromI][7].setPiece(0, 0);
      }
      
    } 
    
    // updates a king value if needed
    if (piece == K) {
      if (pieceColor == 1) {
        wKingI = toI;
        wKingJ = toJ;
        wKingMoved = true;
      } else {
        bKingI = toI;
        bKingJ = toJ;
        bKingMoved = true;
      }
    }
    
    // updates the rook moved value if needed
    if (piece == R) {
      if (pieceColor == 1) {
        if (fromI == 7 && fromJ == 0) {
          wZeroRookMoved = true;
        } else if (fromI == 7 && fromJ == 7) {
          wSevenRookMoved = true;
        }
      } else {
        if (fromI == 0 && fromJ == 0) {
          bZeroRookMoved = true;
        } else if (fromI == 0 && fromJ == 7) {
          bSevenRookMoved = true;
        }
      }
    }

    // updates the graphic on the "from" button
    Button fromBtn = getButton(fromI, fromJ);

    if (fromI % 2 != fromJ % 2) {
      ImageView img = new ImageView(DarkTile);
      img.setFitHeight(80);
      img.setFitWidth(80);
      fromBtn.setGraphic(img);
    } else {
      ImageView img = new ImageView(LightTile);
      img.setFitHeight(80);
      img.setFitWidth(80);
      fromBtn.setGraphic(img);
    }

    // if a castle move is performed, set the spot the rook was at to a blank tile, and set the rook
    // to be next to the king
    if (castling && piece == K) {
      if (pieceColor == 1) {
        if (toJ == 2) {
          Button corner = getButton(fromI, 0);
          ImageView img = new ImageView(DarkTile);
          img.setFitHeight(80);
          img.setFitWidth(80);
          corner.setGraphic(img);
          
          Button nextToKing = getButton(fromI, 3);
          ImageView img2 = new ImageView(LWR);
          img2.setFitHeight(80);
          img2.setFitWidth(80);
          nextToKing.setGraphic(img2);
        } else if (toJ == 6) {
          Button corner = getButton(fromI, 7);
          ImageView img = new ImageView(LightTile);
          img.setFitHeight(80);
          img.setFitWidth(80);
          corner.setGraphic(img);

          Button nextToKing = getButton(fromI, 5);
          ImageView img2 = new ImageView(LWR);
          img2.setFitHeight(80);
          img2.setFitWidth(80);
          nextToKing.setGraphic(img2);
        }
      } else if (pieceColor == 2) {
        if (toJ == 2) {
          Button corner = getButton(fromI, 0);
          ImageView img = new ImageView(LightTile);
          img.setFitHeight(80);
          img.setFitWidth(80);
          corner.setGraphic(img);

          Button nextToKing = getButton(fromI, 3);
          ImageView img2 = new ImageView(DBR);
          img2.setFitHeight(80);
          img2.setFitWidth(80);
          nextToKing.setGraphic(img2);
        } else if (toJ == 6) {
          Button corner = getButton(fromI, 8);
          ImageView img = new ImageView(DarkTile);
          img.setFitHeight(80);
          img.setFitWidth(80);
          corner.setGraphic(img);

          Button nextToKing = getButton(fromI, 5);
          ImageView img2 = new ImageView(DBR);
          img2.setFitHeight(80);
          img2.setFitWidth(80);
          nextToKing.setGraphic(img2);
        }
      }
    }


    // updates the piece values found in the "to" tile in the tiles array
    if (piece == P && (toI == 7 || toI == 0)) {
      // auto-promotes a pawn to a queen if pawn moved to last row (the only way a pawn gets to last
      // row of board is on the opposite side, hence warranting a promotion)
      tiles[toI][toJ].setPiece(pieceColor, Q);
    } else {
      tiles[toI][toJ].setPiece(pieceColor, piece);
    }
    
    
    
    // gets the "to" button
    Button toBtn = getButton(toI, toJ);

    // updates the graphic on the "to" button based on the tile color, as well as the piece type and
    // piece color of the piece moved onto the tile
    if (toI % 2 != toJ % 2) { // these tiles are all dark
      if (pieceColor == 1) { // 1 corresponds to a white piece
        if (piece == P) {
          // promotes the pawn if necessary
          if (toI == 0) {
            ImageView img = new ImageView(DWQ);
            img.setFitHeight(80);
            img.setFitWidth(80);
            toBtn.setGraphic(img);
          } else {
            ImageView img = new ImageView(DWP);
            img.setFitHeight(80);
            img.setFitWidth(80);
            toBtn.setGraphic(img);
          }
        } else if (piece == N) {
          ImageView img = new ImageView(DWN);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        } else if (piece == B) {
          ImageView img = new ImageView(DWB);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        } else if (piece == R) {
          ImageView img = new ImageView(DWR);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        } else if (piece == Q) {
          ImageView img = new ImageView(DWQ);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        } else {
          ImageView img = new ImageView(DWK);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        }
      } else if (pieceColor == 2) { // 2 corresponds to a black piece
        if (piece == P) {
          // promotes the pawn if necessary
          if (toI == 7) {
            ImageView img = new ImageView(DBQ);
            img.setFitHeight(80);
            img.setFitWidth(80);
            toBtn.setGraphic(img);
          } else {
            ImageView img = new ImageView(DBP);
            img.setFitHeight(80);
            img.setFitWidth(80);
            toBtn.setGraphic(img);
          }
        } else if (piece == N) {
          ImageView img = new ImageView(DBN);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        } else if (piece == B) {
          ImageView img = new ImageView(DBB);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        } else if (piece == R) {
          ImageView img = new ImageView(DBR);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        } else if (piece == Q) {
          ImageView img = new ImageView(DBQ);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        } else {
          ImageView img = new ImageView(DBK);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        }
      }
    } else { // these tiles are all light
      if (pieceColor == 1) { // 1 corresponds to a white piece
        if (piece == P) {
          // promotes the pawn if necessary
          if (toI == 0) {
            ImageView img = new ImageView(LWQ);
            img.setFitHeight(80);
            img.setFitWidth(80);
            toBtn.setGraphic(img);
          } else {
            ImageView img = new ImageView(LWP);
            img.setFitHeight(80);
            img.setFitWidth(80);
            toBtn.setGraphic(img);
          }
        } else if (piece == N) {
          ImageView img = new ImageView(LWN);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        } else if (piece == B) {
          ImageView img = new ImageView(LWB);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        } else if (piece == R) {
          ImageView img = new ImageView(LWR);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        } else if (piece == Q) {
          ImageView img = new ImageView(LWQ);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        } else {
          ImageView img = new ImageView(LWK);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        }
      } else if (pieceColor == 2) { // 2 corresponds to a black piece
        if (piece == P) {
          // promotes the pawn if necessary
          if (toI == 7) {
            ImageView img = new ImageView(LBQ);
            img.setFitHeight(80);
            img.setFitWidth(80);
            toBtn.setGraphic(img);
          } else {
            ImageView img = new ImageView(LBP);
            img.setFitHeight(80);
            img.setFitWidth(80);
            toBtn.setGraphic(img);
          }
        } else if (piece == N) {
          ImageView img = new ImageView(LBN);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        } else if (piece == B) {
          ImageView img = new ImageView(LBB);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        } else if (piece == R) {
          ImageView img = new ImageView(LBR);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        } else if (piece == Q) {
          ImageView img = new ImageView(LBQ);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        } else {
          ImageView img = new ImageView(LBK);
          img.setFitHeight(80);
          img.setFitWidth(80);
          toBtn.setGraphic(img);
        }
      }
    }
 
    // updates the tile of the taken pawn in case of an en passant move
    Button passanted = new Button();
    
    if (passanting && piece == P) {
      if (pieceColor == 1) {
        passanted = getButton(toI + 1, toJ);
        tiles[toI + 1][toJ].setPiece(0, 0);
        if (toI + 1 % 2 == toJ % 2) { // these tiles are all dark
          ImageView img = new ImageView(DarkTile);
          img.setFitHeight(80);
          img.setFitWidth(80);
          passanted.setGraphic(img);
        } else {
          ImageView img = new ImageView(LightTile);
          img.setFitHeight(80);
          img.setFitWidth(80);
          passanted.setGraphic(img);
        }
      } else {
        passanted = getButton(toI - 1, toJ);
        tiles[toI - 1][toJ].setPiece(0, 0);
        if (toI - 1 % 2 == toJ % 2) { // these tiles are all dark
          ImageView img = new ImageView(DarkTile);
          img.setFitHeight(80);
          img.setFitWidth(80);
          passanted.setGraphic(img);
        } else {
          ImageView img = new ImageView(LightTile);
          img.setFitHeight(80);
          img.setFitWidth(80);
          passanted.setGraphic(img);
        }
      }
    }
  }
  
  /**
   * Gets the button found at i, j on the board GridPane
   * 
   * @param i - the row the button is found at (in the GridPane)
   * @param j - the column the button is found at (in the GridPane)
   * @return - the Button found at the given row/col
   */
  private Button getButton(int i, int j) {
    // gets the list of children (buttons) contained in board
    ObservableList<Node> children = board.getChildren();

    // checks each child to get the one with matching constraints, and returns the button which
    // is found at i, j on the gridpane
    for (Node node : children) {
      if(board.getRowIndex(node) == i && board.getColumnIndex(node) == j) {
        return (Button) node;
      }
    }
    // else return null
    return null;
  }
  
  /**
   * Gets the "best" move for the given color on the given board state
   * 
   * @param color - the color who is next to move
   * @param tiles - the state of the board
   * @return - an int array s.t. (0 = fromI, 1 = fromJ, 2 = toI, 3 = toJ)
   */
  private int[] getMove(int color, Tile[][] tiles) {
    
    List<Integer> values = new ArrayList<Integer>();
    
    List<int[]> moves = new ArrayList<int[]>();
    List<int[]> moves2 = new ArrayList<int[]>();
    List<int[]> moves3 = new ArrayList<int[]>();
    int possible;
    int possible2;
    int possible3;
    
    
    moves = getAllMovesDetailed(color, tiles);
    possible = moves.size();
    for (int[] move : moves) {
      Tile[][] tiles2 = fakeMove(move, tiles);
      moves2 = getAllMovesDetailed(color, tiles2);
      possible2 = moves2.size();
      for (int[] move2 : moves2) {
        Tile[][] tiles3 = fakeMove(move2, tiles2);
        moves3 = getAllMovesDetailed(color, tiles3);
        possible3 = moves3.size();
        for (int[] move3 : moves3) {
          Tile[][] tiles4 = fakeMove(move3, tiles3);
          values.add(getBoardValue(tiles4));
        }
      }
     }
    
    
    return null;
  }

  /**
   * Simulates a move on the board by returning a copy of what the tiles array would look like after
   * the given move
   * 
   * @param move - the given move to be simulated
   * @param tiles - the tiles array before the move
   * @return Tile[][] representation of the board after the given move
   */
  private Tile[][] fakeMove(int[] move, Tile[][] tiles) {
    return null;
  }
  
  /**
   * Calculates the value of a given board state based on how many pieces each color has, or whether
   * a checkmate/stalemate has occured
   * 
   * @param tiles - the current board state
   * @return the int value of pieces on the board (white - black)
   */
  private int getBoardValue(Tile[][] tiles) {
    return 0;
  }
  
}

