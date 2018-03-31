package SATW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import org.python.modules.math;

import Simullated.Client;
import Simullated.Truck;
import Simullated.Util;
import instancias.Parametros;

public class SimullatedAnnealing_TW {

	Client[] clients;
	LinkedList<ArrayList<Client>> S;			// solução atual
	public LinkedList<ArrayList<Client>> Sb; 	// melhor solução
	//LinkedList<ArrayList<Client>> c_sol;		// copia solução

	public double Ts = 100;//
	public double Tf = 0.001;
	public double Tb = 0;
	public double Tr = Ts;
	public double Tk = 0;
	public int R = 10;
	int nResets = 0;
	public double tau = 0;
	int k = 0;
	Util u = new Util();

	public SimullatedAnnealing_TW(Client[] c,Random rand) {
		this.clients = c;
		this.S = new LinkedList<ArrayList<Client>>();
		this.Sb = new LinkedList<ArrayList<Client>>();
		this.tau = rand.nextDouble();
	}
	// incluir métodos para geração de vizinhos
	public LinkedList<ArrayList<Client>> SimullatedAnnealing_TW_CVRP(Random rand,Parametros par) {
		//Step SA-1
		S = new LinkedList<ArrayList<Client>>();
		S  = PFIH(par);
		Sb = S;
		//Step SA-2
		S = interchangeLSD(par,S, rand);
		//Step SA-3
		Ts= Tb = Tr = Tk = 100;
		tau = 0.5;

		do{//Step SA-4
			LinkedList<ArrayList<Client>> S1 = ConstroiNS2and1(rand, u.copia(S),par);	
			Double DS = u.total_cost(S1) - u.total_cost(S); 
			//Step SA-5
			double o0 = rand.nextDouble();
			if(DS <= 0 || (DS>0 && math.exp(-DS/Tk)>= o0)){
				S = S1;

				if(u.total_cost(S)< u.total_cost(Sb)){
					S = interchangeLSD(par, S, rand);
					Sb = S;
					Tb = Tk;
					nResets = 0;
				}
			}
			//Step SA-6
			k++;
			Tk = Tk/(1+ tau*(math.sqrt(Tk)));

			ArrayList<LinkedList<ArrayList<Client>>> NS = ConstroiNS(rand,S,par);
			if(NS.size() == 0 || Tk <= Tf){
				Tr = ((Tr/2) > Tb) ? (Tr/2) : Tb;
				Tk = Tr;
				nResets++;
			}
			//Step SA-7
			if(nResets >= R){
				break;
			}
		}while(true);

		return Sb;		
	}
	public LinkedList<ArrayList<Client>> PFIH(Parametros par) {
		LinkedList<ArrayList<Client>> ret = new LinkedList<ArrayList<Client>>();

		int tamanho = clients.length - 1;
		boolean vaiAdicionar;
		ArrayList<Client> rotaAtual = new ArrayList<Client>();
		for (int c = 1; c < clients.length; c++) {

			if(rotaAtual.size() == 0){
				rotaAtual.add(clients[c]);
				checkConstraint(par, rotaAtual);
			}
			else{
				int bestIndice = -1;
				double Bestcusto = Double.MAX_VALUE;
				for (int i = 0; i<rotaAtual.size();i++) {
					ArrayList<Client> rotaAtualAux = u.copia_rota(rotaAtual);
					rotaAtualAux.add(i, clients[c]);
					if (checkConstraint(par, rotaAtualAux)) {
						double custo = u.calc_custo(rotaAtualAux);
						if(custo < Bestcusto){
							bestIndice = i;
							Bestcusto = custo;
						}
					}
				}
				ArrayList<Client> rotaAtualAux = u.copia_rota(rotaAtual);
				rotaAtualAux.add(rotaAtualAux.size(), clients[c]);
				if (checkConstraint(par, rotaAtualAux)) {
					double custo = u.calc_custo(rotaAtualAux);
					if(custo < Bestcusto){
						bestIndice = rotaAtualAux.size();
						Bestcusto = custo;
					}
				}	

				if(bestIndice > -1){
					rotaAtual = rotaAtualAux;
				}else{
					ret.add(rotaAtual);
					rotaAtual = new ArrayList<Client>();
					c--;
				}
			}
		}		
		if(rotaAtual.size() > 0){
			ret.add(rotaAtual);
		}
		return ret;
	}
	public LinkedList<ArrayList<Client>> interchangeLSD(Parametros par, LinkedList<ArrayList<Client>> S,Random rand) {
		LinkedList<ArrayList<Client>> ret = new LinkedList<ArrayList<Client>>(S);
		Double CS = u.total_cost(S);
		ArrayList<LinkedList<ArrayList<Client>>> NS = ConstroiNS(rand,S,par);
		//ret = S;
		for (int i = 0; i < NS.size(); i++) {
			Double CS1 = u.total_cost(NS.get(i));
			if(CS1 < CS){
				CS  = CS1;
				ret = NS.get(i);
				continue;
			}			
		}

		return ret;	
	}


	public LinkedList<ArrayList<Client>> ConstroiNS2and1(Random rand,LinkedList<ArrayList<Client>> S, Parametros par){

		LinkedList<ArrayList<Client>> newS = new LinkedList<ArrayList<Client>>();
		newS = u.copia(S);
		for (int q = 0; q < S.size()-1; q++) {
			ArrayList<Client> Rq = S.get(q);
			for (int p = q+1; p < S.size(); p++) {				
				ArrayList<Client> Rp = S.get(p);
				LinkedList<ArrayList<Client>> rest = new LinkedList<>();
				LinkedList<ArrayList<Client>> newAtu = new LinkedList<>();
				for (int i = 0; i < S.size(); i++) {	
					if(i != p && i != q);
					rest.add(S.get(i));
				}

				LinkedList<ArrayList<Client>> op2 = operation(2,0,Rq,Rp, rand);
				Rq = op2.get(0);
				Rp = op2.get(1);

				LinkedList<ArrayList<Client>> op1 = operation(1,0,Rq,Rp,rand);
				Rq = op1.get(0);
				Rp = op1.get(1);

				if(Rp.size() > 0 && Rq.size() > 0){
					newAtu.add(Rp);
					newAtu.add(Rq);
					newAtu.addAll(rest);
					//	NS.add(newS);
					if(checkSolutionConstraint(par, newAtu)){
						if(u.total_cost(newAtu) < u.total_cost(newS)){
							newS = new LinkedList<>();
							newS = newAtu;
						}
					}
				}
			}
		}		
		return newS;
		//	return NS;
	}

	public LinkedList<ArrayList<Client>> operation(int q,int p,ArrayList<Client> Rq, ArrayList<Client> Rp, Random rand){

		LinkedList<ArrayList<Client>> ret = new LinkedList<ArrayList<Client>>();

		try{
			if(q > Rq.size())q = Rq.size();
			if(p > Rp.size())p = Rp.size();
			ArrayList<Integer> indexRq = new ArrayList<Integer>();
			for (int i = 0; i < q; i++) {
				int indexRand = round((Rq.size()-1) * rand.nextDouble());
				do{
					indexRand = round((Rq.size()-1) * rand.nextDouble());
				}while(indexRq.contains(indexRand));

				if(!indexRq.contains(indexRand)){
					indexRq.add(indexRand);
				}			
			}

			ArrayList<Integer> indexRp = new ArrayList<Integer>();						
			for (int i = 0; i < p; i++) {
				int indexRand = round(((Rp.size()-1) * rand.nextDouble()));
				do{
					indexRand = round(((Rp.size()-1) * rand.nextDouble()));
				}while(indexRp.contains(indexRand));

				if(!indexRp.contains(indexRand)){
					indexRp.add(indexRand);
				}	
			}
			ArrayList<Client> removedClientsq = new ArrayList<>();
			//Remover
			Collections.sort(indexRq);
			Collections.reverse(indexRq);
			if(indexRq.size()>0){
				for (int i = 0; i < indexRq.size(); i++) {
					int iremove = indexRq.get(i);
					removedClientsq.add(Rq.remove(iremove));					
				}
			}
			Collections.sort(indexRp);
			Collections.reverse(indexRp);
			ArrayList<Client> removedClientsp = new ArrayList<>();
			if(indexRp.size()>0){
				for (int i = 0; i < indexRp.size(); i++) {
					int iremove = indexRp.get(i);
					removedClientsp.add(Rp.remove(iremove));	
				}
			}
			//Trocar
			if(indexRq.size()>0){
				for (int i = 0; i < removedClientsq.size(); i++) {
					Client c = removedClientsq.get(i);
					int iremove = indexRq.get(i);
					if(iremove > Rp.size()){
						Rp.add(Rp.size(), c);
					}
					else{
						Rp.add(iremove, c);
					}
				}
			}
			if(indexRp.size()>0){
				for (int i = 0; i < removedClientsp.size(); i++) {
					Client c = removedClientsp.get(i);
					int iremove = indexRp.get(i);
					if(iremove > Rq.size()){
						Rq.add(Rq.size(), c);
					}
					else{
						Rq.add(iremove, c);
					}
				}
			}


			ret.add(Rq);
			ret.add(Rp);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return ret;
	}

	public ArrayList<LinkedList<ArrayList<Client>>> ConstroiNS(Random rand,LinkedList<ArrayList<Client>> S,Parametros par){
		ArrayList<LinkedList<ArrayList<Client>>> NS = new ArrayList<LinkedList<ArrayList<Client>>>();

		for (int q = 0; q <S.size()-1; q++) {
			ArrayList<Client> Rq = new ArrayList<Client>(S.get(q));

			for (int p = q+1; p < S.size(); p++) {				
				ArrayList<Client> Rp =  new ArrayList<Client>(S.get(p));
				LinkedList<ArrayList<Client>> rest = new LinkedList<>();
				for (int i = 0; i < S.size(); i++) {	
					if(i != p && i != q);
					rest.add(S.get(i));
				}


				double custoOriginalRq = u.calc_custo(Rq);
				double custoOriginalRp = u.calc_custo(Rp);

				for (int iq = 0; iq < 3; iq++) {
					for (int ip = 0; ip < 3; ip++) {
						if(iq == 0 && ip == 0)continue;
						if(Rq.size() < iq) continue;
						if(Rp.size() < ip) continue;

						LinkedList<ArrayList<Client>> op = operation(iq,ip,Rq,Rp,rand);
						Rq = op.get(0);
						Rp = op.get(1);

						double custoNewRq = u.calc_custo(Rq);
						double custoNewRp = u.calc_custo(Rp);

						if(custoNewRq + custoNewRp < custoOriginalRp + custoOriginalRq)
						{
							LinkedList<ArrayList<Client>> newS = new LinkedList<ArrayList<Client>>();

							newS.add(Rp);
							newS.add(Rq);
							newS.addAll(rest);
							if(checkSolutionConstraint(par, newS)){
								NS.add(newS);
							}
						}
					}
				}				
			}
		}
		return NS;
	}

	public double hourToMinutes(double hour){
		
		double min = hour*60;
		return min;
		
	}
	public boolean checkConstraint(Parametros par ,ArrayList<Client> rota){

		//Restrição 6
		if(u.calc_cap((rota)) > Truck.capacity){
			return false;
		}

		double timeservice = 0D;
		int idAnt = 0;
		double timeChegadaAnt = 0D;
		double timeWatingAnt = 0D;
		double timeAnterior = par.timeInit;
		double qtdAcum = 0;
		
		for(int c = 0; c < rota.size(); c++)
		{
			double timeChegada = timeAnterior+hourToMinutes(par.θ_ij[idAnt][Integer.parseInt(rota.get(c).id)]);
			double timeWating = 0;
			if(timeChegada < rota.get(c).getTwa()){
				timeWating = rota.get(c).getTwa() - timeChegada;
				//timeChegada = rota.get(c).getTwa();
			}
			
			//Restrição 9
			if(c>0){
				if(timeChegadaAnt+hourToMinutes(par.θ_ij[idAnt][Integer.parseInt(rota.get(c).id)])+rota.get(c-1).getTs() +timeWatingAnt > timeChegada){
					return false;
				}
			}

			//Restrição 10
			if(!(rota.get(c).getTwa()<= timeChegada+timeWating && timeChegada+timeWating <= rota.get(c).getTwd())){
				return false;
			}

			rota.get(c).setTa(timeChegada);
			double timeSaida =  timeChegada + rota.get(c).getTs() + timeWating;
			timeservice += hourToMinutes(par.θ_ij[idAnt][Integer.parseInt(rota.get(c).id)])+rota.get(c).getTs()+ timeWating;

			rota.get(c).setTa(timeChegada);
			rota.get(c).setTd(timeSaida);
			rota.get(c).setTw(timeWating);
			
			timeAnterior = timeSaida;
			timeChegadaAnt = timeChegada;
			timeWatingAnt = timeWating;

			qtdAcum +=  rota.get(c).getDemand();
			idAnt = Integer.parseInt(rota.get(c).id);
		}

		//Restrição 7
		if(timeservice > par.Rv)
		{
			return false;
		}

		//Restrição 6
		if(qtdAcum > par.kv)
		{
			return false;
		}
		return true;		
	}

	public boolean checkSolutionConstraint(Parametros par ,LinkedList<ArrayList<Client>> solution){
		boolean ret = true;
		for (ArrayList<Client> rota : solution) {
			if(!checkConstraint(par, rota)){
				ret = false;
			}
		}
		return ret;
	}

	private int round(double d){
		double dAbs = Math.abs(d);
		int i = (int) dAbs;
		double result = dAbs - (double) i;
		if(result<0.5){
			return d<0 ? -i : i;            
		}else{
			return d<0 ? -(i+1) : i+1;          
		}
	}
}