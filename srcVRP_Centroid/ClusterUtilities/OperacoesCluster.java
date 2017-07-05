package ClusterUtilities;

import java.util.LinkedList;

import Principal.Caminhos;
import Principal.Grafo;

public class OperacoesCluster {
	public static void adicionarDepotsClusterSeparado(LinkedList<Integer> cluster) {
		cluster.addFirst(Grafo.indexDepot);
		cluster.addLast(Grafo.indexDepot);
	}
	public static void removerDepotsClusterSeparado(LinkedList<Integer> cluster) {
		cluster.remove(0);
		cluster.remove(cluster.size()-1);
	}
	public static void removerDepots(LinkedList<LinkedList<Integer>> clusters) {
		for(LinkedList<Integer> clusterAtual : clusters) {
			if(clusterAtual.size() <= 2) continue;
			clusterAtual.remove(0);
			clusterAtual.remove(clusterAtual.size()-1);
		}
	}
	public static LinkedList<LinkedList<Integer>> copiarConjuntoClusters(LinkedList<LinkedList<Integer>> clusters) {
		LinkedList<LinkedList<Integer>> copiaClusters = new LinkedList<LinkedList<Integer>>();
		
		for(LinkedList<Integer> clusterAtual : clusters) {
			LinkedList<Integer> novoCluster = new LinkedList<Integer>();
			for(Integer estadoAtual : clusterAtual) {
				novoCluster.addLast(estadoAtual);
			}
			copiaClusters.addLast(novoCluster);
		}
		return copiaClusters;
	}
	public static double demandaTotalCluster(LinkedList<Integer> cluster) {
		double demandaTotal = 0;
		for(Integer estado : cluster) {
			demandaTotal += Grafo.vertices[estado].getDemanda();
		}
		return demandaTotal;
	}
	public static double tempoTotalCluster(LinkedList<Integer> cluster) {
		double tempoTotal = 0;
		int estadoAnt = Grafo.indexDepot;
		for(Integer estado : cluster) {
			tempoTotal += Grafo.tempoEntre(estadoAnt, estado, Caminhos.veloc);
			estadoAnt = estado;
		}
		return tempoTotal;
	}
	public static int encontrarIndexCluster(int estado, LinkedList<LinkedList<Integer>> clusters) {
		int resp = -1;
		for(LinkedList<Integer> clusterAtual : clusters) {
			if(clusterAtual.indexOf(estado)!=-1) {
				resp = clusters.indexOf(clusterAtual);
				break;
			}
		}
		return resp;
	}
}
