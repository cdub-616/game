package Main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import Data.Animation;
import Data.Atext;
import Data.Click;
import Data.RECT;
import Data.Sprite;
import Data.Frame;
import Data.MoveRobot;
import FileIO.EZFileWrite;
import Graphics.Graphic;
import Graphics.TileMap;
import FileIO.EZFileRead;
import Input.Mouse;
import logic.Control;
import particles.ParticleSystem;
import particles.Rain;
import script.ScriptAnimation;
import script.Command;
import script.ScriptObstacle;
import script.ScriptReader;
import script.ScriptSprite;
import script.ScriptStartPosition;
import script.ScriptSubImage;
import script.ScriptSubObstacle;
import script.ScriptText;
import sound.Sound;
import timer.stopWatchX;

public class Main{
	// Fields (Static) below...
	public static String coord = "";             //coordinate tool
	private static int[] buffer;                 //some hypothetical game variables
	//private static String[] myRobotTags;
	private static ScriptReader scriptReader;              
	private static ArrayList<RECT> rectList = new ArrayList<>();
	private static ArrayList<Sprite> spriteList = new ArrayList<>();
	private static ArrayList<Sprite> robotSpriteList = new ArrayList<>();
	private static ArrayList<ScriptText> scriptTexts = new ArrayList<>();
	private static ArrayList<ScriptAnimation> scriptAnimations = 
		new ArrayList<>();
	//private static ArrayList<BufferedImage> robotBufs = new ArrayList<>();
	private static Frame robotFrame;
	//private static TileMap tileMap;
	//private static Sprite sprMap1, sprBush;
	//private static RECT rectBush;
	private static MoveRobot robotMove;
	private static Animation botAnim;
	
	private static int startX, startY, curX, curY, newX, newY;
	private static boolean startOver = true, exit;
	// End Static fields...
	
	public static void main(String[] args) {
		Control ctrl = new Control();				// Do NOT remove!
		ctrl.gameLoop();							// Do NOT remove!
	}
	
	/* This is your access to things BEFORE the game loop starts */
	public static void start(Control ctrl){
		//TODO:  Code your starting conditions here...NOT DRAW CALLS HERE! 
		//(no addSprite or drawString)
		
		//fields
		ArrayList<ScriptSprite> scriptSprites = new ArrayList<>();  
		//ArrayList<ScriptText> scriptTexts = new ArrayList<>();
		ArrayList<ScriptObstacle> scriptObstacles = new ArrayList<>();
		ArrayList<ScriptSubImage> scriptSubImages = new ArrayList<>();
		ArrayList<ScriptStartPosition> scriptStartPositions = new ArrayList<>();
		ArrayList<ScriptSubObstacle> scriptSubObstacles = new ArrayList<>();
		
		//scripting
		scriptReader = new ScriptReader("script.txt");
		scriptSprites = scriptReader.getScriptSprites();
		scriptObstacles = scriptReader.getScriptObstacles();
		scriptSubImages = scriptReader.getScriptSubImage();
		scriptStartPositions = scriptReader.getScriptStartPosition();
		scriptSubObstacles = scriptReader.getScriptSubObstacles();
		scriptAnimations = scriptReader.getScriptAnimations();
		
		if (!scriptSprites.isEmpty()) {
			for (ScriptSprite spr: scriptSprites) {
				BufferedImage buf = ctrl.getSpriteFromBackBuffer(spr.getTag())
					.getSprite();
				Sprite sprite = new Sprite(spr.getX(), spr.getY(), buf, 
					spr.getTag());
				spriteList.add(sprite);
				//ctrl.addSpriteToFrontBuffer(sprite);
			}
		}
		if (!scriptObstacles.isEmpty()) {
			for (ScriptObstacle obs: scriptObstacles) {
				BufferedImage buf = ctrl.getSpriteFromBackBuffer(obs.getSTag())
					.getSprite();
				Sprite sprite = new Sprite(obs.getX(), obs.getY(), buf,
					obs.getSTag());
				spriteList.add(sprite);
				//ctrl.addSpriteToFrontBuffer(sprite);
				RECT rect = new RECT(obs.getX(), obs.getY(), obs.getX() + 
					obs.getObSize(), obs.getY() + obs.getObSize(), 
					obs.getRTag());
				rectList.add(rect);
			}
		}
		
		//map
		ScriptSubImage grassImage = new ScriptSubImage();
		grassImage = scriptSubImages.get(0);
		BufferedImage sheet = ctrl.getSpriteFromBackBuffer("sheet").getSprite();
		BufferedImage grass = sheet.getSubimage(grassImage.getX(), 
			grassImage.getY(), grassImage.getWidth(), grassImage.getHeight());
		TileMap tileMap = new TileMap(grass);
		Sprite sprMap1 = new Sprite();
		sprMap1 = tileMap.getSprite();
		spriteList.add(sprMap1);
		
		//obstacles
		ScriptSubObstacle bushImage = new ScriptSubObstacle();
		bushImage = scriptSubObstacles.get(0);
		BufferedImage bush = sheet.getSubimage(bushImage.getBufX(), 
			bushImage.getBufY(), bushImage.getWidth(), bushImage.getHeight());
		Sprite sprBush = new Sprite(bushImage.getX(), bushImage.getY(), bush, 
			bushImage.getSTag());
		RECT rectBush = new RECT(bushImage.getX(), bushImage.getY(), 
			bushImage.getX() + bushImage.getObSize(), bushImage.getY() + 
			bushImage.getObSize(), bushImage.getRTag());
		rectList.add(rectBush);
		spriteList.add(sprBush);
		
		//start
		ScriptStartPosition start = new ScriptStartPosition();
		start = scriptStartPositions.get(0);
		startX = start.getStartX();
		startY = start.getStartY();
		
		//robot animation
		if (!scriptAnimations.isEmpty()) {
			ScriptAnimation sAnima = new ScriptAnimation();
			sAnima = scriptAnimations.get(0);
			int delay = sAnima.getDelay();
			boolean isLooping = sAnima.getIsLooping();
			int startX = sAnima.getStartX();
			int startY = sAnima.getStartY();
			int bitSize = sAnima.getBitSize();
			int numImages = sAnima.getNumImages();
			botAnim = new Animation(delay, isLooping);
			BufferedImage buf = sheet.getSubimage(startX, startY, bitSize, 
				bitSize);
			Sprite downLeft = new Sprite(0, 0, buf, "down1");
			robotSpriteList.add(downLeft);
			buf = sheet.getSubimage(startX + bitSize, startY, bitSize, bitSize);
			Sprite downRight = new Sprite(0, 0, buf, "down3");
			robotSpriteList.add(downRight);
			buf = sheet.getSubimage(startX + bitSize * 2, startY, bitSize, 
				bitSize);
			Sprite down = new Sprite(0, 0, buf, "down0");
			Sprite downAgain = new Sprite(0, 0, buf, "down2");
			robotSpriteList.add(down);
			robotSpriteList.add(downAgain);
			buf = sheet.getSubimage(startX + bitSize * 3, startY, bitSize, 
				bitSize);
			Sprite leftForward = new Sprite(0, 0, buf, "left1");
			robotSpriteList.add(leftForward);
			buf = sheet.getSubimage(startX + bitSize * 4,  startY, bitSize, 
				bitSize);
			Sprite left = new Sprite(0, 0, buf, "left0");
			robotSpriteList.add(left);
			buf = sheet.getSubimage(startX + bitSize * 5, startY, bitSize, 
				bitSize);
			Sprite rightForward = new Sprite(0, 0, buf, "right1");
			robotSpriteList.add(rightForward);
			buf = sheet.getSubimage(startX + bitSize * 6, startY, bitSize, 
				bitSize);
			Sprite right = new Sprite(0, 0, buf, "right0");
			robotSpriteList.add(right);
			buf = sheet.getSubimage(startX + bitSize * 7, startY, bitSize, 
				bitSize);
			Sprite upLeft = new Sprite(0, 0, buf, "up1");
			robotSpriteList.add(upLeft);
			buf = sheet.getSubimage(startX + bitSize * 8, startY, bitSize, 
				bitSize);
			Sprite upRight = new Sprite(0, 0, buf, "up3");
			robotSpriteList.add(upRight);
			buf = sheet.getSubimage(startX + bitSize * 9, startY, bitSize, 
				bitSize);
			Sprite up = new Sprite(0, 0, buf, "up0");
			Sprite upAgain = new Sprite(0, 0, buf, "up2");
			robotSpriteList.add(up);
			robotSpriteList.add(upAgain);
		}
		//send robot animation sprites to backbuffer
		ctrl.addBufImageToBackBuffer(robotSpriteList);
	}
	
	/* This is your access to the "game loop" (It is a "callback" method from 
	 * the Control class (do NOT modify that class!))*/
	public static void update(Control ctrl) {
		// TODO: This is where you can code! (Starting code below is just to 
		// show you how it works)
		
		//fields
		int botStep = scriptAnimations.get(0).getStep();
		//String[] myRobotTags = new String[]{"robDown", "robUp", "robRight", 
		//	"robLeft"};
		Animation robotAnim = new Animation();
		
		//coordinate tool
		Point p = Mouse.getMouseCoords();
		coord = p.toString();                           //coordinate tool
		ctrl.drawString(500, 360, coord, Color.WHITE);  //coordinate tool
		
		//draw sprites ***make sure map sprite is in 0***
		if (!spriteList.isEmpty()) {
			for (Sprite spr: spriteList) {
				ctrl.addSpriteToFrontBuffer(spr);
			}
		}
						
		//draw texts
		if (!scriptTexts.isEmpty()) {
			scriptTexts = scriptReader.getScriptTexts();
			for (ScriptText txt: scriptTexts) {
				ctrl.drawString(txt.getX(), txt.getY(), txt.getText(), 
					txt.getColor());
			}
		}
		
		//robot animation
		//Animation botAnim = new Animation(100, false);
		//robotAnim = new Animation(100, false);
		
		if (startOver) {
			curX = startX;
			curY = startY;
			newX = startX;
			newY = startY;
			startOver = false;
			//robotMove = new MoveRobot(myRobotTags, botAnim, botStep, curX, 
			//	curY, startX, startY);
			robotMove = new MoveRobot(botAnim, botStep, curX, curY, startX, 
				startY);
		}
		if (Control.getMouseInput() != null) {
			Click click = Control.getMouseInput();
			if (click.getButton() == 1)	{
				newX = (int)p.getX();
				newY = (int)p.getY();
			}
		}
		if (!robotMove.compareCoords(newX, newY)) {
			curX = robotFrame.getX();
			curY = robotFrame.getY();
			//robotMove = new MoveRobot(myRobotTags, robotAnim, botStep, curX, 
			//	curY, newX, newY);
			robotMove = new MoveRobot(botAnim, botStep, curX, curY, newX, 
				newY);
		}
		robotAnim = robotMove.getAnimation();
		robotFrame = botAnim.getCurrentFrame();
		if (robotFrame != null) {
			ctrl.addSpriteToFrontBuffer(robotFrame.getX(), robotFrame.getY(), 
				robotFrame.getSpriteTag());
		}
		curX = robotFrame.getX();
		curY = robotFrame.getY();
		int myBotSize = 32;
		RECT mybot = new RECT(curX, curY, curX + myBotSize, curY + myBotSize,
			"myBotRECT");
		
		//check for collision
		for (RECT rect: rectList) {
			if (rect.isCollision(rect, mybot)) {
				ctrl.drawString(curX, curY, "hi", Color.white);
				startOver = true;
			}
		}	
	}
	
	// Additional Static methods below...(if needed)
	//create a routine to save the game data
	public static void saveData() {
		//save data to a String to output...
		String out = "";
		for (int i = 0; i < buffer.length; i++)
			out += buffer[i] + "*";
		out = out.substring(0, out.length() - 1);  //remove trailing delimiter
		//save output String to file
		EZFileWrite ezw = new EZFileWrite("save.txt");
		ezw.writeLine(out);
		ezw.saveFile();
	}
	
	//create a routine to restore the game data
	public static void loadData() {
		//retrieve data from the file
		EZFileRead ezr = new EZFileRead("save.txt");
		String raw = ezr.getLine(0);  //read our one and only line (index #0)
		//break this down into tokens
		StringTokenizer st = new StringTokenizer(raw, "*");
		if (st.countTokens() != buffer.length)
			return;  //these must match!!!
		for (int i = 0; i < buffer.length; i++) {
			String value = st.nextToken();
			int val = Integer.parseInt(value);
			buffer[i] = val;
		}
	}
}
