drop table Customers cascade constraints;
drop table Rentals cascade constraints;
drop table Reservations cascade constraints;
drop table Vehicles cascade constraints;
drop table VehicleTypes cascade constraints;


create table VehicleTypes(
  vtname varchar2(20) primary key,
  features varchar2(20),
  hourlyRate number(10,2) not null,
  kiloRate number(10,2) not null,
  kiloLimitPerHour number(10,2) not null,
  tankRefillFee number(10,2) not null);


create table Vehicles(
  vid integer primary key,
  vlicense varchar2(10) not null,
  make varchar2(10) not null,
  model varchar2(10) not null,
  year integer not null,
  color varchar2(10) not null,
  odometer number(5,2) not null,                        
  status varchar2(10) not null,
  vtname varchar2(10) not null,
  location varchar2(10) not null,
  foreign key (vtname) references VehicleTypes(vtname));

create table Customers(
  dlicense varchar2(20) primary key,
  cellphone varchar2(20) not null,
  name varchar2(10) not null,
  address varchar2(20) not null
  );

create table Reservations(
  confNo integer GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) primary key,
  vid integer,
  dlicense varchar(20),
  startTimestamp timestamp not null,
  endTimestamp timestamp not null,
  cardName varchar(20) not null,
  cardNo varchar(20) not null,
  expDate timestamp not null,
  foreign key (vid) references Vehicles,
  foreign key (dlicense) references Customers(dlicense));

create table Rentals(
  rid integer GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) primary key,
  vid integer,
  confNo integer,
  startOdometer number(6,0) not null,
  beginTimestamp timestamp,
  returnTimestamp timestamp,
  endOdometer number(6,0),   
  fullTank integer,
  finalCost number(6,0),
  foreign key (vid) references Vehicles,
  foreign key(confNo) references Reservations(confNo));

insert into Customers values ('1233377','123-333-7321','Fay','12 Ave NW');
insert into Customers values ('1244466','123-111-7341','Hed','123 A street NW');
insert into Customers values ('1235557','123-336-7416','Sony','12 B street NW');
insert into Customers values ('1266647','123-556-7431','Roger','3 Ave SW');
insert into Customers values ('1221117','177-776-7512','Kristy','1 street NE');
insert into Customers values ('1233117','123-446-7324','Crystal','3 elmo street NW');
insert into Customers values ('1111167','122-646-7213','Jered','13 bear street NE');
insert into Customers values ('123','123-456-7890','Bob','10 Hi Street');


insert into VehicleTypes values ('Electric','fun', 5.44, 21.00, 80.00, 20.00);
insert into VehicleTypes values ('Van','not fun', 5.44, 25.00, 50.00, 50.00);
insert into VehicleTypes values ('Cruiser','fast', 2.54, 20.00, 75.00, 40.00);
insert into VehicleTypes values ('Truck','big', 8.00, 2.00, 7.00, 50.00);
insert into VehicleTypes values ('Hybrid','yaay', 6.44, 20.50, 60.00, 30.00);
insert into VehicleTypes values ('TypeTest','test', 3.00, 23.00, 73.00, 20.00);
insert into VehicleTypes values ('TypeTest2','loud', 1.44, 10.50, 65.00, 20.00);


insert into Vehicles values (1,'plate1','make1','m1',12,'blue',12.23,'b','Electric','Vancouver');
insert into Vehicles values (2,'plate2','make2','m2',02,'red',12.23,'f','Truck','Vancouver');
insert into Vehicles values (3,'plate3','make1','m2',02,'red',12.23,'f','Truck','Vancouver');
insert into Vehicles values (4,'plate4','make1','m3',12,'blue',12.23,'b','Electric','Vancouver');
insert into Vehicles values (5,'plate5','make2','m5',02,'red',12.23,'f','Truck','Vancouver');
insert into Vehicles values (6,'plate6','make1','m2',02,'red',12.23,'f','Truck','Vancouver');
insert into Vehicles values (7,'plate7','make3','m2',02,'red',12.23,'f','Cruiser','Richmond');
insert into Vehicles values (8,'plate7','make2','m2',02,'red',12.23,'f','Truck','Richmond');
insert into Vehicles values (9,'plate7','make2','m2',02,'red',12.23,'f','Cruiser','Richmond');
insert into Vehicles values (10,'plate7','make1','m2',02,'red',12.23,'f','Electric','Richmond');
insert into Vehicles values (11,'plate7','make1','m2',02,'red',12.23,'f','Cruiser','Richmond');

