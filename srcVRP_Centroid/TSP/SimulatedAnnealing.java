package TSP;

import Principal.Caminhos;
import Principal.Random;
import Principal.Grafo;

import java.util.LinkedList;

public class SimulatedAnnealing {
	private static boolean estaNoPatamarDeModificacao(int modificacao, int patamar) {
		int inversoPatamar = patamar * -1;
		if(modificacao <= patamar && modificacao >= inversoPatamar) return true;
		return false;
	}
	private static boolean aceita(int distTotalAnterior, int distTotalAtual, double temperatura) {
		double deltaE = distTotalAnterior - distTotalAtual; //deltaE será negativo
		double expoente = deltaE/temperatura;
		double probabilidadeAceitacao = Math.pow(Math.E, expoente); //Numero "e" elevado a (deltaE/temperatura)
		
		double random = Random.gerarAleatorio();
		if(probabilidadeAceitacao > random) return true;
		else return false;
	}
	public static void Simulated_Annealing(LinkedList<Integer> cluster) {
		if(cluster.size()==1) return; //Primeira solucao trivial (1 estado)
		else if(cluster.size()==2) { //Segunda solucao trivial (2 estados)
			cluster.addFirst(Grafo.indexDepot);
			cluster.addLast(Grafo.indexDepot);
			int distAnterior = Caminhos.distanciaTotalCluster(cluster);
			
			int valorSwap1 = cluster.get(1);
			int valorSwap2 = cluster.get(2);
			cluster.set(1, valorSwap2);
			cluster.set(2, valorSwap1);
			
			int distAtual = Caminhos.distanciaTotalCluster(cluster);
			
			cluster.remove(cluster.size()-1);
			cluster.remove(0);
			if(distAtual <= distAnterior) return;
			else {
				cluster.set(1, valorSwap1); //Se piorou, volta ao que era antes
				cluster.set(2, valorSwap2); //Se melhorou ou continuou a mesma distancia, permanece solucao atual
			}
			return;
		}
		
		
		//Simulated Annealing propriamente dito comeca a partir deste ponto
		int tamanhoLista = cluster.size();
		
 		double temperatura = (double) tamanhoLista*tamanhoLista;
		double taxaResfriamento = (double) (1/(double)(tamanhoLista*tamanhoLista));
		double limiteTemperatura = taxaResfriamento;
		int ultimaModificacao = 0;
		int patamarModificacao = (tamanhoLista*tamanhoLista);
		
		int posicaoSwap1;
		int posicaoSwap2;
		
		int distTotalAnterior;
		int distTotalAtual;
		
		cluster.addFirst(Grafo.indexDepot);
		cluster.addLast(Grafo.indexDepot);
 		distTotalAnterior = Caminhos.distanciaTotalCluster(cluster);
 		tamanhoLista = tamanhoLista+2; //Mais os 2 depots adicionados
 		
		while(temperatura >= limiteTemperatura || estaNoPatamarDeModificacao((int)ultimaModificacao,patamarModificacao)) {
			
			double posSwap1 = (double) Math.random()*tamanhoLista;
			double posSwap2 = (double) Math.random()*tamanhoLista;
			
			posicaoSwap1 = (int) posSwap1;
			posicaoSwap2 = (int) posSwap2;
			
			while(posicaoSwap1==posicaoSwap2 || posicaoSwap1==0 || posicaoSwap2==0 || 
												posicaoSwap1==tamanhoLista-1 || posicaoSwap2==tamanhoLista-1) {
				posSwap1 = (double) Math.random()*tamanhoLista;
				posSwap2 = (double) Math.random()*tamanhoLista;
				posicaoSwap1 = (int) posSwap1;
				posicaoSwap2 = (int) posSwap2;
			}
			
			int valorSwap1 = cluster.get(posicaoSwap1);
			int valorSwap2 = cluster.get(posicaoSwap2);

			cluster.set(posicaoSwap1, valorSwap2);
			cluster.set(posicaoSwap2, valorSwap1);
		
			distTotalAtual = Caminhos.distanciaTotalCluster(cluster);
			boolean modificou = false;
			
			if(distTotalAtual > distTotalAnterior) { //Solucao atual eh pior que a anterior
				boolean aceitacao = aceita(distTotalAnterior, distTotalAtual, temperatura);
				if(aceitacao==false) { //Retoma a condicao inicial
					cluster.set(posicaoSwap1, valorSwap1); //Volta ao estado anterior
					cluster.set(posicaoSwap2, valorSwap2); //Volta ao estado anterior
							
					modificou = false;
				}
				else {
					distTotalAnterior = distTotalAtual;
					modificou = true;
				}
			}
			else { //Solucao atual eh melhor que a anterior
				distTotalAnterior = distTotalAtual;
				modificou = true;
			}
			if(modificou == true) {
				ultimaModificacao += 1;
			}
			else ultimaModificacao -=1;
			temperatura = temperatura - (temperatura*taxaResfriamento);
		}
		cluster.remove(cluster.size()-1);
		cluster.remove(0);
 	}
}
