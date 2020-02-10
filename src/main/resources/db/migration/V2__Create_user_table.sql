create table user
(
  id           bigint auto_increment
    primary key,
  account_id   varchar(100) null,
  name         varchar(50)  null,
  token        char(36)     null,
  bio          varchar(256) null,
  avatar_url   varchar(256) null,
  pwd          varchar(50)  null,
  gmt_create   bigint       null,
  gmt_modified bigint       null
);

create index account_id
  on user (account_id);

create index name
  on user (name);

create index token
  on user (token);
