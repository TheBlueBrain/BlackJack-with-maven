package orgs;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BlackjackServer{
	Connections conn;
	List<Player> players = new ArrayList<Player>();
	Deck deck;
	volatile List<Player> queue = new ArrayList<>();
	volatile boolean isRunning = false;
	public BlackjackServer(){
		conn = new Connections();
		try{
			conn.startServer();
		}catch(IOException e){
			throw new RuntimeException(e);
		}
		deck = new Deck();
		Thread t = new Thread(() -> {
			while(true){
				try{
					Socket c = conn.acceptClient();
					if(isRunning){
						queue.add(new Player(c));
					}else{
						players.add(new Player(c));
					}

				}catch(IOException e){
					//ignore
				}
			}
		});
		t.start();
	}
	public void startGame(){
		while(true){
			while(players.isEmpty()){
				try{
					Thread.sleep(5000);
				}catch(InterruptedException e){
					throw new RuntimeException(e);
				}
			}
			isRunning = true;
			List<Card> dealer = new ArrayList<>();
			dealer.add(deck.draw());
			dealer.add(deck.draw());
			int dealerScore = Card.calculateScore(dealer);
			for(Player p : players){
				p.hand.add(deck.draw());
				p.hand.add(deck.draw());
				p.sendHandInfo();
				p.sendDealerFirstCard(dealer.get(0));
				for(Player other : players){
					if(other != p){
						other.notifyofOtherPlayer(p.id, p.hand);
					}
				}
			}
			if(dealerScore == 21){
				for(Player p : players){
					if(Card.calculateScore(p.hand) == 21){

						p.sendGameResult(null);
					}else{
						p.money -= p.bet;
						p.sendGameResult(false);
					}
					p.score = -1; // Dealer has blackjack, all players lose

				}
			}

			for(Player p : players){
				p.score = Card.calculateScore(p.hand);
				if(p.score == 21 && p.hand.size() == 2 && dealerScore != 21){
					p.multiplier = 1.5;
					p.money += (p.bet * p.winMultiplier).floor();
					p.sendGameResult(true);

					p.score = -1;
				}
			}
			for(Player p : players){
				while(true){
					Boolean cont = p.sendHitRequest();
					if(cont == null && p.score != -1){
						p.hand.add(deck.draw());
						p.sendHandInfo();
						break;
					}

					if(cont && p.score != -1){
						p.hand.add(deck.draw());
						p.sendHandInfo();
						for(Player other : players){
							if(other != p){
								other.notifyofOtherPlayer(p.id, p.hand);
							}
						}
						p.score = Card.calculateScore(p.hand);
						if(p.score > 21){
							p.money -= p.bet;
							p.sendGameResult(false);
							p.score = -1;
							break;
						}else if(p.score == 21){
							break;
						}
					}else{
						break;
					}
				}
			}
			for(Player p : players){
				if(p.score!=-1)
					p.sendDealerCards(dealer);
			}

			if(dealerScore <= 16){
				dealer.add(deck.draw());
				dealerScore = Card.calculateScore(dealer);
				for(Player p : players){
					p.sendDealerCards(dealer);
				}
			}
			for(Player p : players){
				if(p.score != -1){
					if(p.score > dealerScore){
						p.money += (p.bet * p.winMultiplier).floor();
						p.sendGameResult(true);
					}else if(p.score == dealerScore){
						p.sendGameResult(null);

					}else{
						p.money -= p.bet;
						p.sendGameResult(false);

					}
				}
			}
			isRunning = false;
			for(Player p : players){
				if(p.nextRount()){
					p.emptyHand();
				}else{
					try{
						p.socket.close();
					}catch(IOException e){
						throw new RuntimeException(e);
					}
				}
			}
			for(Player p : queue){
				players.add(p);
				p.sendJoinedGame();
			}
			queue.clear();
			players.removeAll(players.stream().filter(player -> player.socket.isClosed()).toList());
		}
	}


}
