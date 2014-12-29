# --- !Ups

CREATE TABLE power_levels (
    id bigint NOT NULL AUTO_INCREMENT,
    user_name text NOT NULL,
    attack decimal(25,5) NOT NULL,
    intelligence decimal(25,5) NOT NULL,
    agility decimal(25,5) NOT NULL,
    timestamp bigint NOT NULL,
    created_at datetime NOT NULL,
    updated_at datetime NOT NULL,
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE power_levels;
