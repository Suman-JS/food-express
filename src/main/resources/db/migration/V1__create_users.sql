create table users (
    id uuid not null,
    email varchar(100) not null,
    password_hash varchar(255) not null,
    name varchar(100) not null,
    profile_image_url varchar(512),
    enabled boolean not null,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone,
    primary key (id)
);

alter table users
    add constraint uk_users_email unique (email);