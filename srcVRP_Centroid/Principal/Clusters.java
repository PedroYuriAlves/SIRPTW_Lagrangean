package Principal;
import java.util.LinkedList;
import ClusterUtilities.*;
import TSP.*;

public class Clusters {
	
	private static int estadoMaisLongeDeDepotSemCluster(LinkedList<Integer> inseridosEmCluster) { //Estado mais longe de "depot" que 
																						  		 //nao esta em um cluster
		int estadoMaisLonge = -1;
		double distanciaMaisLonge = 0;
		double distanciaAtual;
		
		boolean estadoAtualInvalido;
		
		for(int i=0; i<Grafo.vertices.length; i++) {
			if(i==Grafo.indexDepot) continue;
			
			estadoAtualInvalido = false;
			for(Integer estadoEmCluster : inseridosEmCluster) {
				if(i==estadoEmCluster) {
					estadoAtualInvalido = true;
					break;
				}
			}
			
			if(estadoAtualInvalido == true) continue;
			else {
				distanciaAtual = Grafo.distanciaEuclidiana(Grafo.indexDepot, i);
			
				if(distanciaAtual > distanciaMaisLonge) {
					estadoMaisLonge = i;
					distanciaMaisLonge = distanciaAtual;
				}
			}
		}
		return estadoMaisLonge;
	}
	
	private static LinkedList<Integer> estadosMaisProximosSemCluster(int centroGeometricoX, int centroGeometricoY, 
																	LinkedList<Integer> inseridosEmCluster) {
		LinkedList<Integer> resp = new LinkedList<Integer>();
		
		double distanciaMaisProxima = -1; //distancia inicial invalida
		double distanciaAtual;
		
		boolean estadoAtualInvalido;
		
		for(int i=0; i<Grafo.vertices.length; i++) {
			if(i==Grafo.indexDepot) continue;
			
			estadoAtualInvalido = false;
			for(Integer estadoEmCluster : inseridosEmCluster) {
				if(i==estadoEmCluster) {
					estadoAtualInvalido = true;
					break;
				}
			}
			
			if(estadoAtualInvalido == true) continue;
			else {
				distanciaAtual = Grafo.distanciaEuclidianaXY(centroGeometricoX, centroGeometricoY, i);
				
				if(distanciaAtual < distanciaMaisProxima || distanciaMaisProxima==-1) { //Distancia mais proxima invalida ou distancia atual
																						//menor que distancia mais proxima
					distanciaMaisProxima = distanciaAtual;
				}
			}
		}
		for(int i=0; i<Grafo.vertices.length; i++) {
			if(i==Grafo.indexDepot) continue;
			
			estadoAtualInvalido = false;
			for(Integer estadoEmCluster : inseridosEmCluster) {
				if(i==estadoEmCluster) {
					estadoAtualInvalido = true;
					break;
				}
			}
			if(estadoAtualInvalido == true) continue;
			else {
				distanciaAtual = Grafo.distanciaEuclidianaXY(centroGeometricoX, centroGeometricoY, i);
				
				if(distanciaAtual == distanciaMaisProxima) { //Distancia atual igual a distancia mais proxima (ja encontrada anteriormente)
					resp.add(i);
				}
			}
		}
		return resp;
	}
	private static void completarCluster(LinkedList<Integer> clusterAtual, LinkedList<Integer> inseridosEmCluster) {
		int totalEstadosExcetoDepot = Grafo.vertices.length - 1;
		double demandaAcumuladaClusterAtual = 0;
		double trajetoAcumuladaClusterAtual = 0;
		int centroGeometricoX = 0;
		int centroGeometricoY = 0;
		
		int estadoAnt = Grafo.indexDepot;
		for(Integer estadoAtual : clusterAtual) {
			demandaAcumuladaClusterAtual += Grafo.vertices[estadoAtual].getDemanda();
			trajetoAcumuladaClusterAtual += Grafo.tempoEntre(estadoAnt, estadoAtual, Caminhos.veloc);
			estadoAnt = estadoAtual;
		}
		centroGeometricoX = K_Medias.centroGeometricoX(clusterAtual);
		centroGeometricoY = K_Medias.centroGeometricoY(clusterAtual);
		
		while(inseridosEmCluster.size() < totalEstadosExcetoDepot) {
			
			LinkedList<Integer> estadosCandidatos = estadosMaisProximosSemCluster(centroGeometricoX, centroGeometricoY, inseridosEmCluster);
			boolean inseriuEstado = false;
			
			for(Integer candidato : estadosCandidatos) {
				double demandaAtual = Grafo.vertices[candidato].getDemanda();
				int ultimo = clusterAtual.getLast();
				double tempo = Grafo.tempoEntre(ultimo, candidato, Caminhos.veloc);
				
				if(demandaAtual+demandaAcumuladaClusterAtual <= Caminhos.capacity && trajetoAcumuladaClusterAtual+ tempo <= Caminhos.tempoMax) {
					
					clusterAtual.add(candidato);
					inseridosEmCluster.add(candidato);
				
					demandaAcumuladaClusterAtual += demandaAtual;
					trajetoAcumuladaClusterAtual += tempo;
					centroGeometricoX = K_Medias.centroGeometricoX(clusterAtual);
					centroGeometricoY = K_Medias.centroGeometricoY(clusterAtual);
					
					inseriuEstado = true;
					break;
				}
			}
			if(inseriuEstado==false) return; //Condicao de parada de acordo com o algoritmo do artigo
		}
	}
	private static LinkedList<LinkedList<Integer>> construirClusters() {
		
		int totalEstadosExcetoDepot = Grafo.vertices.length - 1;
		
		LinkedList<Integer> inseridosEmCluster = new LinkedList<Integer>();
		LinkedList<LinkedList<Integer>> clusters = new LinkedList<LinkedList<Integer>>();
		
		while(inseridosEmCluster.size() < totalEstadosExcetoDepot) {
			LinkedList<Integer> clusterAtual = new LinkedList<Integer>();
			
			int primeiroEstado = estadoMaisLongeDeDepotSemCluster(inseridosEmCluster);
			if(primeiroEstado == -1) break;
			double tempo = Grafo.tempoEntre(Grafo.indexDepot, primeiroEstado, Caminhos.veloc);
			if(Grafo.vertices[primeiroEstado].getDemanda() <= Caminhos.capacity && tempo <= Caminhos.tempoMax) {
				clusterAtual.add(primeiroEstado);
			}
			else break; //Valor de demanda de um estado acima do limite dos caminhoes (valor invalido na entrada)
			inseridosEmCluster.add(primeiroEstado);
			completarCluster(clusterAtual, inseridosEmCluster);
			
			if(clusterAtual.size() > 0) clusters.add(clusterAtual);
		}
		return clusters;
	}
	private static boolean ajustarClusters(LinkedList<LinkedList<Integer>> clusters) {
		int clusterSwap1;
		int clusterSwap2;
		int clusterSwapCandidato;
		
		double centroGeometricoX;
		double centroGeometricoY;
		double centroGeometricoXantigo;
		double centroGeometricoYantigo;
		
		double menorDistancia;
		double distanciaAtual;
		double distanciaAntiga;
		double demandaClusterAtual;
		double trajetoClusterAtual;
		double demandaEstadoAtual;
		
		boolean modificou = false;
		
		for(int i=0; i<Grafo.vertices.length; i++) {
			if(i==Grafo.indexDepot) continue;
			else {
				clusterSwap1 = OperacoesCluster.encontrarIndexCluster(i, clusters); //Cluster original do estado atual
				clusterSwap2 = -1;
				if(clusterSwap1==-1) {
					continue;
				}

				centroGeometricoXantigo = K_Medias.centroGeometricoX(clusters.get(clusterSwap1));
				centroGeometricoYantigo = K_Medias.centroGeometricoY(clusters.get(clusterSwap1));
				distanciaAntiga = Grafo.distanciaEuclidianaXY(centroGeometricoXantigo, centroGeometricoYantigo, i);
				
				menorDistancia = distanciaAntiga;
				demandaEstadoAtual = Grafo.vertices[i].getDemanda();
				
				for(LinkedList<Integer> clusterAtual : clusters) {
					clusterSwapCandidato = clusters.indexOf(clusterAtual);
					
					if(clusterSwapCandidato==clusterSwap1) continue; //Mesmo cluster
					if(clusterAtual.size()==0) continue;
					
					centroGeometricoX = K_Medias.centroGeometricoX(clusterAtual);
					centroGeometricoY = K_Medias.centroGeometricoY(clusterAtual);		
					distanciaAtual = Grafo.distanciaEuclidianaXY(centroGeometricoX, centroGeometricoY, i);
					
					if(distanciaAtual==distanciaAntiga) continue; //Nao ocorrem mudancas significativas
					else if(distanciaAtual < menorDistancia) {
						
						demandaClusterAtual = OperacoesCluster.demandaTotalCluster(clusterAtual);
						trajetoClusterAtual = OperacoesCluster.tempoTotalCluster(clusterAtual);
						
						double trajetoEstadoAtual = Grafo.tempoEntre(clusterAtual.getLast(), i, Caminhos.veloc);
						
						if(demandaEstadoAtual + demandaClusterAtual <= Caminhos.capacity && trajetoEstadoAtual + trajetoClusterAtual <= Caminhos.tempoMax) {
							clusterSwap2 = clusterSwapCandidato;
							menorDistancia = distanciaAtual;
						}
					}
				}
				if(clusterSwap1==-1 || clusterSwap2==-1) continue; //Um cluster eh invalido
				if(clusterSwap1==clusterSwap2) continue; //Mesmo cluster
				
				//Fazendo swap do estado "i"
				LinkedList<Integer> cluster1 = clusters.get(clusterSwap1); 
				LinkedList<Integer> cluster2 = clusters.get(clusterSwap2);
				cluster1.remove(cluster1.indexOf(i));
				cluster2.addLast(i);
				
				modificou = true;
			}
		}
		return modificou;
	}
	@SuppressWarnings("unchecked")
	private static LinkedList<LinkedList<Integer>> estabelecerRota(LinkedList<LinkedList<Integer>> clusters, 
																   int maxIteracoes) {
		
		LinkedList<LinkedList<Integer>> resposta = new LinkedList<LinkedList<Integer>>();
		
		for(int i=0; i<clusters.size(); i++) {
			LinkedList<Integer> clusterAtual = clusters.get(i);
			LinkedList<Integer> novoCluster;
			if(clusterAtual.size() <= 8) { //Utilizando Brute Force
				novoCluster = BruteForce.solucaoOtima(clusterAtual);
			}
			else { //Utilizando Simulated Annealing no Cluster
				LinkedList<Integer> clusterCopia = (LinkedList<Integer>) clusterAtual.clone();
				LinkedList<Integer> melhorCluster = (LinkedList<Integer>) clusterCopia.clone();
				
				int menorCusto;
				int custoAtual;
				
				OperacoesCluster.adicionarDepotsClusterSeparado(melhorCluster);
				menorCusto = Caminhos.distanciaTotalCluster(melhorCluster);
				OperacoesCluster.removerDepotsClusterSeparado(melhorCluster);
				
				for(int iteracoes=0; iteracoes<maxIteracoes; iteracoes++) { //Parametro para j pode ser modificado
					SimulatedAnnealing.Simulated_Annealing(clusterCopia);
					
					OperacoesCluster.adicionarDepotsClusterSeparado(clusterCopia);
					custoAtual = Caminhos.distanciaTotalCluster(clusterCopia);
					OperacoesCluster.removerDepotsClusterSeparado(clusterCopia);
					
					if(custoAtual < menorCusto) {
						melhorCluster = (LinkedList<Integer>) clusterCopia.clone();
						menorCusto = custoAtual;
					}
				}
				novoCluster = melhorCluster;
			}
			novoCluster.addFirst(Grafo.indexDepot);
			novoCluster.addLast(Grafo.indexDepot);
			resposta.add(novoCluster);
		}
		return resposta;
	}
	public static void centroidVRP(int maxIteracoes) {
		LinkedList<LinkedList<Integer>> configuracaoAtual = construirClusters();
		LinkedList<LinkedList<Integer>> melhorConfiguracao;
		
		configuracaoAtual = estabelecerRota(configuracaoAtual, maxIteracoes);
		int menorDistancia = Caminhos.distanciaTotalRotasCluster(configuracaoAtual);
		int distanciaAtual = menorDistancia;
		
		OperacoesCluster.removerDepots(configuracaoAtual);
		melhorConfiguracao = OperacoesCluster.copiarConjuntoClusters(configuracaoAtual);
		
		boolean modificou = false;
		
		while(true) {
			modificou = ajustarClusters(configuracaoAtual);
			if(!modificou) break;
						
			configuracaoAtual = estabelecerRota(configuracaoAtual, maxIteracoes);
			distanciaAtual = Caminhos.distanciaTotalRotasCluster(configuracaoAtual);
			
			if(distanciaAtual < menorDistancia) {
				menorDistancia = distanciaAtual;
				OperacoesCluster.removerDepots(configuracaoAtual);
				melhorConfiguracao = OperacoesCluster.copiarConjuntoClusters(configuracaoAtual);
			}
			else {
				OperacoesCluster.removerDepots(configuracaoAtual);
			}
		}
		for(LinkedList<Integer> clusterAtual : melhorConfiguracao) {
			clusterAtual.addFirst(Grafo.indexDepot);
			clusterAtual.addLast(Grafo.indexDepot);	
		}
		Caminhos.copiarDeListaParaSolucao(melhorConfiguracao);
	}
}
