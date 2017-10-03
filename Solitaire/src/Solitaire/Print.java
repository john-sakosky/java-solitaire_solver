package Solitaire;
import java.util.ArrayList;
//import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Print handles all output methods for the program, both to the console and file.<p>
 * 
 * The output is built around a buffer using an ArrayList of strings.  Rather then 
 * each method outputting directly to the console or text file, every string is stored
 * in the buffer.  The method 'Output' is used to write the buffer to the console and or 
 * the text file.  It also clears the ArrayList after the data has been written.
 */
public class Print{
	
	private ArrayList<String> buffer = new ArrayList<String>();
	private PrintWriter fileOut;
	
	/**
	 * Output() writes the contents of the ArrayList 'buffer' to either the console and or
	 * the text file.  The selection of destinations is made via the boolean inputs.  After 
	 * completing the write operation, all values are cleared from 'buffer'
	 * 
	 * 
	 * @param console (boolean) when true, data is written to the console
	 * @param file	  (boolean) when true, data is written to the text file
	 */
	private void Output(boolean console, boolean file){
		if(console){
			for(int i=0; i<buffer.size(); i++){
				System.out.printf(buffer.get(i));
			}
		}
		if(file){
			for(int i=0; i<buffer.size(); i++){
				fileOut.printf(buffer.get(i));
			}
		}
		buffer.clear();
	}
	
	public Print() throws FileNotFoundException{
		fileOut = new PrintWriter("output.txt");
	}
	
	public void PrintBoard(Board board){
		if(board.getSizePile()>0&&board.getSizePile()>board.getPileShowMax()) buffer.add("XXX  ");
		else buffer.add("     ");
		if(board.getPileShowMax()>=3){ 
			PrintCard(board.getCardPile(board.getPileShowMax()-3), false); buffer.add("  ");
			PrintCard(board.getCardPile(board.getPileShowMax()-2), false); buffer.add("  ");
			PrintCard(board.getCardPile(board.getPileShowMax()-1), false); buffer.add("  |");
		}
		else if(board.getPileShowMax()>=2){ 
			PrintCard(board.getCardPile(board.getPileShowMax()-2), false); buffer.add("  ");
			PrintCard(board.getCardPile(board.getPileShowMax()-1), false); buffer.add("       |");
		}
		else if(board.getPileShowMax()>=1){ 
			PrintCard(board.getCardPile(board.getPileShowMax()-1), false); buffer.add("            |");
		}
		else buffer.add("               |");
		for(int i=0; i<4; i++){
			if(board.getSizeFoundation(i)>0){
				PrintCard(board.getCardFoundation(i), false);
			}
			else buffer.add("___");
			buffer.add("|");
		}
		
		
		buffer.add("%n%n   0    1    2    3    4    5    6%n");
		/*buffer.add("   "+stacks.get(0).size()
				+"    "+stacks.get(1).size()
				+"    "+stacks.get(2).size()
				+"    "+stacks.get(3).size()
				+"    "+stacks.get(4).size()
				+"    "+stacks.get(5).size()
				+"    "+stacks.get(6).size());*/
		
		for(int row=0; row<board.getMaxSizeStack(); row++){
			buffer.add(row + " ");
			for(int column=0; column<7; column++){
				if(row<board.getSizeStack(column)){
					PrintCard(board.getCardStack(column, row), false);
					buffer.add("  ");
				}
				else buffer.add("     ");
			}
			buffer.add("%n");
		}
		buffer.add("----------------------------------------------------------------------------%n");
		Output(true,true);
	}
	public void PrintBoardAll(Board board){
		//----------------
		buffer.add("//Printing Full Board%n");
		//----------------
		for(int i=0; i<board.getMaxSizeStack(); i++){
			for(int j=0; j<7; j++){
				if(i<board.getSizeStack(j)){
					PrintCard(board.getCardStack(j, i), true);
					buffer.add("  ");
				}
				else buffer.add("     ");
			}
			buffer.add("%n");
		}
	}
	public void PrintCard(Cards card, boolean forceVisable){
		if(!card.getVisibility() || forceVisable){
			buffer.add("XXX");
		}
		else{
			switch(card.getValue()){
			case Ace: 	buffer.add("A"); break;
			case two: 	buffer.add("2"); break;
			case three: buffer.add("3"); break;
			case four:	buffer.add("4"); break;
			case five:	buffer.add("5"); break;
			case six:	buffer.add("6"); break;
			case seven:	buffer.add("7"); break;
			case eight: buffer.add("8"); break;
			case nine:	buffer.add("9"); break;
			case ten:	buffer.add("1"); break;
			case Jack:	buffer.add("J"); break;
			case Queen:	buffer.add("Q"); break;
			case King:	buffer.add("K"); break;
			default:	buffer.add("?");
			}
			
			buffer.add("_");
			
			switch(card.getSuit()){
			case Hearts:	buffer.add("H"); break;
			case Spades:	buffer.add("S"); break;
			case Clubs:		buffer.add("C"); break;
			case Diamonds:	buffer.add("D"); break;
			default:		buffer.add("?");
			}
		}
	}
	public void PrintActionAces(Board board, Suits suit){
		buffer.add("Action - Moved Ace of "+suit+" to Foundation%n");
		PrintBoard(board);
	}
	public void PrintActionKings(Board board, Suits suit, int columnA, int columnB){
		buffer.add("Action - Moved Stack starting with King of "+suit+" from column "+columnA+" to "+columnB+"%n");
		PrintBoard(board);
	}
	public void PrintActionShift(Board board, Value value, Suits suit, int columnA, int columnB){
		buffer.add("Action - Moved Stack starting with the "+value+" of "+suit+" from column "+columnA+" to "+columnB+"%n");
		PrintBoard(board);
	}
	public void PrintActionPileToBoard(Board board, Value value, Suits suit, int columnA){
		buffer.add("Action - Moved "+value+" of "+suit+" from the pile to column "+columnA+"%n");
		PrintBoard(board);
	}
	public void PrintActionPileToFoundation(Board board, Value value, Suits suit){
		buffer.add("Action - Moved "+value+" of "+suit+" from the pile to the foundation%n");
		PrintBoard(board);
	}
	public void PrintActionBottoms(Board board, Value value, Suits suit, int columnA){
		buffer.add("Action - Moved "+value+" of "+suit+" from column "+columnA+" to the foundation.%n");
		PrintBoard(board);
	}
	public void PrintAction(){
	}
	public void PrintStatusFinal(boolean pass, int numberOfMoves){
		buffer.add("Game Over...%n");
		if(pass) buffer.add("Game is Complete!!! %nCompleted in "+numberOfMoves+" moves.%n");
		else buffer.add("Game could not finish :(%n");
		Output(true,true);
		fileOut.close();
	}
	public void PrintString(String s,boolean console, boolean file){
		buffer.add(s);
		Output(console,file);
	}
	public void PrintTest(){
		int var = 53;
		String[] part = new String[10];
		part[0]="Hello\nLine2";
		part[1]="\n\n";
		part[2]= ""+var;
		buffer.add(part[0]+part[1]+part[2]);
		buffer.add("Hello%nLine2");
		buffer.add("\n\n");
		buffer.add(""+var);
		Output(true,true);
	}
}