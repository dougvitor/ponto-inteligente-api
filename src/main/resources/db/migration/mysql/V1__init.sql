	drop table if exists empresa;
	drop table if exists funcionario;
	drop table if exists lancamento;
 
 	create table empresa (
       id bigint not null,
        cnpj varchar(255) not null,
        data_atualizacao datetime not null,
        data_criacao datetime not null,
        razao_social varchar(255) not null,
        primary key (id)
    ) engine=InnoDB default charset=utf8;
    
    create table funcionario (
       id bigint not null,
        cpf varchar(255) not null,
        data_atualizacao datetime not null,
        data_criacao datetime not null,
        email varchar(255) not null,
        nome varchar(255) not null,
        perfil varchar(255) not null,
        qtd_horas_almoco float not null,
        qtd_horas_trabalho_dia float not null,
        senha varchar(255) not null,
        valor_hora decimal(19,2),
        empresa_id bigint,
        primary key (id)
    ) engine=InnoDB default charset=utf8;
    
    create table lancamento (
       id bigint not null,
        data datetime not null,
        data_atualizacao datetime not null,
        data_criacao datetime not null,
        descricao varchar(255) not null,
        localizacao varchar(255) not null,
        tipo varchar(255) not null,
        funcionario_id bigint,
        primary key (id)
    ) engine=InnoDB default charset=utf8;
    
    alter table empresa
    	modify id bigint(20) not null auto_increment;
    	
    alter table funcionario
    	modify id bigint(20) not null auto_increment;
    	
    alter table lancamento
    	modify id bigint(20) not null auto_increment;
    
    alter table funcionario 
       add constraint FK4cm1kg523jlopyexjbmi6y54j 
       foreign key (empresa_id) 
       references empresa (id);
    
    alter table lancamento 
       add constraint FK46i4k5vl8wah7feutye9kbpi4 
       foreign key (funcionario_id) 
       references funcionario (id);