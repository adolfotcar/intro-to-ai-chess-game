import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Stack;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.Box;
import javax.swing.BoxLayout;

/*
	author: Adolfo Carvalho
  //most code already implemented by lecturer...
*/

public class ChessProject extends JFrame implements MouseListener, MouseMotionListener {
    JLayeredPane layeredPane;
    JPanel chessBoard;
    JLabel chessPiece;
    int xAdjustment;
    int yAdjustment;
	  int startX;
	  int startY;
	  int initialX;
	  int initialY;
	  JPanel panels;
	  JLabel pieces;
    Boolean win;
    String winner;
    Boolean white2Move;
    int moveCounter;
    int agentType;
    AIAgent agent;
    Boolean agentwins;
    Stack temporary;

    /*draws the board and adds the pieces into it*/
    public ChessProject(){
      /*drawing the board*/
      Dimension boardSize = new Dimension(600, 600);
      layeredPane = new JLayeredPane();
      getContentPane().add(layeredPane);
      layeredPane.setPreferredSize(boardSize);
      layeredPane.addMouseListener(this);
      layeredPane.addMouseMotionListener(this);
      chessBoard = new JPanel();
      layeredPane.add(chessBoard, JLayeredPane.DEFAULT_LAYER);
      chessBoard.setLayout( new GridLayout(8, 8) );
      chessBoard.setPreferredSize( boardSize );
      chessBoard.setBounds(0, 0, boardSize.width, boardSize.height);
      //adding the squares
      for (int i = 0; i < 64; i++) {
        JPanel square = new JPanel( new BorderLayout() );
        chessBoard.add( square );

        int row = (i / 8) % 2;
        if (row == 0)
          square.setBackground( i % 2 == 0 ? Color.white : Color.gray );
        else
          square.setBackground( i % 2 == 0 ? Color.gray : Color.white );
      }
      /*end drawing*/

      /*adding the pieces to the board*/
      for(int i=8;i < 16; i++){
        pieces = new JLabel( new ImageIcon("WhitePawn.png") );
        panels = (JPanel)chessBoard.getComponent(i);
        panels.add(pieces);
      }
		  pieces = new JLabel( new ImageIcon("WhiteRook.png") );
		  panels = (JPanel)chessBoard.getComponent(0);
	    panels.add(pieces);
		  pieces = new JLabel( new ImageIcon("WhiteKnight.png") );
		  panels = (JPanel)chessBoard.getComponent(1);
	    panels.add(pieces);
		  pieces = new JLabel( new ImageIcon("WhiteKnight.png") );
		  panels = (JPanel)chessBoard.getComponent(6);
	    panels.add(pieces);
		  pieces = new JLabel( new ImageIcon("WhiteBishop.png") );
		  panels = (JPanel)chessBoard.getComponent(2);
	    panels.add(pieces);
		  pieces = new JLabel( new ImageIcon("WhiteBishop.png") );
		  panels = (JPanel)chessBoard.getComponent(5);
	    panels.add(pieces);
		  pieces = new JLabel( new ImageIcon("WhiteKing.png") );
		  panels = (JPanel)chessBoard.getComponent(3);
	    panels.add(pieces);
		  pieces = new JLabel( new ImageIcon("WhiteQueen.png") );
		  panels = (JPanel)chessBoard.getComponent(4);
	    panels.add(pieces);
		  pieces = new JLabel( new ImageIcon("WhiteRook.png") );
		  panels = (JPanel)chessBoard.getComponent(7);
	    panels.add(pieces);
		  for(int i=48;i < 56; i++){
        pieces = new JLabel( new ImageIcon("BlackPawn.png") );
        panels = (JPanel)chessBoard.getComponent(i);
        panels.add(pieces);
		  }
		  pieces = new JLabel( new ImageIcon("BlackRook.png") );
		  panels = (JPanel)chessBoard.getComponent(56);
	    panels.add(pieces);
		  pieces = new JLabel( new ImageIcon("BlackKnight.png") );
		  panels = (JPanel)chessBoard.getComponent(57);
	    panels.add(pieces);
		  pieces = new JLabel( new ImageIcon("BlackKnight.png") );
		  panels = (JPanel)chessBoard.getComponent(62);
	    panels.add(pieces);
		  pieces = new JLabel( new ImageIcon("BlackBishop.png") );
		  panels = (JPanel)chessBoard.getComponent(58);
	    panels.add(pieces);
		  pieces = new JLabel( new ImageIcon("BlackBishop.png") );
		  panels = (JPanel)chessBoard.getComponent(61);
	    panels.add(pieces);
		  pieces = new JLabel( new ImageIcon("BlackKing.png") );
		  panels = (JPanel)chessBoard.getComponent(59);
	    panels.add(pieces);
		  pieces = new JLabel( new ImageIcon("BlackQueen.png") );
		  panels = (JPanel)chessBoard.getComponent(60);
	    panels.add(pieces);
		  pieces = new JLabel( new ImageIcon("BlackRook.png") );
		  panels = (JPanel)chessBoard.getComponent(63);
	    panels.add(pieces);
      moveCounter =0;
      white2Move = true;
      agent = new AIAgent();
      agentwins = false;
      temporary = new Stack();
      /*end adding pieces*/
    }

/*
  Method to check were a White Pawn can move to. There are two main conditions here. Either the White Pawn is in
  its starting position in which case it can move either one or two squares or it has already moved and the it can only
  one square down the board. The Pawn can also take an opponent piece in a diagonal movement. and if it makes it to the
  bottom of the board it turns into a Queen (this should be handled where the move is actually being made and not in this
  method).
*/
private Stack getWhitePawnSquares(int x, int y, String piece){
  Stack moves = new Stack();
  Square startSquare = new Square(x, y, piece);
  
  //if it's a valid move, will be added to the stack of moves
  Move validMove;

  //temporary positions of the ending X and Y positions
  int tmpX1 = x+1;
  int tmpX2 = x-1;
  int tmpY1 = y+1;
  int tmpY2 = y+2;

  //if pawn is starting in row 1 (first movement)
  if (y == 1) {
    //if there's no piece in the row ahead
    if ((!piecePresent((x*75)+20, (tmpY1 * 75) + 20))) {
      Square tmpSquare = new Square(x, tmpY1, getPieceNameBySquare(x, tmpY1));
      validMove = new Move(startSquare, tmpSquare);
      moves.push(validMove);
    }
    //if there's no pice ahead or no piece in two rows ahead, it's a valid move
    if ((!piecePresent((x*75)+20, (tmpY1*75)+20))&&(!piecePresent((x*75)+20, (tmpY2*75)+20))) {
      Square tmpSquare = new Square(x, tmpY2, getPieceNameBySquare(x, tmpY2));
      validMove = new Move(startSquare, tmpSquare);
      moves.push(validMove);
    }
    //if there is a piece in the next row and col, have to check if it's black
    if (((piecePresent((tmpX1*75)+20, (tmpY1*75)+20))&&(tmpX1>=0)&&(tmpX1<=7))&&(checkWhiteOponent((tmpX1*75)+20, (tmpY1*75)+20))) {
      Square tmpSquare = new Square(tmpX1, tmpY1, getPieceNameBySquare(tmpX1, tmpY1));
      validMove = new Move(startSquare, tmpSquare);
      moves.push(validMove);      
    }
    //if there is a piece in the next row and prev col, have to check if it's black
    if (((piecePresent((tmpX2*75)+20, (tmpY1*75)+20))&&(tmpX2>=0)&&(tmpX2<=7))&&(checkWhiteOponent((tmpX2*75)+20, (tmpY1*75)+20))) {
      Square tmpSquare = new Square(tmpX2, tmpY1, getPieceNameBySquare(tmpX2, tmpY1));
      validMove = new Move(startSquare, tmpSquare);
      moves.push(validMove);
    }
  } 
  //if pawn was already moved
  else {
    //if there's no piece in the row ahead
    if ((!piecePresent((x*75)+20, (tmpY1*75)+20))&&(tmpY1>=0)&&(tmpY1<=7)) {
      Square tmpSquare = new Square(x, tmpY1, getPieceNameBySquare(x, tmpY1));
      validMove = new Move(startSquare, tmpSquare);
      moves.push(validMove);
    }
    //if there is a piece in the next row and col, have to check if it's black
    if (((piecePresent((tmpX1*75)+20, (tmpY1*75)+20))&&(tmpX1>=0)&&(tmpX1<=7)&&(tmpY1>=0)&&(tmpY1<=7))&&(checkWhiteOponent((tmpX1*75)+20, (tmpY1*75)+20))) {
      Square tmpSquare = new Square(tmpX1, tmpY1, getPieceNameBySquare(tmpX1, tmpY1));
      validMove = new Move(startSquare, tmpSquare);
      moves.push(validMove);
    }
    if (((piecePresent((tmpX2*75)+20, (tmpY1*75)+20))&&(tmpX2>=0)&&(tmpX2<=7)&&(tmpY1>=0)&&(tmpY1<=7))&&(checkWhiteOponent((tmpX2*75)+20, (tmpY1*75)+20))) {
      Square tmpSquare = new Square(tmpX2, tmpY1, getPieceNameBySquare(tmpX2, tmpY1));
      validMove = new Move(startSquare, tmpSquare);
      moves.push(validMove);
    }
  }

  return moves;
}

/*
  Similar to the getWhitePawnSquares method, but for black pawns
*/
private Stack getBlackPawnSquares(int x, int y, String piece){
  Stack moves = new Stack();
  Square startSquare = new Square(x, y, piece);
  
  //if it's a valid move, will be added to the stack of moves
  Move validMove;

  //temporary positions of the ending X and Y positions
  int tmpX1 = x+1;
  int tmpX2 = x-1;
  int tmpY1 = y+1;
  int tmpY2 = y+2;

  //if pawn is starting in row 1 (first movement)
  if (y == 1) {
    //if there's no piece in the row ahead
    if ((!piecePresent((x*75)+20, (tmpY1 * 75) + 20))) {
      Square tmpSquare = new Square(x, tmpY1, getPieceNameBySquare(x, tmpY1));
      validMove = new Move(startSquare, tmpSquare);
      moves.push(validMove);
    }
    //if there's no pice ahead or no piece in two rows ahead, it's a valid move
    if ((!piecePresent((x*75)+20, (tmpY1*75)+20))&&(!piecePresent((x*75)+20, (tmpY2*75)+20))) {
      Square tmpSquare = new Square(x, tmpY2, getPieceNameBySquare(x, tmpY2));
      validMove = new Move(startSquare, tmpSquare);
      moves.push(validMove);
    }
    //if there is a piece in the next row and col, have to check if it's white
    if (((piecePresent((tmpX1*75)+20, (tmpY1*75)+20))&&(tmpX1>=0)&&(tmpX1<=7))&&(!checkWhiteOponent((tmpX1*75)+20, (tmpY1*75)+20))) {
      Square tmpSquare = new Square(tmpX1, tmpY1, getPieceNameBySquare(tmpX1, tmpY1));
      validMove = new Move(startSquare, tmpSquare);
      moves.push(validMove);      
    }
    //if there is a piece in the next row and prev col, have to check if it's white
    if (((piecePresent((tmpX2*75)+20, (tmpY1*75)+20))&&(tmpX2>=0)&&(tmpX2<=7))&&(!checkWhiteOponent((tmpX2*75)+20, (tmpY1*75)+20))) {
      Square tmpSquare = new Square(tmpX2, tmpY1, getPieceNameBySquare(tmpX2, tmpY1));
      validMove = new Move(startSquare, tmpSquare);
      moves.push(validMove);
    }
  } 
  //if pawn was already moved
  else {
    //if there's no piece in the row ahead
    if ((!piecePresent((x*75)+20, (tmpY1*75)+20))&&(tmpY1>=0)&&(tmpY1<=7)) {
      Square tmpSquare = new Square(x, tmpY1, getPieceNameBySquare(x, tmpY1));
      validMove = new Move(startSquare, tmpSquare);
      moves.push(validMove);
    }
    //if there is a piece in the next row and col, have to check if it's white
    if (((piecePresent((tmpX1*75)+20, (tmpY1*75)+20))&&(tmpX1>=0)&&(tmpX1<=7)&&(tmpY1>=0)&&(tmpY1<=7))&&(!checkWhiteOponent((tmpX1*75)+20, (tmpY1*75)+20))) {
      Square tmpSquare = new Square(tmpX1, tmpY1, getPieceNameBySquare(tmpX1, tmpY1));
      validMove = new Move(startSquare, tmpSquare);
      moves.push(validMove);
    }
    if (((piecePresent((tmpX2*75)+20, (tmpY1*75)+20))&&(tmpX2>=0)&&(tmpX2<=7)&&(tmpY1>=0)&&(tmpY1<=7))&&(!checkWhiteOponent((tmpX2*75)+20, (tmpY1*75)+20))) {
      Square tmpSquare = new Square(tmpX2, tmpY1, getPieceNameBySquare(tmpX2, tmpY1));
      validMove = new Move(startSquare, tmpSquare);
      moves.push(validMove);
    }
  }

  return moves;
}



/*
  Method to check if there is a BlackKing in the surrounding squares of a given Square.
  The method should return true if there is no King in any of the squares surrounding
  the square that was submitted to the method.

  Method provided by the lecturer...won't touch it
*/
private Boolean checkSurroundingSquares(Square s){
  Boolean possible = false;
  int x = s.getXC()*75;
  int y = s.getYC()*75;
  //checking if there's any black king in the surrounding squares of the square s
  if(!((getPieceName((x+75), y).contains("BlackKing"))||
       (getPieceName((x-75), y).contains("BlackKing"))||
       (getPieceName(x,(y+75)).contains("BlackKing"))||
       (getPieceName((x), (y-75)).contains("BlackKing"))||
       (getPieceName((x+75),(y+75)).contains("BlackKing"))||
       (getPieceName((x-75),(y+75)).contains("BlackKing"))||
       (getPieceName((x+75),(y-75)).contains("BlackKing"))||
       (getPieceName((x-75), (y-75)).contains("BlackKing"))))
          possible = true;

  return possible;
}

/*
  The getKingSquares method takes as an input any coordinates from a square and returns a stack of all the possible
  valid moves that the WhiteKing can move to.

  So lets consider how the King can move. The King can essentially move in any direction as long as there is not another
  king in an adajacent square to were the king lands. Additionally, the King can only move one square at a time.

  To support this method we will also create a helper method called checkSurroundingSquares(Square s){, see above
  that returns a Boolean value to let us know if a supplied square will be adajacent to another square with a
  BlackKing present. Essentially if we consider that the board is a set of squares with coordinates for each square (x, y), this
  allows us to identify the possible squares that we should be investigating, see below;

  This shows us that for a single square with coordinates of (x, y) we need to check eight other potential squares.
  Remember we only need to check squares and contsruct moves if the movement (Piece on a Square --> a new Square) is
  going to be placing the piece back on the board, if we are not taking our own piece and if the resulting landing square
  is not adjacent to the enemy King.

  Method provided by the lecturer...won't touch it
*/
private Stack getKingSquares(int x, int y, String piece){
  Square startingSquare = new Square(x, y, piece);
  Stack moves = new Stack();
  Move validM, validM2, validM3, validM4;
  int tmpx1 = x+1;
  int tmpx2 = x-1;
  int tmpy1 = y+1;
  int tmpy2 = y-1;

/*
  we can create three different columes to check.
    - if x increases by one square, using the variable tmpx1 (x+1)
    - if x decreases by one square, using the variable tmpx2 (x-1)
    - or if x stays the same.
*/
  if(!((tmpx1 > 7))){
    /*
      This is the first condition where we will be working with the column where x increases.
      If we consider x increasing, we need to make sure that we don't fall off the board, so we use
      a condition here to check that the new value of x (tmpx1) is not greater than 7.

      From the grid above we can see in this column that there are three possible squares for us to check in
      this column:
      - were y decreases, y-1
      - were y increases, y+1
      - or were y stays the same

      The first step is to construct three new Squares for each of these possibilities.
      As the unchanged y value is already a location on the board we don't need to check the location and can simply
      make a call to checkSurroundingSquares for this new Square.

      If checkSurroundingSquares returns a positive value we jump inside the condition below:
        - firstly we create a new Move, which takes the starting square and the landing square that we have just checked with
          checkSurroundingSquares.
        - Next we need to figure out if there is a piece present on the square and if so make sure
          that the piece is an opponents piece.
        - Once we make sure that we are either moving to an empty square or we are taking our opponents piece we can push this
          possible move onto our stack of possible moves called "moves".

      This process is followed again for the other temporary squares created.

      After we check for all possoble squares on this column, we repeat the process for the other columns as identified above
      in the grid.
    */
    Square tmp = new Square(tmpx1, y, getPieceNameBySquare(tmpx1, y));
    Square tmp1 = new Square(tmpx1, tmpy1, getPieceNameBySquare(tmpx1, tmpy1));
    Square tmp2 = new Square(tmpx1, tmpy2, getPieceNameBySquare(tmpx1, tmpy2));
    if(checkSurroundingSquares(tmp)){
      validM = new Move(startingSquare, tmp);
      if(!piecePresent(((tmp.getXC()*75)+20), (((tmp.getYC()*75)+20)))){
        moves.push(validM);
      }
      else{
        if(checkWhiteOponent(((tmp.getXC()*75)+20), (((tmp.getYC()*75)+20)))){
          moves.push(validM);
        }
      }
    }
    if(!(tmpy1 > 7)){
      if(checkSurroundingSquares(tmp1)){
        validM2 = new Move(startingSquare, tmp1);
        if(!piecePresent(((tmp1.getXC()*75)+20), (((tmp1.getYC()*75)+20)))){
          moves.push(validM2);
        }
        else{
          if(checkWhiteOponent(((tmp1.getXC()*75)+20), (((tmp1.getYC()*75)+20)))){
            moves.push(validM2);
          }
        }
      }
    }
    if(!(tmpy2 < 0)){
      if(checkSurroundingSquares(tmp2)){
        validM3 = new Move(startingSquare, tmp2);
        if(!piecePresent(((tmp2.getXC()*75)+20), (((tmp2.getYC()*75)+20)))){
          moves.push(validM3);
        }
        else{
          System.out.println("The values that we are going to be looking at are : "+((tmp2.getXC()*75)+20)+" and the y value is : "+((tmp2.getYC()*75)+20));
          if(checkWhiteOponent(((tmp2.getXC()*75)+20), (((tmp2.getYC()*75)+20)))){
            moves.push(validM3);
          }
        }
      }
    }
  }
  if(!((tmpx2 < 0))){
    Square tmp3 = new Square(tmpx2, y, getPieceNameBySquare(tmpx2, y));
    Square tmp4 = new Square(tmpx2, tmpy1, getPieceNameBySquare(tmpx2, tmpy1));
    Square tmp5 = new Square(tmpx2, tmpy2, getPieceNameBySquare(tmpx2, tmpy2));
    if(checkSurroundingSquares(tmp3)){
      validM = new Move(startingSquare, tmp3);
      if(!piecePresent(((tmp3.getXC()*75)+20), (((tmp3.getYC()*75)+20)))){
        moves.push(validM);
      }
      else{
        if(checkWhiteOponent(((tmp3.getXC()*75)+20), (((tmp3.getYC()*75)+20)))){
          moves.push(validM);
        }
      }
    }
    if(!(tmpy1 > 7)){
      if(checkSurroundingSquares(tmp4)){
        validM2 = new Move(startingSquare, tmp4);
        if(!piecePresent(((tmp4.getXC()*75)+20), (((tmp4.getYC()*75)+20)))){
          moves.push(validM2);
        }
        else{
          if(checkWhiteOponent(((tmp4.getXC()*75)+20), (((tmp4.getYC()*75)+20)))){
            moves.push(validM2);
          }
        }
      }
    }
    if(!(tmpy2 < 0)){
      if(checkSurroundingSquares(tmp5)){
        validM3 = new Move(startingSquare, tmp5);
        if(!piecePresent(((tmp5.getXC()*75)+20), (((tmp5.getYC()*75)+20)))){
          moves.push(validM3);
        }
        else{
          if(checkWhiteOponent(((tmp5.getXC()*75)+20), (((tmp5.getYC()*75)+20)))){
            moves.push(validM3);
          }
        }
      }
    }
  }
  Square tmp7 = new Square(x, tmpy1, getPieceNameBySquare(x, tmpy1));
  Square tmp8 = new Square(x, tmpy2, getPieceNameBySquare(x, tmpy2));
  if(!(tmpy1 > 7)){
    if(checkSurroundingSquares(tmp7)){
      validM2 = new Move(startingSquare, tmp7);
      if(!piecePresent(((tmp7.getXC()*75)+20), (((tmp7.getYC()*75)+20)))){
        moves.push(validM2);
      }
      else{
        if(checkWhiteOponent(((tmp7.getXC()*75)+20), (((tmp7.getYC()*75)+20)))){
          moves.push(validM2);
        }
      }
    }
  }
  if(!(tmpy2 < 0)){
    if(checkSurroundingSquares(tmp8)){
      validM3 = new Move(startingSquare, tmp8);
      if(!piecePresent(((tmp8.getXC()*75)+20), (((tmp8.getYC()*75)+20)))){
        moves.push(validM3);
      }
      else{
        if(checkWhiteOponent(((tmp8.getXC()*75)+20), (((tmp8.getYC()*75)+20)))){
          moves.push(validM3);
        }
      }
    }
  }
  return moves;
} // end of the method getKingSquares()


/*
    Method to return all the possible moves that a Queen can make

    Method provided by the lecturer...won't touch it
*/
private Stack getQueenMoves(int x, int y, String piece){
  Stack completeMoves = new Stack();
  Stack tmpMoves = new Stack();
  Move tmp;
  /*
      The Queen is a pretty easy piece to figure out if you have completed the
      Bishop and the Rook movements. Either the Queen is going to move like a
      Bishop or its going to move like a Rook, so all we have to do is make a call to both of these
      methods.
  */
  tmpMoves = getRookMoves(x, y, piece);
  while(!tmpMoves.empty()){
    tmp = (Move)tmpMoves.pop();
    completeMoves.push(tmp);
  }
  tmpMoves = getBishopMoves(x, y, piece);
  while(!tmpMoves.empty()){
    tmp = (Move)tmpMoves.pop();
    completeMoves.push(tmp);
  }
  return completeMoves;
}

/*
  Method to return all the squares that a Rook can move to. The Rook can either move in an x direction or
  in a y direction as long as there is nothing in the way and it can take its opponents piece but not its
  own piece. As seen in the below grid the Rook can either move in a horizontal direction (x changing value)
  or in a vertical movement (y changing direction)

  Method provided by the lecturer...won't touch it
*/
private Stack getRookMoves(int x, int y, String piece){
  Square startingSquare = new Square(x, y, piece);
  Stack moves = new Stack();
  Move validM, validM2, validM3, validM4;
  /*
    There are four possible directions that the Rook can move to:
      - the x value is increasing
      - the x value is decreasing
      - the y value is increasing
      - the y value is decreasing

    Each of these movements should be catered for. The loop guard is set to incriment up to the maximun number of squares.
    On each iteration of the first loop we are adding the value of i to the current x coordinate.
    We make sure that the new potential square is going to be on the board and if it is we create a new square and a new potential
    move (originating square, new square).If there are no pieces present on the potential square we simply add it to the Stack
    of potential moves.
    If there is a piece on the square we need to check if its an opponent piece. If it is an opponent piece its a valid move, but we
    must break out of the loop using the Java break keyword as we can't jump over the piece and search for squares. If its not
    an opponent piece we simply break out of the loop.

    This cycle needs to happen four times for each of the possible directions of the Rook.
  */
  for(int i=1;i < 8;i++){
    int tmpx = x+i;
    int tmpy = y;
    if(!(tmpx > 7 || tmpx < 0)){
      Square tmp = new Square(tmpx, tmpy, getPieceNameBySquare(tmpx, tmpy));
      validM = new Move(startingSquare, tmp);
      if(!piecePresent(((tmp.getXC()*75)+20), (((tmp.getYC()*75)+20)))){
        moves.push(validM);
      }
      else{
        if(checkWhiteOponent(((tmp.getXC()*75)+20), ((tmp.getYC()*75)+20))){
          moves.push(validM);
          break;
        }
        else{
          break;
        }
      }
    }
  }//end of the loop with x increasing and Y doing nothing...
  for(int j=1;j < 8;j++){
    int tmpx1 = x-j;
    int tmpy1 = y;
    if(!(tmpx1 > 7 || tmpx1 < 0)){
      Square tmp2 = new Square(tmpx1, tmpy1, getPieceNameBySquare(tmpx1, tmpy1));
      validM2 = new Move(startingSquare, tmp2);
      if(!piecePresent(((tmp2.getXC()*75)+20), (((tmp2.getYC()*75)+20)))){
        moves.push(validM2);
      }
      else{
        if(checkWhiteOponent(((tmp2.getXC()*75)+20), ((tmp2.getYC()*75)+20))){
          moves.push(validM2);
          break;
        }
        else{
          break;
        }
      }
    }
  }//end of the loop with x increasing and Y doing nothing...
  for(int k=1;k < 8;k++){
    int tmpx3 = x;
    int tmpy3 = y+k;
    if(!(tmpy3 > 7 || tmpy3 < 0)){
      Square tmp3 = new Square(tmpx3, tmpy3, getPieceNameBySquare(tmpx3, tmpy3));
      validM3 = new Move(startingSquare, tmp3);
      if(!piecePresent(((tmp3.getXC()*75)+20), (((tmp3.getYC()*75)+20)))){
        moves.push(validM3);
      }
      else{
        if(checkWhiteOponent(((tmp3.getXC()*75)+20), ((tmp3.getYC()*75)+20))){
          moves.push(validM3);
          break;
        }
        else{
          break;
        }
      }
    }
  }//end of the loop with x increasing and Y doing nothing...
  for(int l=1;l < 8;l++){
    int tmpx4 = x;
    int tmpy4 = y-l;
    if(!(tmpy4 > 7 || tmpy4 < 0)){
      Square tmp4 = new Square(tmpx4, tmpy4, getPieceNameBySquare(tmpx4, tmpy4));
      validM4 = new Move(startingSquare, tmp4);
      if(!piecePresent(((tmp4.getXC()*75)+20), (((tmp4.getYC()*75)+20)))){
        moves.push(validM4);
      }
      else{
        if(checkWhiteOponent(((tmp4.getXC()*75)+20), ((tmp4.getYC()*75)+20))){
          moves.push(validM4);
          break;
        }
        else{
          break;
        }
      }
    }
  }//end of the loop with x increasing and Y doing nothing...
  return moves;
}// end of get Rook Moves.

/*
  Method to return all the squares that a Bishop can move to. As seen in the below grid, the Bishop
  can move in a diagonal moement. There are essentially four different directions from a single
  square that the Bishop can move along. The Bishop can move any distance along this diagonal
  as long as there is nothing in the way. The Bishop can also take an opponent piece but cannot take its
  own piece.

  Method provided by the lecturer...won't touch it
*/
private Stack getBishopMoves(int x, int y, String piece){
  Square startingSquare = new Square(x, y, piece);
  Stack moves = new Stack();
  Move validM, validM2, validM3, validM4;
  /*
    The Bishop can move along any diagonal until it hits an enemy piece or its own piece
    it cannot jump over its own piece. We need to use four different loops to go through the possible movements
    to identify possible squares to move to. The temporary squares, i.e. the values of x and y must change by the
    same amount on each iteration of each of the loops.

    If the new values of x and y are on the board, we create a new square and a new move (from the original square to the new
    square). We then check if there is a piece present on the new square:
    - if not we add the move as a possible new move
    - if there is a piece we make sure that we can capture our opponents piece and we cannot take our own piece
      and then we break out of the loop

    This process is repeated for each of the other three possible diagonals that the Bishop can travel along.

  */
  for(int i=1;i < 8;i++){
    int tmpx = x+i;
    int tmpy = y+i;
    if(!(tmpx > 7 || tmpx < 0 || tmpy > 7 || tmpy < 0)){
      Square tmp = new Square(tmpx, tmpy, getPieceNameBySquare(tmpx, tmpy));
      validM = new Move(startingSquare, tmp);
      if(!piecePresent(((tmp.getXC()*75)+20), (((tmp.getYC()*75)+20)))){
        moves.push(validM);
      }
      else{
        if(checkWhiteOponent(((tmp.getXC()*75)+20), ((tmp.getYC()*75)+20))){
          moves.push(validM);
          break;
        }
        else{
          break;
        }
      }
    }
  } // end of the first for Loop
  for(int k=1;k < 8;k++){
    int tmpk = x+k;
    int tmpy2 = y-k;
    if(!(tmpk > 7 || tmpk < 0 || tmpy2 > 7 || tmpy2 < 0)){
      Square tmpK1 = new Square(tmpk, tmpy2, getPieceNameBySquare(tmpk, tmpy2));
      validM2 = new Move(startingSquare, tmpK1);
      if(!piecePresent(((tmpK1.getXC()*75)+20), (((tmpK1.getYC()*75)+20)))){
        moves.push(validM2);
      }
      else{
        if(checkWhiteOponent(((tmpK1.getXC()*75)+20), ((tmpK1.getYC()*75)+20))){
          moves.push(validM2);
          break;
        }
        else{
          break;
        }
      }
    }
  } //end of second loop.
  for(int l=1;l < 8;l++){
    int tmpL2 = x-l;
    int tmpy3 = y+l;
    if(!(tmpL2 > 7 || tmpL2 < 0 || tmpy3 > 7 || tmpy3 < 0)){
      Square tmpLMov2 = new Square(tmpL2, tmpy3, getPieceNameBySquare(tmpL2, tmpy3));
      validM3 = new Move(startingSquare, tmpLMov2);
      if(!piecePresent(((tmpLMov2.getXC()*75)+20), (((tmpLMov2.getYC()*75)+20)))){
        moves.push(validM3);
      }
      else{
        if(checkWhiteOponent(((tmpLMov2.getXC()*75)+20), ((tmpLMov2.getYC()*75)+20))){
          moves.push(validM3);
          break;
        }
        else{
          break;
        }
      }
    }
  }// end of the third loop
  for(int n=1;n < 8;n++){
    int tmpN2 = x-n;
    int tmpy4 = y-n;
    if(!(tmpN2 > 7 || tmpN2 < 0 || tmpy4 > 7 || tmpy4 < 0)){
      Square tmpNmov2 = new Square(tmpN2, tmpy4, getPieceNameBySquare(tmpN2, tmpy4));
      validM4 = new Move(startingSquare, tmpNmov2);
      if(!piecePresent(((tmpNmov2.getXC()*75)+20), (((tmpNmov2.getYC()*75)+20)))){
        moves.push(validM4);
      }
      else{
        if(checkWhiteOponent(((tmpNmov2.getXC()*75)+20), ((tmpNmov2.getYC()*75)+20))){
          moves.push(validM4);
          break;
        }
        else{
          break;
        }
      }
    }
  }// end of the last loop
  return moves;
}


/*
    Method fo return all the squares that a Knight can attack. The knight is possibly the simplest piece
    to get possible movements from. The Knight can essentially move in an L direction from any square on the
    board as long as the landing square is on the board and we can take an opponents piece but not our own piece.

    Method provided by the lecturer...won't touch it
*/
private Stack getKnightMoves(int x, int y, String piece){
  Square startingSquare = new Square(x, y, piece);
  Stack moves = new Stack();
  Stack attackingMove = new Stack();
  Square s = new Square(x+1, y+2, getPieceNameBySquare(x+1, y+2));
  moves.push(s);
  Square s1 = new Square(x+1, y-2, getPieceNameBySquare(x+1, y-2));
  moves.push(s1);
  Square s2 = new Square(x-1, y+2, getPieceNameBySquare(x-1, y+2));
  moves.push(s2);
  Square s3 = new Square(x-1, y-2, getPieceNameBySquare(x-1, y-2));
  moves.push(s3);
  Square s4 = new Square(x+2, y+1, getPieceNameBySquare(x+2, y+1));
  moves.push(s4);
  Square s5 = new Square(x+2, y-1, getPieceNameBySquare(x+2, y-1));
  moves.push(s5);
  Square s6 = new Square(x-2, y+1, getPieceNameBySquare(x-2, y+1));
  moves.push(s6);
  Square s7 = new Square(x-2, y-1, getPieceNameBySquare(x-2, y-1));
  moves.push(s7);

  for(int i=0;i < 8;i++){
    Square tmp = (Square)moves.pop();
    Move tmpmove = new Move(startingSquare, tmp);
    if((tmp.getXC() < 0)||(tmp.getXC() > 7)||(tmp.getYC() < 0)||(tmp.getYC() > 7)){

    }
    else if(piecePresent(((tmp.getXC()*75)+20), (((tmp.getYC()*75)+20)))){
      if(piece.contains("White")){
        if(checkWhiteOponent(((tmp.getXC()*75)+20), ((tmp.getYC()*75)+20))){
          attackingMove.push(tmpmove);
        }
      }
    }
    else{
      attackingMove.push(tmpmove);
    }
  }
  return attackingMove;
}


/*
        Method to colour a stack of Squares (with possible moves)

        Method provided by the lecturer...won't touch it
*/
private void colorSquares(Stack squares){
  Border greenBorder = BorderFactory.createLineBorder(Color.GREEN, 3);
  while(!squares.empty()){
    Square s = (Square)squares.pop();
    int location = s.getXC() + ((s.getYC())*8);
    JPanel panel = (JPanel)chessBoard.getComponent(location);
    panel.setBorder(greenBorder);
  }
}

/*
    Method to get the landing square of a bunch of moves...

    Method provided by the lecturer...won't touch it
*/
private void getLandingSquares(Stack found){
  Move tmp;
  Square landing;
  Stack squares = new Stack();
  while(!found.empty()){
    tmp = (Move)found.pop();
    landing = (Square)tmp.getLanding();
    squares.push(landing);
  }
  colorSquares(squares);
}


/*
  Method to find all the White Pieces.

  Method provided by the lecturer...won't touch it
*/
  private Stack findWhitePieces(){
    Stack squares = new Stack();
    String icon;
    int x;
    int y;
    String pieceName;
    for(int i=0;i < 600;i+=75){
      for(int j=0;j < 600;j+=75){
        y = i/75;
        x=j/75;
        Component tmp = chessBoard.findComponentAt(j, i);
        if(tmp instanceof JLabel){
          chessPiece = (JLabel)tmp;
          icon = chessPiece.getIcon().toString();
          pieceName = icon.substring(0, (icon.length()-4));
          if(pieceName.contains("White")){
            Square stmp = new Square(x, y, pieceName);
            squares.push(stmp);
          }
        }
      }
    }
    return squares;
  }

  /*
  Method to find all the Black Pieces.
  */
  private Stack findBlackPieces(){
    Stack squares = new Stack();
    String icon;
    int x;
    int y;
    String pieceName;
    for(int i=0;i < 600;i+=75){
      for(int j=0;j < 600;j+=75){
        y = i/75;
        x=j/75;
        Component tmp = chessBoard.findComponentAt(j, i);
        if(tmp instanceof JLabel){
          chessPiece = (JLabel)tmp;
          icon = chessPiece.getIcon().toString();
          pieceName = icon.substring(0, (icon.length()-4));
          if(pieceName.contains("Black")){
            Square stmp = new Square(x, y, pieceName);
            squares.push(stmp);
          }
        }
      }
    }
    return squares;
  }

	/*
		This method checks if there is a piece present on a particular square.
	*/
	private Boolean piecePresent(int x, int y){
		Component c = chessBoard.findComponentAt(x, y);

    return !(c instanceof JPanel);
	}

	/*
		This is a method to check if a piece is a Black piece in the given newX,newY position.
	*/
	private Boolean checkWhiteOponent(int newX, int newY){
		Component c1 = chessBoard.findComponentAt(newX, newY);
		JLabel awaitingPiece = (JLabel)c1;
		String tmp1 = awaitingPiece.getIcon().toString();

    return tmp1.contains("Black");
	}

  /*
  Method remove the green borders from a square
  */
  private void resetBorders(){
    Border empty = BorderFactory.createEmptyBorder();
    for(int i=0;i < 64;i++){
      JPanel tmppanel = (JPanel)chessBoard.getComponent(i);
      tmppanel.setBorder(empty);
    }
  }

  /*
    The method printStack takes in a Stack of Moves and prints out all possible moves.

    Method provided by the lecturer...won't touch it
  */
  private void printStack(Stack input){
    Move m;
    Square s, l;
    while(!input.empty()){
      m = (Move)input.pop();
      s = (Square)m.getStart();
      l = (Square)m.getLanding();
      System.out.println("The possible move that was found is : ("+s.getXC()+" , "+s.getYC()+"), landing at ("+l.getXC()+" , "+l.getYC()+")");
    }
  }

  /*
    Decides which kind of AI entity is gonna move and makes that move for the white pieces
  */
  private void makeAIMove(){
    resetBorders();
    layeredPane.validate();
    layeredPane.repaint();
    Stack white = findWhitePieces();
    //for AI move we'll need to have the black possible moves as well
    Stack black = findBlackPieces();
    Stack completeMoves = new Stack();
    Stack completeBlackMoves = new Stack();
    Stack temporary;
    Move tmp;

    //looping through all the white pieces in order to decide which is the best move
    while(!white.empty()){
      //temporary variable that will hold the elemnt on the top of the stack
      Square s = (Square)white.pop();
      String tmpString = s.getName();
      Stack tmpMoves = new Stack();

      /*
          We need to identify all the possible moves that can be made by the AI Opponent
          getting the possible moves based on what kind of piece we're currently analysing
      */
      if(tmpString.contains("Knight")){
        tmpMoves = getKnightMoves(s.getXC(), s.getYC(), s.getName());
      }
      else if(tmpString.contains("Bishop")){
        tmpMoves = getBishopMoves(s.getXC(), s.getYC(), s.getName());
      }
      else if(tmpString.contains("Pawn")){
        tmpMoves = getWhitePawnSquares(s.getXC(), s.getYC(), s.getName());
      }
      else if(tmpString.contains("Rook")){
        tmpMoves = getRookMoves(s.getXC(), s.getYC(), s.getName());
      }
      else if(tmpString.contains("Queen")){
        tmpMoves = getQueenMoves(s.getXC(), s.getYC(), s.getName());
      }
      else if(tmpString.contains("King")){
        tmpMoves = getKingSquares(s.getXC(), s.getYC(), s.getName());
      }

      //removing the possible moves from the tmpMoves (that only hold moves for the current piece) into the complete moves (that holds moves for all white pieces)
      while(!tmpMoves.empty()){
        tmp = (Move)tmpMoves.pop();
        completeMoves.push(tmp);
      }
    }

    //looping through all the black pieces in order to decide which is the best move
    while(!black.empty()){
      //temporary variable that will hold the elemnt on the top of the stack
      Square s = (Square)black.pop();
      String tmpString = s.getName();
      Stack tmpMoves = new Stack();

      /*
          We need to identify all the possible moves that can be made by the AI Opponent
          getting the possible moves based on what kind of piece we're currently analysing
      */
      if(tmpString.contains("Knight")){
        tmpMoves = getKnightMoves(s.getXC(), s.getYC(), s.getName());
      }
      else if(tmpString.contains("Bishop")){
        tmpMoves = getBishopMoves(s.getXC(), s.getYC(), s.getName());
      }
      else if(tmpString.contains("Pawn")){
        tmpMoves = getBlackPawnSquares(s.getXC(), s.getYC(), s.getName());
      }
      else if(tmpString.contains("Rook")){
        tmpMoves = getRookMoves(s.getXC(), s.getYC(), s.getName());
      }
      else if(tmpString.contains("Queen")){
        tmpMoves = getQueenMoves(s.getXC(), s.getYC(), s.getName());
      }
      else if(tmpString.contains("King")){
        tmpMoves = getKingSquares(s.getXC(), s.getYC(), s.getName());
      }

      //removing the possible moves from the tmpMoves (that only hold moves for the current piece) into the complete moves (that holds moves for all white pieces)
      while(!tmpMoves.empty()){
        tmp = (Move)tmpMoves.pop();
        completeBlackMoves.push(tmp);
      }
    }

    //copying our complete moves into a temporary stack and colouring it
    temporary = (Stack)completeMoves.clone();
    getLandingSquares(temporary);
    printStack(temporary);

    //if there are no completeMoves, it means AI is in a Stale Mate and can't move, game is over.
    if(completeMoves.size() == 0){
      JOptionPane.showMessageDialog(null, "Cogratulations, you have placed the AI component in a Stale Mate Position");
      System.exit(0);
    }
    //if not in stale mate, it means white can move
    else{
      //going through possible white moves, getting the start and landing positions and then moving them to the testing, which is a helper variable
      Stack testing = new Stack();
      while(!completeMoves.empty()){
        Move tmpMove = (Move)completeMoves.pop();
        testing.push(tmpMove);
      }
      //going through possible black moves, getting the start and landing positions and then moving them to the testing2, which is a helper variable
      Stack testing2 = new Stack();
      while(!completeBlackMoves.empty()){
        Move tmpMove = (Move)completeBlackMoves.pop();
        Square s1 = (Square)tmpMove.getStart();
        Square s2 = (Square)tmpMove.getLanding();
        testing2.push(tmpMove);
      }
      Border redBorder = BorderFactory.createLineBorder(Color.RED, 3);
     
      //selects a move based on the player's choice at the begining of the match
      Move selectedMove = agentType==0 ? agent.randomMove(testing) : (agentType==1 ? agent.nextBestMove(testing) : agent.twoLevelsDeep(testing));
      Square startingPoint = (Square)selectedMove.getStart();
      Square landingPoint = (Square)selectedMove.getLanding();
      int startX1 = (startingPoint.getXC()*75)+20;
      int startY1 = (startingPoint.getYC()*75)+20;
      int landingX1 = (landingPoint.getXC()*75)+20;
      int landingY1 = (landingPoint.getYC()*75)+20;
      System.out.println("-------- Move "+startingPoint.getName()+" ("+startingPoint.getXC()+", "+startingPoint.getYC()+") to ("+landingPoint.getXC()+", "+landingPoint.getYC()+")");

      Component c  = (JLabel)chessBoard.findComponentAt(startX1, startY1);
      Container parent = c.getParent();
      parent.remove(c);
      int panelID = (startingPoint.getYC() * 8)+startingPoint.getXC();
      panels = (JPanel)chessBoard.getComponent(panelID);
      panels.setBorder(redBorder);
      parent.validate();

       Component l = chessBoard.findComponentAt(landingX1, landingY1);
       //if the AI move is taking over a piece, have to remove it from the board
       if(l instanceof JLabel){
          Container parentlanding = l.getParent();
          JLabel awaitingName = (JLabel)l;
          String agentCaptured = awaitingName.getIcon().toString();
          //if it's a king, game is over
          if(agentCaptured.contains("King")){
            agentwins = true;
          }
          parentlanding.remove(l);
          parentlanding.validate();
          pieces = new JLabel( new ImageIcon(startingPoint.getName()+".png") );
          int landingPanelID = (landingPoint.getYC()*8)+landingPoint.getXC();
          panels = (JPanel)chessBoard.getComponent(landingPanelID);
          panels.add(pieces);
          panels.setBorder(redBorder);
          layeredPane.validate();
          layeredPane.repaint();

          //letting user know that the game is over
          if(agentwins){
            JOptionPane.showMessageDialog(null, "The AI Agent has won!");
            System.exit(0);
          }
        }
        //if landing in a blank square
        else{
          pieces = new JLabel( new ImageIcon(startingPoint.getName()+".png") );
          int landingPanelID = (landingPoint.getYC()*8)+landingPoint.getXC();
          panels = (JPanel)chessBoard.getComponent(landingPanelID);
          panels.add(pieces);
          panels.setBorder(redBorder);
          layeredPane.validate();
          layeredPane.repaint();
        }
        white2Move = false;
  }
}

  /*
    This is a method to check if a piece is a white piece.
  */
  private Boolean checkBlackOponent(int newX, int newY){
    Component c1 = chessBoard.findComponentAt(newX, newY);
    JLabel awaitingPiece = (JLabel)c1;
    String tmp1 = awaitingPiece.getIcon().toString();

    return tmp1.contains("White");
  }

  /*
  Returns the piece in a given square, or empty if it's blank
  */
  private String getPieceName(int x, int y){
    Component c1 = chessBoard.findComponentAt(x, y);
    if(c1 instanceof JPanel){
			return "empty";
		}
		else if(c1 instanceof JLabel){
      JLabel awaitingPiece = (JLabel)c1;
  		String tmp1 = awaitingPiece.getIcon().toString();

			return tmp1;
		}
    else{
      return "empty";
    }
  }

  //the getPiceName requires x,y in pixels...this method adjusts the square number to pixels and returns the piece in that square
  private String getPieceNameBySquare(int x, int y){
    return getPieceName(x*75, y*75);
  }

  /*
  Checks if the piece was moved from the position x,y
  */
  private Boolean pieceMove(int x, int y){
    return !((startX == x)&&(startY == y));
  }

	/*
		This method is called when we press the Mouse. So we need to find out what piece we have
		selected. We may also not have selected a piece!
	*/
    public void mousePressed(MouseEvent e){
        chessPiece = null;
        String name = getPieceName(e.getX(), e.getY());
        /*if(moveCounter == 0){
          makeAIMove();
        }*/
        Component c =  chessBoard.findComponentAt(e.getX(), e.getY());
        if (c instanceof JPanel)
			     return;

        Point parentLocation = c.getParent().getLocation();
        xAdjustment = parentLocation.x - e.getX();
        yAdjustment = parentLocation.y - e.getY();
        chessPiece = (JLabel)c;
		    initialX = e.getX();
		    initialY = e.getY();
		    startX = (e.getX()/75);
	      startY = (e.getY()/75);

        if(name.contains("Knight")){

          getKnightMoves(startX, startY, name);
        }

        chessPiece.setLocation(e.getX() + xAdjustment, e.getY() + yAdjustment);
        chessPiece.setSize(chessPiece.getWidth(), chessPiece.getHeight());
        layeredPane.add(chessPiece, JLayeredPane.DRAG_LAYER);
    }

    public void mouseDragged(MouseEvent me) {
        if (chessPiece == null) return;
         chessPiece.setLocation(me.getX() + xAdjustment, me.getY() + yAdjustment);
     }

 	/*
		This method is used when the Mouse is released...we need to make sure the move was valid before
		putting the piece back on the board.
	*/
    public void mouseReleased(MouseEvent e) {
        if(chessPiece == null) return;

        chessPiece.setVisible(false);
		    Boolean success =false;
        Boolean promotion = false;
        Boolean progression = false;
        Component c =  chessBoard.findComponentAt(e.getX(), e.getY());

        String tmp = chessPiece.getIcon().toString();
		    String pieceName = tmp.substring(0, (tmp.length()-4));
		    Boolean validMove = false;

        int landingX = (e.getX()/75);
        int landingY  = (e.getY()/75);

        int xMovement = Math.abs((e.getX()/75)-startX);
        int yMovement = Math.abs((e.getY()/75)-startY);
        System.out.println("----------------------------------------------");
        System.out.println("The piece that is being moved is : "+pieceName);
        System.out.println("The starting coordinates are : "+"( "+startX+","+startY+")");
        System.out.println("The xMovement is : "+xMovement);
        System.out.println("The yMovement is : "+yMovement);
        System.out.println("The landing coordinates are : "+"( "+landingX+","+landingY+")");
        System.out.println("----------------------------------------------");

        /*
          We need a process to identify whos turn it is to make a move.
        */
        Boolean possible = false;

        if(white2Move){
          if(pieceName.contains("White")){
            possible = true;
          }
        }
        else{
          if(pieceName.contains("Black")){
            possible = true;
          }
        }
      if(possible){

        if(pieceName.contains("Bishop")){
          /*
            The Bishop is a simple piece and has a very long range so usually is used to attack from far away.
            It moves along the diagonal as long as nothing is in the way to its destination. Like all other Chess
            pieces it can take its opponent piece but cannot take its own piece.
          */
          Boolean inTheWay = false;
          if(((landingX < 0) || (landingX > 7))||((landingY < 0)||(landingY > 7))){
            validMove = false;
          }
          else if(!pieceMove(landingX, landingY)){
            validMove = false;
          }
          else{
            validMove = true;
            /*
              The condition to check for a validMove simple asks is the x movement is the same as the y
              movement, if so we are moving along a diagonal. We then need to identify which diagonal and
              make sure there is nothing in the way.
            */
            if(xMovement == yMovement){
              if((startX-landingX < 0)&&(startY-landingY < 0)){
                for(int i=0; i < xMovement;i++){
                  if(piecePresent((initialX+(i*75)), (initialY+(i*75)))){
                    inTheWay = true;
                  }
                }
              }
              else if((startX-landingX < 0)&&(startY-landingY > 0)){
                for(int i=0; i < xMovement;i++){
                  if(piecePresent((initialX+(i*75)), (initialY-(i*75)))){
                    inTheWay = true;
                  }
                }
              }
              else if((startX-landingX > 0)&&(startY-landingY > 0)){
                for(int i=0; i < xMovement;i++){
                  if(piecePresent((initialX-(i*75)), (initialY-(i*75)))){
                    inTheWay = true;
                  }
                }
              }
              else if((startX-landingX > 0)&&(startY-landingY < 0)){
                for(int i=0; i < xMovement;i++){
                  if(piecePresent((initialX-(i*75)), (initialY+(i*75)))){
                    inTheWay = true;
                  }
                }
              } // end of the conditions to check the appropriate diagonal to see if there is a piece in the way.
              if(inTheWay){
                validMove = false;
              }
              else{
                /*
                    This is cool there is nothing in the way and we just need to make sure that we are not able to
                    take our own piece.
                */
                if(piecePresent(e.getX(), (e.getY()))){
                  if(pieceName.contains("White")){
                    if(checkWhiteOponent(e.getX(), e.getY())){
                      validMove = true;
                      if(getPieceName(e.getX(), e.getY()).contains("King")){
                        winner = "White Wins!";
                      }
                    }
                    else{
                      validMove = false;
                    }
                  }
                  else{
                    if(checkBlackOponent(e.getX(), e.getY())){
                      validMove = true;
                      if(getPieceName(e.getX(), e.getY()).contains("King")){
                        winner = "Black Wins!";
                      }
                    }
                    else{
                      validMove = false;
                    }
                  }
                }
                else{ // There is no piecePresent on the square that we are moving to.
                  validMove = true;
                }
              }
            }
            else{
              validMove = false;
            }
          }
        }
        else if(pieceName.contains("Rook")){
          /*
            The Rook moves along the x direction or the y direction any number of squares as long as there
            is nothing in its way. It can take an opponents piece and as you know it cannot take its own piece.
          */
            Boolean intheway = false;
            if(((landingX < 0) || (landingX > 7))||((landingY < 0)||(landingY > 7))){
              validMove = false;
            }
            else if(!pieceMove(landingX, landingY)){
              validMove = false;
            }
            else{
              /*
                The following condition simply asks if we have either a movement in the x direction
                or in the y direction.
              */
              if(((xMovement!=0)&&(yMovement == 0))||((xMovement==0)&&(yMovement!=0)))
              {
                if(xMovement!=0){ // we have a movement in the x direction
                  if(startX-landingX > 0){
                    /*
                        We need to check all the squares up to the landing square to make sure nothing
                        is in the way.
                    */
                    for(int i=0;i < xMovement;i++){
                      if(piecePresent(initialX-(i*75), e.getY())){
                        intheway = true;
                        break;
                      }
                      else{
                        intheway = false;
                      }
                    }
                  }
                  else{ // we have a movement in the y direction
                    for(int i=0;i < xMovement;i++){
                      if(piecePresent(initialX+(i*75), e.getY())){
                        intheway = true;
                        break;
                      }
                      else{
                        intheway = false;
                      }
                    }
                  }
                }
                else{ // we must have a movement in the y dirction
                  if(startY-landingY > 0){
                    for(int i=0;i < yMovement;i++){
                      if(piecePresent(e.getX(),initialY-(i*75))){
                        intheway = true;
                        break;
                      }
                      else{
                        intheway = false;
                      }
                    }
                  }
                  else{
                    for(int i=0;i < yMovement;i++){
                      if(piecePresent(e.getX(),initialY+(i*75))){
                        intheway = true;
                        break;
                      }
                      else{
                        intheway = false;
                      }
                    }
                  }
                }
                if(intheway){
                  /*
                      if we have found that there is something in the way to the square we are trying to
                      move to we simply set the validMove boolean to false...
                  */
                  validMove = false;
                }
                else{
                  /*
                      okay so we have established that there is nothing in the way and we want to make try and
                      move the piece. The other condiion that we need to check is to see if there is a piece at
                      the landing square that we want to move to.
                  */
                  if(piecePresent(e.getX(), (e.getY()))){
                    if(pieceName.contains("White")){
                      if(checkWhiteOponent(e.getX(), e.getY())){
                        validMove = true;
                        /*
                          Every time we make a take a piece we need to check if the piece that has been captured
                          is a King.
                        */

                        if(getPieceName(e.getX(), e.getY()).contains("King")){
                          winner = "White Wins!";
                        }
                      }
                      else{
                        validMove = false;
                      }
                    }
                    else{
                      if(checkBlackOponent(e.getX(), e.getY())){
                        validMove = true;
                        if(getPieceName(e.getX(), e.getY()).contains("King")){
                          winner = "Black Wins!";
                        }
                      }
                      else{
                        validMove = false;
                      }
                    }
                  }
                  else{
                    validMove = true;
                  }
                }
            }
            else{
              validMove = false;
            }
          }
        }
        else if(pieceName.contains("Knight")){

          /*
              We know that the Knight can move in an L direction. It means that if the xMovement == 1 then the yMovement
              must equal 2 and also the other way around...
              We need to check the square that we are moving to and make sure that if there is a piece present
              that its not our own piece...
          */
          if(((xMovement == 1)&&(yMovement ==2))||((xMovement==2)&&(yMovement==1))){
            if(!piecePresent(e.getX(), e.getY())){
              validMove = true;
            }
            else{
              if(pieceName.contains("White")){
                if(checkWhiteOponent(e.getX(), e.getY())){
                  validMove = true;
                  if(getPieceName(e.getX(), e.getY()).contains("King")){
                    winner = "White Wins!";
                  }
                }
              }
              else{
                if(checkBlackOponent(e.getX(), e.getY())){
                  validMove = true;
                  if(getPieceName(e.getX(), e.getY()).contains("King")){
                    winner = "Black Wins!";
                  }
                }
              }
            }
          }
        }
        else if(pieceName.contains("King")){
          if((xMovement == 0) && (yMovement==0)){
                  validMove = false;
          }
          else{
            if(((landingX < 0) || (landingX > 7))||((landingY < 0)||(landingY > 7))){
              validMove = false;
            }
            else{
              if((xMovement > 1) || (yMovement > 1)){
                validMove = false;
              }
              else{
                /*
                    The Kind can only move one square at a time but also needs to make sure that there is at least one square between
                    both Kings once the move has concluded.
                */
                if((getPieceName((e.getX()+75), e.getY()).contains("King"))||(getPieceName((e.getX()-75), e.getY()).contains("King"))||(getPieceName((e.getX()),(e.getY()+75)).contains("King"))||(getPieceName((e.getX()), (e.getY()-75)).contains("King"))||(getPieceName((e.getX()+75),(e.getY()+75)).contains("King"))||(getPieceName((e.getX()-75),(e.getY()+75)).contains("King"))||(getPieceName((e.getX()+75),(e.getY()-75)).contains("King"))||(getPieceName((e.getX()-75), (e.getY()-75)).contains("King"))){
                        validMove = false;
                }
                else{
                  /*
                      We need to make sure that if there is a piece on the potential landing square that the King cannot takes its
                      own piece but can take an opponents piece.
                  */
                  if(piecePresent(e.getX(), e.getY())){
                          if(pieceName.contains("White")){
                            if(checkWhiteOponent(e.getX(), e.getY())){
                              validMove = true;
                            }
                          }
                          else{
                            if(checkBlackOponent(e.getX(), e.getY())){
                              validMove = true;
                            }
                          }
                        }
                        else{
                          validMove = true;
                        }
                      }
                    }
                  }
          }//end of zero distance check
        }
        else if(pieceName.contains("Queen")){
          boolean inTheWay = false;
          if(((landingX < 0) || (landingX > 7))||((landingY < 0)||(landingY > 7))){
            validMove = false;
          }
          else if(!pieceMove(landingX, landingY)){
            validMove = false;
          }
          else{
		          /*
			           The Queen can either move like the Rook or a Bishop...You already have this code working!

                 The first check is to see if the Queen is making a valid Bishop Like move...
		          */
              if(xMovement == yMovement){
                if((startX-landingX < 0)&&(startY-landingY < 0)){
                  for(int i=0; i < xMovement;i++){
                    if(piecePresent((initialX+(i*75)), (initialY+(i*75)))){
                      inTheWay = true;
                    }
                  }
                }
                else if((startX-landingX < 0)&&(startY-landingY > 0)){
                  for(int i=0; i < xMovement;i++){
                    if(piecePresent((initialX+(i*75)), (initialY-(i*75)))){
                      inTheWay = true;
                    }
                  }
                }
                else if((startX-landingX > 0)&&(startY-landingY > 0)){
                  for(int i=0; i < xMovement;i++){
                    if(piecePresent((initialX-(i*75)), (initialY-(i*75)))){
                      inTheWay = true;
                    }
                  }
                }
                else if((startX-landingX > 0)&&(startY-landingY < 0)){
                  for(int i=0; i < xMovement;i++){
                    if(piecePresent((initialX-(i*75)), (initialY+(i*75)))){
                      inTheWay = true;
                    }
                  }
                }

                if(inTheWay){
                  validMove = false;
                }
                else{
                  /*
                    If there is nothing in the way when moving the Queen like a Bishop, we have two cases:
                      - there is a piece on the square we are trying to move to and we need to make sure that its
                        an enemy piece
                      - or there is nothing in the way and everything is cool
                  */
                  if(piecePresent(e.getX(), (e.getY()))){
                    if(pieceName.contains("White")){
                      if(checkWhiteOponent(e.getX(), e.getY())){
                        validMove = true;
                        if(getPieceName(e.getX(), e.getY()).contains("King")){
                          winner = "White Wins!";
                        }
                      }
                    }
                    else{ // We are moving the Black Queen...
                      if(checkBlackOponent(e.getX(), e.getY())){
                        validMove = true;
                        if(getPieceName(e.getX(), e.getY()).contains("King")){
                          winner = "Black Wins!";
                        }
                      }
                    }
                  }
                  else{
                    validMove = true;
                  }
                }
              } // end of the condition where the Queen is moving like a Bishop
              else if(((xMovement!=0)&&(yMovement == 0))|| ((xMovement==0)&&(yMovement!=0))){
                /*
                    This is the condition that handles the Queen movements that are moving like a Rook
                */
                if(xMovement!=0){
                  //we have movement along the x axis
                  if(startX-landingX > 0){
                    //movement in the left direction....
                    for(int i=0;i < xMovement;i++){
                      if(piecePresent(initialX-(i*75), e.getY())){
                        inTheWay = true;
                        break;
                      }
                      else{
                        inTheWay = false;
                      }
                    }
                  }
                  else{
                    for(int i=0;i < xMovement;i++){
                      if(piecePresent(initialX+(i*75), e.getY())){
                        inTheWay = true;
                        break;
                      }
                      else{
                        inTheWay = false;
                      }
                    }
                  }
                }// This is the end of the xMovement condition
                else{
                  //we have movement along the y axis
                  if(startY-landingY > 0){
                    //movement in the left direction....
                    for(int i=0;i < yMovement;i++){
                      if(piecePresent(e.getX(),initialY-(i*75))){
                        inTheWay = true;
                        break;
                      }
                      else{
                        inTheWay = false;
                      }
                    }
                  }
                  else{
                    for(int i=0;i < yMovement;i++){
                      if(piecePresent(e.getX(),initialY+(i*75))){
                        inTheWay = true;
                        break;
                      }
                      else{
                        inTheWay = false;
                      }
                    }
                  }
                }

                if(inTheWay){
                  validMove = false;
                }
                else{
                  /*
                    This is cool and just like before there is now nothing in the way and the Queen should be able to make the
                    movement. We need to check if there is something on the square that we are moving to and if so make sure its
                    an enemy piece and if there is nothing on the square that we are moving to ... make the move.
                  */
                  if(piecePresent(e.getX(), (e.getY()))){
                    if(pieceName.contains("White")){
                      if(checkWhiteOponent(e.getX(), e.getY())){
                        validMove = true;
                        if(getPieceName(e.getX(), e.getY()).contains("King")){
                          winner = "White Wins!";
                        }
                      }
                    }
                    else{
                      if(checkBlackOponent(e.getX(), e.getY())){
                        validMove = true;
                        if(getPieceName(e.getX(), e.getY()).contains("King")){
                          winner = "Black Wins!";
                        }
                      }
                    }
                  }
                  else{
                    validMove = true;
                  }
                }
              }
            }
        }
        else if(pieceName.equals("BlackPawn")){
          if(startY == 6){ // This is were the pawn is making its first move....
            /*
            if the pawn is makeing ites first movement....
            the pawn can either move one square or two squares...in the Y direction
            as long as we are moving up the board!!! and also there is no movement in the
            x direction
            */
            if(((yMovement==1)||(yMovement == 2))&&(startY > landingY)&&(xMovement ==0)){
              if(yMovement == 2){
                if((!piecePresent(e.getX(), e.getY()))&&(!piecePresent(e.getX(), (e.getY()+75)))){
                  validMove = true;
                }
              }
              else{
                if(!piecePresent(e.getX(), e.getY())){
                  validMove = true;
                }
              }
            }
            else if((yMovement == 1)&&(startY > landingY)&&(xMovement == 1)){
              if(piecePresent(e.getX(), e.getY())){
                if(checkBlackOponent(e.getX(), e.getY())){
                  validMove = true;
                  if(getPieceName(e.getX(), e.getY()).contains("King")){
                    winner = "Black Wins!";
                  }
                }
              }
            }
          }
          else{// This is were the pawn is making all subsequent moves...
            if(((yMovement==1))&&(startY > landingY)&&(xMovement ==0)){
              if(!piecePresent(e.getX(), e.getY())){
                validMove = true;
                /*
                  We need a variable to inform us that the Black Pawn has reached the top
                  of the board and can now trun into a Black Queen
                */
                if(landingY == 0){
                  progression = true;
                }
              }
            }
            else if((yMovement == 1)&&(startY > landingY)&&(xMovement == 1)){
              if(piecePresent(e.getX(), e.getY())){
                if(checkBlackOponent(e.getX(), e.getY())){
                  validMove = true;
                  if(landingY == 0){
                    progression = true;
                  }
                  if(getPieceName(e.getX(), e.getY()).contains("King")){
                    winner = "Black Wins!";
                  }
                }
              }
            }
          }
        }
        else if(pieceName.equals("WhitePawn")){
			       if(startY == 1){
               if(((xMovement == 0))&&((yMovement==1)||((yMovement)==2))){
                 if(yMovement==2){
                   if((!piecePresent(e.getX(), (e.getY())))&&(!piecePresent(e.getX(), (e.getY()-75)))){
                     validMove = true;
                   }
                 }
                 else{
                   if((!piecePresent(e.getX(), (e.getY())))){
                     validMove = true;
                   }
                 }
               }
               else if((piecePresent(e.getX(), e.getY()))&&(xMovement == yMovement)&&(xMovement == 1)&&(startY < landingY)){
                 /*
                    This is the condition were we are checking for a move to take a piece when the white pawn is making its
                    first move. We check the following:
                      - that there is a piece present,
                      - we are moving in a diagonal,
                      - the movement is only one square,
                      - we are moving down the board
                 */
                 if(checkWhiteOponent(e.getX(), e.getY())){
                   validMove = true;
                   if(startY == 6){
                     success = true;
                   }
                   if(getPieceName(e.getX(), e.getY()).contains("King")){
                     winner = "White Wins!";
                   }
                 }
               }
             } // End of the movements for when the pawn is making its first move.
             else{
               if((startX-1 >= 0)||(startX +1 <=7)){
                 if((piecePresent(e.getX(), e.getY()))&&(xMovement == yMovement)&&(xMovement == 1)){
                   if(checkWhiteOponent(e.getX(), e.getY())){
                     validMove = true;
                     if(startY == 6){
                       success = true;
                     }
                     if(getPieceName(e.getX(), e.getY()).contains("King")){
                       winner = "White Wins!";
                     }
                   }
                 }// checking the case for when there is a piecePresent
                 else{
                   if(!piecePresent(e.getX(), (e.getY()))){
                     if(((xMovement == 0))&&((e.getY()/75)-startY)==1){
                       if(startY == 6){
                         success = true;
                       }
                       validMove = true;
                     }
                   }
                 }
               }
             }
           }
    } //end of possible.

    if(!validMove){
      int location=0;
      if(startY ==0){
        location = startX;
      }
      else{
        location  = (startY*8)+startX;
      }
      String pieceLocation = pieceName+".png";
      pieces = new JLabel( new ImageIcon(pieceLocation) );
      panels = (JPanel)chessBoard.getComponent(location);
      panels.add(pieces);
		}
		else{ // this is the condition where we have a valid move and need to visually show it.
      white2Move = !white2Move;

      if(progression){
        int location = 0 + (e.getX()/75);
				if (c instanceof JLabel){
	        Container parent = c.getParent();
          parent.remove(0);
					pieces = new JLabel( new ImageIcon("BlackQueen.png") );
					parent = (JPanel)chessBoard.getComponent(location);
			    parent.add(pieces);
				}
        if(winner !=null){
          JOptionPane.showMessageDialog(null, winner);
          System.exit(0);
        }
      }
			else if(success){
				int location = 56 + (e.getX()/75);
				if (c instanceof JLabel){
          Container parent = c.getParent();
          parent.remove(0);
					pieces = new JLabel( new ImageIcon("WhiteQueen.png") );
					parent = (JPanel)chessBoard.getComponent(location);
          parent.add(pieces);
				}
				else{
					Container parent = (Container)c;
          pieces = new JLabel( new ImageIcon("WhiteQueen.png") );
					parent = (JPanel)chessBoard.getComponent(location);
          parent.add(pieces);
				}
			}
			else{
				if (c instanceof JLabel){
          Container parent = c.getParent();
          parent.remove(0);
          parent.add( chessPiece );
        }
        else {
          Container parent = (Container)c;
          parent.add( chessPiece );
        }
        chessPiece.setVisible(true);
        if(winner !=null){
          JOptionPane.showMessageDialog(null, winner);
          System.exit(0);
        }
			}
      makeAIMove();
		} // end of the else condition
  }// end of the mouseReleased event.

    public void mouseClicked(MouseEvent e) {

    }
    public void mouseMoved(MouseEvent e) {
   }
    public void mouseEntered(MouseEvent e){

    }
    public void mouseExited(MouseEvent e) {

    }

	/*
		main method that starts the game and asks the player which kind of AI entity they'd like to play against.
	*/
    public static void main(String[] args) {
        ChessProject chess = new ChessProject();
        chess.setDefaultCloseOperation(DISPOSE_ON_CLOSE );
        chess.pack();
        chess.setResizable(true);
        chess.setLocationRelativeTo( null );
        chess.setVisible(true);
        Object[] options = {"Random Moves","Best Next Move","Based on Opponents Moves"};
        chess.agentType = JOptionPane.showOptionDialog(chess,"Lets play some Chess, choose your AI opponent","Introduction to AI Continuous Assessment", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,options[2]);
        System.out.println("The selected variable is : "+chess.agentType);
        chess.makeAIMove();
  }
}
