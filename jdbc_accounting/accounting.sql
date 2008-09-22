-- note using all integer for decimal amounts to simplify example

-- To recreate schema, run this command in the SQL GUI
-- drop schema PUBLIC cascade;

-- create schema PUBLIC authorization DBA;

--drop table dual;
create table dual (
dummy int
);

insert into dual values (1);


--drop table customer;

create table customer (
    id integer primary key,
    name varchar(255),
    phone_number varchar(50),
    balance integer,
    is_overdue char(1)
);

--drop sequence customer_sequence;
create sequence customer_sequence start with 1000;

--drop table invoice;
create table invoice(
    id integer primary key,
    customer_id integer,
    invoice_date date,
    total integer,
    pending_balance integer,
    foreign key(customer_id) references customer
  );

--drop sequence invoice_sequence;
create sequence invoice_sequence start with 10000;

--drop table payment;
create table payment(
    id integer primary key,
    customer_id integer,
    amount integer,
    payment_date datetime
);

--drop sequence payment_sequence;
create sequence payment_sequence start with 10000;

create table manufacturer
(
    id int,
    name varchar(100),
    primary key(id)
);
create sequence manufacturer_sequence start with 10000;



create table product
(
    id int,
    name varchar(100),
    price integer,
    manufacturer_id int,
    primary key(id),
    foreign key(manufacturer_id) references manufacturer
);

create sequence product_sequence start with 10000;


create table line_item
(
    invoice_id int,
    line_number int,
    qty int,
    product_id int,
    price integer,
    primary key(invoice_id, line_number),
    foreign key(product_id) references product,
    foreign key(invoice_id) references invoice
);


--drop table shoppin_list;

create table shopping_list(
    id integer primary key,
    customer_id integer,
    name varchar(100)
);

--drop sequence shopping_list_sequence;
create sequence shopping_list_sequence start with 10000;

create table shopping_list_item
(
    shopping_list_id int,
    line_number int,
    qty int,
    product_id int,
    primary key(shopping_list_id, line_number),
    foreign key(product_id) references product,
    foreign key(shopping_list_id) references shopping_list
);



commit;

