CREATE TABLE energy_alerts (
                               id uuid PRIMARY KEY,
                               user_id uuid NOT NULL REFERENCES auth_users(id) ON DELETE CASCADE,
                               alert_type varchar(50) NOT NULL,
                               message text NOT NULL,
                               today_kwh numeric(10,3),
                               average_kwh numeric(10,3),
                               difference_percent numeric(10,2),
                               alert_date date NOT NULL,
                               created_at timestamp with time zone NOT NULL DEFAULT now(),

                               CONSTRAINT uk_energy_alert_user_type_date
                                   UNIQUE (user_id, alert_type, alert_date)
);