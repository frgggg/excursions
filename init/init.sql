-- for excursion service
create user e_e with password 'e_e';
create database e_e;
grant all privileges on database e_e to e_e;

-- for user service
create user e_u with password 'e_u';
create database e_u;
grant all privileges on database e_e to e_u;

-- for place service
create user e_p with password 'e_p';
create database e_p;
grant all privileges on database e_p to e_p;