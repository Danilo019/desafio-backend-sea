# Frontend - Desafio Backend SEA

Sistema de Gestão de Clientes desenvolvido em React com as cores da SEA Tecnologia.

## 🎨 Design

O frontend foi desenvolvido seguindo o esquema de cores da SEA Tecnologia:

- **Azul Escuro Principal**: #1e3a5f
- **Azul Médio**: #2c5f7d
- **Laranja (Destaque)**: #ff9800
- **Amarelo**: #ffc107
- **Fundo**: #0d1f2d

## 🚀 Tecnologias Utilizadas

- **React 18**: Biblioteca principal
- **React Router DOM**: Navegação entre páginas
- **Styled Components**: Estilização com CSS-in-JS
- **Axios**: Requisições HTTP para o backend
- **React Hook Form**: Gerenciamento de formulários
- **React Input Mask**: Máscaras para inputs (CPF, CEP, telefone)
- **React Toastify**: Notificações toast
- **React Icons**: Ícones do sistema
- **JWT Decode**: Decodificação de tokens JWT
- **Vite**: Build tool e dev server

## 📦 Instalação

```bash
# Instalar dependências
npm install

# Rodar em desenvolvimento
npm run dev

# Build para produção
npm run build
```

## 🏗️ Estrutura do Projeto

```
frontend/
├── src/
│   ├── components/          # Componentes reutilizáveis
│   │   ├── Layout/         # Layout principal com sidebar
│   │   ├── PrivateRoute/   # Proteção de rotas
│   │   └── ClienteModal/   # Modal de cadastro/edição
│   ├── pages/              # Páginas da aplicação
│   │   ├── Login/          # Página de login
│   │   ├── Dashboard/      # Dashboard principal
│   │   └── Clientes/       # Listagem e CRUD de clientes
│   ├── services/           # Serviços de API
│   │   ├── api.js          # Configuração do Axios
│   │   ├── authService.js  # Serviços de autenticação
│   │   └── clienteService.js # Serviços de clientes
│   ├── styles/             # Estilos globais
│   │   ├── theme.js        # Tema com cores da SEA
│   │   └── GlobalStyles.js # Estilos globais
│   ├── App.jsx             # Componente principal
│   └── main.jsx            # Ponto de entrada
├── index.html              # HTML principal
├── vite.config.js          # Configuração do Vite
└── package.json            # Dependências
```

## 🔐 Autenticação

O sistema possui dois tipos de usuários:

### 1. Usuário Admin
- **E-mail**: admin@sea.com
- **Senha**: 123qwe!@#
- **Permissões**: Total (criar, editar, excluir clientes)

### 2. Usuário Padrão
- **E-mail**: user@sea.com
- **Senha**: 123qwe123
- **Permissões**: Apenas visualização

## ✨ Funcionalidades

### Dashboard
- Estatísticas gerais do sistema
- Informações sobre funcionalidades
- Diferenciação de recursos por tipo de usuário

### Gestão de Clientes
- **Listagem**: Tabela com paginação e busca
- **Busca**: Por nome, CPF ou e-mail
- **Cadastro**: Formulário completo com validações
- **Edição**: Atualização de dados (apenas admin)
- **Exclusão**: Remoção de clientes (apenas admin)
- **Visualização**: Detalhes completos do cliente

### Validações
- **Nome**: Mínimo 3 caracteres, máximo 100
- **CPF**: Validação de formato e persistência sem máscara
- **CEP**: Consulta automática via ViaCEP
- **E-mail**: Formato válido obrigatório
- **Telefone**: Máscara automática e tipos (Celular, Residencial, Comercial)

### Recursos Avançados
- **Múltiplos Telefones**: Adicionar/remover dinamicamente
- **Múltiplos E-mails**: Adicionar/remover dinamicamente
- **Consulta CEP**: Preenchimento automático de endereço
- **Máscaras**: CPF, CEP e telefone formatados
- **Responsivo**: Layout adaptável para diferentes telas

## 🎨 Componentes Principais

### Layout
- Sidebar com navegação
- Header com título e subtítulo
- Informações do usuário logado
- Botão de logout

### ClienteModal
- Modal para cadastro/edição/visualização
- Formulário completo com validações
- Seções organizadas (Dados Pessoais, Endereço, Telefones, E-mails)
- Integração com ViaCEP

### Clientes (Listagem)
- Tabela responsiva com grid
- Busca em tempo real
- Paginação
- Ações por linha (visualizar, editar, excluir)
- Estados de loading e vazio

## 🔄 Integração com Backend

O frontend se comunica com o backend através de:

- **Base URL**: `/api` (proxy configurado no Vite)
- **Autenticação**: JWT Bearer Token
- **Interceptors**: Adiciona token automaticamente e trata erros 401

### Endpoints Utilizados

- `POST /auth/login` - Login
- `GET /clientes` - Listar clientes (com paginação)
- `GET /clientes/:id` - Buscar cliente por ID
- `POST /clientes` - Criar cliente
- `PUT /clientes/:id` - Atualizar cliente
- `DELETE /clientes/:id` - Excluir cliente
- `GET /cep/:cep` - Consultar CEP

## 🎯 Diferencial do Projeto

1. **Design Personalizado**: Cores e identidade visual da SEA Tecnologia
2. **UX Moderna**: Interface intuitiva e responsiva
3. **Animações**: Transições suaves e feedback visual
4. **Validações Completas**:Front e backend sincronizados
5. **Código Organizado**: Componentização e separação de responsabilidades
6. **Boas Práticas**: Hooks, Context API, código limpo

## 🚀 Deploy

O projeto está pronto para deploy em:
- Vercel
- Netlify
- AWS S3 + CloudFront
- Qualquer servidor estático

```bash
# Build para produção
npm run build

# A pasta dist/ conterá os arquivos otimizados
```

## 📱 Responsividade

O sistema é totalmente responsivo e se adapta a:
- Desktop (> 1024px)
- Tablet (768px - 1024px)
- Mobile (< 768px)

## 🎨 Paleta de Cores Completa

```javascript
{
  primary: '#1e3a5f',
  primaryDark: '#0d1f2d',
  primaryLight: '#2c5f7d',
  accent: '#ff9800',
  accentLight: '#ffc107',
  cardBg: '#1a3a52',
  cardBgHover: '#234563',
  textPrimary: '#ffffff',
  textSecondary: '#b0c4d4',
  textMuted: '#7a8fa3',
  success: '#4caf50',
  error: '#f44336',
  warning: '#ff9800',
  info: '#2196f3',
  background: '#0d1f2d',
}
```

---

**Desenvolvido com ❤️ para o Desafio Backend SEA**
