

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




create table invoice(
    id integer primary key,
    customer_id integer,
    invoice_date date,
    amount decimal(12,2),
    pending_balance decimal(12,2)
);





create table payment(
    id integer primary key,
    customer_id integer,
    amount decimal(12,2),
    payment_date timestamp
);




create table manufacturer
(
id int,
name varchar(100),
primary key(id)
);




create table product
(
id int,
name varchar(100),
price decimal(12,2),
manufacturer_id int,
primary key(id),
foreign key(manufacturer_id) references manufacturer
);




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



