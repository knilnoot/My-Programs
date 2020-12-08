/**
 * @(#)AsteroidsGame.java
 * Asteroids Applet application
 * @version 1.00 2014/2/4
 */

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.lang.Math;
import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
public class AsteroidsGame extends JFrame{
	public AsteroidsGame () {
		add (new runAsteroidsGame());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
		setLocationRelativeTo(null);
		setTitle("Asteroids");
		setResizable(true);
		setVisible(true);
		System.out.println("Program Started!");
	}
	public static void main(String[] args) {
		new AsteroidsGame ();
	}
}
class runAsteroidsGame extends JPanel implements ActionListener{
	public runAsteroidsGame () {
		addKeyListener(new TAdapter ());
		setBackground(Color.black);
		setFocusable(true);
		timer = new Timer(30, this);
		timer.start ();
	}
	static final int UP=KeyEvent.VK_UP,
			RIGHT=KeyEvent.VK_RIGHT,
			DOWN=KeyEvent.VK_DOWN,
			LEFT=KeyEvent.VK_LEFT,
			ENTER=KeyEvent.VK_ENTER,
			CTRL=KeyEvent.VK_CONTROL;
int numShotsFired=0,numShotsHit=0;
static final double MAXASTVEL=4.2,MINASTVEL=.3;
///	static final int SHOTLIFE=40;
					//  numAsteroids,numHit,numSplit,shotDelay
	int[][] LEVELVALUES;/* =
		{{2,3,2,15},{4,3,2,14},{6,3,2,13},{2,4,2,12},{4,4,2,11},
		 {3,3,3,10},{2,4,3,9},{5,3,3,8},{3,2,8,7},{8,2,6,6},
		 {15,3,2,5},{1,2,32,5},{1,2,54,5},{2,2,30,5},{5,3,4,5},
		 {4,4,3,5},{1,7,2,5},{25,2,2,5},{50,1,1,5},{10,30,1,5},
		 {3,7,2,0}
		};*/
	String[] LEVELMSG;/*=
		{"Press enter to start","","Getting harder...","Only 2 asteroids?","Try 4",
		 "Triplets","Triplets 2","Triplets 3","Octuplets","Sextuplets",
		 "A lotta rocks","Kaboom","Kaboom 2","Kaboom 3","",
		 "","Interstellar pebbles","Where to begin?","Thought the last one was bad?","Dang asteroids won't go away",
		 "Machine Gun"
		};*/
	int numLevels;
	int delay=25;
	long prevTime,curTime;
	//Thread thread;
	Graphics g;
	Image img;
	Dimension dim;


	double shotAccuracy=0;

	Ship ship;
	Asteroid[] asteroids;
	Shot[] shots;
	int numAsteroids,numShots,numLives,level;
	boolean paused,beginningOfLevel,shipFiring,appletInitialized;
	public Timer timer;

	String errorMsg="";

	public void init(){
		appletInitialized=false;
		//thread=new Thread(this);
		//thread.start();
	}

	public void setUpApplet(){
		level=0;
		dim=getSize();
		img=createImage(dim.width,dim.height);
		g=img.getGraphics();
		//addKeyListener(this);
		readLevels("Levels.txt");
		setUp(0);
		appletInitialized=true;
	}

	private void readLevels(String fileName){
		URL url=null;
		/*try{
			url=new URL(getCodeBase(),fileName);
		}catch(MalformedURLException e){
			errorMsg+=e;
			numLevels=1;
			LEVELMSG=new String[]{new String("CORRUPT Level.txt FILE")};
			LEVELVALUES=new int[][]{{2,3,2,15}};
			return;
		}*/
		try{
			InputStream in=url.openStream();
			BufferedReader dis=new BufferedReader(new InputStreamReader(in));
			for(int i=0;i<9;i++){ // skip format info at beginning of text file
				dis.readLine();
			}
			numLevels=Integer.parseInt(dis.readLine());
			LEVELVALUES=new int[numLevels][5];
			LEVELMSG=new String[numLevels];
			for(int i=0;i<numLevels;i++){
				LEVELMSG[i]=new String(dis.readLine());
				for(int j=0;j<5;j++){
					LEVELVALUES[i][j]=Integer.parseInt(dis.readLine());
				}
			}
			in.close();
		}catch(IOException e){
			errorMsg+=e;
			numLevels=1;
			LEVELMSG=new String[]{new String("CORRUPT "+fileName+" FILE")};
			LEVELVALUES=new int[][]{{2,3,2,15}};
			return;
		}
	}

	private void setUp(int level){
		if(level==0){
			numShotsFired=0;
			numShotsHit=0;
			shotAccuracy=0;
		}
		if(level>=numLevels)
			level=0;
		paused=true;
		beginningOfLevel=true;
		shipFiring=false;
		if(level==0)
			numLives=20;
		numShots=0;
		ship=new Ship(dim.width/2,dim.height/2,0,.35,7*Math.PI/180,.98,LEVELVALUES[level][3]);
		numAsteroids=LEVELVALUES[level][0];
		asteroids=new Asteroid[(int)(numAsteroids*
				Math.pow(LEVELVALUES[level][2],LEVELVALUES[level][1]-1))]; //size per asteroid
		shots=new Shot[10000]; // SHOT LIMIT!!!!!!!!!!
		for(int i=0;i<numAsteroids;i++)
			asteroids[i]=new Asteroid(Math.random()*dim.width,Math.random()*dim.height,LEVELVALUES[level][4], // x,y,r
					MAXASTVEL,MINASTVEL,
					LEVELVALUES[level][1],LEVELVALUES[level][2]); // numHit, numSplit
	}

	public void paint(Graphics gfx){
		g.setColor(Color.black);
		g.fillRect(0,0,dim.width,dim.height);
		if(appletInitialized){
			for(int i=0;i<numAsteroids;i++)
				asteroids[i].draw(g);
			for(int i=0;i<numShots;i++)
				shots[i].draw(g);
			ship.draw(g);
			g.setColor(Color.white);
			g.drawString("lives: "+numLives+" level: "+level,10,10);
			if(beginningOfLevel)
				g.drawString(LEVELMSG[level],10,30);
		}
		g.setColor(Color.white);
		g.drawString("Shot Accuracy: "+(double)((int)(shotAccuracy*10000))/100+" %",10,dim.height-1);
		g.drawString(errorMsg,10,dim.height-10);
		gfx.drawImage(img,0,0,this);
	}

	public void update(Graphics gfx){
		paint(gfx);
		update();
	}

	public void update(){
		setUpApplet();
		//for(;;){
		//try{
			prevTime=System.currentTimeMillis();
			if(!paused){
				ship.move(0,0,dim.width,dim.height);
			}
			if(beginningOfLevel || !paused){
				if(shipFiring){
					Shot tempShot=ship.shoot();
					if(tempShot!=null && numShots<shots.length){
						shots[numShots]=tempShot;
						numShots++;
					}
				}
				for(int i=0;i<numShots;i++){
					shots[i].move(0,0,dim.width,dim.height);
					/*shots[i].subLife--;
					shots[i].subLife2--;
					if(shots[i].getSubLife()<=0) {
						shots[i].xVel=shots[i].SHOTVEL*Math.cos(shots[i].Ang+Math.PI*1.1); //shots[i].xVel/=2*Math.PI;
						shots[i].yVel=shots[i].SHOTVEL*Math.sin(shots[i].Ang+Math.PI*1.1); //shots[i].yVel/=2*Math.PI;
					}
					if(shots[i].getSubLife2()<=0) {
						shots[i].xVel=shots[i].SHOTVEL*Math.cos(shots[i].Ang); //shots[i].xVel/=2*Math.PI;
						shots[i].yVel=shots[i].SHOTVEL*Math.sin(shots[i].Ang); //shots[i].yVel/=2*Math.PI;
					}*/
					if(shots[i].getLife()<=0){
						numShots--;
						for(int j=i;j<numShots;j++)
							shots[j]=shots[j+1];
						i--;//check shot shifted over too
					}
				}
				for(int i=0;i<numAsteroids;i++){
					asteroids[i].move(0,0,dim.width,dim.height);
					for(int j=0;j<numShots;j++){
						if(asteroids[i].shotCollision(shots[j])){
							numShotsHit++;
							shotAccuracy=(double)numShotsHit/numShotsFired;
							numShots--; // remove shot
							for(int k=j;k<numShots;k++)
								shots[k]=shots[k+1];
							j--; //check shot shifted over too
							if(asteroids[i].getHitsLeft()<=1){
								numAsteroids--;
								for(int k=i;k<numAsteroids;k++)
									asteroids[k]=asteroids[k+1];
							}else{
								numAsteroids+=asteroids[i].getNumSplit()-1;
								for(int k=numAsteroids-asteroids[i].getNumSplit()+1;
										k<numAsteroids;k++)
									asteroids[k]=asteroids[i].createSplitAsteroid();
								asteroids[i]=asteroids[i].createSplitAsteroid();
							}
						}
					}
					if(!beginningOfLevel && asteroids[i].shipCollision(ship)){
						numLives--;
						shipFiring=false;
						if(numLives<=0){
							level=0;
							setUp(0);
							break;
						}
						ship=new Ship(dim.width/2,dim.height/2,0,.35,7*Math.PI/180,.98,
								LEVELVALUES[level][3]);
						numShots=0;
						paused=true;
						beginningOfLevel=true;
						break;
					}
				}
				if(numAsteroids<=0)
					setUp(++level);
			}
			repaint();
			/*try{
				curTime=System.currentTimeMillis();
				Thread.sleep(((delay-(curTime-prevTime))>0)? (delay-(curTime-prevTime)) : 0);
			}catch(InterruptedException e){
			}*/
		/*}catch(Exception e){
			errorMsg+=e;
			repaint();
		}*/
		//}
	}

	private class TAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e){
			if(paused)
				return;
			if(e.getKeyCode()==UP)
				ship.startAccelerating();
			else if(e.getKeyCode()==RIGHT)
				ship.startTurningRight();
			else if(e.getKeyCode()==LEFT)
				ship.startTurningLeft();
			else if(e.getKeyCode()==CTRL)
				shipFiring=true;
		}

		public void keyReleased(KeyEvent e){
			if(e.getKeyCode()==ENTER){
				beginningOfLevel=false;
				paused=!paused;
			}
			else if(e.getKeyCode()==UP)
				ship.stopAccelerating();
			else if(e.getKeyCode()==RIGHT)
				ship.stopTurningRight();
			else if(e.getKeyCode()==LEFT)
				ship.stopTurningLeft();
			else if(e.getKeyCode()==CTRL)
				shipFiring=false;
			else if(e.getKeyCode()==KeyEvent.VK_F12)
				numAsteroids=0;
		}

		//public void keyTyped(KeyEvent e){
		//}
	}

	    //public static void main(String[] args) {

    	//new AsteroidsGame();
    	//System.out.println("Hello World!");
    //}
}
