import java.util.*;

public class AIAgent{
  Random rand;

  public AIAgent(){
    rand = new Random();
  }

  /*
  The method randomMove takes as input a stack of potential moves that the AI agent
  can make. The agent uses a rondom number generator to randomly select a move from
  the inputted Stack and returns this to the calling agent.
  */
  public Move randomMove(Stack possibilities){
    return randomMove(possibilities, "AI agent randomly selected move: ");
  }

  public Move randomMove(Stack possibilities, String message) {
    int moveID = rand.nextInt(possibilities.size());
    System.out.println(message+moveID);
    for(int i=1;i < (possibilities.size()-(moveID));i++){
      possibilities.pop();
    }
    Move selectedMove = (Move)possibilities.pop();
    return selectedMove;
  }

  public Move nextBestMove(Stack possibilities){
    //cloining it, because it might be necessary to do a random on the available possibilities, so we need to save a copy
    Stack tmpPossibilities = (Stack) possibilities.clone();
    //it will hold a stack of the movements with the highets points
    Stack betterMoves = new Stack();
    //starts in -1, so the center of the board can be 0
    //points taken from https://www.masterclass.com/articles/chess-piece-guide#understanding-the-points-values-of-chess-pieces
    int movePoints = -1;

    for (int i = 0; i < tmpPossibilities.size(); i++) {
      Move possibility = (Move) possibilities.pop();
  
      //giving 0 points to the center of the board
      if (((possibility.getLanding().getYC()==3)||(possibility.getLanding().getYC()==4))&&(movePoints<=0)) {
        movePoints = 0;
        betterMoves.add(possibility);
      }
      //if it can take over a pawn
      if (possibility.getLanding().getName().contains("BlackPawn")) {
        //only matters if the it has currently less or euqal to 2 points(otherwise it has a better move)
        if (movePoints<=2) {
          //if it hass less than 2 points (but not equal), have to clear the stack
          if (movePoints<2)
            betterMoves.clear();
          movePoints = 2;
          betterMoves.add(possibility);
        }
      }
      //if it can take over a knight or a bishop
      if (possibility.getLanding().getName().contains("BlackKnight")||possibility.getLanding().getName().contains("Bishop")) {
        //only matters if the it has currently less or euqal to 3 points(otherwise it has a better move)
        if (movePoints<=3) {
          //if it hass less than 3 points (but not equal), have to clear the stack
          if (movePoints<3)
            betterMoves.clear();
          movePoints = 3;
          betterMoves.add(possibility);
        }
      }
      //if it can take over a rook
      if (possibility.getLanding().getName().contains("BlackRook")){
        //only matters if the it has currently less or euqal to 5 points(otherwise it has a better move)
        if (movePoints<=5) {
          //if it hass less than 5 points (but not equal), have to clear the stack
          if (movePoints<5)
            betterMoves.clear();
          movePoints = 5;
          betterMoves.add(possibility);
        }
      }
      //if it can take over a queen
      if (possibility.getLanding().getName().contains("BlackQueen")){
        //only matters if the it has currently less or euqal to 9 points(otherwise it has a better move)
        if (movePoints<=9) {
          //if it hass less than 9 points (but not equal), have to clear the stack
          if (movePoints<9)
            betterMoves.clear();
          movePoints = 9;
          betterMoves.add(possibility);
        }
      }
      //if it can take over the king
      if (possibility.getLanding().getName().contains("BlackKing")){
        if (movePoints<100) {
          movePoints = 100;
          betterMoves.clear();
          betterMoves.add(possibility);
          break;
        }
      }
    }

    //if no points were awarded, then just does a random move
    //if not performs a random on the betterMoves stack
    return (movePoints<0) ? randomMove(tmpPossibilities) : randomMove(betterMoves, "AI agent selected a move based on next move: ");
  }

  //for now only random moves were implemented
  public Move twoLevelsDeep(Stack possibilities){
    return randomMove(possibilities, "AI agent selected a move based on a two level deep: ");
  }
}
