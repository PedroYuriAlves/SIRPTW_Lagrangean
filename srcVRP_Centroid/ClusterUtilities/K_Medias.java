package ClusterUtilities;

import java.util.LinkedList;

import Principal.Grafo;

public class K_Medias {
	public static int centroGeometricoX(LinkedList<Integer> estados) {
		int somatoriaX = 0;
		for(Integer estadoAtual : estados) {
			somatoriaX += Grafo.vertices[estadoAtual].getX();
		}
		int quantidadeEstados = estados.size();
		int resp = somatoriaX/quantidadeEstados;
		return resp;
	}
	public static int centroGeometricoY(LinkedList<Integer> estados) {
		int somatoriaY = 0;
		for(Integer estadoAtual : estados) {
			somatoriaY += Grafo.vertices[estadoAtual].getY();
		}
		int quantidadeEstados = estados.size();
		int resp = somatoriaY/quantidadeEstados;
		return resp;
	}
}
