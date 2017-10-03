package Solitaire;
import java.util.ArrayList;

/**
* The Board class contains the structures and methods to place and move cards.<p>
* There are 3 ArrayLists that comprise the three separate areas for cards.  The stacks 
* are the 7 columns of cards that represent the main focus of the game.  The foundation
* is the 4 columns that the cards end up in.  And the pile of remaining cards, herein 
* refereed to just as 'pile'.<p>
* In the interest of efficiency, the ArrayLists do not contain objects, merely an integer that 
* refers to the actual card within the deck(the pseudo equivalent of pointers).
* 
*/
public class Board{
	private Deck deck;
	//private Cards[] cards = new Cards[52];
	//private ArrayList<Integer> deck = new ArrayList<Integer>();
	private ArrayList<ArrayList<Integer>> stacks = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Integer>> foundation = new ArrayList<ArrayList<Integer>>();
	private ArrayList<Integer> pile = new ArrayList<Integer>();
	private int pileShowMax = 0;
	private int pileRuns = 0;
	private int pileRunMax = 3;
	private int pileMoves = 0;
	
	
	public Board(){
		CreateBoard();
		deck = new Deck();
		deck.Shuffle();
		//deck.PrintDeck();
		Deal();
		//PrintBoardAll();
		//PrintBoard();
		
	}
	private void CreateBoard(){
		for(int i=0; i<7; i++){
			stacks.add(new ArrayList<Integer>());
		}
		for(int i=0; i<4; i++){
			foundation.add(new ArrayList<Integer>());
		}
		//System.out.println("stacks = "+stacks.size());
	}
	/**
	 * CheckVisibility()  
	 * Evaluates each column in stacks to ensure the bottom card is visible (face up).  
	 * If a bottom card is not visible (typically after the car below it was moved), 
	 * it's visibility is changed to true using deck.setVisibility().
	 * 
	 */
	private void CheckVisability(int column){
		if(stacks.get(column).size()>0){
			int cardNum = (stacks.get(column).get(stacks.get(column).size()-1));
			if(!deck.getCardVisability(cardNum)){
				deck.setCardVisability(cardNum, true);
			}
		}
		
	}
	
	public void Deal(){
		int a = 0;
		
		for(int i=0; i<7; i++){
			for(int j=i; j<7; j++){
				stacks.get(j).add(a++);
			}
			deck.setCardVisability(stacks.get(i).get(getSizeStack(i)-1),true);
			//--------------------
			//System.out.print("Set card: in column " + i + " and row " + (getSizeStack(i)-1) + " to visable ");
			//deck.PrintCardVisable(stacks.get(i).get(getSizeStack(i)-1));
			//System.out.println();
			//--------------------
		}
		while(a<52){
			pile.add(a++);
			deck.setCardVisability(a-1,true);
		}
	}
	
	public Cards getCardStack(int column, int row){
		return deck.getCard(stacks.get(column).get(row));
	}
	public Cards getCardStackLast(int column){
		if(stacks.get(column).size()==0) System.out.println("Error: Attempted to get card from empty stack!");
		return deck.getCard(stacks.get(column).get(stacks.get(column).size()-1));
	}
	public Cards getCardFoundation(int column){
		return deck.getCard(foundation.get(column).get(foundation.get(column).size()-1));
	}
	public Cards getCardPile(int i){
		return deck.getCard(pile.get(i));
	}
	public Cards getCardPileLast(){
		return deck.getCard(pile.get(pileShowMax-1));
	}
	public int getPileShowMax(){
		return pileShowMax;
	}
	public boolean usePile(){
		if(pileShowMax>0&&(pileMoves<(pileRunMax*52)))return true;
		else return false;
	}
	public int getSizeFoundation(int column){
		return foundation.get(column).size();
	}
	public int getSizeStack(int stackNum){
		//if(stacks.get(stackNum).isEmpty()) return 0;
		int a=stacks.get(stackNum).size();
		//System.out.println(a);
		return a;
	}
	public int getSizePile(){
		return pile.size();
	}
	public int getMaxSizeStack(){
		int a=0;
		for(int i=0; i<stacks.size(); i++){
			int b = getSizeStack(i);
			if(a<b) a=b;
		}
		return a;
	}
	public void incrementPile (){
		int pileSize = pile.size();
		if(pileShowMax==pileSize) pileShowMax = 0;
		else{
			pileShowMax += 1;
			if(pileShowMax>pileSize) pileShowMax = pileSize;
		}
		pileMoves++;
		//PrintBoard();
	}
	public void moveBoardToBoard(int columnStart, int rowStart, int columnFinish, boolean kings){
		if(!deck.getCardVisability(stacks.get(columnStart).get(rowStart))) System.out.println("Cannot move this card");
		else if(kings||(deck.getCardColor(stacks.get(columnStart).get(rowStart)) != deck.getCardColor(stacks.get(columnFinish).get(stacks.get(columnFinish).size()-1)))){
			while(stacks.get(columnStart).size()>rowStart){
				stacks.get(columnFinish).add(stacks.get(columnStart).get(rowStart));
				stacks.get(columnStart).remove(rowStart);
			}
		}
		else System.out.println("Invald move");
		//Flip the exposed card from the column that the cards were moved from
		CheckVisability(columnStart);
		//PrintBoard();
	}
	public void moveBoardToFoundation(int columnStacks, int columnFoundation){
		int row = stacks.get(columnStacks).size()-1;
		foundation.get(columnFoundation).add(stacks.get(columnStacks).get(row));
		stacks.get(columnStacks).remove(row);
		CheckVisability(columnStacks);
		//PrintBoard();
	}
	public void movePileToBoard(int column){
		//pileMaxShow
		stacks.get(column).add(pile.get(pileShowMax-1));
		pile.remove(pileShowMax-1);
		pileShowMax--;
		//PrintBoard();
		pileMoves += (pileRunMax-pileRuns);
	}
	public void movePileToFoundation(int column){
		foundation.get(column).add(pile.get(pileShowMax-1));
		pile.remove(pileShowMax-1);
		pileShowMax--;
		//PrintBoard();
		pileMoves += (pileRunMax-pileRuns);
	}
	/**
	 * ValidateFoundation()  
	 * Examines order of cards in foundation
	 */
	public boolean ValidateFoundation(){
		boolean pass = true;
		for(int column = 0; column<4; column++){
			int row = 0;
			while((row+1)<foundation.get(column).size()){
				Cards card1 = deck.getCard(foundation.get(column).get(row));
				Cards card2 = deck.getCard(foundation.get(column).get(row+1));
				if(card1.getSuit() != card2.getSuit()) pass = false;
				else if(card1.getValue().ordinal() != card2.getValue().ordinal()-1) pass = false;
				row++;
			}
		}
		return pass;
	}
	
	
}