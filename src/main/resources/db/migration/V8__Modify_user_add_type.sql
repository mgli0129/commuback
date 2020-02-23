alter table user
	add acct_type char(3) not null after name;

alter table user
	add trd_type char(3) null after pwd;