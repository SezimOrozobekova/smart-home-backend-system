CREATE TABLE device_types (
                              id UUID PRIMARY KEY,
                              code VARCHAR(50) NOT NULL UNIQUE,
                              name VARCHAR(100) NOT NULL,
                              category VARCHAR(50) NOT NULL,
                              icon VARCHAR(100),
                              is_controllable BOOLEAN NOT NULL DEFAULT FALSE,
                              is_active BOOLEAN NOT NULL DEFAULT TRUE,
                              created_at TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP NOT NULL
);

CREATE TABLE devices (
                         id UUID PRIMARY KEY,
                         room_id UUID,
                         device_type_id UUID NOT NULL,
                         name VARCHAR(150) NOT NULL,
                         external_id VARCHAR(255),
                         model VARCHAR(100),
                         firmware_version VARCHAR(50),
                         is_active BOOLEAN NOT NULL DEFAULT TRUE,
                         created_at TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP NOT NULL,


                         CONSTRAINT fk_devices_room
                             FOREIGN KEY (room_id)
                                 REFERENCES rooms(id)
                                 ON DELETE SET NULL,

                         CONSTRAINT fk_devices_device_type
                             FOREIGN KEY (device_type_id)
                                 REFERENCES device_types(id)
);

CREATE TABLE device_states (
                               id UUID PRIMARY KEY,
                               device_id UUID NOT NULL UNIQUE,
                               is_online BOOLEAN NOT NULL DEFAULT FALSE,
                               is_on BOOLEAN NOT NULL DEFAULT FALSE,
                               power_watts NUMERIC(10,2),
                               peak_capacity_watts NUMERIC(10,2),
                               last_seen_at TIMESTAMP,
                               raw_state JSONB,
                               recorded_at TIMESTAMP NOT NULL,

                               CONSTRAINT fk_device_states_device
                                   FOREIGN KEY (device_id)
                                       REFERENCES devices(id)
                                       ON DELETE CASCADE
);

CREATE TABLE device_commands (
                                 id UUID PRIMARY KEY,
                                 device_id UUID NOT NULL,
                                 issued_by UUID NOT NULL,
                                 command VARCHAR(50) NOT NULL,
                                 payload JSONB,
                                 status VARCHAR(20) NOT NULL,
                                 issued_at TIMESTAMP NOT NULL,
                                 completed_at TIMESTAMP,

                                 CONSTRAINT fk_device_commands_device
                                     FOREIGN KEY (device_id)
                                         REFERENCES devices(id)
                                         ON DELETE CASCADE,

                                 CONSTRAINT fk_device_commands_user
                                     FOREIGN KEY (issued_by)
                                         REFERENCES auth_users(id)
);

CREATE INDEX idx_devices_room_id ON devices(room_id);
CREATE INDEX idx_devices_device_type_id ON devices(device_type_id);
CREATE INDEX idx_device_commands_device_id ON device_commands(device_id);
CREATE INDEX idx_device_commands_issued_at ON device_commands(issued_at DESC);