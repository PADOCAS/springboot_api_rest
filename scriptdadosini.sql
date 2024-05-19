--Retirar Constrint que o hibernate cria para uma unique key do role_id (não pode ter), deve ter apenas uma unique com as 2 colunas envolvidas!!
ALTER TABLE usuario_role
DROP CONSTRAINT 'nome da constraint tabela usuario_role -> unique de uma coluna';

--Roles:
insert into (id, nome_role) role values (1, 'ROLE_ADMIN');
insert into (id, nome_role) role values (2, 'ROLE_USER');
--User admin (login:admin senha:admin):
insert into usuario (id,bairro,cep,complemento,cpf,data_nascimento,email,localidade,login,logradouro,nome,salario,senha,token,uf,profissao_id)
values (1, null, null, null, null, '1980-01-01', 'teste@bol.com.br', null, 'admin', null, 'ADMINISTRADOR DO SISTEMA', 0, '$2a$10$PKoRIBU3hSGGvhh7XfUO6u1dYZ3/a993b0fs8vo6Crh2JAceYOTZe', null, null, null);
--User Role:
insert into usuario_role (usuario_id, role_id) values (1, 1);
--Config Geral (E-mail):  (configurar com dados de um e-mail válido responsável por envio de e-mail pelo sistema)
insert into config_geral (id,email,senha,smtp_host,smtp_port,socket_port) values (1, '--insira o email que será responsável pelo envio--', '--insira a senha do email responsável pelo envio--', '--insira o smtp_host -> exemplo: smtp.gmail.com--', 465, 465);
-- Profissao:
insert into profissao (id, descricao) values (1, 'PROGRAMADOR JÚNIOR JAVA');
insert into profissao (id, descricao) values (2, 'PROGRAMADOR PLENO JAVA');
insert into profissao (id, descricao) values (3, 'PROGRAMADOR SÊNIOR JAVA');
insert into profissao (id, descricao) values (4, 'ANALISTA DE SISTEMAS');
insert into profissao (id, descricao) values (5, 'GERENTE DE PROJETOS');
insert into profissao (id, descricao) values (6, 'SUPORTE DE TI');
insert into profissao (id, descricao) values (7, 'PROGRAMADOR JÚNIOR .NET');
insert into profissao (id, descricao) values (8, 'PROGRAMADOR PLENO .NET');
insert into profissao (id, descricao) values (9, 'PROGRAMADOR SÊNIOR .NET');
insert into profissao (id, descricao) values (10, 'PROGRAMADOR JÚNIOR PHYTHON');
insert into profissao (id, descricao) values (11, 'PROGRAMADOR PLENO PHYTHON');
insert into profissao (id, descricao) values (12, 'PROGRAMADOR SÊNIOR PHYTHON');
insert into profissao (id, descricao) values (13, 'SECRETÁRIA');
insert into profissao (id, descricao) values (14, 'AUXILIAR DE RH');
