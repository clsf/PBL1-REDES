package com.testefront.demo;

import com.testefront.demo.controller.ApplicationController;
import com.testefront.demo.service.BrokerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@EnableFeignClients
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}

@Component  // Componente Spring para ser injetado e executado após a inicialização
class CommandLineApp implements CommandLineRunner {

	@Autowired
	private ApplicationController applicationController;

	@Autowired
	private BrokerService service;

	@Value("${feign.client.config.broker.url}")
	private String url;

	@Value("${server.port}")
	private String port;


	//MAIN PRINCIPAL
	@Override
	public void run(String... args) throws Exception {
		System.out.println("O sistema está funcionando na porta: "+port);
		System.out.println("A url do broker: "+url);
		System.out.println("#####################################");
		System.out.println("###    Bem-vindo ao Sistema!    ###");
		System.out.println("#####################################");

		var cont = true;
		while(cont){
			//EXIBE O PRIMEIRO MENU E PEGA OPCAO DO USUÁRIO
			int option = menuDevices();

			switch (option){
				case 1:
					System.out.println("Buscando dispositivos...");
					System.out.println(service.getDevices());
					break;
				case 2:
					System.out.println("Digite o numero equivalente ao dispositivo: ");
					var device = menuDevice();
					deviceOptions(device);
					break;
			}
			if(option==0) cont= false;
		}

	}
	//MENU QUE EXIBE AS OPÇÕES DOS DISPOSITIVOS
	public int menuDevices(){
		Scanner scanner = new Scanner(System.in);
		System.out.println("Dispositivos: ");

		int index = 1;

		for (String item : service.devices()) {
			System.out.println(index + ". " + item);
			index++;
		}

		int opcao = 0;

		System.out.println("Opção: ");
		int quant = 0;
		//O MENU MUDA DE ACORDO SE TEM DISPOSITIVO DISPONIVEL
		if(service.devices().isEmpty()){
			System.out.println("1. Buscar dispositivo \n0. Encerrar aplicação");
			quant = 2;
		}else{
			System.out.println("1. Buscar dispositivo \n2. Selecionar dispositivo \n0. Encerrar aplicação");
			quant = 3;
		}
		var valid = false;
		//VERIFICACAO DE OPCAO VÁLIDA
		while (!valid){
			try{
				opcao = scanner.nextInt();
				if (opcao > quant) throw new Exception();
				valid = true;
			}catch (Exception e){
				scanner.nextLine(); // Limpa o buffer
				System.out.println("Opção inválida. Tente novamente:");
			}
		}
		return opcao;
	}

	//PEGA A OPÇÃO DO USUARIO COM RELACAO AO DISPOSITIVO
	public int menuDevice(){
		Scanner scanner = new Scanner(System.in);
		int opcao = 0;
		var valid = false;

		while (!valid){
			try{
				opcao = scanner.nextInt();
				if (opcao == 0) throw new Exception();
				if (opcao > service.devices().size()) throw new Exception();
				valid = true;
			}catch (Exception e){
				scanner.nextLine(); // Limpa o buffer
				System.out.println("Opção inválida. Tente novamente:");
			}
		}
		return opcao;
	}

	//EXIBE AS AÇÕES QUE PODEM SER FEITAS COM O DISPOSITIVO
	public void deviceOptions(int device) throws Exception {
		Scanner scanner = new Scanner(System.in);
		boolean cont = true;
		for (int i = 0; i < 50; i++) {  // Imprimir 50 linhas em branco
			System.out.println();
		}
		System.out.println(service.getState(device));
		while (cont) {
			int opcao = 0;
			var valid = false;

			while (!valid) {
				try {
					System.out.println("Opções: \n1. Atualizar \n2.Alterar a velocidade \n3.Voltar ");
					opcao = scanner.nextInt();
					if (opcao == 0 || opcao >3) throw new Exception("Opção inválida! Tente novamente.");
					valid = true;
				} catch (Exception e) {
					System.out.println(e.getMessage());
					scanner.nextLine(); // Limpa o buffer
				}
			}

			if(opcao == 3) cont = false;
			else if(opcao == 1) System.out.println(service.getState(device)); //PEGA O ESTADO ATUAL DO DISPOSITIVO
			else {
				valid = false;
				while (!valid) {
					try {
						System.out.println("Digite 0 para desligar ou escolha uma velocidade de 1 a 4");
						opcao = scanner.nextInt();
						if (opcao >4) throw new Exception("Opção inválida! Tente novamente.");
						valid = true;
					} catch (Exception e) {
						scanner.nextLine(); // Limpa o buffer
						System.out.println("Opção inválida");
					}
				}
				System.out.println(service.updateState(device, opcao)); //MANDA ATUALIZAÇÃO PRO DISPOSITIVO E EXIBE NA TELA O RESULTADO
			}

		}
	}
}

