# excursions-users

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
Url of excursion service: "excursions-excursions.url".
Path to check tickets for user: "excursions-excursions.api-user-tickets-count".

Entities.
1) User.
    1.1) Fields.
        1.1.1) name String(1:90) - name of user.
        1.1.2) id Long(1:MAX_LONG) - id of user
        1.1.3) coins Long(1:MAX_LONG) - coins of user
        1.1.4) coinsLastUpdate LocalDataTime(not null) - time of last update of coins
    1.2) Methods.
        1.2.1)  Name: "create".
                Url "/user".
                Method: POST.
                In: json{name}.
                Return: Excursion
        1.2.2)  Name: "create".
                Url "/user/{id}".
                Method: PUT.
                In: json{name}.
                Return: Excursion
        1.2.3)  Name: "getAll".
                Url "/user".
                Method: GET.
                In: -.
                Return: List<User>
                
        1.2.4)  Name: "get".
                Url "/user/{id}".
                Method: GET.
                In: -.
                Return: User
        
        1.2.5)  Name: "setNewTicketsEnable".
                Url "/user/{id}/coins-up-by-user".
                Method: PUT.
                In path variable: coins.
                Return: -
        1.2.6)  Name: "setNewTicketsEnable".
                Url "/user/{id}/coins-down-by-user".
                Method: PUT.
                In path variable: coins.
                Return: -
                
        1.2.7)  Name: "setNewTicketsEnable".
                Url "/user/{id}/coins-up-by-excursion".
                Method: PUT.
                In path variable: coins.
                Return: -
        1.2.7)  Name: "setNewTicketsEnable".
                Url "/user/{id}/coins-down-by-excursion".
                Method: PUT.
                In path variable: coins.
                Return: -
