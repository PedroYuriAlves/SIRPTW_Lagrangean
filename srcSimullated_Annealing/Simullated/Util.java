package Simullated;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import instancias.Parametros;
import instancias.Sol_XZ;

public class Util{
	int deposito;
	//Clientes == Cidade
	public static Client [] clients = null;
	private Scanner sc;

	//vai ler a entrada
	public void inicializa(List<Integer> SRt,Sol_XZ XZ_Best,Parametros par,int t) {	
		//	sc = new Scanner(System.in);
		int dimension=0;
		//	while(sc.hasNext()){
		String dimension1 = String.valueOf(SRt.size()+1); //String que procura a quantidade de v�rtices
		dimension1 = dimension1.replace("DIMENSION : ", "");
		dimension = Integer.parseInt(dimension1); //Transformando String "dimension" em inteiro


		String capacidade = String.valueOf(par.kv);
		Truck.capacity = Integer.parseInt(capacidade); 

		//	else if(cmd[0].equals("NODE_COORD_SECTION")){						
		clients = new Client[dimension];
		clients[0]= new Client();
		//	sc.nextInt();										
		clients[0].setX(par.iPosX[0]);
		clients[0].setY(par.iPosY[0]);
		clients[0].id = String.valueOf(0);
		clients[0].idSIRP = String.valueOf(0);	
		clients[0].setTwa(0);
		clients[0].setTwd(1440);
		for (int i = 0;i< SRt.size();i++){
			clients[i+1]= new Client();
			//	sc.nextInt();										
			clients[i+1].setX(par.iPosX[SRt.get(i)]);
			clients[i+1].setY(par.iPosY[SRt.get(i)]);
			clients[i+1].id = String.valueOf((i+1));
			clients[i+1].idSIRP = String.valueOf(SRt.get(i));
			clients[i+1].setDemand(XZ_Best.z.qjt[SRt.get(i)][t]);
			clients[i+1].setTwa(par.ws_jt[SRt.get(i)][t]);
			clients[i+1].setTwd(par.we_jt[SRt.get(i)][t]);
			clients[i+1].setTwa(par.ws_jt[SRt.get(i)][t]);
			clients[i+1].setTs(15); // Tempo de Serviço
		}



		deposito = 0;


	}
	//retorna vetor de clientes
	public Client[] getClients(){		
		return this.clients;		
	}
	// Custo total da solu��o, que � o conjunto de rotas Os itens do linked list s�o as rotas e os dos array list s�o os clientes

	public double total_cost(LinkedList<ArrayList<Client>> s){		
		double c=0;

		for (int i = 0; i <s.size(); i++) {
			c+=calc_custo(s.get(i));
		}

		return c;
	}

	public double total_cost_cluster(LinkedList<Cluster> c){		
		double t=0;
		for (int i = 0; i <c.size(); i++) {
			ArrayList<Client> l =c.get(i).getL();
			t+=calc_custo(l);
		}
		return t;
	}

	//calcula a demanda atendida na rota
	public double calc_cap(ArrayList<Client> rota){
		double custo=0;
		for (int i = 0; i < rota.size(); i++) {
			custo+=rota.get(i).getDemand();
		}
		return custo;		
	}

	//copia um conjunto de rotas para outro
	public LinkedList<ArrayList<Client>> copia(LinkedList<ArrayList<Client>> sol2){
		LinkedList<ArrayList<Client>> sol1= new LinkedList<ArrayList<Client>>();
		for (int i = 0; i < sol2.size(); i++) {
			ArrayList<Client> rota = new ArrayList<Client>();
			sol1.add(rota);
			for (int j = 0; j < sol2.get(i).size(); j++) {
				rota.add(sol2.get(i).get(j));
			}
		}
		return sol1;
	}

	public ArrayList<Client> copia_rota(ArrayList<Client> r1){		
		Iterator<Client> it =r1.iterator();	
		ArrayList<Client> r2= new ArrayList<Client>();		
		while (it.hasNext()) {
			r2.add(it.next());
		}
		return r2;
	}


	//calcula o custo de UMA rota
	public double calc_custo(ArrayList<Client> rota){
		Client dep= clients[0];
		double custo=0;			
		if(rota.size()>0){			
			if(rota.size()>1){
				//calcula custo entre vertices
				for (int i = 1; i < rota.size(); i++) {
					custo+=dist(rota.get(i-1), rota.get(i));
				}
			}
			//incluir caso do deposito
			custo+=dist(rota.get(0), dep);
			custo+=dist(rota.get(rota.size()-1),dep);			
		}		
		return custo;				
	}

	public double calc_custo_sem_dep(ArrayList<Client> rota){				
		double custo=0;			
		//calcula custo entre vertices
		for (int i = 1; i < rota.size(); i++) {
			custo+=dist(rota.get(i-1), rota.get(i));
		}							
		return custo;				
	}

	//distancia euclidiana entre os clientes
	public double dist(Client c1,Client c2) {	
		return 	Math.sqrt(  Math.pow(  (  c1.getX() - c2.getX()  )  , 2  ) + Math.pow( ( c1.getY() - c2.getY()  )  , 2  )    );		
	}

	//distancia entre clientes e o centro geom�trico do cluster
	public double dist_cluster(Client c1,double[] gc) {	
		return 	Math.round(  Math.sqrt(  Math.pow(  (  c1.getX() - gc[0])  , 2  ) + Math.pow( ( c1.getY() - gc[1])  , 2  )    )  ) ;		
	}


	public void imprime_rotas(LinkedList<ArrayList<Client>> s){

		Iterator<ArrayList<Client>> iterator = s.iterator();
		double total=0;
		int r =1;
		int x =0;		
		while (iterator.hasNext()) {
			ArrayList<Client> l = iterator.next();

			double custo_rota=calc_custo(l);
			total+=custo_rota;
			if(l.size()>0){
				System.out.print("Rota #"+r+": 0 ");
				for (int i = 0; i < l.size(); i++) {
					System.out.print(l.get(i).id+ " ");
					x++;
				}
				System.out.print("0 custo: "+custo_rota+" demanda atendida: "+calc_cap(l)+"\n");
				r++;
			}			
		}

		System.out.println("Custo: "+ total);
	}

	public void imprime_rotas_cluster(LinkedList<Cluster> c){
		Iterator<Cluster> iterator = c.iterator();
		double total=0;
		int r =1;
		int x =0;		
		while (iterator.hasNext()) {
			Cluster l = iterator.next();

			double custo_rota=calc_custo(l.getL());
			total+=custo_rota;
			if(l.getL().size()>0){
				System.out.print("Rota #"+r+": 0 ");
				for (int i = 0; i < l.getL().size(); i++) {
					System.out.print(l.getL().get(i).id+ " ");
					x++;
				}
				System.out.print("0 custo: "+custo_rota+" demanda atendida: "+calc_cap(l.getL())+"\n");
				r++;
			}
		}

		System.out.println("Custo: "+ total);


	}

	public boolean cluster_igual(LinkedList<Cluster> a,LinkedList<Cluster> b){
		if(a.size()!=b.size()) return false;		
		for (int i = 0; i < a.size(); i++) {
			if(	!	(	rota_igual(	a.get(i).getL(),b.get(i).getL()	)	)	) return false;
		}		
		return true;

	}


	public boolean rota_igual(ArrayList<Client> a,ArrayList<Client> b){

		if(a.size()!=b.size()) return false;

		for (int i = 0; i < a.size(); i++) {
			if(a.get(i)!=b.get(i)) return false;		
		}

		return true;


	}


	public LinkedList<Cluster> copia_cluster(LinkedList<Cluster> a){
		LinkedList<Cluster> copia = new LinkedList<Cluster>();				
		for (int i = 0; i < a.size(); i++) {
			Cluster c = new Cluster();
			double[] gc = new double[2];
			gc[0]=a.get(i).getGc()[0];
			gc[1]=a.get(i).getGc()[1];
			c.setGc(gc);			
			ArrayList<Client> l = new ArrayList<Client>();
			for (int j = 0; j < a.get(i).getL().size(); j++) {
				l.add(a.get(i).getL().get(j));
			}
			c.setL(l);
			copia.add(c);
		}
		return copia;
	}

	public boolean sol_valida(LinkedList<ArrayList<Client>> sol){
		for (int i = 0; i < sol.size(); i++) {
			if(!rota_valida(sol.get(i))) return false;
		}		
		return true;

	}

	public boolean rota_valida(ArrayList<Client> rota){							
		if(calc_cap(rota)>Truck.capacity) return false; 				

		return true;		
	}

}