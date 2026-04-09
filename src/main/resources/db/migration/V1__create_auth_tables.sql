CREATE TABLE auth_roles (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE auth_users (
                            id UUID PRIMARY KEY,
                            email VARCHAR(255) NOT NULL UNIQUE,
                            name VARCHAR(255) NOT NULL,
                            is_active BOOLEAN NOT NULL,
                            password_hash VARCHAR(255) NOT NULL,
                            created_at TIMESTAMP NOT NULL,
                            updated_at TIMESTAMP NOT NULL,
                            status VARCHAR(50) NOT NULL,
                            telegram_chat_id BIGINT,
                            role_id INTEGER NOT NULL,
                            CONSTRAINT fk_auth_users_role
                                FOREIGN KEY (role_id)
                                    REFERENCES auth_roles(id)
);