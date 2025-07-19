## Java Core Practice 

### 📌 Sobre o Projeto

Este projeto é uma aplicação de gerenciamento de tarefas (Task Manager), desenvolvida com o objetivo de construir um backend robusto em Java mais puro possível, ``evitando abstrações típicas de frameworks`` como Spring. A ideia central é **reforçar o raciocínio lógico, aplicar princípios fundamentais da linguagem e utilizar padrões de projeto de forma prática**.

 - **A aplicação possui duas formas de execução**:

> 💡 Modo Desktop: Interface gráfica construída com Java Swing, permitindo ao usuário criar e gerenciar tarefas de forma local, com uma experiência simples e funcional.

> 💡 Modo Web (opcional): Versão empacotada como um arquivo .war, que pode ser implantado em um servidor Tomcat configurado manualmente, possibilitando o consumo dos dados via endpoints que retornam JSON, caso se deseje uma integração web ou API.

Essa abordagem mista serve para demonstrar como um mesmo backend pode ser estruturado para diferentes formas de acesso (desktop e web), sempre com foco em manter um código limpo, compreensível e bem estruturado.

![gif](/docs/gifs/board-test.gif)

---

### 📚 Sumário

- [🧱 Arquitetura da Aplicação](#-arquitetura-da-aplicação)
- [💾 Estratégias de Persistência](#-estratégias-de-persistência)
- [🖥️ Camada de Apresentação](#-camada-de-apresentação)
- [🧠 Padrões de Projeto](#-padrões-de-projeto-utilizados)
- [✅ Testes Unitários](#-testes)
- [⚙️ Configuração e Dependências](#-configuração-e-dependências)
---

### 🧱 Arquitetura da Aplicação

A aplicação foi desenvolvida seguindo os princípios da Arquitetura Limpa (Clean Architecture), com uma separação clara entre lógica de negócio (core) e infraestrutura (infra). Essa abordagem proporciona maior flexibilidade, testabilidade e facilidade de manutenção ao longo do tempo.

##### 📂 Estrutura em Camadas

> A aplicação está dividida principalmente em dois módulos:

- **Core**:
  - Contém toda a lógica de negócio da aplicação. É onde ficam as entidades, casos de uso (use cases), e interfaces (contracts) que definem os pontos de comunicação com a camada externa.
  Esta camada não possui nenhuma dependência de implementação, garantindo total independência e reutilização.


- **Infra**:
  - Responsável por implementar as interfaces definidas no core. Essa camada cuida de aspectos como:
  - Persistência de dados (ex: arquivos locais, banco de dados, etc.)
  - Comunicação externa
  - Interface gráfica (Swing)
  - Servidor HTTP (para a versão web)

#### 🔁 Regras de Dependência

A principal regra seguida é:

> As dependências sempre apontam para dentro.

Ou seja, o core define contratos e o infra fornece implementações concretas. Isso permite que o core possa ser testado e evoluído isoladamente, e até mesmo reutilizado em diferentes ambientes (desktop, web, testes automatizados, etc.).

---

### 💾 Estratégias de Persistência

A aplicação foi projetada com foco em flexibilidade e evolução gradual. Por isso, foram implementadas múltiplas formas de persistência de dados, todas utilizando o padrão Repository, o que permitiu alternar facilmente entre diferentes estratégias sem alterar a lógica de negócio.

#### 🧩 1. Persistência em Memória

A primeira implementação foi feita em memória, utilizando estruturas como Map para armazenar tarefas e quadros temporariamente. Essa abordagem foi essencial nos estágios iniciais do desenvolvimento, pois:

- Simplificou os testes e validação da lógica de negócio.
- Eliminou a complexidade de persistência externa.
- Acelerou a prototipagem e os testes unitários do core.


#### 🗂️ 2. Persistência em Arquivos (CSV)

Na etapa seguinte, foi adicionada uma implementação de persistência baseada em arquivos CSV. Cada entidade (como tarefas ou quadros) é salva em arquivos locais, permitindo:

- Uma forma simples e visual de inspecionar os dados.
- Persistência entre execuções da aplicação sem necessidade de banco de dados.
- Continuidade da separação por meio de interfaces definidas no core.


#### 🛢️ 3. Persistência com Banco de Dados (PostgreSQL)

Por fim, foi implementada a persistência com banco de dados relacional PostgreSQL, tornando a aplicação pronta para cenários mais reais e robustos. Essa implementação trouxe:

- Suporte a consultas mais eficientes e estruturadas.
- Escalabilidade e integridade transacional.
- Um ambiente mais próximo de produção.


#### 🔁 Troca Transparente via Padrão Repository

Todas as formas de persistência seguem o padrão Repository, com interfaces definidas no módulo core e implementações específicas no módulo infra. Graças à Arquitetura Limpa e à inversão de dependência, trocar a forma de persistência é simples:

> **Basta injetar uma nova implementação no lugar da anterior, sem alterar nenhuma regra de negócio.**


Essa estrutura garante alta coesão, baixo acoplamento e grande facilidade para testes, manutenção ou expansão futura da aplicação.

---

### 🖥️ Camada de Apresentação

Dentro da pasta infra, a camada de apresentação está organizada na pasta presenters, e foi dividida em duas interfaces distintas para refletir os dois modos de acesso à aplicação: modo desktop (Swing) e modo web (Servlet).

#### 🎛️ Interface Desktop (Swing)

A subpasta swing contém todos os componentes relacionados à interface gráfica da aplicação, construída com Java Swing. Aqui ficam:

- Telas de criação e listagem de tarefas e quadros (JFrames, JPanels, etc.)
- Componentes de interação do usuário
- Lógica de exibição desacoplada do core da aplicação

> Essa interface permite que o usuário utilize a aplicação de forma local, com uma experiência visual simples e funcional, sem depender de servidor web.


#### 🌐 Interface Web (Servlet)

A subpasta web abriga a implementação da camada de apresentação para a versão web da aplicação, utilizando Java Servlet. A principal funcionalidade atual é:

- Expor um endpoint HTTP que permite consultar os boards cadastrados, retornando os dados em formato JSON.


![Json retornado como resposta pelo servidor tom cat](/docs/imgs/tomcat-board-json.png)


> Essa implementação permite que o backend seja acessado por aplicações externas (como um frontend web ou cliente REST), sem alterar a lógica de negócio.

#### 🔄 Flexibilidade da Abordagem

Graças à arquitetura limpa, ambas as interfaces consomem os casos de uso e entidades do core, sem duplicar regras de negócio. Isso garante:

- Reutilização de código
- Testabilidade isolada
- Possibilidade de adicionar novas formas de apresentação (ex: CLI, app mobile) com o mínimo de esforço

---

### 🧠 Padrões de Projeto Utilizados

Durante o desenvolvimento, foram aplicados alguns padrões de projeto clássicos para garantir flexibilidade, extensibilidade e organização da aplicação. A escolha de cada padrão foi feita com base na intenção de simular funcionalidades típicas de frameworks e ambientes reais — mas com código totalmente manual e transparente, como parte do aprendizado de backend com Java puro.

#### 🛠️ Service Locator (Simulação de Container de Injeção)

Foi implementado um Service Locator dentro da pasta infra/config, com o objetivo de simular o funcionamento de um container de injeção de dependências, semelhante ao que frameworks como Spring oferecem.

>Esse componente centraliza a criação e o fornecimento das dependências (repositórios, serviços, controladores), permitindo:

- Separar a lógica de instanciação e configuração dos componentes da aplicação.
- Reduzir o acoplamento direto entre classes.
- Facilitar a troca de implementações (ex: mudar de um repositório em memória para um com PostgreSQL sem alterar o core).

Essa abordagem foi essencial para manter a inversão de dependência, que é um dos pilares da arquitetura limpa.

#### 🔄 Observer (Delete em Cascata na Persistência com Arquivo)

> Na implementação da persistência baseada em arquivos (CSV), foi utilizado o padrão Observer para simular um comportamento comum de banco de dados relacionais: deleção em cascata (cascade delete).

**Como funciona:**

- Quando um Board (quadro) é removido, os observers (no caso, repositórios de entidades dependentes como tarefas) são notificados automaticamente.
- Esses observers executam a lógica para remover as entidades associadas ao board excluído, garantindo integridade dos dados.

---

### ✅ Testes

A aplicação conta com uma camada de testes unitários bem estruturada, com foco principal nos casos de uso (use cases), que representam o coração da lógica de negócio. Isso segue a proposta da Arquitetura Limpa, onde os use cases devem ser totalmente independentes de detalhes externos (como banco de dados, interface gráfica ou frameworks).

#### 🧪 Estratégia de Testes

- Todos os use cases (como criação, atualização, listagem e remoção de tarefas e quadros) foram testados de forma isolada, garantindo que a lógica de negócio funcione corretamente, independentemente da camada de infraestrutura.
- Foram utilizados mocks de repositórios por meio do Mockito, injetando comportamentos controlados para simular dependências externas.
- Os testes foram escritos com o JUnit 5 (Jupiter), garantindo legibilidade, modularidade e facilidade de manutenção.


#### 🎯 Benefícios

- Os testes ajudam a validar que a lógica de negócio segue as regras esperadas, independente da forma de persistência ou da interface utilizada.
- Facilitam a evolução do código com segurança, permitindo alterações na camada de infraestrutura sem afetar a lógica central.
- Ajudam a identificar regressões rapidamente durante refatorações.

---

### ⚙️ Configuração e Dependências

> O projeto foi construído com Maven como sistema de build e gerenciamento de dependências, utilizando Java 21 como linguagem alvo.

Desde o início, houve a preocupação em manter o projeto enxuto, evitando bibliotecas ou frameworks que pudessem mascarar a lógica de negócio. Todas as dependências foram escolhidas com critério, apenas para atender necessidades específicas, sem comprometer a clareza da arquitetura ou o aprendizado da base da linguagem.

#### 📁 Estrutura de Build

- Empacotamento: .war — permitindo a execução em servidores web como o Apache Tomcat.
- Compatibilidade: Java 21
- Gerenciador de build: Maven
- Separação clara de responsabilidades entre módulos core e infra.

#### 📦 Dependências Utilizadas

> Abaixo estão as únicas dependências externas utilizadas no projeto:

- ✅ Execução e Web
  - Jakarta Servlet API (jakarta.servlet-api):
  Usada para a implementação dos Servlets que expõem a API web. Escopo provided, pois o container (Tomcat) fornece essa lib em tempo de execução.
  - Jackson (jackson-databind, jackson-datatype-jsr310):
  Utilizado para serialização e desserialização de objetos Java em JSON, principalmente na versão web da aplicação.


- ✅ Banco de Dados
  - PostgreSQL Driver (postgresql):
  Driver oficial para conexão com banco de dados PostgreSQL. Utilizado na implementação de repositórios relacionais.


- ✅ Testes
  - JUnit Jupiter (junit-jupiter):
Framework de testes unitários padrão no ecossistema Java moderno (JUnit 5).

  - Mockito (mockito-core, mockito-junit-jupiter):
Utilizado para criação de mocks e testes isolados dos componentes de negócio, reforçando a testabilidade do core desacoplado.

> Essa configuração leve foi essencial para manter o controle total sobre a estrutura do código e entender com clareza os princípios fundamentais da construção de backends robustos em Java puro.