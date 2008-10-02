-- note using all integer for decimal amounts to simplify example
-- To recreate schema, run this command in the SQL GUI
-- drop schema PUBLIC cascade;
-- create schema PUBLIC authorization DBA;


create table dual (
dummy int
);

insert into dual values (1);


create table customer (
    id integer primary key,
    name varchar(255),
    phone_number varchar(50),
    balance integer,
    is_overdue varchar(1),
    constraint unique_customer_name unique (name)
);

create sequence customer_sequence;


create table invoice (
    id integer primary key,
    invoice_number integer,
    customer_id integer,
    invoice_date date,
    total integer,
    pending_balance integer,
    constraint unique_invoice_number unique (invoice_number),
    foreign key(customer_id) references customer
  );


create sequence invoice_sequence;
create sequence invoice_number_sequence start with 10000;


create table payment(
    id integer primary key,
    customer_id integer,
    amount integer,
    payment_date datetime
);

create sequence payment_sequence;

create table manufacturer
(
    id integer primary key,
    name varchar(100),
    primary key(id),
    constraint unique_manufacturer_name unique (name)
);
create sequence manufacturer_sequence start with 1000;



create table product
(
    id integer,
    name varchar(100),
    price integer,
    manufacturer_id integer,
    primary key(id),
    constraint unique_product_manu_name unique  (name, manufacturer_id),
    foreign key(manufacturer_id) references manufacturer
);

create sequence product_sequence;


create table line_item
(
    invoice_id integer,
    line_number integer,
    qty integer,
    product_id integer,
    price integer,
    constraint unique_line_item_line_number unique (invoice_id, line_number),
    primary key(invoice_id, line_number),
    foreign key(product_id) references product,
    foreign key(invoice_id) references invoice
);



create table shopping_list(
    id integer primary key,
    customer_id integer,
    name varchar(100)
);

create sequence shopping_list_sequence start with 10000;

create table shopping_list_item
(
    shopping_list_id integer,
    line_number integer,
    qty integer,
    product_id integer,
    primary key(shopping_list_id, line_number),
    foreign key(product_id) references product,
    foreign key(shopping_list_id) references shopping_list
);



commit;

