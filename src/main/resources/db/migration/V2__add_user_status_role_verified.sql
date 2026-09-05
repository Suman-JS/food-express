alter table users add column status varchar(20);

update users
set status = case when enabled then 'ACTIVE' else 'INACTIVE' end;

alter table users alter column status set not null;

alter table users drop column enabled;

alter table users add column role varchar(20) not null default 'USER';

alter table users add column verified_at timestamp(6) with time zone;