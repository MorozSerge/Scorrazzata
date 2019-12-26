insert into usr (id, username, password, active, lon, lat)
    values(1, 'admin', '123', true, 30, 60);

insert into user_role (user_id, roles)
    values (1, 'USER'), (1, 'ADMIN');