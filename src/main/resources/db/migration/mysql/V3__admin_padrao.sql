INSERT INTO empresa (id, cnpj, data_atualizacao, data_criacao, razao_social)
VALUES (NULL, '8219127000121', CURRENT_DATE(), CURRENT_DATE(), 'DouG IT');

INSERT INTO funcionario(id, cpf, data_atualizacao, data_criacao, email, nome, perfil, qtd_horas_almoco, qtd_horas_trabalho_dia, senha, valor_hora, empresa_id)
VALUES(NULL, '16248890935', CURRENT_DATE(), CURRENT_DATE(), 'adm@dvfs.com', 'Admin', 'ROLE_ADMIN', 5, 40, '$2a$10$5lyaH.G6eAycqa4iIa8ZgO25jZqE5MYoakQN/V6VA0v8ay6r5agiu', NULL,
(SELECT id FROM empresa WHERE cnpj like '8219127000121'));