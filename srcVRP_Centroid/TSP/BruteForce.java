package TSP;

import Principal.Caminhos;
import Principal.Grafo;
import java.util.LinkedList;

public class BruteForce {
	@SuppressWarnings("unchecked")
	public static void gerarTodasSolucoes(LinkedList<Integer> cluster, LinkedList<LinkedList<Integer>> mapa, 
			   							  LinkedList<Integer> atual, int pos) {

		int proximaPos = pos+1;

		if(pos==0) {
			for(Integer estadoAtual : cluster) {
				LinkedList<Integer> listaAtual = new LinkedList<Integer>();
				listaAtual.addFirst(Grafo.indexDepot);
				listaAtual.addLast(estadoAtual);
				gerarTodasSolucoes(cluster, mapa, listaAtual, proximaPos);
			}
			return;
		}
		else if(pos==cluster.size()-1) {
			for(Integer estadoAtual : cluster) {
				if(!atual.contains(estadoAtual)) {
					atual.addLast(estadoAtual);
					atual.addLast(Grafo.indexDepot);
					LinkedList<Integer> solucao = (LinkedList<Integer>) atual.clone();
					mapa.addLast(solucao);
					atual.remove(atual.size()-1); //removendo ultimo estado
					atual.remove(atual.size()-1); //removendo ultimo depot
				}
			}
			return;
		}
		else {
			for(Integer estadoAtual : cluster) {
				if(!atual.contains(estadoAtual)) {
					atual.addLast(estadoAtual);
					gerarTodasSolucoes(cluster, mapa, atual, proximaPos);
					atual.remove(atual.indexOf(estadoAtual));
				}
			}
			return;
		}
	}
	@SuppressWarnings("unchecked")
	public static LinkedList<Integer> solucaoOtima(LinkedList<Integer> cluster) {
		if(cluster.size()<=1) return cluster;
		else if(cluster.size()==2) {
			cluster.addFirst(Grafo.indexDepot);
			cluster.addLast(Grafo.indexDepot);
			int custoAnterior = Caminhos.distanciaTotalCluster(cluster);

			int valorSwap1 = cluster.get(1);
			int valorSwap2 = cluster.get(2);
			cluster.set(1, valorSwap2);
			cluster.set(2, valorSwap1);

			int custoAtual = Caminhos.distanciaTotalCluster(cluster);

			if(custoAtual <= custoAnterior) {
				cluster.remove(cluster.size()-1);
				cluster.remove(0);
				return cluster;
			}
			else {
				cluster.set(1, valorSwap1);
				cluster.set(2, valorSwap2);
				cluster.remove(cluster.size()-1);
				cluster.remove(0);
				return cluster;
			}
		}

		LinkedList<LinkedList<Integer>> mapa = new LinkedList<LinkedList<Integer>>();
		gerarTodasSolucoes(cluster, mapa, null, 0);

		int menorCusto = -1;
		int custoAtual;
		LinkedList<Integer> melhorSolucao = new LinkedList<Integer>();

		for(LinkedList<Integer> solucao : mapa) {
			custoAtual = Caminhos.distanciaTotalCluster(solucao);
			if(menorCusto==-1 || custoAtual < menorCusto) {
				menorCusto = custoAtual;
				melhorSolucao.clear();
				melhorSolucao = (LinkedList<Integer>) solucao.clone();
			}
		}
		mapa.clear();
		melhorSolucao.remove(melhorSolucao.size()-1);
		melhorSolucao.remove(0);
		return melhorSolucao;
	}
}
