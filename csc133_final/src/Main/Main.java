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
import particles.Shiny;
import script.ScriptAnimation;
import script.Command;
import script.ScriptItemGoal;
import script.ScriptObstacle;
import script.ScriptReader;
import script.ScriptSound;
import script.ScriptSprite;
import script.ScriptHudSubImage;
import script.ScriptHudSubObstacle;
import script.ScriptStartPosition;
import script.ScriptSubGoal;
import script.ScriptSubImage;
import script.ScriptSubObstacle;
import script.ScriptText;
import sound.Sound;
import timer.stopWatchX;

public class Main{
	// Fields (Static) below...
	public static String coord = "";             //coordinate tool
	//game variables for save/load
	private static ArrayList<Integer> buffer = new ArrayList<>();
	private static ArrayList<String> inventoryList = new ArrayList<>();          
	private static ArrayList<RECT> rectList = new ArrayList<>();
	private static ArrayList<RECT> hudRectList = new ArrayList<>();
	private static ArrayList<RECT> goalRectList = new ArrayList<>();
	private static ArrayList<RECT> itemGoalRectList = new ArrayList<>();
	private static ArrayList<Sprite> spriteList = new ArrayList<>();
	private static ArrayList<Sprite> hudSpriteList = new ArrayList<>();
	private static ArrayList<Sprite> spriteItemList = new ArrayList<>();
	private static ArrayList<ScriptText> scriptTexts = new ArrayList<>();
	private static ArrayList<ScriptAnimation> scriptAnimations = 
		new ArrayList<>();
	private static ArrayList<ScriptSound> scriptSounds = new ArrayList<>();
	private static ScriptReader scriptReader;    
	private static Frame robotFrame;
	private static MoveRobot moveRobot;
	private static Animation robotAnim;
	private static int startX, startY, curX, curY, newX, newY, level = 1,
		goalX, goalY;
	private static boolean startOver = true, startHud = false, hasItem = false,
		activeHud = false, loaded = false, saved = false, showItem = true,
		newLevel = true;
	private static Sound song;
	private static Sound backToStart, finish, gotIt;
	private static Sprite sprCursor;
	private static Shiny shiny, shinyGoal;
	private static RECT hudRedButton, hudBlackButton;
	// End Static fields...
	
	public static void main(String[] args) {
		Control ctrl = new Control();				// Do NOT remove!
		ctrl.gameLoop();							// Do NOT remove!
	}
	
	/* This is your access to things BEFORE the game loop starts */
	public static void start(Control ctrl){
		//TODO:  Code your starting conditions here...NOT DRAW CALLS HERE! 
		//(no addSprite or drawString)

		//hide mouse cursor
		ctrl.hideDefaultCursor();
		
		//initialize buffer for load/save
		buffer.add(level);
		int item = hasItem ? 1 : 0;
		buffer.add(item);
		
		//robot animation
		/*if (!scriptAnimations.isEmpty()) {
			ScriptAnimation sAnima = scriptAnimations.get(0);
			int delay = sAnima.getDelay();
			boolean isLooping = sAnima.getIsLooping();
			int sX = sAnima.getStartX();
			int sY = sAnima.getStartY();
			int bitSize = sAnima.getBitSize();
			botStep = sAnima.getStep();
			botAnim = new Animation(delay, isLooping);
			BufferedImage buf = sheet.getSubimage(sX, sY, bitSize, bitSize);
			Sprite downLeft = new Sprite(0, 0, buf, "down1");
			robotSpriteList.add(downLeft);
			buf = sheet.getSubimage(sX + bitSize, sY, bitSize, bitSize);
			Sprite downRight = new Sprite(0, 0, buf, "down3");
			robotSpriteList.add(downRight);
			buf = sheet.getSubimage(sX + bitSize * 2, sY, bitSize, bitSize);
			Sprite down = new Sprite(0, 0, buf, "down0");
			Sprite downAgain = new Sprite(0, 0, buf, "down2");
			robotSpriteList.add(down);
			robotSpriteList.add(downAgain);
			buf = sheet.getSubimage(sX + bitSize * 3, sY, bitSize, bitSize);
			Sprite leftForward = new Sprite(0, 0, buf, "left1");
			robotSpriteList.add(leftForward);
			buf = sheet.getSubimage(sX + bitSize * 4,  sY, bitSize, bitSize);
			Sprite left = new Sprite(0, 0, buf, "left0");
			robotSpriteList.add(left);
			buf = sheet.getSubimage(sX + bitSize * 5, sY, bitSize, bitSize);
			Sprite rightForward = new Sprite(0, 0, buf, "right1");
			robotSpriteList.add(rightForward);
			buf = sheet.getSubimage(sX + bitSize * 6, sY, bitSize, bitSize);
			Sprite right = new Sprite(0, 0, buf, "right0");
			robotSpriteList.add(right);
			buf = sheet.getSubimage(sX + bitSize * 7, sY, bitSize, bitSize);
			Sprite upLeft = new Sprite(0, 0, buf, "up1");
			robotSpriteList.add(upLeft);
			buf = sheet.getSubimage(sX + bitSize * 8, sY, bitSize, bitSize);
			Sprite upRight = new Sprite(0, 0, buf, "up3");
			robotSpriteList.add(upRight);
			buf = sheet.getSubimage(sX + bitSize * 9, sY, bitSize, bitSize);
			Sprite up = new Sprite(0, 0, buf, "up0");
			Sprite upAgain = new Sprite(0, 0, buf, "up2");
			robotSpriteList.add(up);
			robotSpriteList.add(upAgain);
		}
		//send robot animation sprites to backbuffer
		ctrl.addBufImageToBackBuffer(robotSpriteList);*/
	}
	
	/* This is your access to the "game loop" (It is a "callback" method from 
	 * the Control class (do NOT modify that class!))*/
	public static void update(Control ctrl) {
		// TODO: This is where you can code! (Starting code below is just to 
		// show you how it works)
		
		//determine level
		if (newLevel) {	
			
			spriteList.clear();
			rectList.clear();
			spriteItemList.clear();
			itemGoalRectList.clear();
			goalRectList.clear();
			
			ArrayList<ScriptSprite> scriptSprites = new ArrayList<>();  
			ArrayList<ScriptObstacle> scriptObstacles = new ArrayList<>();
			ArrayList<ScriptSubImage> scriptSubImages = new ArrayList<>();
			ArrayList<ScriptStartPosition> scriptStartPositions = 
				new ArrayList<>();
			ArrayList<ScriptSubObstacle> scriptSubObstacles = new ArrayList<>();
			ArrayList<ScriptSubGoal> scriptSubGoals = new ArrayList<>();
			ArrayList<ScriptItemGoal> scriptItemGoals = new ArrayList<>();
			ArrayList<ScriptHudSubObstacle> scriptHudSubObstacles = 
				new ArrayList<>();
			ArrayList<ScriptHudSubImage> scriptHudSubImages = new ArrayList<>();
			
			String levelNumber = "script.txt";
			//level = 2;
			//level = 3;
			if (level == 2) {
				levelNumber = "script2.txt";
			}
			if (level == 3) {
				levelNumber = "script3.txt";
			}
			if (level == 4) {
				System.exit(0);
			}
			//scripting
			scriptReader = new ScriptReader(levelNumber);
			scriptSprites = scriptReader.getScriptSprites();
			scriptObstacles = scriptReader.getScriptObstacles();
			scriptSubImages = scriptReader.getScriptSubImage();
			scriptStartPositions = scriptReader.getScriptStartPosition();
			scriptSubObstacles = scriptReader.getScriptSubObstacles();
			scriptAnimations = scriptReader.getScriptAnimations();
			scriptSounds = scriptReader.getScriptSounds();
			scriptSubGoals = scriptReader.getScriptSubGoals();
			scriptItemGoals = scriptReader.getScriptItemGoals();
			scriptHudSubImages = scriptReader.getHudSubImages();
			scriptHudSubObstacles = scriptReader.getHudSubObstacles();
			
			if (!scriptSprites.isEmpty()) {
				for (ScriptSprite spr: scriptSprites) {
					BufferedImage buf = ctrl.getSpriteFromBackBuffer
						(spr.getTag()).getSprite();
					Sprite sprite = new Sprite(spr.getX(), spr.getY(), buf, 
							spr.getTag());
					spriteList.add(sprite);
				}
			}
			if (!scriptObstacles.isEmpty()) {
				for (ScriptObstacle obs: scriptObstacles) {
					BufferedImage buf = ctrl.getSpriteFromBackBuffer
					(obs.getSTag()).getSprite();
					Sprite sprite = new Sprite(obs.getX(), obs.getY(), buf,
							obs.getSTag());
					spriteList.add(sprite);
					RECT rect = new RECT(obs.getX(), obs.getY(), obs.getX() + 
							obs.getObSize(), obs.getY() + obs.getObSize(), 
							obs.getRTag());
					rectList.add(rect);
				}
			}
				
			//map
			BufferedImage sheet = ctrl.getSpriteFromBackBuffer("sheet")
				.getSprite();		
			if (!scriptSubImages.isEmpty()) {
			ScriptSubImage mapImage = new ScriptSubImage();
			mapImage = scriptSubImages.get(0);
			BufferedImage map = sheet.getSubimage(mapImage.getX(), 
				mapImage.getY(), mapImage.getWidth(), mapImage.getHeight());
			TileMap tileMap = new TileMap(map);
			Sprite sprMap = new Sprite();
			sprMap = tileMap.getSprite();
			spriteList.add(sprMap);
			}
				
			//subObstacles
			if (!scriptSubObstacles.isEmpty()) {
			ScriptSubObstacle obImage = new ScriptSubObstacle();
				for (ScriptSubObstacle ob: scriptSubObstacles) {
					obImage = ob;
					BufferedImage buf = sheet.getSubimage(obImage.getBufX(), 
						obImage.getBufY(), obImage.getWidth(), 
						obImage.getHeight());
					Sprite sprite = new Sprite(obImage.getX(), obImage.getY(), 
						buf, obImage.getSTag());
					RECT rect = new RECT(obImage.getX(), obImage.getY(), 
						obImage.getX() + obImage.getObSize(), obImage.getY() + 
						obImage.getObSize(), obImage.getRTag());
					rectList.add(rect);
					spriteList.add(sprite);
				}
			}
			
			//HUD images
			if (!scriptHudSubImages.isEmpty()) {
				ScriptHudSubImage hudSubImage = new ScriptHudSubImage();
				for (ScriptHudSubImage hudImOb: scriptHudSubImages) {
					hudSubImage = hudImOb;
					BufferedImage buf = sheet.getSubimage(hudSubImage.getBufX(), 
						hudSubImage.getBufY(), hudSubImage.getWidth(), 
						hudSubImage.getHeight());
					Sprite sprite = new Sprite(hudSubImage.getBufX(), 
						hudSubImage.getBufY(), buf, hudSubImage.getSTag());
					hudSpriteList.add(sprite);
				}
			}
			//HUD obstacles
			if (!scriptHudSubObstacles.isEmpty()) {
				ScriptHudSubObstacle hudImage = new ScriptHudSubObstacle();
				for (ScriptHudSubObstacle hudOb: scriptHudSubObstacles) {
					hudImage = hudOb;
					BufferedImage buf = sheet.getSubimage(hudImage.getBufX(), 
						hudImage.getBufY(), hudImage.getWidth(), 
						hudImage.getHeight());
					Sprite sprite = new Sprite(hudImage.getX(), hudImage.getY(), 
						buf, hudImage.getSTag());
					RECT rect = new RECT(hudImage.getX(), hudImage.getY(), 
						hudImage.getX() + hudImage.getObSize(), hudImage.getY() + 
						hudImage.getObSize(), hudImage.getRTag());
					hudRectList.add(rect);
					hudSpriteList.add(sprite);
				}
			}
			//add HUD sprites to back buffer
			ctrl.addBufImageToBackBuffer(hudSpriteList);
				
			//itemGoal
			if (!scriptItemGoals.isEmpty()) {
			ScriptItemGoal keyImage = new ScriptItemGoal();
			keyImage = scriptItemGoals.get(0);
			BufferedImage key = sheet.getSubimage(keyImage.getBufX(), 
				keyImage.getBufY(), keyImage.getWidth(), keyImage.getHeight());
			Sprite sprKey = new Sprite(keyImage.getX(), keyImage.getY(), key, 
				keyImage.getSTag());
			RECT rectKey = new RECT(keyImage.getX(), keyImage.getY(), 
				keyImage.getX() + keyImage.getObSize(), keyImage.getY() + 
				keyImage.getObSize(), keyImage.getRTag());
			spriteItemList.add(sprKey);
			itemGoalRectList.add(rectKey);
			}
				
			//goal
			if (!scriptSubGoals.isEmpty()) {
			ScriptSubGoal goalImage = new ScriptSubGoal();
			goalImage = scriptSubGoals.get(0);
			BufferedImage goal = sheet.getSubimage(goalImage.getBufX(), 
				goalImage.getBufY(), goalImage.getWidth(), 
				goalImage.getHeight());
			Sprite goalSprite = new Sprite(goalImage.getX(), goalImage.getY(), 
				goal, goalImage.getSTag());
			RECT goalRect = new RECT(goalImage.getX(), goalImage.getY(), 
				goalImage.getX() + goalImage.getObSize(), goalImage.getY() + 
				goalImage.getObSize(), goalImage.getRTag());
			spriteList.add(goalSprite);
			goalRectList.add(goalRect);
			goalX = goalImage.getX();
			goalY = goalImage.getY();
			}
				
			//start
			if (!scriptStartPositions.isEmpty()) {
			ScriptStartPosition start = new ScriptStartPosition();
			start = scriptStartPositions.get(0);
			startX = start.getStartX();
			startY = start.getStartY();
			}
		
			//shiny objects
			shiny = new Shiny(startX - 16, startY - 16, 64, 64, 8, 64, 8);
			shinyGoal = new Shiny(goalX - 28, goalY - 28, 200, 200, 8, 64, 16);

			//music
			if (!scriptSounds.isEmpty()) {
			song = new Sound(scriptSounds.get(0).getFileName());
			song.setLoop();
			backToStart = new Sound(scriptSounds.get(1).getFileName());
			finish = new Sound(scriptSounds.get(2).getFileName());
			gotIt = new Sound(scriptSounds.get(3).getFileName());
			}
						
			//add cursor sprite to ArrayList
			if (!scriptSubImages.isEmpty()) {
			ScriptSubImage cursorImage = new ScriptSubImage();
			cursorImage = scriptSubImages.get(1);
			BufferedImage cursor = sheet.getSubimage(cursorImage.getX(), 
				cursorImage.getY(), cursorImage.getWidth(), 
				cursorImage.getHeight());
			sprCursor = new Sprite(0, 0, cursor, cursorImage.getSTag());
			}
			
			newLevel = false;
		}
		
		//coordinate tool & mouse cursor
		Point p = Mouse.getMouseCoords();
		coord = p.toString();                           //coordinate tool
		ctrl.drawString(500, 360, coord, Color.WHITE);  //coordinate tool
		  ctrl.addSpriteToOverlayBuffer(p.x, p.y, sprCursor.getTag());
		
		//draw sprites ***make sure map sprite is in 0***
		if (!spriteList.isEmpty()) {
			for (Sprite spr: spriteList) {
				ctrl.addSpriteToFrontBuffer(spr);
			}
		}
		
		//draw start particles
		ParticleSystem pm = shiny.getParticleSystem();
		Iterator<Frame> it = pm.getParticles();
		while (it.hasNext()) {
			Frame par = it.next();
			Sprite spr = ctrl.getSpriteFromBackBuffer(par.getSpriteTag());
			BufferedImage buf = ctrl.getSpriteFromBackBuffer(spr.getTag())
				.getSprite();
			Sprite sprite = new Sprite(par.getX(), par.getY(), buf, 
				par.getSpriteTag());
			ctrl.addSpriteToFrontBuffer(sprite);
		}
		
		//draw goal particles
		ParticleSystem goalPM = shinyGoal.getParticleSystem();
		Iterator<Frame> goalIT = goalPM.getParticles();
		while (goalIT.hasNext()) {
			Frame par = goalIT.next();
			Sprite spr = ctrl.getSpriteFromBackBuffer(par.getSpriteTag());
			BufferedImage buf = ctrl.getSpriteFromBackBuffer(spr.getTag())
				.getSprite();
			Sprite sprite = new Sprite(par.getX(), par.getY(), buf, 
				par.getSpriteTag());
			ctrl.addSpriteToFrontBuffer(sprite);
		}
		
		//draw itemGoal
		if (!spriteItemList.isEmpty()) {
			if (showItem) {
				for (Sprite spr: spriteItemList) {
					ctrl.addSpriteToFrontBuffer(spr);
				}
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
		
		//draw hud
		if (startHud) {
			int blackX = 180, blackY = 175, redX = 315, redY = 175;
			if (!hudSpriteList.isEmpty()) {
				ctrl.addSpriteToHudBuffer(100, 100, hudSpriteList.get(0).getTag());
				ctrl.drawHudString(116, 125, "Inventory: ", Color.white);
				String inventoryItem = "empty";
				if (!inventoryList.isEmpty()) {
					inventoryItem = inventoryList.get(0);
				}
				ctrl.drawHudString(230, 125, inventoryItem, Color.white);
				ctrl.drawHudString(116, 155, "Level: ", Color.white);
				ctrl.drawHudString(190, 155, Integer.toString(level), Color.white);
				ctrl.drawHudString(116, 190, "Load", Color.white);
				ctrl.drawHudString(250, 190, "Save", Color.white);
				ctrl.addSpriteToHudBuffer(blackX, blackY, 
					hudSpriteList.get(1).getTag());
				ctrl.addSpriteToHudBuffer(redX, redY, 
					hudSpriteList.get(2).getTag());
			}
			
			//HUD rects
			int buttonSize = 16;
			if (!hudRectList.isEmpty()) {
				hudRedButton = new RECT(redX, redY, redX + buttonSize, 
					redY + buttonSize, hudRectList.get(1).getTag());
				hudBlackButton = new RECT(blackX, blackY, blackX + buttonSize, 
					blackY + buttonSize, hudRectList.get(0).getTag());
				String load = "Loaded...", save = "Saved...";
				if (loaded) {
					ctrl.drawHudString(180, 215, load, Color.white);
				}
				if (saved) {
					ctrl.drawHudString(180, 215, save, Color.white);
				}
			}
		}
		
		//robot animation
		Animation botAnim = new Animation(100, false);
		int botStep = 10;
		String[] myRobotTags = new String[]{"robDown", "robUp", "robRight", 
			"robLeft"};
		if (startOver) {
			curX = startX;
			curY = startY;
			newX = startX;
			newY = startY;
			startOver = false;
			moveRobot = new MoveRobot(myRobotTags, botAnim, botStep, curX, 
				curY, startX, startY);
		}
		if (Control.getMouseInput() != null) {
			Click click = Control.getMouseInput();
			if (click.getButton() == 1)	{
				int hudX = (int)p.getX();
				int hudY = (int)p.getY();
				if (!activeHud) {
				newX = (int)p.getX();
				newY = (int)p.getY();
				}
				//check for collision with HUD RECTS
				if (hudRedButton != null) {
					if (hudRedButton.isCollision(hudX, hudY)) {
						activeHud = true;
						buffer = new ArrayList<>();
						buffer.add(level);
						int item = hasItem ? 1 : 0;
						buffer.add(item);
						saveData(buffer);
						saved = true;
						loaded = false;
					}
				}
				if (hudBlackButton != null) {
					if (hudBlackButton.isCollision(hudX, hudY)) {
						buffer = loadData(buffer);
						level = buffer.get(0);
						int item = buffer.get(1);
						if (item == 1) {
							hasItem = true;
						}
						else {
							hasItem = false;
						}
						loaded = true;
						saved = false;
						if (hasItem) {
							showItem = false;
						}
						else {
							showItem = true;
						}
					}
				}
			}
			if (click.getButton() == 3) {
				startHud = !startHud;
				activeHud = !activeHud;
				loaded = false;
				saved = false;
			}
		}
		if (!activeHud) {
			if (!moveRobot.compareCoords(newX, newY)) {
				curX = robotFrame.getX();
				curY = robotFrame.getY();
				moveRobot = new MoveRobot(myRobotTags, botAnim, botStep, curX, 
						curY, newX, newY);
			}
			robotAnim = moveRobot.getAnimation();
			robotFrame = robotAnim.getCurrentFrame();
			if (robotFrame != null) {
				ctrl.addSpriteToFrontBuffer(robotFrame.getX(), 
					robotFrame.getY(), robotFrame.getSpriteTag());
			}
			curX = robotFrame.getX();
			curY = robotFrame.getY();
			int myBotSize = 32;
			RECT mybot = new RECT(curX, curY, curX + myBotSize, 
				curY + myBotSize, "myBotRECT");
				
			//check for general collision
			for (RECT rect: rectList) {
				if (rect.isCollision(rect, mybot)) {
					backToStart.playWAV();
					startOver = true;
				}
			}
			
			//check for itemGoal collision
			for (RECT rect: itemGoalRectList) {
				if (rect.isCollision(rect, mybot)) {
					gotIt.playWAV();
					showItem = false;
					inventoryList.add(rect.getTag());
					hasItem = true;
				}
			}
			
			//check for goal collision
			for (RECT rect: goalRectList) {
				if (rect.isCollision(rect, mybot)) {
					if (hasItem) {
						finish.playWAV();
						level += 1;
						startOver = true;
						newLevel = true;
						song.pauseWAV();
						song.resetWAV();
						inventoryList.clear();
						showItem = true;
					}
					else {
						ctrl.drawString(rect.getX1() - 30, rect.getY2() + 20,
							"Missing something...", Color.white);
					}
				}
			}
		}
		
	}
	
	// Additional Static methods below...(if needed)
	//create a routine to save the game data
	public static void saveData(ArrayList<Integer> buf) {
		//save data to a String to output...
		String out = "";
		for (int i = 0; i < buf.size(); i++)
			out += buf.get(i) + "*";
		out = out.substring(0, out.length() - 1);  //remove trailing delimiter
		//save output String to file
		EZFileWrite ezw = new EZFileWrite("save.txt");
		ezw.writeLine(out);
		ezw.saveFile();
	}
	
	//create a routine to restore the game data
	public static ArrayList<Integer> loadData(ArrayList<Integer> buf) {
		//retrieve data from the file
		EZFileRead ezr = new EZFileRead("save.txt");
		String raw = ezr.getLine(0);  //read our one and only line (index #0)
		//break this down into tokens
		StringTokenizer st = new StringTokenizer(raw, "*");
		if (st.countTokens() != buf.size()) {
			return buf;  //these must match!!!
		}
		else {
			buf = new ArrayList<>();
		}
		for (int i = 0; i < buffer.size(); i++) {
			String value = st.nextToken();
			int val = Integer.parseInt(value);
			buf.add(val);
		}
		return buf;
	}
}
