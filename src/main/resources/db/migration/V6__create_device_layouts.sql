create table device_layouts (
                                id uuid primary key,
                                device_id uuid not null unique,
                                position_x double precision not null,
                                position_y double precision not null,
                                position_z double precision not null,
                                rotation_x double precision not null,
                                rotation_y double precision not null,
                                rotation_z double precision not null,
                                scale_x double precision not null,
                                scale_y double precision not null,
                                scale_z double precision not null,
                                created_at timestamp not null,
                                updated_at timestamp not null,
                                constraint fk_device_layouts_device
                                    foreign key (device_id) references devices(id) on delete cascade
);