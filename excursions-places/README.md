# excursions-places

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

Entities.
1) Place.
    1.1) Fields.
        1.1.1) name String(1:90) - name of place.
        1.1.2) id Long(1:MAX_LONG) - id of place
        1.1.3) address String(1:100) - address of place.
        1.1.4) info String(1:200) - info of place.
    1.2) Methods.
        1.2.1)  Name: "create".
                Url "/place".
                Method: POST.
                In: json{name, info, address}.
                Return: Excursion
        1.2.2)  Name: "create".
                Url "/place/{id}".
                Method: PUT.
                 In: json{name, info, address}.
                Return: Place
        1.2.3)  Name: "getAll".
                Url "/place".
                Method: GET.
                In: -.
                Return: List<Place>
                
        1.2.4)  Name: "get".
                Url "/place/{id}".
                Method: GET.
                In: -.
                Return: Place
        
        1.2.5)  Name: "getNotExistPlacesIds".
                Url "/place/check".
                Method: GET.
                In path variable: places-ids-for-check.
                Return: List<Long>