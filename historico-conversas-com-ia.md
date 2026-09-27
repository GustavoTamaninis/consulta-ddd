# Conversa exportada — Copilot

**Meu prompt:** Melhore a responsividade da div interna do formulário. Desejo que, preferencialmente, o botão fique ao lado do input

**Resposta do Copilot:**

Vou ajustar apenas a composição interna: envolver o form-control em uma coluna responsiva e reservar outra coluna para o botão, empilhando-os apenas em telas pequenas

Ajustei a div interna do formulário em src/main/resources/templates/index.html.
Agora:
• Em telas médias ou maiores, o input ocupa col-md-9 e o botão col-md-3, ficando lado a lado.
• Em telas pequenas, ambos ocupam col-12 e ficam empilhados.
• Removi as classes de fundo usadas para teste visual.
• Adicionei g-2 para criar espaçamento consistente entre os controles.
• O botão ocupa toda a largura da coluna com w-100.
A validação com git diff --check passou sem erros.

---

**Meu prompt:** Ajuste o formulário para que ele envie uma requisição GET para o endpoint "consultar" e passe o ddd como um path variable na url.

**Resposta do Copilot:**

Vou verificar a rota atual do controller e alinhar o action do formulário ao formato esperado pelo endpoint, preservando o envio via GET e o ddd na URL.

O formulário agora envia uma requisição GET para o endpoint com o DDD como variável de caminho:
/api/ddd/v1/{ddd}
Exemplo para o valor 47:
/api/ddd/v1/47

Foi adicionado um listener ao envio do formulário que monta dinamicamente a URL usando encodeURIComponent. Também removi o atributo name do input para evitar que o navegador acrescente ?ddd=47 como query parameter.
A alteração foi validada com git diff --check.

---

**Meu prompt:** Alinhe as seções, que estão desalinhadas (a primeira está menor que a segunda)

**Resposta do Copilot:**

Vou comparar as classes das duas seções: a diferença relevante é que a segunda usa .row sem zerar as margens horizontais, fazendo-a avançar além da largura da primeira.

Alinhei as seções adicionando m-0 à seção de resultado:

`<section id="resultado" class="row m-0 my-5 p-5 bg-white rounded border">`

A classe .row aplica margens horizontais negativas por padrão. Como a primeira seção já usava m-0 e a segunda não, a segunda ficava mais larga. Agora ambas respeitam a mesma largura do .container.

---

**Meu prompt:** Crie um script JS que exiba div #resultado com os valores da resposta do servidor após a requisição do DDD. Esconda essa div enquanto o usuário não tiver realizado a sua primeira requisição.

**Resposta do Copilot:**

Vou verificar o formato exato de DddResponse e da lista de municípios para mapear a resposta sem assumir nomes de propriedades, depois conecto o formulário via fetch e deixo #resultado oculto inicialmente.

---

**Meu prompt:** Explique-me como tratar as exceções "DddInvalido" e "DddVazio" no back-end. O objetivo não é tratar os erros diretamente no front-end, mas no back-end, apenas exibindo-os ao usuário no front-end . Não codifique nada.

**Resposta do Copilot:**

Vou localizar onde essas exceções são definidas e lançadas para explicar o fluxo correto do back-end e como o front-end deve apenas apresentar a resposta.

No estado atual do projeto, DddInvalido e DddVazio estão apenas declaradas; elas ainda não são lançadas nem convertidas em respostas HTTP. O tratamento deve ficar dividido em três responsabilidades:

1. Back-end valida o DDD
2. Back-end transforma as exceções em respostas HTTP
3. Front-end apenas exibe a mensagem recebida

1. Definir o significado de cada exceção
   • DddVazio: deve ocorrer quando o valor não foi informado ou contém apenas espaços.
   • DddInvalido: deve ocorrer quando o valor foi informado, mas não representa um DDD válido — por exemplo, letras, caracteres especiais, quantidade incorreta de dígitos ou número fora da faixa permitida.
   A validação pode ocorrer no serviço ou em uma camada de domínio. O importante é que o controller não precise decidir como validar o valor. Ele apenas encaminha a solicitação para o serviço.
   A regra poderia ser conceitualmente:

se ddd for nulo ou vazio:

* lançar DddVazio


se ddd não tiver exatamente dois dígitos ou estiver fora da regra:

* lançar DddInvalido


caso contrário:

* consultar a API externa


2. Converter as exceções em respostas HTTP
   O ideal é criar um tratamento global de exceções no Spring, usando uma classe de aconselhamento como @RestControllerAdvice.

DddVazio -> 400 Bad Request -> A requisição está incompleta.
DddInvalido -> 422 Unprocessable Entity ou 400 Bad Request -> O formato ou valor informado não é aceito.

A resposta não deve ser uma página HTML nem um stack trace. Ela deve ser um JSON padronizado, por exemplo:
```
{
  "status": 400,
  "erro": "DddVazio",
  "mensagem": "O DDD deve ser informado."
}
```
ou:
```
{
  "status": 422,
  "erro": "DddInvalido",
  "mensagem": "O DDD deve ser um número válido entre 11 e 99."
}
```
Assim, o back-end mantém o controle das regras e o front-end não precisa conhecer detalhes de validação.

3. Atenção ao formato atual da rota
   A rota atual usa o DDD como variável de caminho:
   /api/ddd/v1/{ddd}
   Isso funciona para valores como:
   /api/ddd/v1/47
   Porém, quando o campo está vazio, o front-end tenta acessar algo equivalente a:
   /api/ddd/v1/
   Essa URL provavelmente não corresponde ao mapeamento /{ddd}. Nesse caso, o Spring pode responder diretamente com 404, sem executar o controller e sem lançar DddVazio.
   Portanto, para tratar vazio explicitamente no back-end, há duas alternativas conceituais:
   • usar um parâmetro de consulta, como /api/ddd/v1?ddd=, permitindo que o controller receba o valor vazio; ou
   • manter a rota atual, mas tratar a ausência do segmento da URL como um erro de requisição separado, sabendo que ela pode ser resolvida pelo mecanismo de roteamento antes de chegar ao serviço.
   Se a intenção é usar especificamente a exceção DddVazio, o primeiro formato costuma ser mais previsível.

4. Relação com as anotações atuais
   O controller atualmente usa @NotNull e @Pattern. Essas validações podem gerar exceções do Bean Validation, como ConstraintViolationException, em vez de gerar diretamente DddVazio e DddInvalido.
   Há duas abordagens válidas:
   • Usar Bean Validation e tratar ConstraintViolationException no @RestControllerAdvice; ou
   • Usar as exceções de domínio DddVazio e DddInvalido, removendo ou reduzindo a responsabilidade das anotações e centralizando as regras no serviço/domínio.
   Como essas duas exceções já existem no projeto, a segunda abordagem é mais coerente se o objetivo é realmente diferenciá-las e retornar mensagens específicas.

5. Responsabilidade do front-end
   O front-end não deve validar a regra de negócio nem criar mensagens próprias para esses casos. Ele deve:

1. enviar o valor ao back-end;
2. verificar se a resposta foi bem-sucedida;
3. em caso de erro, ler o JSON retornado;
4. exibir o campo mensagem ao usuário.

Atualmente, o JavaScript apenas registra o erro com console.error. Para o usuário visualizar o problema, a interface precisará ter uma área de mensagem, mas essa mensagem deve vir da resposta do back-end.
Em resumo: as exceções são lançadas no back-end, o @RestControllerAdvice define o status e o JSON da resposta, e o front-end apenas apresenta a mensagem recebida.

Resumo:
• DddVazio → 400 Bad Request
• DddInvalido → 422 Unprocessable Entity
Também seria aceitável usar 400 Bad Request para ambas, especialmente em APIs que adotam uma política mais simples. Porém, a distinção entre 400 e 422 comunica melhor a diferença: ausência do dado versus dado presente, mas inválido.

---

**Meu prompt:** Para o segundo caso, UNPROCESSABLE_CONTENT também seria viável?

**Resposta do Copilot:**

Sim. UNPROCESSABLE_CONTENT também é viável para DddInvalido.
Ele corresponde ao status HTTP 422, definido atualmente como 422 Unprocessable Content. Em versões anteriores da especificação, era conhecido como 422 Unprocessable Entity.
A distinção fica:
• DddVazio → 400 Bad Request
• DddInvalido → 422 Unprocessable Content
No Spring, a constante equivalente é HttpStatus.UNPROCESSABLE_CONTENT nas versões mais recentes. Em versões anteriores, pode aparecer como HttpStatus.UNPROCESSABLE_ENTITY.
Use UNPROCESSABLE_CONTENT quando a requisição estiver sintaticamente correta, mas o valor do DDD não puder ser aceito pelas regras da aplicação.

---

**Meu prompt:** Avalie se converti corretamente as exceções em respostas http

**Resposta do Copilot:**

A estrutura do handler está próxima do objetivo, mas encontrei problemas que impedem considerar a conversão correta: as exceções são checked, não são lançadas pelo serviço, e o controller provavelmente nem compila sem declarar ou tratar essas exceções. Vou confirmar isso no build.

A conversão está parcialmente correta, mas ainda não está funcional.

O que está correto
No GlobalExceptionHandler:
• DddVazioException → 400 Bad Request
• DddInvalidoException → 422 Unprocessable Content
• exceções não tratadas → 500 Internal Server Error
O uso de @RestControllerAdvice, @ExceptionHandler e ResponseEntity está adequado. O ErrorResponse também define um corpo JSON com mensagem e status.

Problemas encontrados

1. O projeto não compila
As exceções foram alteradas para estender Exception, tornando-se checked exceptions. O método do serviço declara que pode lançá-las, mas o controller não as captura nem declara:
dddService.consultar(ddd)
A compilação falha porque DddController não trata DddVazioException.

2. As exceções nunca são lançadas
O DddService apenas declara:
throws DddVazioException, DddInvalidoException
Mas não há nenhuma validação nem nenhum throw. Portanto, o GlobalExceptionHandler não será acionado para esses casos.

3. A validação atual usa outras exceções
O controller ainda utiliza @NotNull e @Pattern. Essas anotações podem gerar exceções de validação do Spring, não necessariamente DddVazioException ou DddInvalidoException. Como elas não estão tratadas especificamente no handler, podem acabar caindo no tratamento genérico de Exception e retornando 500, o que estaria incorreto.

4. O GlobalExceptionHandler não deve estender RuntimeException
Esta declaração:
public class GlobalExceptionHandler extends RuntimeException
é desnecessária e conceitualmente inadequada. O handler não é uma exceção; ele deve ser apenas uma classe de tratamento anotada com @RestControllerAdvice.

Conclusão
Os status escolhidos estão corretos:
- DddVazioException → 400
- DddInvalidoException → 422
- erros inesperados → 500
Porém, a implementação ainda precisa ser ajustada para compilar e funcionar: as exceções devem ser efetivamente lançadas, o controller precisa conseguir propagá-las ao handler global e as validações atuais devem ser alinhadas com essa estratégia.

---

**Meu prompt:** A validação portanto deve ser feita no DddService, já que ele é quem lança exceção?

**Resposta do Copilot:**

Sim. Se a estratégia escolhida é lançar DddVazioException e DddInvalidoException, o DddService deve validar o DDD antes de consultar a API externa e lançar a exceção correspondente.

O fluxo ficaria:
```
Controller
    ↓
DddService.consultar(ddd)
    ↓
valida o valor
    ↓
lança DddVazioException ou DddInvalidoException
    ↓
GlobalExceptionHandler converte em HTTP 400 ou 422
```

A responsabilidade seria:
- Controller: receber a requisição e chamar o serviço.
- DddService: aplicar as regras de negócio:
    - vazio ou somente espaços → DddVazioException;
    - formato ou faixa inválida → DddInvalidoException;
    - válido → consultar a API externa.
• GlobalExceptionHandler: transformar as exceções em respostas HTTP.
• Front-end: apenas exibir a mensagem recebida.

Há uma ressalva importante: a validação de entrada simples também poderia ficar no controller usando Bean Validation. Porém, nesse caso, seriam tratadas exceções próprias do Spring, e não necessariamente suas exceções de domínio. Como você deseja usar especificamente DddVazioException e DddInvalidoException, faz sentido centralizar essa validação no serviço.

Também é recomendável remover ou alinhar as anotações @NotNull e @Pattern do controller, para não manter duas estratégias de validação concorrentes. Caso contrário, o Spring poderá rejeitar a requisição antes que ela chegue ao DddService.

---

**Meu prompt:** O que há de errado no tratamento da exceção nas camadas controller e service? Não codifique nada

**Resposta do Copilot:**

O problema principal é que controller e service não estão alinhados sobre quem valida e quem lança as exceções.

No DddService

1. As exceções são apenas declaradas, não lançadas
O método informa que pode lançar DddVazioException e DddInvalidoException, mas não valida o parâmetro nem executa throw. Portanto, essas exceções nunca são produzidas pelo serviço.

2. A consulta externa ocorre sem validação prévia
O serviço monta a URL e chama a Brasil API diretamente. Um DDD vazio ou inválido deveria ser rejeitado antes dessa chamada, evitando enviar dados incorretos para outro serviço.

3. A regra de negócio ainda não está centralizada
Se o service é responsável por lançar essas exceções, ele precisa ser a autoridade sobre as regras de validade do DDD. Atualmente, essa responsabilidade está parcialmente nas anotações do controller.

No DddController

1. O controller usa uma estratégia de validação diferente
Ele utiliza @NotNull e @Pattern, que podem gerar exceções de validação do Spring antes de o método chegar ao DddService. Nesse cenário, DddVazioException e DddInvalidoException não serão lançadas pelo serviço.
Isso cria duas estratégias concorrentes:
• validação automática no controller;
• validação esperada no service.
É necessário escolher uma estratégia principal ou tratar corretamente ambas.

2. O controller não trata nem propaga corretamente as exceções checked
Como as exceções estendem Exception, elas são checked exceptions. O serviço declara que pode lançá-las, mas o método do controller não as captura nem declara que também pode propagá-las. Por isso, o projeto não compila.
O @RestControllerAdvice não elimina as regras de compilação do Java. Ele trata a exceção em tempo de execução, mas o método ainda precisa estar estruturado para permitir que ela seja lançada.

3. O parâmetro de rota não representa bem o caso vazio
A rota usa:
/api/ddd/v1/{ddd}
Quando o usuário não informa nada, a requisição tende a ser /api/ddd/v1/, sem o segmento {ddd}. Nesse caso, o Spring pode nem entrar no método do controller; pode responder 404 diretamente. Assim, o service não recebe um valor vazio para lançar DddVazioException.

4. A assinatura do controller representa apenas sucesso
O método está tipado como resposta de DddResponse, embora também possa resultar em respostas de erro com ErrorResponse. Isso não é necessariamente um erro de execução quando se usa um handler global, mas evidencia que o controller foi pensado apenas para o fluxo bem-sucedido.

Fluxo esperado
A arquitetura deve definir claramente:

* o controller recebe o valor;
* o service valida;
* o service lança DddVazioException ou DddInvalidoException;
* a exceção propaga até o GlobalExceptionHandler;
* o handler converte para 400 ou 422;
* o front-end exibe a mensagem.

No estado atual, o handler global está preparado para converter as exceções, mas o service não as lança e o controller não consegue propagá-las corretamente.

---

**Meu prompt:** Revise novamente o meu código, parte de sua avaliação estava errada.

**Resposta do Copilot:**

Você tem razão: a minha avaliação anterior estava parcialmente desatualizada. O código mudou desde a análise anterior.

O que eu havia avaliado incorretamente
Eu disse que o DddService não lançava nenhuma exceção. Isso não é mais verdade: atualmente ele já lança DddVazioException quando ddd é null ou vazio:
```
if (ddd == null || ddd.isEmpty()) {
    throw new DddVazioException(...);
}
```
Também disse que o GlobalExceptionHandler ainda estendia RuntimeException. Isso já foi corrigido: agora ele é corretamente apenas uma classe anotada com @RestControllerAdvice.
Além disso, a validação @Pattern foi removida do controller, então não há mais aquela concorrência específica entre @Pattern e a validação no serviço.

O que ainda está errado

1. O projeto não compila
DddVazioException ainda estende Exception, portanto é uma checked exception. O DddService lança essa exceção, mas seu método não a declara nem a trata. A falha atual é:
unreported exception DddVazioException; must be caught or declared to be thrown
Consequentemente, a aplicação ainda não consegue executar o tratamento global.

2. DddInvalidoException não é lançada
Ela está importada no service e tratada no handler, mas o serviço não possui uma regra que identifique um DDD inválido nem executa throw new DddInvalidoException(...).
Atualmente, qualquer valor não vazio segue diretamente para a Brasil API, inclusive:
• letras;
• valores com mais ou menos de dois dígitos;
• caracteres especiais;
• números fora da faixa permitida.

3. A validação de vazio não trata espaços
O teste ddd.isEmpty() detecta "", mas não detecta uma string formada apenas por espaços, como "   ". Esse valor deveria ser considerado vazio caso o front-end ou outro cliente envie espaços.

4. Há imports desnecessários no controller
DddInvalidoException, DddVazioException e @NotNull estão importados, mas não são usados. Isso não impede a execução, mas indica que sobrou código da estratégia anterior.

5. A rota ainda não representa naturalmente o DDD vazio
Com a rota:
/api/ddd/v1/{ddd}
uma requisição sem DDD geralmente não chega ao método como uma string vazia; ela pode não corresponder à rota e resultar em 404. Portanto, o DddVazioException do service só será acionado se o controller realmente receber null ou "".

O que está correto
- DddVazioException está associada a 400 Bad Request.
- DddInvalidoException está associada a 422 Unprocessable Content.
O uso de @RestControllerAdvice e @ExceptionHandler está correto.
- O handler global não precisa ser chamado explicitamente pelo controller.
- O controller não precisa capturar a exceção para o @RestControllerAdvice tratá-la; ela pode propagar-se até o handler. Porém, como suas exceções são checked, o código precisa primeiro ser válido para o compilador.

Portanto, a avaliação correta do estado atual é: o fluxo arquitetural está encaminhado, mas o projeto ainda não compila, a exceção de DDD inválido ainda não é lançada e a validação de vazio está incompleta.

---

**Meu prompt:** Avalie o tratamento que fiz da exceção DddVazioException, e melhore o tratamento

**Resposta do Copilot:**

A implementação atual nem compila: DddVazioException e DddInvalidoException não herdam de Throwable, e o ErrorResponse depende de Lombok que não está sendo processado neste projeto. Vou corrigir a hierarquia das exceções e tornar a resposta de erro explícita e consistente, sem expor detalhes internos em erros 500.

O tratamento da DddVazioException foi corrigido e fortalecido.
- DddVazioException agora herda de RuntimeException, permitindo que seja lançada e capturada corretamente.
- DddInvalidoException também foi corrigida, pois apresentava o mesmo problema de hierarquia.
- O GlobalExceptionHandler retorna:
    - 400 Bad Request para DDD nulo ou em branco;
    - 422 Unprocessable Content para DDD inválido;
    - 500 Internal Server Error com mensagem genérica, sem expor detalhes internos.
• O ErrorResponse deixou de depender do Lombok para garantir compilação e serialização correta.
• Foram adicionados testes para DDD nulo, DDD em branco e resposta global de erro.

Validação executada com sucesso

---

**Meu prompt:** Quando uma consulta com ddd vazio é realizado, a exceção impressa é a Exception normal

**Resposta do Copilot:**

O problema está no mapeamento: uma URL sem valor (/api/ddd/v1/) não entra em /{ddd}, então a validação do serviço nunca ocorre e o Spring cai no tratamento genérico. Vou mapear também a rota sem segmento e cobrir esse caminho com teste.

Corrigido o tratamento de consultas com DDD vazio.
A rota agora aceita /api/ddd/v1/ e /api/ddd/v1 sem segmento, encaminhando a requisição para o serviço. Assim, DddVazioException é lançada e convertida corretamente em resposta HTTP 400, em vez de cair no tratamento genérico de Exception.
Também foi adicionado teste cobrindo esse fluxo. ./mvnw test passou.

---

**Meu prompt:** Crie agora um regex que valide se o ddd:
- é um número inteiro; e
- é ou está entre 11 e 99

**Resposta do Copilot:**

Vou reinstalar a validação no serviço, onde ela também cobre chamadas fora do controller, usando um regex que aceite somente dois dígitos no intervalo de 11 a 99 e retorne DddInvalidoException para os demais casos.

Adicionei a validação com o regex:
`^(?:1[1-9]|[2-9][0-9])$`
Ele aceita somente números inteiros entre 11 e 99, inclusive:
• Válidos: 11, 25, 47, 99
• Inválidos: 10, 100, 1.1, -11, 11
DDD vazio continua gerando DddVazioException. Valores que não atendem ao regex geram DddInvalidoException com HTTP 422. Os testes passaram.

---

**Meu prompt:** Exiba essas duas exceções e a exception genérica no front-end

**Resposta do Copilot:**

Vou ajustar o front-end para ler mensagem das respostas de erro e exibir separadamente DDD vazio, DDD inválido e falhas genéricas, mantendo o resultado oculto quando a consulta falhar.

O front-end agora exibe as mensagens das três situações de erro:
- DDD vazio: Erro! O campo de DDD está vazio.
- DDD inválido: Erro! O DDD deve ser um número inteiro entre 11 e 99.
- Erro genérico: Não foi possível concluir a consulta.
Foi adicionada uma área de alerta Bootstrap no formulário. O JavaScript lê o campo mensagem da resposta JSON da API, exibe o erro na tela e oculta o resultado anterior quando uma nova consulta falha. Os testes Maven passaram.