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

/**
 * Tile - ADT to represent a square on a chess board, holding int values representing its color, as
 * well as the piece (or lack thereof) on it
 *
 * @author Danny Southard
 */
public class Tile {
  public int[] values;
  
  /**
   * Constructor for the tile using given integers representing the color of the tile, the color of
   * the piece on the tile, and the piece.
   * 
   * Color:         Piece Color:
   * 0 - Light      0 - no piece on tile
   * 1 - Dark       1 - white
   *                2 - black
   * Piece:
   * 0 - Pawn       3 - Rook
   * 1 - Knight     4 - Queen
   * 2 - Bishop     5 - King
   */
  public Tile(int color, int pieceColor, int piece) {
    values = new int[3];
    values[0] = color;
    values[1] = pieceColor;
    values[2] = piece;
  }
  
  /**
   * Getter method for the int value for the piece on the given tile
   * 
   * @return the int value for the piece on the tile
   */
  public int getPiece() {
    return values[2];
  }
  
  /**
   * Getter method for the int value for the color of the given tile
   * 
   * @return the int value for the piece on the tile
   */
  public int getColor() {
    return values[0];
  }
  
  /**
   * Getter method for the int value for the piece color for the given tile
   * 
   * @return the int value for the color of the piece stored in the tile
   */
  public int getPieceColor() {
    return values[1];
  }
  
  /**
   * Sets a new piece to the tile when a piece is moved off of/onto the tile
   * 
   * @param pieceColor - the color of the piece now on the tile
   * @param piece - the type of piece now on the tile
   */
  public void setPiece(int pieceColor, int piece) {
    values[1] = pieceColor;
    values[2] = piece;
  }
}
