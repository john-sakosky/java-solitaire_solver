package Solitaire;
import java.util.ArrayList;
import java.util.Random;


public class Deck{
	private Cards[] cards = new Cards[52];
	private ArrayList<Integer> deck = new ArrayList<Integer>();
	
	
	public Deck(){
		for(int i=0; i<4; i++){
			for(int j=0; j<13; j++){
				//Create new card passing unique combination of suit and name.
				cards[i*13+j] = new Cards(Suits.values()[i],Value.values()[j]);
				deck.add(i*13+j);
			}
		}
	}
	
	public void Shuffle(){
		ShuffleMongean();
		ShuffleRandom();
	}
	//Shuffle using the over-under method (Mongean Shuffle) by moving every other card to the bottom of the deck
	public void ShuffleMongean(){
		for(int i=1; i<52; i+=2){
			deck.add(0,deck.get(i));
			deck.remove(i+1);
		}
	}
	//Shuffle by moving cards to random positions
	public void ShuffleRandom(){
		Random seed = new Random();
		Random generator = new Random(seed.nextInt());
		int newPosition;
		for(int i=0; i<52; i++){
			newPosition = generator.nextInt(52);
			if(newPosition != i){
				deck.add(newPosition,deck.get(i));
				if(newPosition<i) deck.remove(i+1);
				else deck.remove(i);
			}
		}
	}
	public void setCardVisability(int i, boolean visable){
		cards[deck.get(i)].setVisability(visable);
	}
	public Cards getCard(int i){
		return cards[deck.get(i)];
	}
	public Color getCardColor(int i){
		return cards[deck.get(i)].getColor();
	}
	public boolean getCardVisability(int i){
		return cards[deck.get(i)].getVisibility();
	}
	
	
}