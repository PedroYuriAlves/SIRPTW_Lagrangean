package Simullated;
import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

public class SimullatedAnnealing_2 {

	
	
	/*public static void main(String[] args) {
		Util z = new Util();
	//	z.inicializa();
		SimullatedAnnealing_2 s= new SimullatedAnnealing_2(z.clients);
		s.Annealing_CVRP(new Random());
		z.imprime_rotas(s.melhor_sol);
	}*/
	
	Client[] clients;
	LinkedList<ArrayList<Client>> sol;			// solução
	public LinkedList<ArrayList<Client>> melhor_sol; 	// melhor solução
	LinkedList<ArrayList<Client>> c_sol;		// copia solução

	Util u = new Util();
	public double aux; 		// renomear

	public SimullatedAnnealing_2(Client[] c) {
		this.clients = c;
		this.sol = new LinkedList<ArrayList<Client>>();
		this.c_sol = new LinkedList<ArrayList<Client>>();
		this.melhor_sol = new LinkedList<ArrayList<Client>>();
	}

	// cria rotas e adiciona na solução, que é o conjunto das rotas
	public void init() {
		sol = new LinkedList<ArrayList<Client>>();	// redundante com o construtor
		int tamanho = clients.length - 1;
		boolean vaiAdicionar;

		while ( tamanho > 0 ) {
			ArrayList<Client> rota = new ArrayList<Client>();
			vaiAdicionar = false;
			for ( int j = 1; j < clients.length; j++ ) {
				if ( ( (u.calc_cap((rota)) + clients[j].getDemand() <= Truck.capacity ) && (!clients[j].isAtendido()) )) {
					rota.add(clients[j]);
					clients[j].setAtendido(true);
					vaiAdicionar = true;
					tamanho--;
				}
			}
			if ( vaiAdicionar )
				sol.add(rota);
		}
	}

	// modificar esse método
	public void move() {
		Random r = new Random();
		Client c1 = menor();
		Client c2 = menor();
		Client c3 = menor();
		Client c4 = menor();
		Client c5 = menor();
		aux = 0;

		// novo cliente que será inserido
		Client [] novo = new Client[5];

		int rota_sai = 0;			// rotas
		int cliente = 0; 			// indice do cliente
		int rota_entra = 0; 		// nova rota onde o cliente seré inserido
		boolean jaExiste = true;	// é um dos 5 menores
		int i = 0;					// contador

		LinkedList<ArrayList<Client>> backup = u.copia(sol);
		boolean precisaBackup = true;

		do {
			//Escolhe 5 clientes diferentes dos "menores" e os remove das suas respectivas listas
			while ( i < 5 ) {
				// pega nós diferentes dos 5 menores
				do {
					rota_sai = r.nextInt(sol.size());
					// se a rota aletoria nao estiver vazia
					if ( !(sol.get(rota_sai).size() == 0) ) {
						// pega o indice aleatorio de um cliente da rota
						cliente = r.nextInt(sol.get(rota_sai).size());
						novo[i] = sol.get(rota_sai).get(cliente);
					
						if ( novo[i] == c1 || novo[i] == c2 || novo[i] == c3 || novo[i] == c4 || novo[i] == c5)
							jaExiste = true;
						else
							jaExiste = false;
					}
					else
						jaExiste = true;
				} while ( jaExiste );
			
				sol.get(rota_sai).remove(cliente);
				i++;
			}
			i = 0;
			int y = 0;
			
			ArrayList<Client> c = new ArrayList<Client>();
			for ( int j = 0; i < novo.length; j++ )
				c.add(novo[j]);

			// insere os 5 clientes removidos em 5 rotas aleatorias
			while ( c.size() > 0 ) {
				rota_entra = r.nextInt(sol.size());
				if ( (u.calc_cap(sol.get(rota_entra)) + c.get(0).getDemand()) <= Truck.capacity) {
					if ( sol.get(rota_entra).size() == 0 ) {
						sol.get(rota_entra).add(c.get(0));
					}
					else {
						int p = (int) achaPosicao(sol.get(rota_entra), c.get(0))[0];
						insere(sol.get(rota_entra), (c.get(0)), p);
					}
					c.remove(0);
				}
				else {
					if ( (i++) > sol.size() * 2 ) {
						i = 0;
						Collections.shuffle(c);
						if ( (y++) > c.size() * 3 )
							break;
					}
				}
			}

			if ( c.size() == 0 )
				precisaBackup = false;
			if ( precisaBackup )
				sol = u.copia(backup);

		} while ( precisaBackup );
	}

	public Client menor() {
		Client escolhido = null;
		double menordist = Double.MAX_VALUE;

		for ( int i = 0; i < sol.size(); i++ ) {
			double d;
			ArrayList<Client> rota = sol.get(i);

			for (int j = 1; j < rota.size(); j++) {
				d = u.dist(rota.get(j-1), rota.get(j));
				if ( (d < menordist ) && (d > aux) ) {
					escolhido = rota.get(j);
					menordist = d;
				}
			}

			if ( rota.size() > 0 ) {
				d = u.dist(clients[0], rota.get(0));
				if ( (d < menordist) && (d > aux) ) {
					escolhido = rota.get(0);
					menordist = d;
				}
				d = u.dist(rota.get(rota.size()-1), clients[0]);
				if ( (d < menordist) && (d > aux) ) {
					escolhido = rota.get(rota.size() - 1);
					menordist = d;
				}
			}
		}
		aux = menordist;
		return escolhido;
	}

	public int[] maiorMedia() {
		double melhor_d = Double.MIN_VALUE; // trocar nome
		int indiceRota = 0;
		int indiceCliente = 0;
		ArrayList<Client> rota;

		for ( int i = 0; i < sol.size(); i++ ) {
			rota = sol.get(i);
			Client anterior, posterior;
			double d = 0; 	// o que faz?
			for ( int j = 0; j < rota.size(); j++ ) {
				// anterior ao cliente 1 é o depósito
				if (j == 0)
					anterior = clients[0];
				else
					anterior = rota.get(j-1);
				// próximo do último cliente é o depósito
				if (j == rota.size()-1)
					posterior = clients[0];
				else
					posterior = rota.get(j+1);
				
				d = (u.dist(anterior,rota.get(j)) + u.dist(rota.get(j), posterior));
				d /= 2;
				
				if ( d > melhor_d && d < aux ) {
					melhor_d = d;
					indiceRota = i;
					indiceCliente = j;
				}
			}
		}

		aux = melhor_d;
		// se remover antes de escolher vai alterar o cenario
		int sol[] = new int[2];
		sol[0] = indiceRota;
		sol[1] = indiceCliente;

		return sol;
	}

	public void replace_highest_average() {
		// lista que conterá os 5 clientes com as maiores médias de dist
		ArrayList<Client> clientes = new ArrayList<Client>();
		aux = Integer.MAX_VALUE;
		int [] temp;
		for ( int i = 0; i < 5; i++ ) {
			temp = maiorMedia();
			clientes.add(sol.get(temp[0]).get(temp[1]));
			sol.get(temp[0]).remove(temp[1]);
		}

		// variável para verificar se existe rota para inserção
		boolean achou = false;
		int [] rotasAleatorias = new int[5];
		
		/* Adiciona todas as listas nas rotas, para poder ser mais aleatorio
			pois no java ele usa o relogio para gerar o numero o que pode causar
			muitas repetições */
		ArrayList<Integer> list = new ArrayList<Integer>();
		for ( int i = 0; i < sol.size(); i++ )
			list.add(new Integer(i));

		// bagunça a lista
		Collections.shuffle(list);
		// se tiver menos do que 5 rotas
		for ( int i = 0; i < 5; i++ )
			rotasAleatorias[i%sol.size()] = list.get(i%sol.size());

		// enquanto nem todos os clientes forem adicionados
		int verificaSeCabe = 0;
		while ( clientes.size() > 0 ) {
			achou = false;
			int melhorI = 0;
			double melhorCusto = Double.MAX_VALUE;
			double custo;
			double [] resposta;
			double [] melhorResposta = new double[2];
			//5 porque é o tamanho das rotas aleatorias			
			//PARA OS 5 CLIENTES COM A MAIOR MEDIA	
			for ( int i = 0; i < 5; i++ ) {
				// calcula o custo da rota aletoria i
				// se tiver custo e porque a rota não está vazia
				resposta = (achaPosicao(sol.get(rotasAleatorias[i]), clientes.get(0)));
				custo = u.calc_custo(sol.get(rotasAleatorias[i]));

				if ( custo > 0 ) {
					custo = resposta[1];
					// calcula o novo custo da rota com a insercao do novo no
					if ( (custo < melhorCusto) && ((u.calc_cap(sol.get(rotasAleatorias[i])) + clientes.get(0).getDemand()) <= Truck.capacity ) ) {
						melhorI = rotasAleatorias[i];
						melhorCusto = custo;
						melhorResposta = resposta;
						achou = true;
					}
				}
				else {
					melhorI = rotasAleatorias[i];
					melhorCusto = custo + (u.dist(clients[0], clientes.get(0)));
					melhorResposta[0] = -1;
					achou = true;
				}
			}
			if ( achou ) {
				insere(sol.get(melhorI), clientes.get(0), ((int) melhorResposta[0]));
				clientes.remove(0);
			}
			// se não existir uma rota que aceite o vertice, sao escolhidas outras rotas
			if ( !achou ) move();
		}
	}



	// incluir métodos para geração de vizinhos
	public void Annealing_CVRP(Random rand) {
		double tempoInicial = System.currentTimeMillis();
		double tempoFinal = 0;
		double fim = 900000;
		Random r = rand;
		init();
		double alpha = 0.99;
		double beta = 1.05;
		double mo = 5;
		melhor_sol = u.copia(sol);
		double t = 5000;
		double MAXTIME = 1.0E11;
		c_sol = u.copia(sol);
		double custoAtual = u.total_cost(c_sol);
		double melhorCusto = u.total_cost(melhor_sol);
		double time = 0;

		do {
			double m = mo;
			do {
				sol = u.copia(c_sol);
				// converte de grafo para lista
				LinkedList<Client> grafoLista = GeracaoDeVizinhos.transformaEmVetor(sol);
				double probabilidade = r.nextDouble();
				if ( probabilidade < (double) (1/3))
					GeracaoDeVizinhos.oneToOneExchange(grafoLista);
				else if ( probabilidade < (double) (2/3))
					GeracaoDeVizinhos.deleteInsert(grafoLista);
				else GeracaoDeVizinhos.partialReversal(grafoLista);
				// converte de lista para grafo
				LinkedList<ArrayList<Client>> sol = GeracaoDeVizinhos.transformaEmGrafo(grafoLista);
				if(u.sol_valida(sol)){
				double novoCusto = u.total_cost(sol);
				double dcusto = u.total_cost(sol) - custoAtual;
				if ( dcusto < 0 ) {
					c_sol = u.copia(sol);
					custoAtual = u.total_cost(c_sol);
					if ( novoCusto < melhorCusto ) {
						melhor_sol = u.copia(sol);
						melhorCusto = novoCusto;
					}
				}

				else if ( (r.nextDouble()) < Math.exp(-(dcusto/t))) {
					c_sol = u.copia(sol);
					custoAtual = u.total_cost(c_sol);
				}
				}

				m--;
			} while ( m >= 0 );
			time += mo;
			mo=mo	+beta;
			t *= alpha;			
			
			tempoFinal = System.currentTimeMillis() - tempoInicial;
		} while ( t > 0.001 && fim > tempoFinal );
		
	}

	public void insere(ArrayList<Client> rota, Client c, int p) {
		if ( p < 0 )
			rota.add(c);
		else
			rota.add(p, c);
	}

	public double[] achaPosicao(ArrayList<Client> rota, Client c ) {
		double [] resposta = new double[2];
		double melhorCusto = Double.MAX_VALUE;
		double custoAux = u.calc_custo(rota);
		double custo = 0;
		int melhorI = 0;
		Client anterior;
		double d;

		for ( int j = 0; j < rota.size(); j++ ) {
			// anterior ao cliente 1 é o depósito
			if (j == 0)
				anterior = clients[0];
			else
				anterior = rota.get(j-1);
			custo = custoAux + (u.dist(anterior,c) + u.dist(rota.get(j), c)) - (u.dist(anterior, rota.get(j)));
			if ( custo < melhorCusto ) {
				melhorCusto = custo;
				melhorI = j;
			}
		}
		if ( rota.size() > 0 ) {
			custo = custoAux + (u.dist(rota.get(rota.size()-1),c) + u.dist(c,clients[0]) - (u.dist(rota.get(rota.size()-1), clients[0])));
			if ( custo < melhorCusto ) {
				melhorCusto = custo;
				melhorI = -1;
			}
		}

		resposta[0] = melhorI;
		resposta[1] = melhorCusto;
		
		return resposta;
	}
}