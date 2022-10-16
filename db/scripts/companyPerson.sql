create table company(
	id serial primary key,
	name varchar(255)
);

create table person(
	id serial primary key,
	name varchar(255),
	company_id int references company(id)
);

insert into company(name) values('Oracle'), ('Google'), ('Yandex'),
('Mail.ru'), ('Sber'), ('Microsoft');
insert into person (name, company_id) values 
('Andrew', 1), ('John', 1), ('Peter', 1), ('Ellison', 1),
('Nicky', 2), ('Jack', 2),
('Alex', 3), ('Vladimir', 3), ('Marina', 3), ('Natasha', 3), 
('Johnson', 5), ('Gref', 5),
('Gates', 6);

select p.name, c.name
from person p join company c on p.company_id = c.id
where c.id <> 5

SELECT c.name, COUNT(p.id) as p_count
FROM company c join person p on c.id = p.company_id
group by c.name
having COUNT(p.id) = (
	select max(t1.p_count) 
	from (
		select company_id, count(id) p_count
		from person
		group by company_id
	) t1
)