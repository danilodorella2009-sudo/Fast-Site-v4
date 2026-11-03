-- Executado automaticamente pelo Spring Boot quando o servidor liga.
-- IF NOT EXISTS: se a tabela já existe, não mexe nela.
-- (colunas novas em tabelas antigas são criadas pela classe DadosIniciais)

-- Contas de login. perfil: ADMIN (vê os cadastros) ou CLIENTE
CREATE TABLE IF NOT EXISTS usuarios (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    perfil     VARCHAR(20)  NOT NULL DEFAULT 'CLIENTE'
) ENGINE=InnoDB;

-- Dados dos clientes (o email liga o cadastro ao login em "usuarios")
CREATE TABLE IF NOT EXISTS cadastros (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(150) NOT NULL,
    email        VARCHAR(150) NULL,
    endereco     VARCHAR(200) NOT NULL,
    cep          VARCHAR(9)   NOT NULL,
    numero       VARCHAR(10)  NOT NULL,
    telefone     VARCHAR(20)  NOT NULL,
    observacao   TEXT,
    data_criacao DATETIME     NOT NULL,
    ativo        TINYINT(1)   NOT NULL DEFAULT 1
) ENGINE=InnoDB;

-- ===== PARTE 4 =====
-- Veículos: cada veículo pertence a um cliente (chave estrangeira -> cadastros)
CREATE TABLE IF NOT EXISTS veiculos (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    placa       VARCHAR(8)   NOT NULL UNIQUE,
    modelo      VARCHAR(100) NOT NULL,
    ano         INT          NOT NULL,
    cadastro_id INT          NOT NULL,
    CONSTRAINT fk_veiculo_cliente FOREIGN KEY (cadastro_id) REFERENCES cadastros (id)
) ENGINE=InnoDB;

-- Ordens de serviço: cada ordem pertence a um veículo (chave estrangeira -> veiculos)
CREATE TABLE IF NOT EXISTS ordens_servico (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    veiculo_id    INT           NOT NULL,
    defeito       VARCHAR(500)  NOT NULL,
    valor         DECIMAL(10,2) NOT NULL,
    status        VARCHAR(20)   NOT NULL,
    data_abertura DATETIME      NOT NULL,
    CONSTRAINT fk_ordem_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculos (id)
) ENGINE=InnoDB;
