package drawgame;

public class ChatInput {
	private String input;
	private String player;
	private InputType type;
	
	public enum InputType{
		GAME, LEAVE, JOIN;
	}
	
	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public InputType getType() {
		return type;
	}

	public void setType(InputType type) {
		this.type = type;
	}

}
