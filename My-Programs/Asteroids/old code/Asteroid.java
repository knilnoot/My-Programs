/**
 * @(#)Asteroid.java
 *
 *
 * @author
 * @version 1.00 2014/2/7
 */

//Asteroids Starts

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;



	public class Asteroid{
		private double x,y,xVel,yVel,radius;
		private int hitsLeft,numSplit;
	static double MAXASTVEL=4.2,MINASTVEL=.3;
	static int SHOTLIFE=40;

		public Asteroid(double x,double y,double radius,double maxVel,double minVel,int hitsLeft,int numSplit){
			this.x=x;
			this.y=y;
			this.radius=radius;
			this.hitsLeft=hitsLeft;
			this.numSplit=numSplit;
			double vel=minVel+maxVel*Math.random(),dir=2*Math.PI*Math.random();
			xVel=vel*Math.cos(dir);
			yVel=vel*Math.sin(dir);
		}

		public void move(double xMin,double yMin,double xMax,double yMax){
			x+=xVel;
			y+=yVel;
			if(x<xMin-radius)
				x+=xMax-xMin+2*radius;
			else if(x>xMax+radius)
				x-=xMax-xMin+2*radius;
			if(y<yMin-radius)
				y+=yMax-yMin+2*radius;
			else if(y>yMax+radius)
				y-=yMax-yMin+2*radius;
		}

		public Asteroid createSplitAsteroid(){
			return new Asteroid(x,y,radius/Math.sqrt(numSplit),MAXASTVEL,MINASTVEL,hitsLeft-1,numSplit);
		}

		public boolean shipCollision(Ship ship){
			if((Math.pow(ship.getX()-x,2)+Math.pow(ship.getY()-y,2))<Math.pow(radius+ship.getRadius(),2))
				return true;
			return false;
		}

		public boolean shotCollision(Shot shot){
			if((Math.pow(shot.getX()-x,2)+Math.pow(shot.getY()-y,2))<Math.pow(radius,2))
				return true;
			return false;
		}

		public void draw(Graphics g){
			g.setColor(Color.gray);
			g.fillOval((int)(x-radius+.5),(int)(y-radius+.5),(int)(2*radius),(int)(2*radius));
		}

		public int getHitsLeft(){
			return hitsLeft;
		}

		public int getNumSplit(){
			return numSplit;
		}
	}
//Asteroids Ends