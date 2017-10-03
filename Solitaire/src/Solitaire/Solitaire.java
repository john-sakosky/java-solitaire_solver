/* Solitaire Project
 * John Sakosky
 * 
 * Build 100
 * 
 * Some notes, 
 * Used 'visibility'(boolean) instead of FaceUp/FaceDown(enum)
 * 
 */
package Solitaire;
//import java.io.FileNotFoundException;
import java.io.IOException;

public class Solitaire {
	
	static Board board = new Board();
	static Print printer;
	
	public static void main(String[] args) throws IOException{
		printer = new Print();
		
		//System.out.printf("Progress:%n[");
		//for(int i=0; i<100; i++){System.out.print("-");}
		//System.out.printf("]%n ");
		//int[][] results = new int[100000][2];
		//for(int i=0; i<100000; i++){
			boolean end = false;
			board = new Board();
			//printer.PrintBoard(board);
			board.incrementPile();
			int numberOfMoves = 0;
			while(!end){
				//Logic
				end = Logic();
				numberOfMoves++;
			}
			
			FinalStatus(numberOfMoves);
			//if(FinalStatus(numberOfMoves)) results[i][0]=1;
			//else results[i][0]=0;
			//results[i][1]=numberOfMoves;
			//if(i%1000==0) System.out.print("*");
		//}
		/*
		int a=0;
		int b=0;
		for(int i=0; i<100000; i++){
			a += results[i][0];
			if(results[i][0]==1)b += results[i][1];
		}
		b = b/a;
		System.out.printf("%ncompleted "+a+" out of 1000000 games in an avg of "+b+"moves.");
		for(int i=0; i<1000; i++){
			System.out.printf("%n");
			for(int j=0; j<100; j++){
				if(results[i*100+j][0]==1)System.out.printf("x");
				else System.out.printf(" ");
			}
		}*/
	}
	
	public static boolean Logic() throws IOException{
		boolean cont = true;
		boolean end = true;
		
		//Logic
		while(cont){
			cont = Aces();
			if(cont) {end = false; WaitForInput();}
		}
		cont = true;
		while(cont){
			cont = Shift();
			if(cont){end = false; WaitForInput();}
		}
		if(Kings()) {end = false; WaitForInput();}
		else if(Pile(false)) {end = false; WaitForInput();}
		else if(Bottoms()) {end = false; WaitForInput();}
		else if(Pile(true)) {end = false; WaitForInput();}
		else if(PileToFoundation()) {end = false; WaitForInput();}
		return end;
	}
	/**
	* Aces() 
	* Analyzes board to find any movable aces.  If ace is found, method analyzes foundation to find the 
	* first empty column then moves the ace there by calling 'board' method 'moveBoardToFoundation' passing the 
	* originating stack column and the destination foundation column.<p>
	* 
	* --Internal Operations--
	* Uses 'for' loop (0-6) to select a stack column, then getCardStackLast().getValue().ordinal()
	* to return the card value as an int for easy comparison.
	* If the card is an ace, a second 'for' loop is used to select a foundation column, and using getSizeFoundation()
	* determine if the column is empty.<p>
	* 
	* --Result--
	* If the method succeeds in locating and moving an ace, the flag 'move' is set to true, else
	* it maintains it's default state of false.
	* 
	* @return	returns the boolean flag 'move'
	*/
	public static boolean Aces(){
		boolean move = false;
		for(int i=0; i<7; i++){
			if(board.getSizeStack(i)>0){
				if(board.getCardStackLast(i).getValue().ordinal()==0){
					int foundation=0;
					for(int j=0; j<4;j++){
						if(board.getSizeFoundation(j)==0){
							foundation = j;
							j=4;
						}
					}
					move = true;
					Suits suit = board.getCardStackLast(i).getSuit();
					board.moveBoardToFoundation(i, foundation);
					printer.PrintActionAces(board, suit);
				}
			}
		}
		return move;
	}
	/**
	* Shift() 
	* Analyzes the main stacks of the board to find groups of cards to move. 
	* using a 'while' loop, it finds the first column with cards.  It then finds
	* the top-most visable card of that column and compares it with the bottom 
	* card of the other columns.  If the top most card of the first column is one
	* value lower and of the opposite color of the bottom card on the second stack, 
	* the top-most card and any cards beneath it are moved to the second column<p>
	* 
	* --Result--
	* If the method succeeds in locating and moving an card or group of cards, the flag 'move' is set to true, else
	* it maintains it's default state of false.
	* 
	* @return	returns the boolean flag 'move'
	*/
	public static boolean Shift(){
		boolean move = false;
		int columnA=0;
		while(columnA<7 && !move){
			int sizeStackA = board.getSizeStack(columnA);
			if(sizeStackA>0){
				Cards card1 = new Cards();
				int row = 0;
				for(int i=(sizeStackA-1);i>=0;i--){
					if(board.getCardStack(columnA, i).getVisibility()) row = i;
				}
				card1 = board.getCardStack(columnA, row);
				int columnB=0;
				while(columnB<7 && !move){
					if(columnB != columnA){
						if(board.getSizeStack(columnB)>0){
							Cards card2 = board.getCardStackLast(columnB);
							Color color1 = card1.getColor();
							Color color2 = card2.getColor();
							int name1 = card1.getValue().ordinal();
							int name2 = card2.getValue().ordinal()-1;
							
							if(color1 != color2 && name1 == name2){
								move = true;
								board.moveBoardToBoard(columnA, row, columnB,false);
								printer.PrintActionShift(board, card1.getValue(), card1.getSuit(), columnA, columnB);
							}
						}
					}
					columnB++;
				}
			}
			columnA++;
		}
		return move;
	}
	/**
	* Kings() 
	* Looks for visible Kings on the board, if one is found, the method looks for an empty column to move it to.  
	* If an empty column is found, it moves the king and any lesser cards on it to the empty column<p>
	* 
	* --Result--
	* If a king is moved, the boolean flag 'move' is set to true, else it retains it's default value of false.
	* 
	* @return	returns the boolean flag 'move'
	*/
	public static boolean Kings(){
		boolean move = false;
		int columnA=0;
		while(columnA<7 && !move){
			int sizeStackA = board.getSizeStack(columnA);
			if(sizeStackA>1){
				int row = 0;
				for(int i=(sizeStackA-1);i>=0;i--){
					if(board.getCardStack(columnA, i).getVisibility()) row = i;
				}
				if(board.getCardStack(columnA, row).getValue()==Value.King&&row>0){
					int columnB=0;
					while(columnB<7 && !move){
						if(columnB != columnA){
							if(board.getSizeStack(columnB)==0){
								move = true;
								Suits suit = board.getCardStack(columnA, row).getSuit();
								board.moveBoardToBoard(columnA, row, columnB,true);
								printer.PrintActionKings(board, suit, columnA, columnB);
							}
						}
						columnB++;
					}
				}
			}
			columnA++;
		}
		return move;
	}
	/**
	* Bottoms() 
	*  Checks if any of the bottom cards in the stacks can be moved to the foundation.  
	*  If a match is found, the card is transfered to the foundation.<p>
	* 
	* --Result--
	* If a card is moved to the foundation, the boolean flag 'move' is set to true, else 
	* it retains it's default value of false.
	* 
	* 
	* @return	returns the boolean flag 'move'
	*/
	public static boolean Bottoms(){
		boolean move = false;
		for(int columnA=0; columnA<7; columnA++){
			if(board.getSizeStack(columnA)>0){
				Cards card1 = board.getCardStackLast(columnA);
				for(int foundation=0; foundation<4; foundation++){
					if(board.getSizeFoundation(foundation)>0){
						Cards card2 = board.getCardFoundation(foundation);
						Suits suit1 = card1.getSuit();
						Suits suit2 = card2.getSuit();
						int value1 = card1.getValue().ordinal();
						int value2 = card2.getValue().ordinal()+1;
						
						if(suit1 == suit2 && value1 == value2){
							//System.out.println(columnA+" to F"+foundation+"..."+suit1 +" to "+ suit2 +" "+ value1 +" to "+ value2);
							move = true;
							Value value = card1.getValue();
							board.moveBoardToFoundation(columnA, foundation);
							printer.PrintActionBottoms(board, value, suit1, columnA);
						}
					}
				}
			}
		}
		return move;
	}
	/**
	* Pile() 
	* Analyzes the pile to determine if any cards can be moved to the stacks.  There are 3 parts 
	* to the analysis performed on each card.  If the card is an Ace, it is sent directly to the 
	* foundation.  If kings are allowed to be placed on the board (determined by input 'king') and the 
	* card a king, the method looks for empty columns on the board and moves the card if there are.  The
	* third is the standard analysis, comparing the card to the bottom cards in each column.<p>
	* 
	* --Result--
	* If any card is moved from the pile, the boolean flag 'move' is set to true, else 
	* it retains it's default value of false. 
	* 
	* @param  kings (boolean) determines weather kings may be placed on the board.
	* @return	returns the boolean flag 'move'
	*/
	public static boolean Pile(boolean kings){
		boolean move = false;
		int count = 0;
		while(!move && count<board.getSizePile()){
			if(board.usePile()){
				Cards card1 = board.getCardPileLast();
				//If card is Ace, move directly to foundation
				if(card1.getValue().ordinal()==0){
					int foundation=0;
					for(int j=0; j<4;j++){
						if(board.getSizeFoundation(j)==0){
							foundation = j;
							j=4;
						}
					}
					move = true;
					board.movePileToFoundation(foundation);
					//printer.PrintActionPileToFoundation(board, card1.getValue(), card1.getSuit());
				}
				else{
					int column = 0;
					while(column<7 && !move){
						//If king logic is activated and card is a king, move to first empty column on board if available.
						if(kings){
							if(board.getSizeStack(column)==0 && card1.getValue()==Value.King){
								//System.out.println("Pile to col: "+column);
								move = true;
								board.movePileToBoard(column);
								printer.PrintActionPileToBoard(board, card1.getValue(), card1.getSuit(), column);
							}
						}
						
						else if(board.getSizeStack(column)>0){
							Cards card2 = board.getCardStackLast(column);
							Color color1 = card1.getColor();
							Color color2 = card2.getColor();
							int value1 = card1.getValue().ordinal();
							int value2 = card2.getValue().ordinal()-1;
							
							if(color1 != color2 && value1 == value2){
								//System.out.println("Pile to col: "+column);
								move = true;
								board.movePileToBoard(column);
								printer.PrintActionPileToBoard(board, card1.getValue(), card1.getSuit(), column);
							}
						}
						column++;
					}
				}
			}
			board.incrementPile();
			count++;
		}
		return move;
	}
	/**
	* PileToFoundation() 
	* Analyzes the pile to find a card that can be moved to the foundation.  
	* The pile is incremented up to 1 complete cycle(every card in the pile is checked).
	* If a card in the pile is of the same suit and has a value one greater then that 
	* of a card in the foundation, that card is moved to the corresponding column 
	* of the foundation<p>
	* 
	* --Result--
	* If a card is moved from the pile to the foundation, the boolean flag 'move' is
	* set to 'true', else it retains its initial value of 'false.
	* 
	* @return	returns the boolean flag 'move'
	*/
	public static boolean PileToFoundation(){
		boolean move = false;
		int count = 0;
		while(!move && count<board.getSizePile()){
			if(board.usePile()){
				Cards card1 = board.getCardPileLast();
				for(int foundation=0; foundation<4; foundation++){
					if(board.getSizeFoundation(foundation)>0){
						Cards card2 = board.getCardFoundation(foundation);
						Suits suit1 = card1.getSuit();
						Suits suit2 = card2.getSuit();
						int value1 = card1.getValue().ordinal();
						int value2 = card2.getValue().ordinal()+1;
						
						if(suit1 == suit2 && value1 == value2){
							move = true;
							board.movePileToFoundation(foundation);
							printer.PrintActionPileToFoundation(board, card1.getValue(), suit1);
						}
					}
				}
			}
			board.incrementPile();
			count++;
		}
		return move;
	}
	/**
	* FinalStatus() 
	* Analyzes the board to determine if the game has been completed (all cards
	* in their respective foundation pile).  If the pile and all stacks are empty, the function 
	* ValidateFoundation() is called to evaluate the foundation for proper card order.  The 
	* result of these evaluations is stored in boolean 'pass'.  If any of the tests fail, 'pass'
	* is set to false.  The value of 'pass' along with the number of moves performed is sent to
	*  the printer to display the final results.<p>
	* 
	* --Result--
	* The value 'pass' is returned reflecting the generalized outcome of the tests performed.
	* 
	* @param  numberOfMoves  (int) value recording the total moves made in the game
	* @return	returns the boolean flag 'pass'
	*/
	public static boolean FinalStatus(int numberOfMoves){
		boolean pass = true;
		if(board.getSizePile()>0) pass = false;
		for(int column=0; column<7; column++){
			if(board.getSizeStack(column)>0) pass = false;
		}
		if(!board.ValidateFoundation()) pass = false;
		printer.PrintStatusFinal(pass, numberOfMoves);
		return pass;
	}
	/**
	* WaitForInput() 
	* Is used to pause the program between moves, displaying a message, and waiting for the 
	* user to press the 'Enter' button to resume the program.<p>
	* 
	* 
	*/
	public static void WaitForInput() throws IOException{
		printer.PrintString("Press Enter to continue...%n%n", true, false);
		//System.in.read();
	}

}

