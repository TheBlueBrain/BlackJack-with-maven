package orgs;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Player{
	List<Card> hand;
	static long TOTAL_MAX_ID = 0;
	boolean isReady = false;
	long id;
	long money = 2000;
	long bet = 0;
	float winMultiplier = 1;
	Socket socket;
	int score = 0;
	public Player(Socket s){
		socket = s;
		hand = new ArrayList<>();
		id = TOTAL_MAX_ID++;
	}

	public void emptyHand(){
		hand.clear();
	}
	public void notifyofOtherPlayer(long id, List<Card> hand){
		if(!hand.isEmpty()){
			String cards = hand.get(0).toString();
			for(int i = 1; i < hand.size(); i++){
				cards += ", " + hand.get(i).toString();
			}

			try{
				Connections.sendMessage(socket, "PLAYER " + id + " HAS: " + cards);
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		}else{
			try{
				Connections.sendMessage(socket, "PLAYER " + id + " HAS NO CARDS");
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		}

	}
	public boolean nextRount(){
		try{
			while(true){
				Connections.sendMessage(socket, "ANOTHER GAME?(YES/NO)");
				String resp = Connections.getMessage(socket);
				if(resp.equals("YES")){
					return true;
				}else if(resp.equals("NO")){
					return false;
				}
			}
		}catch(IOException e){
			return false;
		}
	}
	public void getBet(){
		try{
			while(true){
				Connections.sendMessage(socket, "CURRENT MONEY AMOUNT: "+ money +"HOW MUCH TO BET?(INTEGER)");
				String resp = Connections.getMessage(socket);
				long bet = Long.parseLong(resp);
				if(bet > money){
					Connections.sendMessage(socket, "NOT ENOUGH MONEY");
				}else if(bet < 0){
					Connections.sendMessage(socket, "BET CANNOT BE NEGATIVE");
				}else{
					this.bet = bet;
					Connections.sendMessage(socket, "BET ACCEPTED: " + bet);
					return;
				}
			}
		}catch(IOException e){
			return;
		}

	}
	public void sendHandInfo(){
		String cards = hand.get(0).toString();
		for(int i = 1; i < hand.size(); i++){
			cards += ", " + hand.get(i).toString();
		}

		try{
			Connections.sendMessage(socket,"DEALT HAND: " + cards);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	public void sendGameResult(Boolean won){
		bet = 0;
		try{
			if(won == null){
				Connections.sendMessage(socket, "TIE GAME CURRENT BALANCE: " + money);
			}else if(won == true){
				Connections.sendMessage(socket, "WON GAME CURRENT BALANCE: " + money);
			}else{
				Connections.sendMessage(socket, "LOST GAME CURRENT BALANCE: " + money);
			}
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	public Boolean sendHitRequest(){
		try{
			while(true){
				Connections.sendMessage(socket, "HIT?(HIT/STAND/DOUBLE)");
				String resp = Connections.getMessage(socket);
				if(resp.equals("HIT")){
					return true;
				}else if(resp.equals("STAND")){
					return false;
				}else if(resp.equals("DOUBLE")){
					if(2*bet > money){
						Connections.sendMessage(socket, "NOT ENOUGH MONEY TO DOUBLE");
						continue;
					}
					bet *= 2;
					return null;
				}
			}
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	public void sendDealerFirstCard(Card c){
		try{
			Connections.sendMessage(socket, "DEALER HAS: " + c);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	public void sendJoinedGame(){
		try{
			Connections.sendMessage(socket, "JOINED");
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	public void sendDealerCards(List<Card> d){
		String cards = d.get(0).toString();
		for(int i = 1; i < d.size(); i++){
			cards += ", " + d.get(i).toString();
		}

		try{
			Connections.sendMessage(socket,"DEALER HAS: " + cards);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
}
