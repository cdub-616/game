package script;

import java.awt.Color;

public class Shadow {
	//fields
	private ScriptText shadowText;
	
	//constructors
	public Shadow() {
		shadowText = new ScriptText();
	}
	
	public Shadow(int shadow, Color shadColor, ScriptText oldText) {
		String display = oldText.getText();
		int newX = oldText.getX() + 2;
		int newY = oldText.getY() + 2;
		Color col = shadColor;
		shadowText = new ScriptText(display, newX, newY, col);
	}
	
	//methods
	public ScriptText getShadow() {
		return shadowText;
	}
}