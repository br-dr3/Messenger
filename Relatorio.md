# Relatório - Exercício Programático 0
---
## Brian Andreossi, 11060215
---
## 1. Introdução
Este relatório pretende elucidar o sistema desenvolvido como solução para o problema **Exercício Programático ZERO**. Para efeito de entendimento mais claro sobre o sistema, foi utilizado a biblioteca Gson para tratamento de objetos que foram enviados pela rede. Além disso, foi utilizado o maven para gerenciamento de dependências.

---
## 2. Requisitos
A seguir, demonstra-se os requisitos do projeto e se o desenvolvedor os alcançou.

O cliente deveria:
- [x] Enviar mensagens ordenadas sequencialmente;
- [x] Enviar mensagens fora de ordem;
- [x] Simular o envio de mensagens perdidas;
- [ ] Enviar mensagens duplicadas.

O servidor deveria:
- [x] Receber simultaneamente mensagens dos clientes;
- [x] Armazenar mensages de um cliente em um buffer;
- [ ] Tratar mensagens duplicadas
- [x] Tratar mensagens fora de ordem
---
## 3. Mensagem
### 3.1 Atributos da mensagem
A seguir, uma tabela dos atributos da classe mensagem, seguidas de uma breve explicação.

|  Atributo  | Tipo   |  Explicação |
|:----------:|--------|:------------|
| id         | **Long**\*  | Guarda o id da mensagem. (único em relação a conversa) |
| content    | String | O conteúdo referente a mensagem (não há distinção entre <br>mensagem de cliente e servidor no conteúdo)|
| to         | **User**\* |  Usuário que receberá a mensagem |
| from       | **User**\* |  Usuário que enviou a mensagem |

\* - Classe Long, definido na biblioteca principal do java.  
\* - User, classe definida neste projeto.

---
## 4. Tratamento de mensagens fora de ordem
Caso o cliente perceba que chegou mensagem fora de ordem, este pede que o servidor as reenvie (que por sua vez pede para o cliente as reenviar caso não as tenha). Isto ocorre por meio do comando **"/getMessage \<id\>"**, que o próprio usuário pode pedir caso deseje.

Quando o servidor recebe, ele trata de buscar em seu histórico de mensagens recebidas aquele id faltante. É percebido a falta de mensagens quando é pulado um certo id para uma conversa (por isso a necessidade de modelar o id da mensagem entendendo que ele é único por conversa).

---
## 5. Tratamento de mensagens duplicadas
Entendendo que não foi implementado este requisito por falta de **tempo**, foi reservado o direito de propor uma possível solução para o problema de mensagens duplicadas.

Uma mensagem duplicada teria um id duplicado também, pois é possível que o usuário envie duas vezes o mesmo conteúdo, caso seja do desejo dele. Logo, uma mensagem que chegasse e já estivesse com aquele id no mapa de histórico, seria rejeitada.

---
## 6. Consumo do Buffer e Tratamento de mensagens perdidas
O servidor, ao receber uma mensagem, coloca na fila global de mensagens para serem enviadas. Dentro dela, já é possível coletar informações de endereço do cliente. Para efeito de facilidade, o cliente sempre ouve na mesma porta, enquanto manda por alguma porta aleatória definida pela própria instanciação da classe DatagramPacket. Tomou-se o cuidado de que essa fila fosse concorrente, para que evitasse erro de consumo e postagem no buffer.

Quando se perde uma mensagem, também se perde um id. Caso esse id seja menor que o máximo recebido, basta usar o comando descrito em **4** (**/getMessage /<id/>**) que o servidor iria reenviar. No caso da mensagem que não foi enviada ser a de id máximo, o cliente não a conhece enquanto o servidor não envia uma com id mais alto que a que deveria vir.

Por falta de **tempo**, não se implementou a política de _timeout_.

---
## 7. Conclusão e Críticas
Como ja explicitado em outras seções, o projeto tomou tempo demasiado do estudante, além de não haver tempo hábil para implementar todos os requisitos pedidos em 9 dias. Talvez esse projeto deveria ter metade do tamanho que tem.

Fora isso, foi possível entender algumas dificuldades de envio de mensagens UDP, implementar soluções de problemas recorrentes para sistemas que em tese seriam distribuídos, e etc.

---

## 8. Manual de utilização
Executar o arquivo Messenger.jar seguindo o parâmetro.
- Server:
```
java -Dserver=true -jar Messenger.jar
```

- Client
```
java -Dusername=<username> -DclientPort=<clientPort> -DserverHostname=<serverHostname> -DserverPort=<serverPort> 
-jar Messenger.java
```

    onde:  
     <username> = nome do usuario  
     <clientPort> = porta que a requisição chega para o cliente  
     <serverHostname> = nome do Host (localhost para execução na mesma máquina)  
     <serverPort> = porta que a requisição chega para o servidor

Ao executar, o cliente estará conversando com o servidor. Mande uma mensagem para o servidor entender que você está conectado.

Use o comando /username \<username\> para se comunicar com o cliente com nome "username"

Use o comando /getMessage \<id\> para receber novamente a mensagem com id = "id".
