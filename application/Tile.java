/**
 * Title: Tile.java
 * Files:  
 * Program function:
 * Author: 
 * Email: 
 * Class:
 * Lecture No.: 
 * Semester:
 */
package application;

/**
 * Tile - Purpose of class
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
