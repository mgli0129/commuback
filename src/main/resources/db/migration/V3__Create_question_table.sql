create table question
(
	id bigint auto_increment
	primary key,
	title varchar(100) not null,
	content text,
	tag varchar(100),
	comment_count bigint default 0,
	view_count bigint default 0,
	like_count bigint default 0,
	gmt_create bigint,
	gmt_modified bigint,
	creator bigint
);
