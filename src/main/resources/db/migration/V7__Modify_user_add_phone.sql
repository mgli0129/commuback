alter table user modify pwd varchar(256) null;

alter table user
	add phone char(11) not null after pwd;