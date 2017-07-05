package Simullated;

public class Client {

	public Client(){
		this.setAtendido(false);		
	}
	private double X;
	private double Y;
	private double demand;
	private boolean atendido;
	
	public String id;
	public String idSIRP;

	private double twa;
	private double twd;
	private double ta;
	private double td;
	
	private double tw;
	private double ts;
	/**
	 * @return the twa
	 */
	public double getTwa() {
		return twa;
	}

	/**
	 * @param twa the twa to set
	 */
	public void setTwa(double twa) {
		this.twa = twa;
	}

	/**
	 * @return the twd
	 */
	public double getTwd() {
		return twd;
	}

	/**
	 * @param twd the twd to set
	 */
	public void setTwd(double twd) {
		this.twd = twd;
	}

	/**
	 * @return the ta
	 */
	public double getTa() {
		return ta;
	}

	/**
	 * @param ta the ta to set
	 */
	public void setTa(double ta) {
		this.ta = ta;
	}

	/**
	 * @return the td
	 */
	public double getTd() {
		return td;
	}

	/**
	 * @param td the td to set
	 */
	public void setTd(double td) {
		this.td = td;
	}

	/**
	 * @return the tw
	 */
	public double getTw() {
		return tw;
	}

	/**
	 * @param tw the tw to set
	 */
	public void setTw(double tw) {
		this.tw = tw;
	}

	/**
	 * @return the ts
	 */
	public double getTs() {
		return ts;
	}

	/**
	 * @param ts the ts to set
	 */
	public void setTs(double ts) {
		this.ts = ts;
	}
	
	public double getX() {
		return X;
	}
	
	public void setX(double x) {
		X = x;
	}
	public double getY() {
		return Y;
	}
	public void setY(double y) {
		Y = y;
	}
	public double getDemand() {
		return demand;
	}
	public void setDemand(double demand) {
		this.demand = demand;
	}

	public boolean isAtendido() {
		return atendido;
	}

	public void setAtendido(boolean atendido) {
		this.atendido = atendido;
	}
	
	
	
}
