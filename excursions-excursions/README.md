# excursions-excursions

DB:
Install postgres.
Set it's 
    url to spring.datasource.url,
    user to spring.datasource.username,
    password to spring.datasource.username.
Example of create db:
# create user user with password 'password';
# create database user_db;
# grant all privileges on database user_db to user;

Settings.
Url of place service: "excursions-places.url".
Path to check places: "excursions-places.api-check".

Url of user service: "excursions-users.url".
Path to up coins for user: "excursions-users.api-coins-up-by-excursion".
Path to down coins for user: "excursions-users.api-coins-down-by-excursion".

excursion.ended.after-day - delete excursion after stop plus it.
excursion.ended.quartz - delete ended excursions job circle.
excursion.wrong-by-places.quartz - delete wrong excursions job circle.
ticket.drop-by-user-before-stop.day - user can't drop ticket when excursion start or start minus it.
tickets.delete.quartz = - delete tickets job circle.

Entities.
1) Excursion.
    1.1) Fields.
        1.1.1) name String(1:90) - name of excursion.
        1.1.2) start LocalDataTime(in future) - time of start of excursion (e.g. 2020-02-22T16:51:43.373404).
        1.1.3) stop LocalDataTime(after start) - time of start of excursion.
        1.1.4) coinsCost Long(1:MAX_LONG) - cost of excursion.
        1.1.5) peopleCount Long(1:100) - maximum people count.
        1.1.6) placesIds List<Long>(not null, not empty) - places for excursion.
        1.1.7) enableNewTickets boolean - can user add ticket for this excursion.
        1.1.8) id Long(1:MAX_LONG) - id of excursion
    1.2) Methods.
        1.2.1)  Name: "create".
                Url "/excursion".
                Method: POST.
                In: json{name, start, stop, coinsCost, peopleCount, placesIds}.
                Return: Excursion
        1.2.2)  Name: "getAll".
                Url "/excursion".
                Method: GET.
                In: -.
                Return: List<Excursion>
                
        1.2.3)  Name: "get".
                Url "/excursion/{id}".
                Method: GET.
                In: -.
                Return: Excursion
        
        1.2.4)  Name: "setNewTicketsEnable".
                Url "/excursion/{id}/set-new-tickets-enable".
                Method: PUT.
                In: -.
                Return: -
        1.2.4)  Name: "setNewTicketsEnable".
                Url "/excursion/{id}/set-new-tickets-not-enable".
                Method: PUT.
                In: -.
                Return: -
                
2) Ticket.
    2.1) Fields.
        2.1.1) id Long(1:MAX_LONG) - id of ticket
        2.1.2) excursionId Long(1:MAX_LONG) - id of excursion
        2.1.3) userId Long(1:MAX_LONG) - id of user
        2.1.4) state TicketState(not null) - is ticket active, need delete or need delete and back coins
        2.1.5) coinsCost Long(1:MAX_LONG) - cost of excursion in moment of bay
    2.2) Methods.
        2.2.1)  Name: "create".
                Url "/ticket".
                Method: POST.
                In path variable: user-id, excursion-id, coins.
                Return: Ticket
        2.2.2)  Name: "for-user-count".
                Url "/ticket/for-user-count".
                Method: GET.
                In path variable: user-id
                Return: count of tickets for user with id user-id
        2.2.2)  Name: "for-user".
                Url "/ticket/for-user-count".
                Method: GET.
                In path variable: user-id
                Return: List<Ticket>
        2.2.3)  Name: "getAll".
                Url "/ticket".
                Method: GET.
                In: -.
                Return: List<Ticket>
               
       1.2.3)  Name: "get".
               Url "/ticket/{id}".
               Method: GET.
               In: -.
               Return: Excursion
       
       1.2.3)  Name: "deleteByUser".
               Url "/ticket/{id}/by-excursion".
               Method: GET.
               In: -.
               Return: -
       1.2.3)  Name: "deleteByExcursion".
               Url "/ticket/{id}/by-user".
               Method: GET.
               In: -.
               Return: -