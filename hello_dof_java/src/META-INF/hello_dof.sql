-- note using all integer for decimal amounts to simplify example
-- To recreate schema, run this command in the SQL GUI
-- drop schema PUBLIC cascade;
-- create schema PUBLIC authorization DBA;


create table manufacturer
(
    id identity primary key,
    name varchar(100),
    constraint unique_manufacturer_name unique (name)
);
--create sequence manufacturer_sequence start with 1000;



create table product
(
    id identity primary key,
    name varchar(100),
    price integer,
    manufacturer_id integer,
    constraint unique_product_manu_name unique  (name, manufacturer_id),
    foreign key(manufacturer_id) references manufacturer
);

--create sequence product_sequence;


commit;

