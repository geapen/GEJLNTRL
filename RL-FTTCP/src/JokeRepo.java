import java.util.LinkedList;

public class JokeRepo {
	
	LinkedList<String> whoIsThere = new LinkedList<String>();
	LinkedList<String> personWho = new LinkedList<String>();
	
	public JokeRepo() {
		super();
		createWhoIsThere();
		createPersonWho();
	}

	private void createWhoIsThere() {
		// TODO Auto-generated method stub
		whoIsThere.add("Canoe");
		whoIsThere.add("Merry");
		whoIsThere.add("Orange");
		whoIsThere.add("Anee");
		whoIsThere.add("Iva");
		whoIsThere.add("Dozen");
		whoIsThere.add("Needle");
		whoIsThere.add("Henrietta");
		whoIsThere.add("Avenue");
		whoIsThere.add("Harry");
		whoIsThere.add("A herd");
		whoIsThere.add("Adore");
		whoIsThere.add("Otto");
		whoIsThere.add("King Tut");
		whoIsThere.add("Lettuce");
		whoIsThere.add("Noah");
		whoIsThere.add("Robin");
		whoIsThere.add("Dwayne");
		whoIsThere.add("Boo");
		whoIsThere.add("Impatient cow");
		whoIsThere.add("A little old lady");
		whoIsThere.add("Sadie");
		whoIsThere.add("Olive");
		whoIsThere.add("Justin");
		whoIsThere.add("Kirtch");
		
	}

	private void createPersonWho() {
		// TODO Auto-generated method stub
		personWho.add("Canoe help me with my homework?");
		personWho.add("Merry Christmas!");
		personWho.add("Orange you going to let me in?");
		personWho.add("Anee one you like!");
		personWho.add("I've a sore hand from knocking!");
		personWho.add("Dozen anybody want to let me in?");
		personWho.add("Needle little money for the movies.");
		personWho.add("Henrietta worm that was in his apple.");
		personWho.add("Avenue knocked on this door before?");
		personWho.add("Harry up, it's cold out here!");
		personWho.add("A herd you were home, so I came over!");
		personWho.add("Adore is between us. Open up!");
		personWho.add("Otto know. I've got amnesia.");
		personWho.add("King Tut-key fried chicken!");
		personWho.add("Lettuce in it's cold out here.");
		personWho.add("Noah good place we can get something to eat?");
		personWho.add("Robin the piggy bank again.");
		personWho.add("Dwayne the bathtub, It's overflowing!");
		personWho.add("Gosh, don't cry it's just a knock knock joke.");
		personWho.add("Mooooo!");
		personWho.add("I didn't know you could yodel.");
		personWho.add("Sadie magic word and watch me disappear!");
		personWho.add("Olive you!");
		personWho.add("Justin time for dinner.");
		personWho.add("God bless you!");
	}
	
	public String getWhoIsThere(int cmd){
		return whoIsThere.get(cmd);
	}
	
	public String getPersonWho(int cmd){
		return personWho.get(cmd);
	}

}
