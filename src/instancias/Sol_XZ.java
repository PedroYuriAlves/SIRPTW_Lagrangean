package instancias;

/**
 * @author Krespo
 *
 */
public class Sol_XZ {

	public Sol_XZ(int S, int T,int V){
		 x = new X(S,T,V);
		 z = new Z(S,T,V);
	}
	public Sol_XZ(){
	
	}
	
	public X x;
	public Z z;
	public double custo;
	public double LB;
	public double UB;
	public String msg = "";
	public int k;
	public int C_LRk;
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Sol_XZ\n"
				+ " [custo=" + custo + ", LB=" + LB + ", UB=" + UB + "]";
	}
}
