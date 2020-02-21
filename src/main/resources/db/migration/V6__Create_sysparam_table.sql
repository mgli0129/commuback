create table sysparam
(
  id int  NOT NULL auto_increment
    primary key,
	redis char comment 'redis开关 Y-连接',
	private_key varchar(1024) null,
  public_key varchar(1024) null
);

