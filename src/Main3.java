import algoritmo.*;
import gurobi.*;
import instancias.Instancia_Rahim;
import instancias.Variaveis_Rahim;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
public class Main3 extends JPanel {
	
  public Main3() {
  }
  
  public void paintComponent(Graphics g) {
    int width = getWidth();
    int height = getHeight();
    g.setColor(Color.black);
    g.drawOval(0, 0, width, height);
    g.drawOval(3, 3, 11, 11);
  }
  public static void main(String args[]) {
    JFrame frame = new JFrame("Oval Sample");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(new Main3());
    frame.setSize(300, 200);
    frame.setVisible(true);
  }
}




