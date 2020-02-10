create table notification
(
	id bigint auto_increment,
	notifier bigint not null comment '通知者',
	receiver bigint not null comment '接受通知者',
	type int not null comment '通知类型',
	outerid bigint not null comment '通知类型对应id',
	notifier_name varchar(100),
	notify_title varchar(256),
	questionid bigint,
	status int default 0 not null comment '状态',
	gmt_create bigint not null,
		primary key (id)
);

