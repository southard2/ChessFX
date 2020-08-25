/**
 * Title: Chess Files: Main.java
 *  Start Date: 7/17/2020
 *  End Date: - 
 *  Author: Danny Southard
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
 * Main - GUI showing the chess board
 *
 * @author Danny Southard
 */
public class Main extends Application {
  private List<String> args;

  // represents no button (tile) having been selected
  public static final int[] NONE = new int[] {-1, -1};
  //used to track the last button pressed
  private int[] pressed = NONE;

  private GridPane board;
  private Tile[][] tiles;
  private boolean whiteToPlay = true;

  public static final int WINDOW_HEIGHT = 720;
  public static final int WINDOW_WIDTH = 784;
  public static final String APP_TITLE = "Chess";

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
    Scene mainScene = new Scene(board, WINDOW_WIDTH, WINDOW_HEIGHT);

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
          btn.setOnAction(e -> buttonPressed(I, J));
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
          btn.setOnAction(e -> buttonPressed(I, J));
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
          btn.setOnAction(e -> buttonPressed(I, J));
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
          btn.setOnAction(e -> buttonPressed(I, J));
          board.add(btn, i, j);
        }
      }
    }

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
    for (int i = 0; i < 8; i++) { // loop to create tiles for each rank
      for (int j = 0; j < 8; j++) { // loop to create tiles for each file
        // for ranks 3 - 6, set up the tiles such that they don't have pieces on them
        if (i > 1 && i < 5) {
          // creates light tiles and places them into the tile array
          if ((i % 2 == 0 && j % 2 == 1) || (i % 2 == 1 && j % 2 == 0)) {
            Tile tile = new Tile(0, 0, 0);
            tiles[i][j] = tile;
          }
          // creates dark tiles and places them into the tile array
          else {
            Tile tile = new Tile(1, 0, 0);
            tiles[i][j] = tile;
          }
        }

        // for the 7th rank (i == 7), sets up alternating light/dark tiles with black pawns
        if (i == 1) {
          if (j % 2 == 0) {
            Tile tile = new Tile(1, 2, P);
            tiles[i][j] = tile;
          } else {
            Tile tile = new Tile(0, 2, P);
            tiles[i][j] = tile;
          }
        }
        // for the 2nd rank (i == 6), sets up alternating light/dark tiles with white pawns
        if (i == 6) {
          if (j % 2 == 0) {
            Tile tile = new Tile(0, 1, P);
            tiles[i][j] = tile;
          } else {
            Tile tile = new Tile(1, 1, P);
            tiles[i][j] = tile;
          }
        }

        // sets up black pieces on the 8th rank
        if (i == 0) {
          if (j == 0) {
            Tile tile = new Tile(0, 2, R);
            tiles[i][j] = tile;
          } else if (j == 1) {
            Tile tile = new Tile(1, 2, N);
            tiles[i][j] = tile;
          } else if (j == 2) {
            Tile tile = new Tile(0, 2, B);
            tiles[i][j] = tile;
          } else if (j == 3) {
            Tile tile = new Tile(1, 2, Q);
            tiles[i][j] = tile;
          } else if (j == 4) {
            Tile tile = new Tile(0, 2, K);
            tiles[i][j] = tile;
          } else if (j == 5) {
            Tile tile = new Tile(1, 2, B);
            tiles[i][j] = tile;
          } else if (j == 6) {
            Tile tile = new Tile(0, 2, N);
            tiles[i][j] = tile;
          } else {
            Tile tile = new Tile(1, 2, R);
            tiles[i][j] = tile;
          }
        }

        // sets up white pieces on the 1st rank
        if (i == 7) {
          if (j == 0) {
            Tile tile = new Tile(1, 1, R);
            tiles[i][j] = tile;
          } else if (j == 1) {
            Tile tile = new Tile(0, 1, N);
            tiles[i][j] = tile;
          } else if (j == 2) {
            Tile tile = new Tile(1, 1, B);
            tiles[i][j] = tile;
          } else if (j == 3) {
            Tile tile = new Tile(0, 1, Q);
            tiles[i][j] = tile;
          } else if (j == 4) {
            Tile tile = new Tile(1, 1, K);
            tiles[i][j] = tile;
          } else if (j == 5) {
            Tile tile = new Tile(0, 1, B);
            tiles[i][j] = tile;
          } else if (j == 6) {
            Tile tile = new Tile(1, 1, N);
            tiles[i][j] = tile;
          } else {
            Tile tile = new Tile(0, 2, R);
            tiles[i][j] = tile;
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
        return;
      }
      // if a black piece is selected on whites turn, or a white piece selected on blacks turn,
      // nothing happens
      else if (tiles[i][j].getPieceColor() == 2 && whiteToPlay) {
        return;
      } else if (tiles[i][j].getPieceColor() == 1 && !whiteToPlay) {
        return;
      }
      pressed = new int[] {i, j, tiles[i][j].getPieceColor(), tiles[i][j].getPiece()};
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
        } else {
          whiteToPlay = true;
        }
      } // TODO: else if (isEnPassant()) and corresponding methods with seperate move
        // instructions
      else {
        pressed = NONE;
        return; // TODO: display some sort of error in GUI if move not valid
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
    return true; // TODO: implement method
  }

  /**
   * Gets a list of all moves a selected pawn can make
   * 
   * @param i - value representing the row of the button pushed
   * @param j - value representing the column of the button pushed
   * @return - a list of int arrays, each representing a move
   */
  private List<int[]> getPawnMoves(int i, int j) {
    return null;
  }

  /**
   * Gets a list of all moves a selected knight can make
   * 
   * @param i - value representing the row of the button pushed
   * @param j - value representing the column of the button pushed
   * @return - a list of int arrays, each representing a move
   */
  private List<int[]> getKnightMoves(int i, int j) {
    return null;
  }

  /**
   * Gets a list of all moves a selected bishop can make
   * 
   * @param i - value representing the row of the button pushed
   * @param j - value representing the column of the button pushed
   * @return - a list of int arrays, each representing a move
   */
  private List<int[]> getBishopMoves(int i, int j) {
    return null;
  }

  /**
   * Gets a list of all moves a selected rook can make
   * 
   * @param i - value representing the row of the button pushed
   * @param j - value representing the column of the button pushed
   * @return - a list of int arrays, each representing a move
   */
  private List<int[]> getRookMoves(int i, int j) {
    return null;
  }

  /**
   * Gets a list of all moves a selected queen can make
   * 
   * @param i - value representing the row of the button pushed
   * @param j - value representing the column of the button pushed
   * @return - a list of int arrays, each representing a move
   */
  private List<int[]> getQueenMoves(int i, int j) {
    return null;
  }

  /**
   * Gets a list of all moves a selected king can make
   * 
   * @param i - value representing the row of the button pushed
   * @param j - value representing the column of the button pushed
   * @ @return - a list of int arrays, each representing a move
   */
  private List<int[]> getKingMoves(int i, int j, boolean isBlack) {
    return null;
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

    // updates the graphic on the "from" button
    Button fromBtn = getButton(fromI, fromJ);
    if (fromI % 2 == fromJ % 2) {
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
    
    // updates the piece values found in the "to" tile in the tiles array
    if (piece == P && (toI == 7 || toI == 0)) {
      // auto-promotes a pawn to a queen if pawn moved to last row (the only way a pawn gets to last
      // row of board is on the opposite side, hence warranting a promotion)
      tiles[toI][toJ].setPiece(Q, pieceColor);
    } else {
      tiles[toI][toJ].setPiece(piece, pieceColor);
    }
    
    
    
    // gets the "to" button
    Button toBtn = getButton(toI, toJ);

    // updates the graphic on the "to" button based on the tile color, as well as the piece type and
    // piece color of the piece moved onto the tile
    if (toI % 2 == toJ % 2) { // these tiles are all dark
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
  }
  
  /**
   * Gets the button found at i, j on the board GridPane
   * 
   * @param i - the column the button is found at (in the GridPane)
   * @param j - the row the button is found at (in the GridPane)
   * @return - the Button found at I, J
   */
  private Button getButton(int i, int j) {
    // gets the list of children (buttons) contained in board
    ObservableList<Node> children = board.getChildren();

    // checks each child to get the one with matching constraints, and returns the button which
    // is found at i, j on the gridpane
    for (Node node : children) {
      if(board.getRowIndex(node) == j && board.getColumnIndex(node) == i) {
        return (Button) node;
      }
    }
    // else return null
    return null;
  }
}
