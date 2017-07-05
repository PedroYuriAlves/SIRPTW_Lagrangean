package Simullated;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Simullated_Annealing {
	Client[] clients;
	LinkedList<ArrayList<Client>> c_sol;
	LinkedList<ArrayList<Client>> sol;
	public LinkedList<ArrayList<Client>> melhor_sol;
	int nSelect = 5;
	Util u = new Util();
	public double aux;
	public Simullated_Annealing(Client[] c){		
		this.clients=c;
		this.sol= new LinkedList<ArrayList<Client>>();
		this.c_sol= new LinkedList<ArrayList<Client>>();
		this.melhor_sol= new LinkedList<ArrayList<Client>>();
	}
		
	/*public static void main(String[] args) {							
		Util z = new Util();
		//z.inicializa();
		Simullated_Annealing s = new Simullated_Annealing(z.clients);
		s.Annealing_CVRP();
		z.imprime_rotas(s.melhor_sol);
	}	*/
	
	//criara rotas e adicionar� na solucao, q � o conjunto das rotaz
	
	public void init(){
		
		sol = new LinkedList<ArrayList<Client>>();
		int tam = clients.length-1;
		boolean vai_add;
		while(tam>0){
			ArrayList<Client> r = new ArrayList<Client>();
			vai_add=false;			
			for(int j=1;j<clients.length;j++){			
				if(((u.calc_cap(r)+ clients[j].getDemand())<=Truck.capacity)&&(!clients[j].isAtendido())){					
					r.add(clients[j]);
					clients[j].setAtendido(true);
					tam--;
					vai_add=true;
				}
				
			}			
				if(vai_add)sol.add(r);				
		}

	}

	
	public void move(){
		aux=0;
		Client [] c12345 = new Client[nSelect];
		for (int i = 0; i < c12345.length; i++) {
			c12345[i]=menor();
		}
		//		Client c1=menor();
		//		Client c2=menor();
		//		Client c3=menor();
		//		Client c4=menor();
		//		Client c5=menor();
		Client [] novo = new Client[nSelect];//novo cliente q sera inserido
		Random r = new Random();
		int rota_sai=0; //rotas
		int cliente=0; //indice do cliente
		int rota_entra=0;//nova rota onde o cliente sera inserido				
		boolean ja_existe = true;//� um dos nSelect menores
		int i =0;
		
		
		//CRIAR BACKUP PARA O CASO DE ESCOLHER CLIENTES E ROTAS Q NAO DE PARA INSERIR 
		LinkedList<ArrayList<Client>> backup = u.copia(sol);
		boolean precisa_backup= true;
		
		
		
		do{
			//Escolhe nSelect clientes diferentes dos "menores" e os remove das suas respectivas listas
			while(i<nSelect){
				//pega n�s diferentes dos nSelect menores
				do{
					rota_sai= r.nextInt(sol.size());
					//	se o a rota aleatoria nao estiver vazia
					if(!(sol.get(rota_sai).size()==0)){
						//pega o indice aleatorio de um cliente da rota 
						cliente=r.nextInt(sol.get(rota_sai).size());	
						novo[i]= sol.get(rota_sai).get(cliente);
						
//						if ( novo[i] == c1 || novo[i]== c2 || novo[i] == c3 || novo[i] ==c4 || novo[i] ==c5) ja_existe=true;
						//						else ja_existe = false;
						 ja_existe = false;
						for (int ci = 0; ci < c12345.length; ci++) {
							if(c12345[ci]==novo[i]){
								ja_existe=true;
								break;
							}
						}
					}
					else ja_existe=true;
				
				} while(ja_existe);
				//remove o cliente da lista
				sol.get(rota_sai).remove(cliente);			
				i++;																		
			}		
			
			i=0;
			int y=0;
			ArrayList<Client> c = new ArrayList<Client>();
			
			for (int j = 0; j < novo.length; j++) {
				c.add(novo[j]);
			}
			
			//Insere os nSelect clientes removidos em nSelect rotas aleat�rias
			while(c.size()>0){
				rota_entra =r.nextInt(sol.size());			
				if( (u.calc_cap(sol.get(rota_entra)) + c.get(0).getDemand()) <= Truck.capacity){
					if (sol.get(rota_entra).size()==0) {												
						sol.get(rota_entra).add(c.get(0));												
					}
					else {
						int p = (int) acha_posicao(sol.get(rota_entra), c.get(0))[0];				
						insere(sol.get(rota_entra), (c.get(0)),p);
						
					}
					c.remove(0);
				}				
				else {
					i++;
					if(i>sol.size()*2){
						i=0;
						Collections.shuffle(c);
						y++;
						if(y>c.size()*3){
							break;
						}
					}
				}
				
			}
			
			if(c.size()==0) precisa_backup=false;
			if(precisa_backup) sol = u.copia(backup); 
			
		}while(precisa_backup);
	}
	
	public Client menor(){
		Client escolhido=null;		
		double menor_dist=Double.MAX_VALUE;		
		for (int i = 0; i < sol.size(); i++) {
			double d;
			ArrayList<Client> rota =sol.get(i);
			for (int j =1; j < rota.size(); j++) {
				d = u.dist(rota.get(j-1), rota.get(j));
				if( ( d  < menor_dist )&&(d>aux)){
						escolhido = rota.get(j);
						menor_dist=d;						
				}	
			}	
			if(rota.size()>0){
				
				d= u.dist(clients[0],rota.get(0));
				if( ( d  < menor_dist )&&(d>aux)){
					escolhido = rota.get(0);
					menor_dist=d;			
				}
				
				d=u.dist(rota.get(rota.size()-1),clients[0]);
				if( ( d  < menor_dist )&&(d>aux)){
					escolhido = rota.get(rota.size()-1);
					menor_dist=d;			
				}
			}
		}
		
		aux=menor_dist;		
		return escolhido;
	}
	
	
	public int[] maior_media(){
		double melhor_d = Double.MIN_VALUE; 
		int indice_rota =0;
		int indice_cliente =0;
		ArrayList<Client> rota;
		for (int i = 0; i < sol.size(); i++) {						
			rota= sol.get(i);			
			Client i_menos_1;
			Client i_mais_1;
			double d=0;
			for (int j = 0; j < rota.size(); j++) {
					//ANTERIOR DO 1 CLIENTE � O DEPOSITO
					if(j==0) i_menos_1= clients[0];
					else i_menos_1= rota.get(j-1);
					//PROXIMO DO ULTIMO CLIENTE � O DEPOSITO
					if(j==rota.size()-1) i_mais_1=clients[0];
					else i_mais_1 = rota.get(j+1);							
					d = (u.dist(i_menos_1,rota.get(j))+ u.dist(rota.get(j),i_mais_1));
					d=d/2;
					if(d>melhor_d && d<aux){
						melhor_d=d;
						indice_rota=i;
						indice_cliente=j;
					}
			}
		}		
		aux=melhor_d;
		//SE REMOVER ANTES DE ESCOLHER VAI ALTERAR O CENARIO
		//sol.get(indice_rota).remove(indice_cliente);
		int sol[] = new int[2];
		sol[0]=indice_rota;
		sol[1]=indice_cliente;
		
		return sol;
	}

	public void replace_highest_average(){
		aux=Integer.MAX_VALUE;
		//lista que contera os nSelect clientes com as maiores medias de distancia
		ArrayList<Client> c= new ArrayList<Client>();
		int[] temp;
		for (int i = 0; i < nSelect; i++) {
			temp = maior_media();
			c.add(sol.get(temp[0]).get(temp[1]));
			sol.get(temp[0]).remove(temp[1]);
		}
		//variavel para verificar se existe rota para insercao
		boolean achou=false;
		//criar� nSelect rotas aleat�rias
		int rotas_aleatorias[] = new int [nSelect];
		//adiciona todas as listas nas rotas, para poder ser mais aleatorio pois no java ele usa o relogio para gerar o numero
		//o que pode causar muitas repeti��es		
		ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i=0; i<sol.size(); i++) {
            list.add(new Integer(i));
        }
        //"Bagun�a a lista"
        Collections.shuffle(list);
        //se tiver menos do q nSelect rotas
        for (int i=0; i<nSelect; i++) {
        	rotas_aleatorias[i%sol.size()]=list.get(i%sol.size());
        }	        	 
        //enquanto nem todos os clientes forem adicionados
		while(c.size()>0){
			achou=false;			        
			int melhor_i=0;
			double melhor_custo=Double.MAX_VALUE;
			double custo;
			double resp[];
			double melhor_resp[]=new double[2];	
			//nSelect porque � o tamanho das rotas aleatorias			
			//PARA OS nSelect CLIENTES COM A MAIOR MEDIA	
			for (int i =0; i<nSelect;i++){				
					//CALCULE O CUSTO DA ROTA ALEATORIA I
					//SE TIVER CUSTO E PQ N ESTA VAZIA A ROTA
					resp = (acha_posicao(sol.get(rotas_aleatorias[i]), c.get(0)));
					custo= u.calc_custo(sol.get(rotas_aleatorias[i]));
					
					if(custo>0){
						custo=	resp[1];
						//CALCULE O NOVO CUSTO DA ROTA COM A INSERCAO DO NOVO NO
						
						if( custo< melhor_custo && ((u.calc_cap(sol.get(rotas_aleatorias[i])) +c.get(0).getDemand()) <=Truck.capacity ) ){
								melhor_i=rotas_aleatorias[i];
								melhor_custo=custo;
								melhor_resp=resp;
								achou  = true;
						}
					}
					else {
						melhor_i=rotas_aleatorias[i];
						melhor_custo=custo+  (u.dist(clients[0], c.get(0)));
						melhor_resp[0]=-1;
						achou  = true;						
					}
				}
			
				if(achou) {
					insere(sol.get(melhor_i), c.get(0), ((int) melhor_resp[0]));
					c.remove(0);
				}
				//se n�o existir uma rota que aceite o vertice, s�o escolhidas outras rotas
				if(!achou){
					//System.out.println("SHUFLOU");
					move();
				}
			}
	}

	
	


	
	public void Annealing_CVRP(Random rand){
		double t_ini = System.currentTimeMillis();
		double t_f =0;
		double fim = 10000;
		if(this.clients.length <= 11){
//			this.nSelect = Math.floorDiv(this.clients.length, 2)-1;
			this.nSelect = 5;
//			if(this.nSelect < 1) this.nSelect = 1;
		}
		//System.out.println("INICIOU O ANNEALING");
		Random r = rand;
		init();
		//System.out.println("passou o init");
		double alpha = 0.99;
		double beta= 	1.05;
		double M0 = 5;
		melhor_sol = u.copia(sol);
		double T = 5000;
		//double MAXTIME = 1.0E11;
		c_sol = u.copia(sol);
		double custo_atual = u.total_cost(c_sol);
		double melhor_custo = u.total_cost(melhor_sol);
		double Time= 0;
		//System.out.println("T "+T); 
		//System.out.println(total_cost(melhor_sol));
		do{
			double M = M0;
			do{
				
				sol=u.copia(c_sol);
				if(r.nextDouble()<=0.8)	move();
				replace_highest_average();
				double novo_custo=u.total_cost(sol);
				double dcusto = u.total_cost(sol) - custo_atual;
				if(dcusto<0){
					c_sol= u.copia(sol);				
					custo_atual = u.total_cost(c_sol);
					if(novo_custo<melhor_custo){
						melhor_sol = u.copia(sol);
						melhor_custo = novo_custo;
					}
				}
				
				else if ((r.nextDouble()) < Math.exp(-(dcusto/T))){
					c_sol= u.copia(sol);				
					custo_atual = u.total_cost(c_sol);
				}							
				
				M=M-1;			
		}while(M>=0);
			Time = Time + M0;			
			T=T*alpha;			
			M0=M0+beta;
		//	System.out.println("T " +T);
	//		System.out.println(total_cost(melhor_sol));			
			t_f = System.currentTimeMillis() - t_ini;	
//			System.out.println("Tempo: "+(t_f));
		}while(T>0.001 && ((fim>t_f)));				
	
	}	
	
	public void insere(ArrayList<Client> rota, Client c, int p){		

		if(p<0) rota.add(c);
		else rota.add(p, c);
		
		
	}
	
	public double[] acha_posicao(ArrayList<Client> rota, Client c){
		
		
		double resp [] = new double [2];
		//resp[0] = rota, resp [1]= posicao na rota, resp [2] =custo da rota;
		double melhor_custo= Double.MAX_VALUE;		
		double custo_=u.calc_custo(rota);
		double custo=0;
		int melhor_i =0;
		Client i_menos_1;

		for (int j = 0; j < rota.size(); j++) {
			//ANTERIOR DO 1 CLIENTE � O DEPOSITO
			if(j==0) i_menos_1= clients[0];
			else i_menos_1= rota.get(j-1);
			//PROXIMO DO ULTIMO CLIENTE � O DEPOSITO
			custo = custo_ + (u.dist(i_menos_1,c)+ u.dist(rota.get(j),c)) - (u.dist(i_menos_1,rota.get(j)));
			
			if(custo<melhor_custo){
				melhor_custo=custo;
				melhor_i=j;
			}
		}
		if(rota.size()>0) {
			
			custo = custo_ + (u.dist(rota.get(rota.size()-1),c)+ u.dist(c,clients[0]) - (u.dist(rota.get(rota.size()-1),clients[0])));
			if(custo<melhor_custo){
				melhor_custo= custo;
				melhor_i= -1;
			}	
		}
		
		/*if(melhor_i==-1){
			
			rota.add(c);
		}else{
			rota.add(melhor_i,c);
		}
		*/
		resp[0]=melhor_i;
		resp[1]=melhor_custo;
		return resp;
	}			


}
