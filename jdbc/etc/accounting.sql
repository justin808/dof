drop schema PUBLIC cascade;

-- create schema PUBLIC authorization DBA;

-- drop table customer;

create table dual (
dummy int
);

insert into dual values (1);



create table customer (
    id integer primary key,
    name varchar(255),
    phone_number varchar(50),
    balance decimal(12,2),
    is_overdue char(1)
);

-- drop sequence customer_sequence;
create sequence customer_sequence start with 1000;

-- drop table invoice;
create table invoice(
    id integer primary key,
    customer_id integer,
    invoice_date date,
    amount decimal(12,2),
    pending_balance decimal(12,2)
);

-- drop sequence invoice_sequence;
create sequence invoice_sequence start with 1000;

-- drop table payment;
create table payment(
    id integer primary key,
    customer_id integer,
    amount decimal(12,2),
    payment_date datetime
);

-- drop sequence payment_sequence;
create sequence payment_sequence start with 1000;

create table manufacturer
(
id int,
name varchar(100),
primary key(id)
);
create sequence manufacturer_sequence start with 1000;



create table product
(
id int,
name varchar(100),
price decimal(12,2),
manufacturer_id int,
primary key(id),
foreign key(manufacturer_id) references manufacturer
);

create sequence product_sequence start with 1000;


create table line_item
(
invoice_id int,
line_number int,
qty int,
product_id int,
price decimal(12,2),
primary key(invoice_id, line_number),
foreign key(product_id) references product,
foreign key(invoice_id) references invoice
);



