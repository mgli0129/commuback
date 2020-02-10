create table comment
(
	id bigint auto_increment,
	parent_id bigint not null comment '父类id',
	content varchar(1024) not null,
	type int not null comment '父类类型-指明一二级',
	like_count bigint default 0,
	comment_count bigint default 0,
	commentator bigint comment '评论者',
	gmt_create bigint,
	gmt_modified bigint,
	primary key (id)
);

