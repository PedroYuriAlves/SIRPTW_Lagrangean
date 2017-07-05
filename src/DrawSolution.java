import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import instancias.Instancia_Rahim;
import instancias.Variaveis_Rahim;

public class DrawSolution  extends JPanel{
	
	public DrawSolution(Variaveis_Rahim var,Instancia_Rahim inst) {
		
	}
	
	  public void paintComponent(Graphics g) {
	    int width = getWidth();
	    int height = getHeight();
	    g.setColor(Color.black);
	    g.drawOval(0, 0, width, height);
	    g.drawOval(3, 3, 11, 11);
	  }
	

}
