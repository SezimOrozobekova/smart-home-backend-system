CREATE TABLE homes (
                       id UUID PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       address VARCHAR(500),
                       owner_id UUID NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL,
                       CONSTRAINT fk_homes_owner
                           FOREIGN KEY (owner_id)
                               REFERENCES auth_users(id)
                               ON DELETE CASCADE
);

CREATE TABLE rooms (
                       id UUID PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       home_id UUID NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL,
                       CONSTRAINT fk_rooms_home
                           FOREIGN KEY (home_id)
                               REFERENCES homes(id)
                               ON DELETE CASCADE
);

CREATE INDEX idx_homes_owner_id ON homes(owner_id);
CREATE INDEX idx_rooms_home_id ON rooms(home_id);