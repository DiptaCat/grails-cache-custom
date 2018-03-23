create sequence HIBERNATE_SEQUENCE;

create table CACHE (
  ID NUMBER(19) not null primary key,
	VERSION NUMBER(19) not null,
	DATE_CREATED TIMESTAMP(6) not null,
	LAST_UPDATED TIMESTAMP(6) not null,
	NAME VARCHAR2(255) not null
);

create table CACHE_ITEM (
	ID NUMBER(19) not null primary key,
	VERSION NUMBER(19) not null,
	CACHE_ID NUMBER(19) not null
		constraint FK1_CACHE_ITEM_CACHE
			references CACHE,
	VALUE BLOB,
	KEY VARCHAR2(255) not null,
	constraint UK1_CACHE_ITEM
		unique (CACHE_ID, KEY)
);