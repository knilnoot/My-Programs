/**
 * @(#)Ship.java
 *
 *
 * @author
 * @version 1.00 2014/2/7
 */

//Ship class starts
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


	public class Ship{
///
			boolean paused;
	int numShotsFired=0,numShotsHit=0;
		double shotAccuracy=0;
int SHOTLIFE=40;
	///

		private final double[] origXPts={12,-12,-8,-12},origYPts={0,-8,0,8},
				origFlameXPts={-8,-9.5,-25,-9.5},origFlameYPts={0,-3,0,3};
		private final int radius=6;

		private int shotDelay,shotDelayLeft;
		private double x,y,xVel,yVel,accel,rotVel,rot,velDecay;
		private boolean accelerating,rotatingLeft,rotatingRight;

		private int[] xPts,yPts,flameXPts,flameYPts;

		public Ship(double x,double y,double rot,double accel,double rotVel,double velDecay,int shotDelay){
			this.x=x;
			this.y=y;
			this.rot=rot;
			xVel=0;
			yVel=0;
			this.accel=accel;
			this.rotVel=rotVel;
			accelerating=false;
			rotatingLeft=false;
			rotatingRight=false;
			this.velDecay=velDecay;
			this.shotDelay=shotDelay;
			shotDelayLeft=0;
			xPts=new int[4];
			yPts=new int[4];
			flameXPts=new int[4];
			flameYPts=new int[4];
		}

		public void move(double xMin,double yMin,double xMax,double yMax){
			if(shotDelayLeft>0)
				shotDelayLeft--;
			if(rotatingLeft)
				rot-=rotVel;
			if(rotatingRight)
				rot+=rotVel;
			if(rot>(2*Math.PI))
				rot-=(2*Math.PI);
			else if(rot<0)
				rot+=(2*Math.PI);
			if(accelerating){
				xVel+=accel*Math.cos(rot);
				yVel+=accel*Math.sin(rot);
			}
			x+=xVel;
			y+=yVel;
			xVel*=velDecay;
			yVel*=velDecay;
			if(x<xMin)
				x+=xMax-xMin;
			else if(x>xMax)
				x-=xMax-xMin;
			if(y<yMin)
				y+=yMax-yMin;
			else if(y>yMax)
				y-=yMax-yMin;
		}

		public void draw(Graphics g){
			for (int i=0;i<4;i++){
				xPts[i]=(int)(origXPts[i]*Math.cos(rot)-origYPts[i]*Math.sin(rot)+x+.5);
				yPts[i]=(int)(origXPts[i]*Math.sin(rot)+origYPts[i]*Math.cos(rot)+y+.5);
				if(accelerating){
					flameXPts[i]=(int)(origFlameXPts[i]*Math.cos(rot)-origFlameYPts[i]*Math.sin(rot)+
							x+.5);
					flameYPts[i]=(int)(origFlameXPts[i]*Math.sin(rot)+origFlameYPts[i]*Math.cos(rot)+
							y+.5);
				}
			}
			if(accelerating){
				g.setColor(Color.red);
				g.fillPolygon(flameXPts,flameYPts,4);
			}
			if(paused)
				g.setColor(Color.darkGray);
			else
				g.setColor(Color.white);
			g.fillPolygon(xPts,yPts,4);
		}

		public Shot shoot(){
			/*if(shotDelayLeft>=-1){ // was: */if(shotDelayLeft<=0){
				shotDelayLeft=shotDelay;
				numShotsFired++;
				shotAccuracy=(double)numShotsHit/numShotsFired;
				return new Shot(x,y,rot,SHOTLIFE);
			}
			return null;
		}

		public void startAccelerating(){
			accelerating=true;
		}

		public void stopAccelerating(){
			accelerating=false;
		}

		public void startTurningLeft(){
			rotatingLeft=true;
		}

		public void stopTurningLeft(){
			rotatingLeft=false;
		}

		public void startTurningRight(){
			rotatingRight=true;
		}

		public void stopTurningRight(){
			rotatingRight=false;
		}

		public double getX(){
			return x;
		}

		public double getY(){
			return y;
		}

		public int getRadius(){
			return radius;
		}
	}
//Ship class ends