create database quartz;
create user "quartz"@"%" identified by "834475476";
grant all privileges on quartz.* to "quartz"@"%" with grant option;

create database monitorconfig;
create user "monitorconfig"@"%" identified by "834475476";
grant all privileges on monitorconfig.* to "monitorconfig"@"%" with grant option;
