# Estrutura do Banco de Dados - Gerenciador de Estoque

┌─────────────────────────────────────────────────────────────────────────┐
│                         BANCO DE DADOS                                  │
│                    gerenciador_senai_db (PostgreSQL)                    │
└─────────────────────────────────────────────────────────────────────────┘

    ┌──────────────────┐
    │   FUNCIONARIOS   │
    ├──────────────────┤
    │ id (PK)         │
    │ nome            │
    │ nif (UNIQUE)    │
    │ senha           │
    │ ativo           │
    │ created_at      │
    │ updated_at      │
    └──────────────────┘
            │
            │ (1:1 opcional)
            │
    ┌──────────────────────────────┐
    │ FUNCIONARIOS_AUTENTICADO     │
    ├──────────────────────────────┤
    │ id (PK)                      │
    │ funcionario_id (FK, UNIQUE)  │
    │ ultimo_acesso               │
    │ tentativas_falhas           │
    │ bloqueado                   │
    │ created_at                  │
    │ updated_at                  │
    └──────────────────────────────┘


    ┌──────────────────┐
    │   CATEGORIAS     │
    ├──────────────────┤
    │ id (PK)         │
    │ nome            │
    │ descricao       │
    │ created_at      │
    │ updated_at      │
    └──────────────────┘
            │
            │ (1:N)
            │
    ┌──────────────────┐
    │   MATERIAIS      │
    ├──────────────────┤
    │ id (PK)         │
    │ nome            │
    │ descricao       │
    │ unidade_medida  │
    │ qtd_minima      │
    │ qtd_em_estoque  │
    │ categoria_id(FK)│◄─┘
    │ created_at      │
    │ updated_at      │
    └──────────────────┘
            │
            │ (1:N)
            │
    ┌─────────────────────────────────────┐
    │ MOVIMENTACOES_ESTOQUE               │
    ├─────────────────────────────────────┤
    │ id (PK)                             │
    │ material_id (FK)                    │◄─┘
    │ tipo_movimentacao (ENTRADA/SAIDA)   │
    │ quantidade                          │
    │ observacao                          │
    │ responsavel                         │
    │ data_movimentacao                   │
    │ created_at                          │
    └─────────────────────────────────────┘


    ┌─────────────────────────────────────┐
    │   ATIVOS_PATRIMONIAIS               │
    ├─────────────────────────────────────┤
    │ id (PK)                             │
    │ codigo_patrimonio (UNIQUE)          │
    │ nome                                │
    │ descricao                           │
    │ localizacao                         │
    │ responsavel                         │
    │ status_ativo (enum)                 │
    │ data_aquisicao                      │
    │ valor_aquisicao                     │
    │ created_at                          │
    │ updated_at                          │
    └─────────────────────────────────────┘
```

---

## Descrição Detalhada das Tabelas

### 1. **FUNCIONARIOS**
Armazena informações dos usuários/funcionários do sistema.

| Campo | Tipo | Constraints | Descrição |
|-------|------|-------------|-----------|
| id | BIGSERIAL | PK | Identificador único automaticamente gerado |
| nome | VARCHAR(255) | NOT NULL | Nome completo do funcionário |
| nif | VARCHAR(50) | UNIQUE, NOT NULL | Número de identificação funcional (E-mail, CPF ou ID) |
| senha | VARCHAR(255) | NOT NULL | Senha criptografada |
| ativo | BOOLEAN | DEFAULT true | Indica se o funcionário está ativo |
| created_at | TIMESTAMP | DEFAULT NOW | Data de criação do registro |
| updated_at | TIMESTAMP | DEFAULT NOW | Data da última atualização |

**Índices:**
- `idx_funcionarios_nif` - Para buscas por NIF
- `idx_funcionarios_ativo` - Para filtrar funcionários ativos

---

### 2. **FUNCIONARIOS_AUTENTICADO** (Opcional)
Armazena informações adicionais de autenticação dos funcionários.

| Campo | Tipo | Constraints | Descrição |
|-------|------|-------------|-----------|
| id | BIGSERIAL | PK | Identificador único |
| funcionario_id | BIGINT | FK, UNIQUE | Referência para funcionário (1:1) |
| ultimo_acesso | TIMESTAMP | | Timestamp do último acesso |
| tentativas_falhas | INTEGER | DEFAULT 0 | Contador de tentativas de login falhadas |
| bloqueado | BOOLEAN | DEFAULT false | Indica se a conta está bloqueada |
| created_at | TIMESTAMP | DEFAULT NOW | Data de criação |
| updated_at | TIMESTAMP | DEFAULT NOW | Data da última atualização |

**Índices:**
- `idx_funcionarios_autenticado_funcionario_id` - Para busca por funcionário

---

### 3. **CATEGORIAS**
Classifica os materiais em estoque.

| Campo | Tipo | Constraints | Descrição |
|-------|------|-------------|-----------|
| id | BIGSERIAL | PK | Identificador único |
| nome | VARCHAR(255) | NOT NULL | Nome da categoria (ex: Ferramentas, Equipamentos) |
| descricao | TEXT | | Descrição detalhada da categoria |
| created_at | TIMESTAMP | DEFAULT NOW | Data de criação |
| updated_at | TIMESTAMP | DEFAULT NOW | Data da última atualização |

**Valores Padrão Inseridos:**
- Ferramentas
- Equipamentos
- Consumíveis
- Mobiliário
- Eletrônicos

**Índices:**
- `idx_categorias_nome` - Para buscas por nome

---

### 4. **MATERIAIS**
Armazena os itens/materiais em estoque.

| Campo | Tipo | Constraints | Descrição |
|-------|------|-------------|-----------|
| id | BIGSERIAL | PK | Identificador único |
| nome | VARCHAR(255) | NOT NULL | Nome do material |
| descricao | TEXT | | Descrição detalhada |
| unidade_medida | VARCHAR(50) | | Unidade (pcs, kg, litros, etc) |
| quantidade_minima | INTEGER | DEFAULT 0 | Quantidade mínima para alerta |
| quantidade_em_estoque | INTEGER | DEFAULT 0 | Quantidade atual em estoque |
| categoria_id | BIGINT | FK NOT NULL | Referência para categoria |
| created_at | TIMESTAMP | DEFAULT NOW | Data de criação |
| updated_at | TIMESTAMP | DEFAULT NOW | Data da última atualização |

**Índices:**
- `idx_materiais_nome` - Para buscas por nome
- `idx_materiais_categoria_id` - Para filtros por categoria
- `idx_materiais_quantidade_em_estoque` - Para relatórios de estoque

**Relacionamentos:**
- Muitos-para-Um com CATEGORIAS (categoria_id)

---

### 5. **ATIVOS_PATRIMONIAIS**
Registra o patrimônio e ativos fixos da empresa.

| Campo | Tipo | Constraints | Descrição |
|-------|------|-------------|-----------|
| id | BIGSERIAL | PK | Identificador único |
| codigo_patrimonio | VARCHAR(50) | UNIQUE | Código único do patrimônio |
| nome | VARCHAR(255) | NOT NULL | Nome do ativo |
| descricao | TEXT | | Descrição completa |
| localizacao | VARCHAR(255) | | Localização atual |
| responsavel | VARCHAR(255) | | Responsável pelo ativo |
| status_ativo | VARCHAR(50) | CHECK (valores) | Status: EM_USO, EM_MANUTENCAO, DISPONIVEL, INATIVO |
| data_aquisicao | DATE | | Data de compra/aquisição |
| valor_aquisicao | NUMERIC(15,2) | | Valor de compra |
| created_at | TIMESTAMP | DEFAULT NOW | Data de criação |
| updated_at | TIMESTAMP | DEFAULT NOW | Data da última atualização |

**Status Permitidos:**
- `EM_USO` - Ativo em uso atualmente
- `EM_MANUTENCAO` - Em manutenção
- `DISPONIVEL` - Disponível para uso
- `INATIVO` - Descontinuado

**Índices:**
- `idx_ativos_patrimonio_codigo` - Para buscas por código
- `idx_ativos_patrimonio_nome` - Para buscas por nome
- `idx_ativos_patrimonio_status` - Para filtros por status
- `idx_ativos_patrimonio_responsavel` - Para rastreamento de responsabilidade

---

### 6. **MOVIMENTACOES_ESTOQUE**
Registra todas as entradas e saídas de materiais.

| Campo | Tipo | Constraints | Descrição |
|-------|------|-------------|-----------|
| id | BIGSERIAL | PK | Identificador único |
| material_id | BIGINT | FK NOT NULL | Referência para material |
| tipo_movimentacao | VARCHAR(50) | CHECK (valores) | Tipo: ENTRADA ou SAIDA |
| quantidade | INTEGER | NOT NULL | Quantidade movimentada |
| observacao | TEXT | | Observações adicionais |
| responsavel | VARCHAR(255) | | Responsável pela movimentação |
| data_movimentacao | TIMESTAMP | DEFAULT NOW | Data/hora da movimentação |
| created_at | TIMESTAMP | DEFAULT NOW | Data de criação do registro |
