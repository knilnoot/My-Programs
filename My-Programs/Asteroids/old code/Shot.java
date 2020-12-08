/**
 * @(#)Shot.java
 *
 *
 * @author
 * @version 1.00 2014/2/7
 */
//Shot class starts
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


	public class Shot{
		public static final double SHOTVEL=5;
		public double x,y,xVel,yVel,Ang; //WAS PRIVATE DOUBLE
		int life;
		int subLife = 35; //for changing direction while travelling
		int subLife2 = 100; //for changing direction while travelling

		public Shot(double x,double y,double angle,int life){
			this.x=x;
			this.y=y;
			xVel=SHOTVEL*Math.cos(angle);
			yVel=SHOTVEL*Math.sin(angle);
			Ang=angle;
			this.life=life;
		}

		public void move(double xMin,double yMin,double xMax,double yMax){
			life--;
			x+=xVel;
			y+=yVel;
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
			g.setColor(Color.yellow);
			g.fillOval((int)(x-.5),(int)(y-.5),3,3);
		}

		public double getX(){
			return x;
		}

		public double getY(){
			return y;
		}

		public int getLife(){
			return life;
		}

		public int getSubLife(){
			return subLife;
		}
		public int getSubLife2(){
			return subLife2;
		}
	}
//Shot class ends