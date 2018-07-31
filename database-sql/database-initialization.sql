create user 'example'@'%' identified by 'example';
create database example;
grant all privileges on .* to 'example'@'%';
grant super on *.* to 'example'@'%';
flush privileges;
