create table company(
	id serial primary key,
	name varchar(255)
);

create table person(
	id serial primary key,
	name varchar(255),
	company_id int references company(id)
);