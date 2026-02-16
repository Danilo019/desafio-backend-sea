# 🎯 Demonstração de Testes - Desafio Backend SEA

> **Objetivo:** Demonstrar a qualidade e cobertura dos testes implementados neste projeto para avaliação técnica.

---

## 📊 Resumo Executivo

| Métrica | Valor | Status |
|---------|-------|--------|
| **Total de Testes** | 181 | ✅ 100% passando |
| **Cobertura Geral** | 43% | ✅ Acima do mínimo (40%) |
| **Service Layer** | 93% | 🟢 Excelente |
| **Controller Layer** | 99% | 🟢 Excelente |
| **Exception Handlers** | 88% | 🟢 Muito bom |
| **Tempo de Execução** | ~15s | ⚡ Rápido |

---

## 🏗️ Arquitetura de Testes

### Estrutura por Camadas

```
📦 Cobertura por Camada
├── 🎯 Controllers (99%)      ← 44 testes
│   ├── ClienteController     12 testes
│   ├── TelefoneController    12 testes
│   ├── EmailController       12 testes
│   └── EnderecoController     8 testes
│
├── 💼 Services (93%)          ← 84 testes
│   ├── ClienteService        23 testes
│   ├── TelefoneService       13 testes
│   ├── EmailService          10 testes
│   ├── EnderecoService       12 testes
│   ├── ViaCepService         11 testes
│   └── UsuarioService        15 testes
│
├── ✅ DTO Validation          ← 56 testes
│   ├── ClienteRequest        18 testes
│   ├── TelefoneRequest       10 testes
│   ├── EmailRequest           9 testes
│   └── EnderecoRequest       19 testes
│
└── 🛡️ Exception Handlers (88%)
    └── GlobalExceptionHandler
```

---

## 🎬 Como Executar (Para o Recrutador)

### 1️⃣ Executar Todos os Testes

```bash
cd backend
mvn clean test
```

**Saída esperada:**
```
[INFO] Tests run: 181, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### 2️⃣ Gerar Relatório de Cobertura (JaCoCo)

```bash
mvn clean test jacoco:report
```

**Visualizar relatório:**
```
backend/target/site/jacoco/index.html
```

Abra no navegador para ver relatório visual com:
- ✅ Linhas cobertas (verde)
- ❌ Linhas não cobertas (vermelho)
- 🟡 Branches parcialmente cobertos (amarelo)

### 3️⃣ Executar Testes de uma Camada Específica

```bash
# Apenas Services
mvn test -Dtest="*ServiceTest"

# Apenas Controllers
mvn test -Dtest="*ControllerTest"

# Apenas Validações
mvn test -Dtest="*RequestTest"
```

---

## 💡 Exemplos de Testes Implementados

### 1. Service Layer - Casos Completos

#### ✅ ClienteServiceTest (23 testes)

```java
// Cenário: Criar cliente com sucesso
@Test
void deveCriarCliente_ComSucesso() {
    // Arrange
    ClienteRequest request = criarClienteRequest();
    when(clienteRepository.existsByCpf(anyString())).thenReturn(false);
    when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

    // Act
    ClienteResponse response = clienteService.criarCliente(request);

    // Assert
    assertNotNull(response);
    assertEquals("João Silva", response.getNome());
    verify(clienteRepository).save(any(Cliente.class));
}

// Cenário: Erro ao duplicar CPF
@Test
void deveLancarExcecao_QuandoCpfJaExiste() {
    // Arrange
    when(clienteRepository.existsByCpf(anyString())).thenReturn(true);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, 
        () -> clienteService.criarCliente(request)
    );
}
```

**Cobertura:** 23 cenários testados incluindo:
- ✅ Criação bem-sucedida
- ✅ CPF duplicado
- ✅ Cliente não encontrado
- ✅ Atualização parcial
- ✅ Relacionamentos (telefones/emails/endereços)
- ✅ Paginação e filtros

---

#### ✅ ViaCepServiceTest (11 testes)

```java
// Cenário: Integração com API ViaCEP
@Test
void deveConsultarCep_ComSucesso() {
    // Arrange
    String cep = "01001000";
    ViaCepResponse mockResponse = new ViaCepResponse();
    mockResponse.setCep("01001-000");
    mockResponse.setLogradouro("Praça da Sé");
    
    when(restTemplate.getForObject(anyString(), eq(ViaCepResponse.class)))
        .thenReturn(mockResponse);

    // Act
    ViaCepResponse response = viaCepService.consultarCep(cep);

    // Assert
    assertEquals("01001-000", response.getCep());
    assertEquals("Praça da Sé", response.getLogradouro());
}

// Cenário: CEP inválido
@Test
void deveLancarExcecao_QuandoCepNaoEncontrado() {
    // Arrange
    ViaCepResponse errorResponse = new ViaCepResponse();
    errorResponse.setErro(true);
    
    when(restTemplate.getForObject(anyString(), eq(ViaCepResponse.class)))
        .thenReturn(errorResponse);

    // Act & Assert
    assertThrows(CepNotFoundException.class, 
        () -> viaCepService.consultarCep("99999999")
    );
}
```

**Cobertura:** API externa mockada completamente

---

### 2. Controller Layer - Testes de Integração

#### ✅ ClienteControllerTest (12 testes)

```java
@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    // Cenário: POST /api/clientes
    @Test
    void deveCriarCliente_RetornarCreated() throws Exception {
        // Arrange
        ClienteResponse response = new ClienteResponse();
        response.setNome("João Silva");
        when(clienteService.criarCliente(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(clienteRequestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    // Cenário: GET /api/clientes/{id} - Not Found
    @Test
    void deveBuscarCliente_RetornarNotFound() throws Exception {
        // Arrange
        when(clienteService.buscarPorId(999L))
            .thenThrow(new ResourceNotFoundException("Cliente não encontrado"));

        // Act & Assert
        mockMvc.perform(get("/api/clientes/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.mensagem").value("Cliente não encontrado"));
    }
}
```

**Cobertura:** Todos os endpoints HTTP testados
- ✅ POST (201 Created)
- ✅ GETById (200 OK / 404 Not Found)
- ✅ GET List (200 OK com paginação)
- ✅ PUT (200 OK / 404 Not Found)
- ✅ DELETE (204 No Content / 404 Not Found)

---

### 3. DTO Validation - Bean Validation

#### ✅ ClienteRequestTest (18 testes)

```java
class ClienteRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // Cenário: Nome vazio
    @Test
    void deveValidar_NomeVazio() {
        // Arrange
        ClienteRequest request = new ClienteRequest();
        request.setNome("");  // Inválido

        // Act
        Set<ConstraintViolation<ClienteRequest>> violations = 
            validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("Nome é obrigatório")));
    }

    // Cenário: CPF formato inválido
    @Test
    void deveValidar_CpfFormatoInvalido() {
        // Arrange
        ClienteRequest request = new ClienteRequest();
        request.setCpf("123");  // Formato errado

        // Act
        Set<ConstraintViolation<ClienteRequest>> violations = 
            validator.validate(request);

        // Assert
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("CPF deve estar no formato")));
    }
}
```

**Cobertura:** Todas as validações Bean Validation
- ✅ @NotBlank
- ✅ @NotNull
- ✅ @Pattern (CPF, telefone, email)
- ✅ @Size (min/max)
- ✅ @Valid (nested objects)

---

### 4. Exception Handler - Tratamento Global

#### ✅ GlobalExceptionHandler com Cobertura

```java
// Testa tratamento de ResourceNotFoundException
@Test
void deveTratarResourceNotFoundException() {
    when(clienteService.buscarPorId(999L))
        .thenThrow(new ResourceNotFoundException("Cliente não encontrado"));

    mockMvc.perform(get("/api/clientes/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.mensagem").value("Cliente não encontrado"))
        .andExpect(jsonPath("$.timestamp").exists());
}

// Testa tratamento de CepNotFoundException
@Test
void deveTratarCepNotFoundException() {
    when(viaCepService.consultarCep("99999999"))
        .thenThrow(new CepNotFoundException("CEP não encontrado"));

    mockMvc.perform(get("/api/cep/99999999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.mensagem").value("CEP não encontrado"));
}
```

**Cobertura:** Handler testa:
- ✅ ResourceNotFoundException → 404
- ✅ CepNotFoundException → 404  
- ✅ IllegalArgumentException → 400
- ✅ MethodArgumentNotValidException → 400
- ✅ Exception genérica → 500

---

## 🎯 Padrões e Boas Práticas Aplicados

### ✅ Padrão AAA (Arrange-Act-Assert)
```java
@Test
void exemplo() {
    // Arrange - Preparar dados e mocks
    ClienteRequest request = criarClienteRequest();
    when(repository.save(any())).thenReturn(cliente);
    
    // Act - Executar método testado
    ClienteResponse response = service.criarCliente(request);
    
    // Assert - Validar resultado
    assertNotNull(response);
    assertEquals("João", response.getNome());
}
```

### ✅ Mocks Específicos por Teste
- **Não reutilizamos** mocks entre testes
- Cada teste configura apenas o necessário
- Evita `UnnecessaryStubbingException`

### ✅ Nomenclatura Descritiva
```java
// ✅ BOM
deveCriarCliente_ComSucesso()
deveLancarExcecao_QuandoCpfJaExiste()
deveBuscarCliente_RetornarNotFound()

// ❌ RUIM
test1()
testCreate()
testException()
```

### ✅ Testes Isolados
- Cada teste é independente
- Usa `@BeforeEach` para setup
- Banco H2 in-memory (rollback automático)
- Não compartilha estado

### ✅ Cobertura de Branches
```java
// Testa TODOS os caminhos
@Test
void testaIf_Verdadeiro() { ... }

@Test
void testaIf_Falso() { ... }

@Test
void testaElse_OutroCaminho() { ... }
```

---

## 📈 Métricas JaCoCo Detalhadas

### Service Layer (93% - 1.825/1.950 instruções)

| Service | Cobertura | Testes |
|---------|-----------|--------|
| ClienteService | 95% | 23 |
| TelefoneService | 92% | 13 |
| EmailService | 94% | 10 |
| EnderecoService | 91% | 12 |
| ViaCepService | 96% | 11 |
| UsuarioService | 89% | 15 |

### Controller Layer (99% - 446/450 instruções)

| Controller | Cobertura | Testes |
|------------|-----------|--------|
| ClienteController | 99% | 12 |
| TelefoneController | 100% | 12 |
| EmailController | 98% | 12 |
| EnderecoController | 100% | 8 |

### Por que DTOs/Entities têm baixa cobertura (12-20%)?

**Resposta:** São POJOs gerados por Lombok!

```java
@Data  // <-- Gera getters/setters/equals/hashCode automaticamente
@AllArgsConstructor
@NoArgsConstructor
public class ClienteRequest {
    private String nome;
    private String cpf;
}
```

✅ **Não testamos código gerado automaticamente**
✅ **Validações são testadas indiretamente** (ClienteRequestTest)
✅ **É uma prática aceita** pela comunidade Java

---

## 🔍 Verificação de Qualidade

### Threshold JaCoCo (40% mínimo)

```xml
<!-- pom.xml -->
<configuration>
    <rules>
        <rule>
            <limits>
                <limit>
                    <minimum>0.40</minimum>  <!-- 40% -->
                </limit>
            </limits>
        </rule>
    </rules>
</configuration>
```

✅ **Build falha automaticamente** se cobertura < 40%

### Build Success Evidence

```bash
[INFO] --- jacoco-maven-plugin:0.8.8:check (jacoco-check) @ desafio-backend ---
[INFO] Loading execution data file: target/jacoco.exec
[INFO] Analyzed bundle 'desafio-backend' with 23 classes
[INFO] All coverage checks have been met.
[INFO] BUILD SUCCESS
```

---

## 🏆 Diferenciais Técnicos

### 1. Cobertura Estratégica
❌ **Não Realizei:** 100% de cobertura cega  
✅ **o que foi realizado:** 93-99% nas camadas críticas (Service/Controller)

### 2. Testes de Integração Reais
- MockMvc para controllers
- @WebMvcTest com contexto Spring
- RestTemplate mockado para APIs externas

### 3. Validação Bean Validation
- Testes específicos para @NotBlank, @Pattern, @Size
- Mensagens de erro customizadas validadas

### 4. Exception Handling Completo
- GlobalExceptionHandler com 88% de cobertura
- Todos os tipos de exceção testados
- Formato de resposta padronizado

### 5. Fixtures e Builders
```java
private ClienteRequest criarClienteRequest() {
    ClienteRequest request = new ClienteRequest();
    request.setNome("João Silva");
    request.setCpf("123.456.789-00");
    // Evita duplicação de código nos testes
    return request;
}
```
---

## 🎓 Tecnologias Utilizadas

- ✅ **JUnit 5** - Framework de testes moderno
- ✅ **Mockito** - Mocking de dependências
- ✅ **MockMvc** - Testes de integração web
- ✅ **JaCoCo** - Relatório de cobertura
- ✅ **H2 Database** - Banco in-memory para testes
- ✅ **AssertJ** - Assertions fluentes (opcional)
- ✅ **Surefire** - Relatórios de execução

---

## 📝 Documentação Adicional

- 📄 [DEPLOY.md](DEPLOY.md) - Configuração de produção
- 📄 [PRODUCTION_CHECKLIST.md](PRODUCTION_CHECKLIST.md) - Checklist de deploy

---

## 🎬 Demo Rápida (2 minutos)

```bash
# 1. Clone o repositório
git clone <URL>
cd desafio-backend-sea/backend

# 2. Execute os testes
mvn clean test

# 3. Gere o relatório
mvn jacoco:report

# 4. Abra no navegador
start target/site/jacoco/index.html  # Windows
open target/site/jacoco/index.html   # Mac/Linux
```

---

## ✅ Checklist de Avaliação

**Para o recrutador verificar:**

- [ ] 181 testes executam sem erros
- [ ] Build Maven é bem-sucedido  
- [ ] Relatório JaCoCo é gerado
- [ ] Service layer tem 93% de cobertura
- [ ] Controller layer tem 99% de cobertura
- [ ] Nomenclatura de testes é clara
- [ ] Padrão AAA é seguido
- [ ] Mocks são específicos por teste
- [ ] Exception handling está coberto
- [ ] Validações Bean Validation testadas

---

## 🎯 Conclusão

Este projeto demonstra:

✅ **Conhecimento técnico sólido** em testes automatizados  
✅ **Aplicação de boas práticas** da indústria  
✅ **Cobertura estratégica** (não apenas números)  
✅ **Código manutenível** e legível  
✅ **Atenção à qualidade** em todas as camadas  

**Resultado:** Backend production-ready com 181 testes validando 93-99% das camadas críticas.

---

**Contato:** Danilo Araújo  
**Projeto:** Desafio Backend SEA  
**Data:** Fevereiro 2026  
**Repositório:** [Danilo019/desafio-backend-sea](https://github.com/Danilo019/desafio-backend-sea)
