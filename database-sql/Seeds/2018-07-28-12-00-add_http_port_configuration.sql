-- Initialize system backend port:
insert into configuration(name, value)
values('Java backend port', '48080');

-- Initializes the log entity history, with the initial empty log:
insert into entity_history(id, entity_name, entity_id, entity_data)
values (0, '', uuid_generate_v4(), '{}'::jsonb);

insert into seed(file_name)
values('2018-07-28-12-00-add_http_port_configuration.sql');
