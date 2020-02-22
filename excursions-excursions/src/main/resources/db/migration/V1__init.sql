
create sequence excursions_sequence start 1 increment 1;
create sequence tickets_sequence start 1 increment 1;

create table excursion_places (
    excursion_id bigint not null,
    place_id bigint
);

create table excursions (
    id bigint not null,
    coins_cost bigint not null check (coins_cost<=9223372036854775807 AND coins_cost>=1),
    enable_new_tickets boolean,
    name varchar(90) not null,
    people_count int not null check (people_count>=1 AND people_count<=100),
    start timestamp not null,
    stop timestamp not null,

    primary key (id)
);

create table tickets (
    id bigint not null,
    coins_cost bigint not null check (coins_cost>=1),
    excursion_id bigint not null,
    state varchar(255) not null,
    user_id bigint not null,

    primary key (id)
);

alter table if exists excursions
    add constraint excursions_name_start_constraint unique (name, start);

alter table if exists excursion_places
    add constraint excursion_places_fk_excursion_id_constraint foreign key (excursion_id) references excursions;

